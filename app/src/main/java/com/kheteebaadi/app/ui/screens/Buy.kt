package com.kheteebaadi.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kheteebaadi.app.data.Product
import com.kheteebaadi.app.ui.*
import com.kheteebaadi.app.ui.components.*
import com.kheteebaadi.app.ui.theme.*

const val UPI_ID = "9719192163@pthdfc"
const val PAYEE = "Soubhagya"
const val BANK_AC = "4267101006029"
const val BANK_IFSC = "CNRB0004267"
const val BANK_NAME = "Canara Bank"

private val CATEGORIES = listOf(
    "fertilizer" to ("खाद" to "Fertilizer"),
    "pesticide" to ("कीटनाशक" to "Pesticide"),
    "herbicide" to ("खरपतवारनाशक" to "Herbicide"),
    "seed" to ("बीज" to "Seed"),
)

@Composable
fun BuyScreen(vm: AppViewModel) {
    var cat by remember { mutableStateOf("") }
    val lang = LocalLang.current
    val list = remember(cat, vm.products) {
        if (cat.isEmpty()) vm.products else vm.products.filter { it.cat == cat }
    }
    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("nav_buy"), vm, showBack = false)
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(selected = cat.isEmpty(), onClick = { cat = "" }, label = { Text(tr("all")) })
            CATEGORIES.forEach { (id, names) ->
                FilterChip(
                    selected = cat == id,
                    onClick = { cat = id },
                    label = { Text(if (lang == Lang.HI) names.first else names.second) }
                )
            }
        }
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list, key = { it.id }) { p -> ProductCard(vm, p) }
            item { Spacer(Modifier.height(8.dp)) }
        }
        if (vm.cartCount > 0) CartBar(vm) { vm.push(Screen.Cart) }
    }
}

@Composable
private fun ProductCard(vm: AppViewModel, p: Product) {
    val lang = LocalLang.current
    val qty = vm.cart[p.id] ?: 0
    KbCard {
        Row(verticalAlignment = Alignment.Top) {
            if (p.img != null) {
                AsyncImage(model = p.img, contentDescription = null, modifier = Modifier.size(46.dp))
            } else {
                Text(p.em, fontSize = 34.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(if (lang == Lang.HI) p.nameHi else p.nameEn,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("${p.brand} · ${p.pack}", fontSize = 13.sp, color = TextMuted)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("₹${p.price}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                    Spacer(Modifier.width(8.dp))
                    if (p.market > p.price) StrikePrice(p.market)
                }
                if (p.stock <= 0) {
                    Text(tr("stock_out"), color = RedDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        if (p.stock <= 0) {
            OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                Text(tr("stock_out"))
            }
        } else if (qty == 0) {
            PrimaryButton(tr("add_to_cart")) { vm.addToCart(p.id) }
        } else {
            QtyStepper(qty, onMinus = { vm.setQty(p.id, qty - 1) }, onPlus = { vm.setQty(p.id, qty + 1) })
        }
    }
}

@Composable
fun QtyStepper(qty: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(onClick = onMinus, modifier = Modifier.size(48.dp), contentPadding = PaddingValues(0.dp)) {
            Text("−", fontSize = 22.sp)
        }
        Text("$qty", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f),
            color = TextDark)
        Button(onClick = onPlus, modifier = Modifier.size(48.dp), contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
            Text("+", fontSize = 22.sp, color = androidx.compose.ui.graphics.Color.White)
        }
    }
}

@Composable
fun CartBar(vm: AppViewModel, onClick: () -> Unit) {
    Surface(color = GreenDark, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🛒 ${vm.cartCount} · ₹${vm.cartTotal()}", color = androidx.compose.ui.graphics.Color.White,
                fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                Text(tr("cart"), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CartScreen(vm: AppViewModel) {
    val lang = LocalLang.current
    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("cart"), vm)
        if (vm.cart.isEmpty()) {
            EmptyState(tr("cart_empty")); return@Column
        }
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vm.cart.keys.toList()) { id ->
                val p = vm.products.firstOrNull { it.id == id } ?: return@items
                val qty = vm.cart[id] ?: 0
                KbCard {
                    Text(if (lang == Lang.HI) p.nameHi else p.nameEn, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("₹${p.price} × $qty = ₹${p.price * qty}", color = GreenDark, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    QtyStepper(qty, onMinus = { vm.setQty(id, qty - 1) }, onPlus = { vm.setQty(id, qty + 1) })
                }
            }
        }
        Surface(color = CardWhite, shadowElevation = 8.dp) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    Text(tr("total"), Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("₹${vm.cartTotal()}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = GreenDark)
                }
                if (vm.cartSavings() > 0) {
                    Row(Modifier.fillMaxWidth().padding(top = 2.dp)) {
                        Text(tr("you_save"), Modifier.weight(1f), color = Gold, fontWeight = FontWeight.SemiBold)
                        Text("₹${vm.cartSavings()}", color = Gold, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(12.dp))
                PrimaryButton(tr("checkout")) { vm.push(Screen.Checkout) }
            }
        }
    }
}

@Composable
fun CheckoutScreen(vm: AppViewModel) {
    val ctx = LocalContext.current
    val total = vm.cartTotal()
    var pay by remember { mutableStateOf("upi") }
    val addrOptions = vm.addresses.map { it.oneLine() }
    var addrIndex by remember { mutableStateOf(if (vm.addresses.isEmpty()) -1 else 0) }

    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("checkout"), vm)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(tr("select_address"))
            if (vm.addresses.isEmpty()) {
                Text(tr("select_address"), color = TextMuted, fontSize = 14.sp)
                GhostButton(tr("add_address")) { vm.push(Screen.Addresses) }
            } else {
                KbDropdown(
                    tr("select_address"),
                    addrOptions,
                    addrOptions.getOrElse(addrIndex) { "" },
                    onSelect = { sel -> addrIndex = addrOptions.indexOf(sel) }
                )
            }

            SectionHeader(tr("payment_method"))
            PayOption("upi", tr("upi"), pay) { pay = it }
            PayOption("bank", tr("bank"), pay) { pay = it }
            PayOption("cod", tr("cod"), pay) { pay = it }

            when (pay) {
                "upi" -> UpiPanel(vm, ctx, total)
                "bank" -> BankPanel()
                else -> KbCard { Text(tr("cod"), color = TextDark) }
            }
        }
        Surface(color = CardWhite, shadowElevation = 8.dp) {
            Column(Modifier.padding(16.dp)) {
                val addrId = vm.addresses.getOrNull(addrIndex)?.id ?: ""
                PrimaryButton(
                    if (pay == "cod") tr("place_order") else tr("i_have_paid"),
                    enabled = !vm.busy && addrId.isNotEmpty()
                ) { vm.placeOrder(pay, addrId) }
            }
        }
    }
}

@Composable
private fun PayOption(value: String, label: String, selected: String, onSelect: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected == value) ChipBg else CardWhite,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, if (selected == value) GreenPrimary else OutlineGray
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp).then(Modifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected == value, onClick = { onSelect(value) })
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        }
    }
}

@Composable
private fun UpiPanel(vm: AppViewModel, ctx: Context, total: Int) {
    val upi = "upi://pay?pa=$UPI_ID&pn=$PAYEE&am=$total&cu=INR"
    KbCard {
        Text("${tr("scan_qr")} · ₹$total", fontWeight = FontWeight.Bold, color = GreenDark)
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { QrImage(upi) }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(tr("upi_id"), color = TextMuted, fontSize = 12.sp)
                Text(UPI_ID, fontWeight = FontWeight.Bold, color = TextDark)
            }
            TextButton(onClick = { copyToClipboard(ctx, UPI_ID); vm.notify(t("copied", vm.lang)) }) {
                Text(tr("copy"), color = GreenPrimary, fontWeight = FontWeight.Bold)
            }
        }
        LabeledRow(tr("payee"), PAYEE)
        Spacer(Modifier.height(8.dp))
        GhostButton(tr("open_upi_app")) { openUpiApp(ctx, upi, vm) }
        Spacer(Modifier.height(12.dp))
        Text(tr("how_to_pay"), fontWeight = FontWeight.Bold, color = GreenDark)
        StepLine("1", tr("upi_step1"))
        StepLine("2", tr("upi_step2"))
        StepLine("3", tr("upi_step3"))
        StepLine("4", tr("upi_step4"))
    }
}

@Composable
private fun BankPanel() {
    KbCard {
        LabeledRow(tr("account_holder"), PAYEE)
        LabeledRow(tr("account_no"), BANK_AC)
        LabeledRow(tr("ifsc"), BANK_IFSC)
        LabeledRow(tr("bank_name"), BANK_NAME)
        Spacer(Modifier.height(8.dp))
        Text(tr("how_to_pay"), fontWeight = FontWeight.Bold, color = GreenDark)
        StepLine("1", tr("upi_step3"))
        StepLine("2", tr("upi_step4"))
    }
}

@Composable
private fun StepLine(n: String, text: String) {
    Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.Top) {
        Text("$n.", fontWeight = FontWeight.Bold, color = GreenPrimary, modifier = Modifier.width(20.dp))
        Text(text, fontSize = 14.sp, color = TextDark)
    }
}

private fun copyToClipboard(ctx: Context, text: String) {
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("upi", text))
}

private fun openUpiApp(ctx: Context, upi: String, vm: AppViewModel) {
    try {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(upi)))
    } catch (e: Exception) {
        vm.notify(t("copied", vm.lang))
    }
}
