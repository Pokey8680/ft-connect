package com.pokey8680.connect3

import android.Manifest
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.pokey8680.connect3.ui.theme.FTConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val facetimeUrl = intent?.data?.toString()

        setContent {
            FTConnectTheme {
                var currentUrl by remember { mutableStateOf(facetimeUrl) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (currentUrl.isNullOrEmpty()) {
                        NoLinkScreen(
                            modifier = Modifier.padding(innerPadding),
                            onOpenUrl = { enteredUrl -> currentUrl = enteredUrl }
                        )
                    } else {
                        FaceTimeWebView(url = currentUrl ?: "about:blank", modifier = Modifier.padding(innerPadding))
                        RequestPermissions()
                    }
                }
            }
        }

    }
}

@Composable
fun NoLinkScreen(modifier: Modifier = Modifier, onOpenUrl: (String) -> Unit) {
    var enteredUrl by remember { mutableStateOf(TextFieldValue("")) }
    val textColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.surface

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
                text = "Or enter one below to open it manually:",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            BasicTextField(
                value = enteredUrl,
                onValueChange = { enteredUrl = it },
                textStyle = TextStyle(color = textColor), // Ensures correct text color in dark mode
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                    .background(backgroundColor) // Adjusts background for dark mode
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onOpenUrl(enteredUrl.text) },
                enabled = enteredUrl.text.startsWith("https://")
            ) {
                Text("Open FaceTime Link")
            }
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

                webChromeClient = object : WebChromeClient() {
                    override fun onPermissionRequest(request: PermissionRequest?) {
                        request?.grant(request.resources) // Grant camera & mic
                    }
                }

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
            Toast.makeText(context, "Camera & Mic permissions required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissions)
    }
}

