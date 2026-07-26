package com.kheteebaadi.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kheteebaadi.app.data.Locations
import com.kheteebaadi.app.ui.*
import com.kheteebaadi.app.ui.components.*
import com.kheteebaadi.app.ui.theme.*

@Composable
fun ProfileScreen(vm: AppViewModel) {
    val u = vm.user
    val lang = LocalLang.current
    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("profile"), vm, showBack = false)
        ColumnScrollHost {
            KbCard {
                Text(u?.name ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                Text(u?.mobile ?: "", fontSize = 14.sp, color = TextMuted)
                Text(tr("number_change_hint"), fontSize = 12.sp, color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp))
                Spacer(Modifier.height(10.dp))
                LabeledRow(tr("district"), u?.district ?: "")
                LabeledRow(tr("tehsil"), u?.tehsil ?: "")
                LabeledRow(tr("block"), u?.block ?: "")
                LabeledRow(tr("village"), u?.village ?: "")
                LabeledRow(tr("land_acres"), (u?.acres ?: 0.0).toString())
                LabeledRow(tr("main_crops"), (u?.crops ?: emptyList()).joinToString(", ") { cropLabel(it, lang) })
            }
            Spacer(Modifier.height(14.dp))
            GhostButton(tr("edit_profile")) { vm.push(Screen.EditProfile) }
            Spacer(Modifier.height(10.dp))
            GhostButton(tr("addresses")) { vm.push(Screen.Addresses) }
            if (u?.isLead == true) {
                Spacer(Modifier.height(10.dp))
                GhostButton("⭐ " + tr("lead_zone")) { vm.push(Screen.LeadZone) }
            }
            if (u?.isAdmin == true) {
                Spacer(Modifier.height(10.dp))
                GhostButton("🧭 " + tr("admin_panel")) { vm.push(Screen.Admin) }
            }
            Spacer(Modifier.height(10.dp))
            GhostButton(tr("logout"), color = RedDanger) { vm.logout() }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun EditProfileScreen(vm: AppViewModel) {
    val u = vm.user
    val lang = LocalLang.current
    var name by remember { mutableStateOf(u?.name ?: "") }
    var district by remember { mutableStateOf(u?.district ?: "") }
    var tehsil by remember { mutableStateOf(u?.tehsil ?: "") }
    var block by remember { mutableStateOf(u?.block ?: "") }
    var village by remember { mutableStateOf(u?.village ?: "") }
    var acres by remember { mutableStateOf((u?.acres ?: 0.0).let { if (it == 0.0) "" else it.toString() }) }
    val crops = remember { mutableStateListOf<String>().apply { addAll(u?.crops ?: emptyList()) } }

    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("edit_profile"), vm)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KbField(name, { name = it }, tr("name"))
            KbDropdown(tr("district"), Locations.districts, district, onSelect = {
                district = it; tehsil = ""; block = ""
            })
            KbDropdown(tr("tehsil"), Locations.tehsils(district), tehsil, onSelect = { tehsil = it },
                enabled = district.isNotEmpty())
            KbDropdown(tr("block"), Locations.blocks(district), block, onSelect = { block = it },
                enabled = district.isNotEmpty())
            KbField(village, { village = it }, tr("village"))
            KbField(acres, { acres = it.filter { c -> c.isDigit() || c == '.' } }, tr("land_acres"),
                keyboard = KeyboardType.Decimal)
            Text(tr("main_crops"), fontWeight = FontWeight.SemiBold, color = TextDark)
            CropChips(CROPS, crops.toSet()) { hi -> if (crops.contains(hi)) crops.remove(hi) else crops.add(hi) }

            Spacer(Modifier.height(4.dp))
            PrimaryButton(tr("save"), enabled = !vm.busy && name.isNotBlank() && district.isNotEmpty()) {
                vm.profileUpdate(name, district, tehsil, block, village, acres, crops.toList())
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun AddressesScreen(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.refresh() }
    var line by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("addresses"), vm)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vm.addresses, key = { it.id }) { a ->
                KbCard {
                    Text(a.oneLine(), color = TextDark, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    GhostButton(tr("delete"), color = RedDanger) { vm.addressDelete(a.id) }
                }
            }
            item {
                KbCard {
                    SectionHeader(tr("add_address"))
                    KbField(line, { line = it }, tr("address_line"))
                    Spacer(Modifier.height(8.dp))
                    KbField(village, { village = it }, tr("village"))
                    Spacer(Modifier.height(8.dp))
                    KbDropdown(tr("district"), Locations.districts, district, onSelect = { district = it })
                    Spacer(Modifier.height(8.dp))
                    KbField(pincode, { pincode = it.filter { c -> c.isDigit() }.take(6) }, tr("pincode"),
                        keyboard = KeyboardType.Number)
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(tr("add_address"), enabled = !vm.busy && line.isNotBlank()) {
                        vm.addressAdd(line, village, district, pincode)
                        line = ""; village = ""; district = ""; pincode = ""
                    }
                }
            }
        }
    }
}
