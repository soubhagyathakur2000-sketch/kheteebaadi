package com.kheteebaadi.app.data

import org.json.JSONArray
import org.json.JSONObject

fun JSONArray.strings(): List<String> = (0 until length()).map { optString(it) }
fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }

/** Product from bootstrap/products. Fields match the website exactly. */
data class Product(val o: JSONObject) {
    val id get() = o.optString("id")
    val cat get() = o.optString("cat")
    val em get() = o.optString("em", "🌿")
    val nameHi get() = o.optString("name_hi")
    val nameEn get() = o.optString("name_en")
    val brand get() = o.optString("brand")
    val pack get() = o.optString("pack")
    val price get() = o.optInt("price")
    val market get() = o.optInt("market")
    val stock get() = o.optInt("stock")
    val crops get() = o.optJSONArray("crops")?.strings() ?: emptyList()
    val img: String? get() = o.optString("img").takeIf { it.isNotBlank() && it != "null" }

    fun doseHi() = o.optString("dose_hi"); fun doseEn() = o.optString("dose_en")
    fun appHi() = o.optString("app_hi"); fun appEn() = o.optString("app_en")
    fun safeHi() = o.optString("safe_hi"); fun safeEn() = o.optString("safe_en")
    fun descHi() = o.optString("desc_hi"); fun descEn() = o.optString("desc_en")
}

data class User(val o: JSONObject) {
    val id get() = o.optString("id")
    val name get() = o.optString("name")
    val mobile get() = o.optString("mobile")
    val role get() = o.optString("role", "farmer")
    val district get() = o.optString("district")
    val tehsil get() = o.optString("tehsil")
    val block get() = o.optString("block")
    val village get() = o.optString("village")
    val acres get() = o.optDouble("acres", 0.0)
    val crops get() = o.optJSONArray("crops")?.strings() ?: emptyList()
    val lang get() = o.optString("lang", "hi")
    val isLead get() = o.optBoolean("lead", false) || o.optInt("lead", 0) == 1 || role.equals("lead", true)
    val isAdmin get() = role.equals("admin", true) || o.optBoolean("admin", false)
}

data class OrderItem(val o: JSONObject) {
    val id get() = o.optString("id")
    val name get() = o.optString("name", o.optString("name_hi"))
    val qty get() = o.optInt("qty", 1)
    val price get() = o.optDouble("price", 0.0)
}

data class Order(val o: JSONObject) {
    val id get() = o.optString("id", o.optString("order_id"))
    val status get() = o.optString("status", o.optString("stage", ""))
    val total get() = o.optDouble("total", o.optDouble("amount", 0.0))
    val saved get() = o.optDouble("saved", o.optDouble("savings", 0.0))
    val pay get() = o.optString("pay", o.optString("payment", ""))
    val payStatus get() = o.optString("pay_status", o.optString("payment_status", ""))
    val date get() = o.optString("date", o.optString("created", ""))
    val items: List<OrderItem>
        get() = o.optJSONArray("items")?.objects()?.map { OrderItem(it) } ?: emptyList()
}

/** Sell-produce request. Moves through 5 stages (see Stages.LISTING). */
data class Listing(val o: JSONObject) {
    val id get() = o.optString("id")
    val crop get() = o.optString("crop")
    val qty get() = o.optString("qty")
    val unit get() = o.optString("unit")
    val grade get() = o.optString("grade")
    val date get() = o.optString("date")
    val loc get() = o.optString("loc", o.optString("location"))
    val photo: String? get() = o.optString("photo").takeIf { it.isNotBlank() && it != "null" }
    val stage get() = o.optInt("stage", 0)
    val price get() = o.optDouble("price", 0.0)
    val payout get() = o.optString("payout", "")
}

/** Support request. Moves through 4 stages (see Stages.SUPPORT). */
data class Support(val o: JSONObject) {
    val id get() = o.optString("id")
    val crop get() = o.optString("crop")
    val problem get() = o.optString("problem")
    val descr get() = o.optString("descr", o.optString("description"))
    val contact get() = o.optString("contact")
    val photo: String? get() = o.optString("photo").takeIf { it.isNotBlank() && it != "null" }
    val stage get() = o.optInt("stage", 0)
    val specialist get() = o.optString("specialist", o.optString("doctor", ""))
    val date get() = o.optString("date")
}

data class Address(val o: JSONObject) {
    val id get() = o.optString("id")
    val line get() = o.optString("line")
    val village get() = o.optString("village")
    val district get() = o.optString("district")
    val pincode get() = o.optString("pincode", o.optString("pin"))
    fun oneLine(): String = listOf(line, village, district, pincode)
        .filter { it.isNotBlank() }.joinToString(", ")
}
