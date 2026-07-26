package com.kheteebaadi.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kheteebaadi.app.data.Order
import com.kheteebaadi.app.ui.*
import com.kheteebaadi.app.ui.components.*
import com.kheteebaadi.app.ui.theme.*

@Composable
fun OrdersScreen(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.refresh() }
    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("my_orders"), vm, showBack = false)
        if (vm.orders.isEmpty()) {
            EmptyState(tr("no_orders"))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.orders, key = { it.id }) { OrderCard(vm, it) }
            }
        }
    }
}

@Composable
private fun OrderCard(vm: AppViewModel, o: Order) {
    KbCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("#${o.id}", fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.weight(1f))
            PaymentChip(o)
        }
        if (o.date.isNotBlank()) Text(o.date, fontSize = 12.sp, color = TextMuted)
        Spacer(Modifier.height(6.dp))
        if (o.status.isNotBlank()) {
            Text(o.status, color = GreenDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(4.dp))
        Row {
            Text("${tr("total")}: ₹${o.total.toInt()}", fontWeight = FontWeight.Bold, color = TextDark)
            if (o.saved > 0) {
                Spacer(Modifier.width(12.dp))
                Text("${tr("you_save")} ₹${o.saved.toInt()}", color = Gold, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GhostButton(tr("cancel"), Modifier.weight(1f), color = RedDanger) { vm.cancelOrder(o.id) }
            GhostButton(tr("remove"), Modifier.weight(1f)) { vm.removeOrder(o.id) }
        }
    }
}

@Composable
private fun PaymentChip(o: Order) {
    val paid = o.payStatus.lowercase() in listOf("done", "paid", "confirmed", "success")
    val label = when {
        o.pay.equals("cod", true) -> "COD"
        o.pay.equals("upi", true) && paid -> "UPI ✓"
        o.pay.equals("upi", true) -> "UPI · Pending"
        o.pay.equals("bank", true) && paid -> "Bank ✓"
        o.pay.equals("bank", true) -> "Bank · Pending"
        paid -> "✓"
        else -> o.pay.uppercase()
    }
    val color = if (paid) GreenPrimary else Gold
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp)) {
        Text(label, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}
