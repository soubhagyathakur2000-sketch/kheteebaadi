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
import com.kheteebaadi.app.data.Support
import com.kheteebaadi.app.ui.*
import com.kheteebaadi.app.ui.components.*
import com.kheteebaadi.app.ui.theme.*

@Composable
fun SupportScreen(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.refresh() }
    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("support"), vm)
        Column(Modifier.padding(16.dp)) {
            PrimaryButton(tr("new_support")) { vm.push(Screen.SupportNew) }
        }
        if (vm.support.isEmpty()) {
            EmptyState(tr("no_requests"))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.support, key = { it.id }) { SupportCard(vm, it) }
            }
        }
    }
}

@Composable
private fun SupportCard(vm: AppViewModel, s: Support) {
    val lang = LocalLang.current
    KbCard {
        Row {
            Column(Modifier.weight(1f)) {
                Text(cropLabel(s.crop, lang).ifBlank { s.crop }, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                if (s.problem.isNotBlank()) Text(s.problem, fontSize = 14.sp, color = TextDark)
                if (s.descr.isNotBlank()) Text(s.descr, fontSize = 13.sp, color = TextMuted)
            }
            s.photo?.let { AsyncImage(model = it, contentDescription = null, modifier = Modifier.size(56.dp)) }
        }
        Spacer(Modifier.height(10.dp))
        StageTracker(SUPPORT_STAGES, s.stage)
        if (s.specialist.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text("${tr("specialist")}: ${s.specialist}", color = GreenDark, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        GhostButton(tr("delete"), color = RedDanger) { vm.supportDelete(s.id) }
    }
}

@Composable
fun SupportNewScreen(vm: AppViewModel) {
    val lang = LocalLang.current
    var crop by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var descr by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf(vm.user?.mobile?.removePrefix("+91") ?: "") }
    var photoUrl by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("new_support"), vm)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KbDropdown(tr("crop"), CROPS.map { if (lang == Lang.HI) it.first else it.second }, crop,
                onSelect = { sel ->
                    crop = CROPS.firstOrNull { (if (lang == Lang.HI) it.first else it.second) == sel }?.first ?: sel
                })
            KbField(problem, { problem = it }, tr("problem"))
            KbField(descr, { descr = it }, tr("description"), singleLine = false)
            KbField(contact, { contact = it.filter { c -> c.isDigit() } }, tr("preferred_contact"),
                keyboard = KeyboardType.Phone)
            PhotoField(vm) { photoUrl = it }

            Spacer(Modifier.height(4.dp))
            PrimaryButton(tr("submit_request"), enabled = !vm.busy && crop.isNotBlank() && problem.isNotBlank()) {
                vm.supportCreate(crop, problem, descr, contact, photoUrl)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
