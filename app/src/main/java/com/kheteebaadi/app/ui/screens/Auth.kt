package com.kheteebaadi.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun BootScreen(vm: AppViewModel) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (vm.bootError) {
            RetryView { vm.boot() }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌱", fontSize = 56.sp)
                Spacer(Modifier.height(8.dp))
                Text("Kheteebaadi", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                Text("खेतीबाड़ी", fontSize = 18.sp, color = GreenPrimary)
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(color = GreenPrimary)
            }
        }
    }
}

@Composable
fun RetryView(onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📶", fontSize = 44.sp)
        Spacer(Modifier.height(10.dp))
        Text(tr("conn_problem"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(Modifier.height(20.dp))
        PrimaryButton(tr("retry")) { onRetry() }
    }
}

@Composable
private fun AuthHeader(vm: AppViewModel) {
    Row(
        Modifier.fillMaxWidth().padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🌱  Kheteebaadi", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenDark,
            modifier = Modifier.weight(1f))
        LangToggle(vm)
    }
}

@Composable
fun LoginScreen(vm: AppViewModel) {
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
    ) {
        AuthHeader(vm)
        Spacer(Modifier.height(28.dp))
        Text("🌱", fontSize = 40.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(tr("tagline"), fontSize = 13.sp, color = TextMuted,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp))
        Spacer(Modifier.height(28.dp))

        MobileField(mobile) { mobile = it.take(10) }
        Spacer(Modifier.height(12.dp))
        KbField(password, { password = it }, tr("password"), isPassword = true)
        Spacer(Modifier.height(20.dp))
        PrimaryButton(tr("login"), enabled = !vm.busy && mobile.length == 10 && password.isNotEmpty()) {
            vm.login("+91$mobile", password)
        }
        Spacer(Modifier.height(12.dp))
        GhostButton(tr("no_account")) { vm.push(Screen.Register) }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun RegisterScreen(vm: AppViewModel) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var tehsil by remember { mutableStateOf("") }
    var block by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var acres by remember { mutableStateOf("") }
    val crops = remember { mutableStateListOf<String>() }

    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("create_account"), vm)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KbField(name, { name = it }, tr("name"))
            MobileField(mobile) { mobile = it.take(10) }
            KbField(password, { password = it }, tr("create_password"), isPassword = true)

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
            CropChips(CROPS, crops.toSet()) { hi ->
                if (crops.contains(hi)) crops.remove(hi) else crops.add(hi)
            }

            Spacer(Modifier.height(4.dp))
            PrimaryButton(
                tr("create_account"),
                enabled = !vm.busy && name.isNotBlank() && mobile.length == 10 &&
                    password.length >= 6 && district.isNotEmpty()
            ) {
                vm.register(name, "+91$mobile", password, district, tehsil, block, village, acres, crops.toList())
            }
            GhostButton(tr("have_account")) { vm.back() }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun MobileField(value: String, onChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(color = ChipBg, shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)) {
            Text("+91", modifier = Modifier.padding(horizontal = 12.dp, vertical = 18.dp),
                color = GreenDark, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        KbField(value, { onChange(it.filter { c -> c.isDigit() }) }, tr("mobile"),
            keyboard = KeyboardType.Phone, modifier = Modifier.weight(1f))
    }
}
