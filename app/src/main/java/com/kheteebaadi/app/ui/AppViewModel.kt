package com.kheteebaadi.app.ui

import android.app.Application
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kheteebaadi.app.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

sealed class Screen {
    data object Boot : Screen()
    data object Login : Screen()
    data object Register : Screen()
    data object Home : Screen()
    data object Buy : Screen()
    data object Cart : Screen()
    data object Checkout : Screen()
    data object Orders : Screen()
    data object Sell : Screen()
    data object SellNew : Screen()
    data object Support : Screen()
    data object SupportNew : Screen()
    data object Learn : Screen()
    data class LearnDetail(val index: Int) : Screen()
    data object Profile : Screen()
    data object EditProfile : Screen()
    data object Addresses : Screen()
    data object LeadZone : Screen()
    data object Admin : Screen()
}

class AppViewModel(app: Application) : AndroidViewModel(app) {

    // ---- state ----
    var lang by mutableStateOf(Lang.from(Prefs.lang(app))); private set
    var booting by mutableStateOf(true); private set
    var bootError by mutableStateOf(false); private set
    var busy by mutableStateOf(false); private set
    var toast by mutableStateOf<String?>(null); private set

    var user by mutableStateOf<User?>(null); private set
    var products by mutableStateOf<List<Product>>(emptyList()); private set
    var orders by mutableStateOf<List<Order>>(emptyList()); private set
    var listings by mutableStateOf<List<Listing>>(emptyList()); private set
    var support by mutableStateOf<List<Support>>(emptyList()); private set
    var addresses by mutableStateOf<List<Address>>(emptyList()); private set
    var adminData by mutableStateOf<JSONObject?>(null); private set

    val cart = mutableStateMapOf<String, Int>()
    val stack = mutableStateListOf<Screen>(Screen.Boot)
    val current: Screen get() = stack.lastOrNull() ?: Screen.Boot

    init { boot() }

    // ---- navigation ----
    fun push(s: Screen) { stack.add(s) }
    fun back(): Boolean { return if (stack.size > 1) { stack.removeAt(stack.size - 1); true } else false }
    fun setRoot(s: Screen) { stack.clear(); stack.add(s) }
    fun clearToast() { toast = null }
    fun notify(msg: String) { toast = msg }

    fun setLanguage(l: Lang) {
        lang = l
        Prefs.setLang(getApplication(), l.code)
    }
    fun toggleLang() = setLanguage(if (lang == Lang.HI) Lang.EN else Lang.HI)

    // ---- helpers ----
    private suspend fun api(action: String, body: JSONObject? = null, csrf: Boolean = false): JSONObject =
        withContext(Dispatchers.IO) { Api.post(action, body, csrf) }

    private fun obj(vararg pairs: Pair<String, Any?>): JSONObject {
        val o = JSONObject()
        for ((k, v) in pairs) o.put(k, v ?: JSONObject.NULL)
        return o
    }

    private fun arr(items: List<Any?>): JSONArray {
        val a = JSONArray(); items.forEach { a.put(it) }; return a
    }

    private fun toastErr(r: JSONObject) {
        toast = errorMessage(r.optString("error", "bad_response"), lang)
    }

    private fun act(block: suspend () -> Unit) = viewModelScope.launch {
        busy = true
        try {
            block()
        } catch (e: IOException) {
            toast = t("conn_problem", lang)
        } catch (e: Exception) {
            toast = errorMessage("bad_response", lang)
        } finally {
            busy = false
        }
    }

    private fun arrObjs(a: JSONArray?): List<JSONObject> =
        if (a == null) emptyList() else (0 until a.length()).map { a.getJSONObject(it) }

    // ---- boot / auth ----
    fun boot() {
        booting = true; bootError = false
        viewModelScope.launch {
            try {
                val r = api("bootstrap")
                if (r.optBoolean("ok")) {
                    Api.captureCsrf(r)
                    products = arrObjs(r.optJSONArray("products")).map { Product(it) }
                    val u = r.optJSONObject("user")
                    if (u != null) {
                        user = User(u)
                        if (u.has("lang")) setLanguage(Lang.from(u.optString("lang")))
                        hydrate(r)
                        setRoot(Screen.Home)
                    } else {
                        setRoot(Screen.Login)
                    }
                } else {
                    setRoot(Screen.Login)
                }
            } catch (e: Exception) {
                bootError = true
            } finally {
                booting = false
            }
        }
    }

    /** Fill lists that bootstrap already returns for a logged-in user. */
    private fun hydrate(r: JSONObject) {
        orders = arrObjs(r.optJSONArray("orders")).map { Order(it) }
        listings = arrObjs(r.optJSONArray("listings")).map { Listing(it) }
        support = arrObjs(r.optJSONArray("support")).map { Support(it) }
        addresses = arrObjs(r.optJSONArray("addresses")).map { Address(it) }
    }

    fun login(mobile: String, password: String) = act {
        val r = api("login", obj("mobile" to mobile, "password" to password))
        if (r.optBoolean("ok")) {
            Api.captureCsrf(r)
            user = User(r.getJSONObject("user"))
            reloadAll()
            setRoot(Screen.Home)
        } else toastErr(r)
    }

    fun register(
        name: String, mobile: String, password: String,
        district: String, tehsil: String, block: String, village: String,
        acres: String, crops: List<String>
    ) = act {
        val body = obj(
            "name" to name, "mobile" to mobile, "password" to password,
            "district" to district, "tehsil" to tehsil, "block" to block,
            "village" to village, "acres" to (acres.toDoubleOrNull() ?: 0.0),
            "role" to "farmer", "lang" to lang.code
        )
        body.put("crops", arr(crops))
        val r = api("register", body)
        if (r.optBoolean("ok")) {
            Api.captureCsrf(r)
            user = User(r.getJSONObject("user"))
            reloadAll()
            setRoot(Screen.Home)
        } else toastErr(r)
    }

    fun logout() = act {
        api("logout")
        user = null; cart.clear()
        orders = emptyList(); listings = emptyList(); support = emptyList(); addresses = emptyList()
        adminData = null
        setRoot(Screen.Login)
    }

    private suspend fun reloadAll() {
        loadOrders(); loadListings(); loadSupport(); loadAddresses()
        if (user?.isAdmin == true) loadAdmin()
    }

    // ---- reads ----
    private suspend fun loadOrders() {
        val r = api("orders"); if (r.optBoolean("ok")) orders = arrObjs(r.optJSONArray("orders")).map { Order(it) }
    }
    private suspend fun loadListings() {
        val r = api("listings"); if (r.optBoolean("ok")) listings = arrObjs(r.optJSONArray("listings")).map { Listing(it) }
    }
    private suspend fun loadSupport() {
        val r = api("support"); if (r.optBoolean("ok")) support = arrObjs(r.optJSONArray("support")).map { Support(it) }
    }
    private suspend fun loadAddresses() {
        val r = api("addresses"); if (r.optBoolean("ok")) addresses = arrObjs(r.optJSONArray("addresses")).map { Address(it) }
    }
    private suspend fun loadAdmin() {
        val r = api("admin_overview"); if (r.optBoolean("ok")) adminData = r
    }

    fun refresh() = act { reloadAll() }

    // ---- cart ----
    fun addToCart(id: String) { cart[id] = (cart[id] ?: 0) + 1 }
    fun setQty(id: String, q: Int) { if (q <= 0) cart.remove(id) else cart[id] = q }
    fun removeFromCart(id: String) { cart.remove(id) }
    fun clearCart() { cart.clear() }

    val cartCount: Int get() = cart.values.sum()
    fun cartTotal(): Int = cart.entries.sumOf { (id, q) -> (products.firstOrNull { it.id == id }?.price ?: 0) * q }
    fun cartSavings(): Int = cart.entries.sumOf { (id, q) ->
        val p = products.firstOrNull { it.id == id }
        if (p != null) (p.market - p.price).coerceAtLeast(0) * q else 0
    }

    // ---- orders ----
    fun placeOrder(pay: String, addrId: String) = act {
        val items = JSONArray()
        cart.forEach { (id, q) -> items.put(JSONObject().put("id", id).put("qty", q)) }
        val body = JSONObject().put("items", items).put("pay", pay).put("addr_id", addrId)
        val r = api("order_create", body, csrf = true)
        if (r.optBoolean("ok")) {
            clearCart(); toast = t("order_placed", lang); loadOrders(); setRoot(Screen.Orders)
        } else toastErr(r)
    }
    fun cancelOrder(id: String) = act {
        val r = api("order_cancel", obj("id" to id), csrf = true)
        if (r.optBoolean("ok")) loadOrders() else toastErr(r)
    }
    fun removeOrder(id: String) = act {
        val r = api("order_remove", obj("id" to id), csrf = true)
        if (r.optBoolean("ok")) loadOrders() else toastErr(r)
    }

    // ---- profile / addresses ----
    fun profileUpdate(
        name: String, district: String, tehsil: String, block: String,
        village: String, acres: String, crops: List<String>
    ) = act {
        val body = obj(
            "name" to name, "district" to district, "tehsil" to tehsil,
            "block" to block, "village" to village, "acres" to (acres.toDoubleOrNull() ?: 0.0)
        )
        body.put("crops", arr(crops))
        val r = api("profile_update", body, csrf = true)
        if (r.optBoolean("ok")) {
            r.optJSONObject("user")?.let { user = User(it) }
            toast = t("saved_ok", lang); back()
        } else toastErr(r)
    }
    fun addressAdd(line: String, village: String, district: String, pincode: String) = act {
        val r = api("address_add", obj("line" to line, "village" to village, "district" to district, "pincode" to pincode), csrf = true)
        if (r.optBoolean("ok")) loadAddresses() else toastErr(r)
    }
    fun addressDelete(id: String) = act {
        val r = api("address_delete", obj("id" to id), csrf = true)
        if (r.optBoolean("ok")) loadAddresses() else toastErr(r)
    }

    // ---- sell produce ----
    fun listingCreate(
        crop: String, qty: String, unit: String, grade: String,
        date: String, loc: String, photoUrl: String?
    ) = act {
        val body = obj("crop" to crop, "qty" to qty, "unit" to unit, "grade" to grade, "date" to date, "loc" to loc)
        if (!photoUrl.isNullOrBlank()) body.put("photo", photoUrl)
        val r = api("listing_create", body, csrf = true)
        if (r.optBoolean("ok")) { toast = t("saved_ok", lang); loadListings(); back() } else toastErr(r)
    }
    fun listingPayout(id: String, method: String) = act {
        val r = api("listing_payout", obj("id" to id, "method" to method), csrf = true)
        if (r.optBoolean("ok")) { toast = t("payout_done", lang); loadListings() } else toastErr(r)
    }
    fun listingDelete(id: String) = act {
        val r = api("listing_delete", obj("id" to id), csrf = true)
        if (r.optBoolean("ok")) loadListings() else toastErr(r)
    }

    // ---- support ----
    fun supportCreate(crop: String, problem: String, descr: String, contact: String, photoUrl: String?) = act {
        val body = obj("crop" to crop, "problem" to problem, "descr" to descr, "contact" to contact)
        if (!photoUrl.isNullOrBlank()) body.put("photo", photoUrl)
        val r = api("support_create", body, csrf = true)
        if (r.optBoolean("ok")) { toast = t("saved_ok", lang); loadSupport(); back() } else toastErr(r)
    }
    fun supportDelete(id: String) = act {
        val r = api("support_delete", obj("id" to id), csrf = true)
        if (r.optBoolean("ok")) loadSupport() else toastErr(r)
    }

    // ---- image upload (used by sell + support) ----
    // NOTE: the exact image_upload contract could not be verified against the
    // site; this sends base64 JSON and reads {ok,url}. Adjust here if needed.
    suspend fun uploadImage(bytes: ByteArray): String? {
        val b64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        val r = api("image_upload", obj("data" to "data:image/jpeg;base64,$b64", "name" to "photo.jpg"), csrf = true)
        return if (r.optBoolean("ok")) (r.optString("url").ifBlank { r.optString("path") }).ifBlank { null } else null
    }

    // ---- admin (generic action + reload) ----
    fun adminAction(action: String, params: JSONObject) = act {
        val r = api(action, params, csrf = true)
        if (r.optBoolean("ok")) { toast = t("saved_ok", lang); loadAdmin() } else toastErr(r)
    }
}
