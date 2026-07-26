package com.kheteebaadi.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kheteebaadi.app.ui.*
import com.kheteebaadi.app.ui.components.*
import com.kheteebaadi.app.ui.theme.*

data class Article(
    val emoji: String,
    val titleHi: String, val titleEn: String,
    val bodyHi: String, val bodyEn: String
)

// Starter bilingual tips. Sync/extend to mirror the website's Learn articles.
val ARTICLES = listOf(
    Article("🌾", "गेहूं की अच्छी पैदावार", "Better wheat yield",
        "समय पर बुवाई (नवंबर पहला पखवाड़ा), प्रमाणित बीज और संतुलित खाद से पैदावार बढ़ती है। बुवाई के 21 दिन बाद पहली सिंचाई ज़रूरी है।",
        "Sow in the first half of November, use certified seed and balanced fertiliser. The first irrigation ~21 days after sowing is critical."),
    Article("💧", "सही सिंचाई", "Right irrigation",
        "फसल की क्रांतिक अवस्थाओं पर ही पानी दें — इससे पानी बचता है और पैदावार बेहतर होती है। खेत में पानी न रुकने दें।",
        "Irrigate at the crop's critical stages only — this saves water and improves yield. Do not let water stagnate."),
    Article("🐛", "कीट प्रबंधन", "Pest management",
        "पहले खेत की निगरानी करें। ज़रूरत होने पर ही अनुशंसित मात्रा में कीटनाशक छिड़कें। मास्क और दस्ताने पहनें।",
        "Scout the field first. Spray recommended pesticide doses only when needed. Always wear a mask and gloves."),
    Article("🌱", "मिट्टी की सेहत", "Soil health",
        "हर 2-3 साल में मिट्टी की जाँच कराएं। जैविक खाद और हरी खाद से मिट्टी की उर्वरता बनी रहती है।",
        "Test your soil every 2-3 years. Organic manure and green manure keep soil fertile."),
)

@Composable
fun LearnScreen(vm: AppViewModel) {
    val lang = LocalLang.current
    Column(Modifier.fillMaxSize()) {
        KbTopBar(tr("nav_learn"), vm)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(ARTICLES) { i, a ->
                KbCard(Modifier.clickable { vm.push(Screen.LearnDetail(i)) }) {
                    Row {
                        Text(a.emoji, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(if (lang == Lang.HI) a.titleHi else a.titleEn,
                            fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    }
                }
            }
        }
    }
}

@Composable
fun LearnDetailScreen(vm: AppViewModel, index: Int) {
    val lang = LocalLang.current
    val a = ARTICLES.getOrNull(index) ?: return
    Column(Modifier.fillMaxSize()) {
        KbTopBar(if (lang == Lang.HI) a.titleHi else a.titleEn, vm)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text(a.emoji, fontSize = 44.sp)
            Spacer(Modifier.height(10.dp))
            Text(if (lang == Lang.HI) a.titleHi else a.titleEn,
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenDark)
            Spacer(Modifier.height(10.dp))
            Text(if (lang == Lang.HI) a.bodyHi else a.bodyEn, fontSize = 15.sp, color = TextDark, lineHeight = 22.sp)
        }
    }
}
