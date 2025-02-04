package com.pokey8680.connect3

import android.Manifest
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokey8680.connect3.ui.theme.FTConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get FaceTime link from Intent
        val facetimeUrl = intent?.data?.toString()

        setContent {
            FTConnectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (facetimeUrl.isNullOrEmpty()) {
                        NoLinkScreen(modifier = Modifier.padding(innerPadding))
                    } else {
                        FaceTimeWebView(
                            url = facetimeUrl,
                            modifier = Modifier.padding(innerPadding)
                        )
                        RequestPermissions()
                    }
                }
            }
        }
    }
}

@Composable
fun NoLinkScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Ask someone to send you a FaceTime link!",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "It will open here when you click on it.",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun FaceTimeWebView(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                    databaseEnabled = true
                    allowContentAccess = true
                    allowFileAccess = true
                }
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                        return false
                    }
                }
                loadUrl(url)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun RequestPermissions() {
    val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted: Map<String, Boolean> ->
        if (!granted.values.all { it }) {
            Toast.makeText(context, "Permissions required for FaceTime", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissions)
    }
}
