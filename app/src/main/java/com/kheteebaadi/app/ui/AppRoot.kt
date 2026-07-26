package com.kheteebaadi.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kheteebaadi.app.ui.screens.*
import com.kheteebaadi.app.ui.theme.CreamBg
import com.kheteebaadi.app.ui.theme.GreenPrimary
import com.kheteebaadi.app.ui.theme.Gold

private data class Tab(val key: String, val icon: ImageVector, val screen: Screen)

private val TABS = listOf(
    Tab("nav_home", Icons.Filled.Home, Screen.Home),
    Tab("nav_buy", Icons.Filled.ShoppingCart, Screen.Buy),
    Tab("nav_sell", Icons.Filled.Agriculture, Screen.Sell),
    Tab("nav_orders", Icons.Filled.ReceiptLong, Screen.Orders),
    Tab("nav_profile", Icons.Filled.Person, Screen.Profile),
)

private fun isMainTab(s: Screen): Boolean = TABS.any { it.screen == s }

@Composable
fun AppRoot() {
    val vm: AppViewModel = viewModel()
    CompositionLocalProvider(LocalLang provides vm.lang) {
        val snackbar = remember { SnackbarHostState() }
        LaunchedEffect(vm.toast) {
            val m = vm.toast
            if (m != null) { snackbar.showSnackbar(m); vm.clearToast() }
        }
        BackHandler(enabled = vm.stack.size > 1) { vm.back() }

        Scaffold(
            containerColor = CreamBg,
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = { if (isMainTab(vm.current)) KbBottomBar(vm) }
        ) { pad ->
            Box(Modifier.fillMaxSize().padding(pad)) {
                ScreenHost(vm)
            }
        }
    }
}

@Composable
private fun KbBottomBar(vm: AppViewModel) {
    NavigationBar(containerColor = Color.White) {
        TABS.forEach { tab ->
            val selected = vm.current == tab.screen
            NavigationBarItem(
                selected = selected,
                onClick = { vm.setRoot(tab.screen) },
                icon = { Icon(tab.icon, contentDescription = t(tab.key, vm.lang)) },
                label = { Text(t(tab.key, vm.lang), maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary,
                    indicatorColor = Gold.copy(alpha = 0.20f)
                )
            )
        }
    }
}

@Composable
private fun ScreenHost(vm: AppViewModel) {
    when (val s = vm.current) {
        Screen.Boot -> BootScreen(vm)
        Screen.Login -> LoginScreen(vm)
        Screen.Register -> RegisterScreen(vm)
        Screen.Home -> HomeScreen(vm)
        Screen.Buy -> BuyScreen(vm)
        Screen.Cart -> CartScreen(vm)
        Screen.Checkout -> CheckoutScreen(vm)
        Screen.Orders -> OrdersScreen(vm)
        Screen.Sell -> SellScreen(vm)
        Screen.SellNew -> SellNewScreen(vm)
        Screen.Support -> SupportScreen(vm)
        Screen.SupportNew -> SupportNewScreen(vm)
        Screen.Learn -> LearnScreen(vm)
        is Screen.LearnDetail -> LearnDetailScreen(vm, s.index)
        Screen.Profile -> ProfileScreen(vm)
        Screen.EditProfile -> EditProfileScreen(vm)
        Screen.Addresses -> AddressesScreen(vm)
        Screen.LeadZone -> LeadZoneScreen(vm)
        Screen.Admin -> AdminScreen(vm)
    }
}
