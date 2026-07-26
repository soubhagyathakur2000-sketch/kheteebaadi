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
import coil.compose.AsyncImage
import com.kheteebaadi.app.data.Listing
import com.kheteebaadi.app.ui.*
import com.kheteebaadi.app.ui.components.*
import com.kheteebaadi.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SellScreen(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.refresh() }
    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("sell_produce"), vm, showBack = false)
        Column(Modifier.padding(16.dp)) {
            PrimaryButton(tr("new_listing")) { vm.push(Screen.SellNew) }
        }
        if (vm.listings.isEmpty()) {
            EmptyState(tr("no_requests"))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.listings, key = { it.id }) { ListingCard(vm, it) }
            }
        }
    }
}

@Composable
private fun ListingCard(vm: AppViewModel, l: Listing) {
    val lang = LocalLang.current
    KbCard {
        Row {
            Column(Modifier.weight(1f)) {
                Text(cropLabel(l.crop, lang), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Text("${l.qty} ${l.unit}  ·  ${tr("grade")} ${l.grade}", fontSize = 13.sp, color = TextMuted)
                if (l.loc.isNotBlank()) Text("📍 ${l.loc}", fontSize = 13.sp, color = TextMuted)
            }
            l.photo?.let { AsyncImage(model = it, contentDescription = null, modifier = Modifier.size(56.dp)) }
        }
        Spacer(Modifier.height(10.dp))
        StageTracker(LISTING_STAGES, l.stage)

        if (l.price > 0) {
            Spacer(Modifier.height(8.dp))
            Text("${tr("price_set_by_admin")}: ₹${l.price.toInt()}", color = GreenDark, fontWeight = FontWeight.Bold)
        }

        // After Delivered (index 3), farmer chooses payout — unless already chosen.
        if (l.stage >= 3 && l.payout.isBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(tr("choose_payout"), fontWeight = FontWeight.SemiBold, color = TextDark)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GhostButton(tr("upi"), Modifier.weight(1f)) { vm.listingPayout(l.id, "upi") }
                GhostButton(tr("bank"), Modifier.weight(1f)) { vm.listingPayout(l.id, "bank") }
            }
        } else if (l.payout.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text("✓ ${l.payout.uppercase()}", color = GreenPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(8.dp))
        GhostButton(tr("delete"), color = RedDanger) { vm.listingDelete(l.id) }
    }
}

@Composable
fun SellNewScreen(vm: AppViewModel) {
    val lang = LocalLang.current
    var crop by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(UNITS.first().let { if (lang == Lang.HI) it.first else it.second }) }
    var grade by remember { mutableStateOf(GRADES.first()) }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var loc by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("new_listing"), vm)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KbDropdown(tr("crop"), CROPS.map { if (lang == Lang.HI) it.first else it.second }, crop,
                onSelect = { sel ->
                    crop = CROPS.firstOrNull { (if (lang == Lang.HI) it.first else it.second) == sel }?.first ?: sel
                })
            Text(if (crop.isBlank()) "" else cropLabel(crop, lang), fontSize = 12.sp, color = TextMuted)
            KbField(qty, { qty = it.filter { c -> c.isDigit() || c == '.' } }, tr("quantity"),
                keyboard = KeyboardType.Decimal)
            KbDropdown(tr("unit"), UNITS.map { if (lang == Lang.HI) it.first else it.second }, unit,
                onSelect = { unit = it })
            KbDropdown(tr("grade"), GRADES, grade, onSelect = { grade = it })
            KbField(date, { date = it }, tr("date"))
            KbField(loc, { loc = it }, tr("location"))
            PhotoField(vm) { photoUrl = it }

            Spacer(Modifier.height(4.dp))
            PrimaryButton(tr("submit_request"), enabled = !vm.busy && crop.isNotBlank() && qty.isNotBlank()) {
                vm.listingCreate(crop, qty, unit, grade, date, loc, photoUrl)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
