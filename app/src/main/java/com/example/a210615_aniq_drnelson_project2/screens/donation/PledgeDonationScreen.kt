package com.example.a210615_aniq_drnelson_project2.screens.donation

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PledgeDonationScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    val campaign = viewModel.selectedCampaign
    val ngoId = campaign?.ngoName ?: ""
    val partnerKey = "a1c348ee2f016a7cf00cbd7fdeece1e2"

    var isLoading by remember { mutableStateOf(true) }
    var donationCompleted by remember { mutableStateOf(false) }

    if (donationCompleted) {
        LaunchedEffect(Unit) {
            navController.navigate(AppScreen.SupportMessage.name)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Complete Donation",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <script async src="https://staging.pledge.to/embed/widget.js"></script>
                <style>
                    body { margin: 0; padding: 16px; font-family: sans-serif; }
                    .plg-donate { width: 100%; }
                </style>
            </head>
            <body>
                <div class="plg-donate"
                    data-partner-key="$partnerKey"
                    data-ein="$ngoId">
                </div>
                <script>
                    window.addEventListener("message", function(event) {
                        try {
                            var data = JSON.parse(event.data);
                            if (data.message === "DonateCompleted") {
                                Android.onDonationCompleted(
                                    data.data.amount || "0",
                                    data.data.total || data.data.amount || "0"
                                );
                            }
                        } catch(e) {}
                    });
                </script>
            </body>
            </html>
        """.trimIndent()

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true

                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onDonationCompleted(amount: String, total: String) {
                            viewModel.setDonationAmount(amount)
                            viewModel.completeDonation(amount, campaign?.name ?: "Donation")
                            donationCompleted = true
                        }
                    }, "Android")

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }
                    }

                    loadDataWithBaseURL(
                        "https://staging.pledge.to",
                        htmlContent,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
}
