package com.kheteebaadi.app.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.kheteebaadi.app.ui.AppViewModel
import com.kheteebaadi.app.ui.Lang
import com.kheteebaadi.app.ui.LocalLang
import com.kheteebaadi.app.ui.theme.*

// ---------- QR ----------
fun qrBitmap(content: String, size: Int = 640): Bitmap {
    val hints = mapOf(EncodeHintType.MARGIN to 1)
    val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val pixels = IntArray(size * size)
    for (y in 0 until size) {
        val offset = y * size
        for (x in 0 until size) {
            pixels[offset + x] = if (matrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
    }
    return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, size, 0, 0, size, size)
    }
}

@Composable
fun QrImage(content: String, sizeDp: Int = 220) {
    val bmp = remember(content) { runCatching { qrBitmap(content) }.getOrNull() }
    if (bmp != null) {
        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = "QR",
            modifier = Modifier.size(sizeDp.dp)
        )
    }
}

// ---------- Top bar with हिं/EN toggle ----------
@Composable
fun KbTopBar(title: String, vm: AppViewModel, showBack: Boolean = true) {
    Surface(color = GreenPrimary, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack) {
                IconButton(onClick = { vm.back() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            } else {
                Spacer(Modifier.width(8.dp))
                Text("🌱", fontSize = 20.sp)
                Spacer(Modifier.width(6.dp))
            }
            Text(
                title,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            LangToggle(vm)
        }
    }
}

@Composable
fun LangToggle(vm: AppViewModel) {
    Surface(
        color = Color.White.copy(alpha = 0.18f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { vm.setLanguage(Lang.HI) }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text("हिं", color = if (vm.lang == Lang.HI) Gold else Color.White,
                    fontWeight = if (vm.lang == Lang.HI) FontWeight.Bold else FontWeight.Normal)
            }
            Text("/", color = Color.White)
            TextButton(onClick = { vm.setLanguage(Lang.EN) }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text("EN", color = if (vm.lang == Lang.EN) Gold else Color.White,
                    fontWeight = if (vm.lang == Lang.EN) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

// ---------- Buttons ----------
@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = Color.White)
    ) { Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
}

@Composable
fun GhostButton(text: String, modifier: Modifier = Modifier, color: Color = GreenPrimary, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) { Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
}

// ---------- Cards / text ----------
@Composable
fun KbCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CardWhite,
        shadowElevation = 2.dp
    ) { Column(Modifier.padding(14.dp), content = content) }
}

@Composable
fun SectionHeader(text: String) {
    Text(text, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = GreenDark,
        modifier = Modifier.padding(vertical = 6.dp))
}

@Composable
fun LabeledRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, color = TextMuted, fontSize = 14.sp, modifier = Modifier.width(120.dp))
        Text(value, color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
    }
}

@Composable
fun EmptyState(text: String) {
    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(text, color = TextMuted, fontSize = 15.sp)
    }
}

@Composable
fun StrikePrice(value: Int) {
    Text("₹$value", color = TextMuted, fontSize = 13.sp, textDecoration = TextDecoration.LineThrough)
}

// ---------- Fields ----------
@Composable
fun KbField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboard: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        visualTransformation = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboard),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenPrimary,
            focusedLabelColor = GreenPrimary
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KbDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                focusedLabelColor = GreenPrimary
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false })
            }
        }
    }
}

// ---------- Crop chips ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropChips(crops: List<Pair<String, String>>, selected: Set<String>, onToggle: (String) -> Unit) {
    val lang = LocalLang.current
    FlowRowSimple {
        crops.forEach { (hi, en) ->
            val on = selected.contains(hi)
            FilterChip(
                selected = on,
                onClick = { onToggle(hi) },
                label = { Text(if (lang == Lang.HI) hi else en) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GreenPrimary,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
            )
        }
    }
}

/** Minimal flow layout so chips wrap onto multiple lines. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowSimple(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.FlowRow { content() }
}

// ---------- Stage tracker ----------
@Composable
fun StageTracker(stages: List<Pair<String, String>>, currentIndex: Int) {
    val lang = LocalLang.current
    Column(Modifier.fillMaxWidth()) {
        stages.forEachIndexed { i, s ->
            val done = i < currentIndex
            val active = i == currentIndex
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                Surface(
                    shape = CircleShape,
                    color = when { done -> GreenPrimary; active -> Gold; else -> OutlineGray },
                    modifier = Modifier.size(22.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            if (done) "✓" else "${i + 1}",
                            color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    if (lang == Lang.HI) s.first else s.second,
                    fontSize = 14.sp,
                    color = if (active) GreenDark else if (done) TextDark else TextMuted,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ColumnScrollHost(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        content = content
    )
}
