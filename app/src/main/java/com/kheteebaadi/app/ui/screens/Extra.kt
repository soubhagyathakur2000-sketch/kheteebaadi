package com.kheteebaadi.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kheteebaadi.app.ui.*
import com.kheteebaadi.app.ui.components.*
import com.kheteebaadi.app.ui.theme.*
import org.json.JSONObject

@Composable
fun LeadZoneScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("lead_zone"), vm)
        ColumnScrollHost {
            KbCard {
                Text("⭐ " + tr("lead_zone"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                Spacer(Modifier.height(6.dp))
                Text(tr("lead_zone_desc"), fontSize = 14.sp, color = TextDark)
            }
            Spacer(Modifier.height(12.dp))
            KbCard {
                Text(tr("nav_buy"), fontWeight = FontWeight.SemiBold, color = TextDark)
                Spacer(Modifier.height(6.dp))
                PrimaryButton(tr("nav_buy")) { vm.setRoot(Screen.Buy) }
            }
        }
    }
}

// ---------------- Admin ----------------

private fun arrOf(data: JSONObject?, vararg keys: String): List<JSONObject> {
    if (data == null) return emptyList()
    for (k in keys) {
        val a = data.optJSONArray(k)
        if (a != null) return (0 until a.length()).map { a.getJSONObject(it) }
    }
    return emptyList()
}

@Composable
fun AdminScreen(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.refresh() }
    val data = vm.adminData

    val farmers = arrOf(data, "users", "farmers")
    val products = arrOf(data, "products")
    val orders = arrOf(data, "orders")
    val support = arrOf(data, "support")
    val listings = arrOf(data, "listings")
    val totalLand = farmers.sumOf { it.optDouble("acres", 0.0) }

    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("admin_panel"), vm)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (data == null) {
                EmptyState(tr("loading")); return@Column
            }
            KbCard {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text(tr("farmers"), color = TextMuted, fontSize = 13.sp)
                        Text("${farmers.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(tr("total_land"), color = TextMuted, fontSize = 13.sp)
                        Text("${"%.1f".format(totalLand)} ${tr("acres")}", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = Gold)
                    }
                }
            }

            AdminSection(tr("products_admin"), products.size) {
                products.forEach { p ->
                    AdminRow(p.optString("name_hi").ifBlank { p.optString("name") } + " · ₹" + p.optInt("price")) {
                        AdminBtn(tr("delete"), RedDanger) {
                            vm.adminAction("admin_delete_product", JSONObject().put("id", p.optString("id")))
                        }
                    }
                }
            }

            AdminSection(tr("farmers"), farmers.size) {
                farmers.groupBy { it.optString("district") }.forEach { (dist, list) ->
                    if (dist.isNotBlank()) Text("📍 $dist", fontWeight = FontWeight.Bold, color = GreenDark,
                        modifier = Modifier.padding(top = 6.dp))
                    list.forEach { f ->
                        val id = f.optString("id")
                        val lead = f.optBoolean("lead", false) || f.optInt("lead", 0) == 1
                        AdminRow("${f.optString("name")} · ${f.optString("mobile")} · ${f.optDouble("acres", 0.0)} ${tr("acres")}") {
                            AdminBtn(if (lead) tr("remove_lead") else tr("make_lead"), Gold) {
                                vm.adminAction("admin_set_role", JSONObject().put("id", id)
                                    .put("role", if (lead) "farmer" else "lead"))
                            }
                            AdminBtn(tr("reset_password"), GreenPrimary) {
                                vm.adminAction("admin_reset_password", JSONObject().put("id", id))
                            }
                            AdminBtn(tr("delete"), RedDanger) {
                                vm.adminAction("admin_delete_user", JSONObject().put("id", id))
                            }
                        }
                    }
                }
            }

            AdminSection(tr("orders_admin"), orders.size) {
                orders.forEach { o ->
                    val id = o.optString("id")
                    AdminRow("#$id · ₹${o.optDouble("total", 0.0).toInt()} · ${o.optString("status")}") {
                        AdminBtn(tr("confirm_payment"), GreenPrimary) {
                            vm.adminAction("admin_confirm_payment", JSONObject().put("id", id))
                        }
                        AdminBtn(tr("delete"), RedDanger) {
                            vm.adminAction("admin_delete_order", JSONObject().put("id", id))
                        }
                    }
                }
            }

            AdminSection(tr("support_admin"), support.size) {
                support.forEach { s -> AdminSupportRow(vm, s) }
            }

            AdminSection(tr("produce_admin"), listings.size) {
                listings.forEach { l -> AdminListingRow(vm, l) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AdminSection(title: String, count: Int, content: @Composable ColumnScope.() -> Unit) {
    var open by remember { mutableStateOf(false) }
    KbCard {
        Row(Modifier.fillMaxWidth().clickable { open = !open }, verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontWeight = FontWeight.Bold, color = GreenDark, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Text("$count ${if (open) "▲" else "▼"}", color = TextMuted)
        }
        AnimatedVisibility(open) { Column(content = content) }
    }
}

@Composable
private fun AdminRow(text: String, actions: @Composable RowScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text, fontSize = 14.sp, color = TextDark)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), content = actions)
    }
    androidx.compose.material3.HorizontalDivider()
}

@Composable
private fun AdminBtn(label: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        Text(label, color = color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AdminSupportRow(vm: AppViewModel, s: JSONObject) {
    val id = s.optString("id")
    var specialist by remember { mutableStateOf("") }
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("${s.optString("crop")} · ${s.optString("problem")} · 📞 ${s.optString("contact")}",
            fontSize = 14.sp, color = TextDark)
        Row(verticalAlignment = Alignment.CenterVertically) {
            KbField(specialist, { specialist = it }, tr("specialist"), modifier = Modifier.weight(1f))
            AdminBtn(tr("appoint_specialist"), GreenPrimary) {
                vm.adminAction("admin_assign_specialist", JSONObject().put("id", id).put("specialist", specialist))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AdminBtn(tr("delete"), RedDanger) {
                vm.adminAction("admin_delete_support", JSONObject().put("id", id))
            }
        }
    }
    androidx.compose.material3.HorizontalDivider()
}

@Composable
private fun AdminListingRow(vm: AppViewModel, l: JSONObject) {
    val id = l.optString("id")
    var price by remember { mutableStateOf("") }
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("${l.optString("crop")} · ${l.optString("qty")} ${l.optString("unit")} · ${tr("grade")} ${l.optString("grade")}",
            fontSize = 14.sp, color = TextDark)
        Row(verticalAlignment = Alignment.CenterVertically) {
            KbField(price, { price = it.filter { c -> c.isDigit() } }, tr("set_price"),
                modifier = Modifier.weight(1f), keyboard = KeyboardType.Number)
            AdminBtn(tr("quality_check"), GreenPrimary) {
                vm.adminAction("admin_listing_qc", JSONObject().put("id", id).put("price", price.toIntOrNull() ?: 0))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AdminBtn(tr("schedule_pickup"), GreenPrimary) {
                vm.adminAction("admin_listing_pickup", JSONObject().put("id", id))
            }
            AdminBtn(tr("mark_delivered"), GreenPrimary) {
                vm.adminAction("admin_listing_delivered", JSONObject().put("id", id))
            }
            AdminBtn(tr("delete"), RedDanger) {
                vm.adminAction("admin_delete_listing", JSONObject().put("id", id))
            }
        }
    }
    androidx.compose.material3.HorizontalDivider()
}
