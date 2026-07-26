package com.kheteebaadi.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.kheteebaadi.app.ui.AppViewModel
import com.kheteebaadi.app.ui.components.GhostButton
import com.kheteebaadi.app.ui.t
import com.kheteebaadi.app.ui.theme.TextMuted
import com.kheteebaadi.app.ui.tr
import kotlinx.coroutines.launch
import java.io.File

/**
 * Lets the user attach a photo from Camera or Gallery. On selection the image
 * is uploaded via image_upload and the returned URL is reported through [onUrl].
 */
@Composable
fun PhotoField(vm: AppViewModel, onUrl: (String?) -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var preview by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    fun handleBytes(bytes: ByteArray?) {
        if (bytes == null) return
        preview = runCatching { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }.getOrNull()
        uploading = true
        scope.launch {
            val url = vm.uploadImage(bytes)
            uploading = false
            onUrl(url)
        }
    }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleBytes(readBytes(ctx, it)) }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) cameraUri?.let { handleBytes(readBytes(ctx, it)) }
    }
    val camPerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val u = newImageUri(ctx); cameraUri = u; camera.launch(u)
        }
    }

    Column {
        Text(tr("add_photo"), color = TextMuted, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GhostButton("📷 " + tr("camera"), Modifier.weight(1f)) {
                if (hasCameraPermission(ctx)) {
                    val u = newImageUri(ctx); cameraUri = u; camera.launch(u)
                } else camPerm.launch(Manifest.permission.CAMERA)
            }
            GhostButton("🖼 " + tr("gallery"), Modifier.weight(1f)) {
                gallery.launch("image/*")
            }
        }
        if (uploading) {
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text(t("loading", vm.lang), fontSize = 13.sp, color = TextMuted)
            }
        }
        preview?.let {
            Spacer(Modifier.height(8.dp))
            Image(it.asImageBitmap(), contentDescription = null, modifier = Modifier.size(120.dp))
        }
    }
}

private fun newImageUri(ctx: Context): Uri {
    val dir = File(ctx.cacheDir, "images").apply { mkdirs() }
    val file = File(dir, "cap_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)
}

private fun readBytes(ctx: Context, uri: Uri): ByteArray? =
    runCatching { ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() } }.getOrNull()

private fun hasCameraPermission(ctx: Context): Boolean =
    ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
