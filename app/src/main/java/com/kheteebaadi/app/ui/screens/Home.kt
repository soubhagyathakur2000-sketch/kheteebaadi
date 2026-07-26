package com.kheteebaadi.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kheteebaadi.app.ui.*
import com.kheteebaadi.app.ui.components.*
import com.kheteebaadi.app.ui.theme.*

private data class Shortcut(val emoji: String, val key: String, val onClick: () -> Unit)

@Composable
fun HomeScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        KbTopBar("Kheteebaadi", vm, showBack = false)
        ColumnScrollHost {
            KbCard {
                Text(
                    "${tr("greeting")}, ${vm.user?.name ?: ""} 🙏",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenDark
                )
                Spacer(Modifier.height(4.dp))
                Text(tr("tagline"), fontSize = 13.sp, color = TextMuted)
            }
            Spacer(Modifier.height(14.dp))
            SectionHeader(tr("quick_actions"))

            val shortcuts = buildList {
                add(Shortcut("📲", "nav_buy") { vm.setRoot(Screen.Buy) })
                add(Shortcut("🌾", "nav_sell") { vm.setRoot(Screen.Sell) })
                add(Shortcut("📋", "nav_orders") { vm.setRoot(Screen.Orders) })
                add(Shortcut("📖", "nav_learn") { vm.push(Screen.Learn) })
                add(Shortcut("🧑‍🌾", "nav_support") { vm.push(Screen.Support) })
                add(Shortcut("👤", "nav_profile") { vm.setRoot(Screen.Profile) })
                if (vm.user?.isLead == true) add(Shortcut("⭐", "nav_lead") { vm.push(Screen.LeadZone) })
                if (vm.user?.isAdmin == true) add(Shortcut("🧭", "nav_admin") { vm.push(Screen.Admin) })
            }
            shortcuts.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { sc ->
                        ShortcutCard(sc, Modifier.weight(1f))
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ShortcutCard(sc: Shortcut, modifier: Modifier) {
    Surface(
        modifier = modifier.height(96.dp).clickable { sc.onClick() },
        shape = RoundedCornerShape(12.dp),
        color = CardWhite,
        shadowElevation = 2.dp
    ) {
        Column(
            Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(sc.emoji, fontSize = 30.sp)
            Spacer(Modifier.height(6.dp))
            Text(tr(sc.key), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        }
    }
}
