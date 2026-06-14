package com.example.a210615_aniq_drnelson_project2.screens.donation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.util.Validators
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel

@Composable
fun SupportMessageScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    var message by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var firestoreError by remember { mutableStateOf<String?>(null) }
    var retryCount by remember { mutableIntStateOf(0) }

    val maxChars = 300
    val maxRetries = 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Leave a message of support",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        if (it.length <= maxChars) {
                            message = it
                        }
                    },
                    label = { Text("Your message") },
                    placeholder = { Text("Write something kind...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${message.length}/$maxChars",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showError && firestoreError != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = firestoreError ?: "Failed to save message",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showError = false
                                firestoreError = null
                                navController.navigate(AppScreen.ThankYou.name)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Skip")
                        }

                        if (retryCount < maxRetries) {
                            Button(
                                onClick = {
                                    showError = false
                                    firestoreError = null
                                    isSubmitting = true
                                    retryCount++
                                    val trimmed = message.trim()
                                    viewModel.storeSupportMessageToFirestore(trimmed) { success, error ->
                                        isSubmitting = false
                                        if (success) {
                                            viewModel.setLastMessage(trimmed)
                                            navController.navigate(AppScreen.ThankYou.name)
                                        } else {
                                            firestoreError = error ?: "Failed to save message"
                                            showError = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Retry (${maxRetries - retryCount}/$maxRetries attempts remaining)")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                val trimmed = message.trim()
                if (Validators.isNonEmptyMessage(trimmed)) {
                    isSubmitting = true
                    retryCount++
                    viewModel.storeSupportMessageToFirestore(trimmed) { success, error ->
                        isSubmitting = false
                        if (success) {
                            viewModel.setLastMessage(trimmed)
                            navController.navigate(AppScreen.ThankYou.name)
                        } else {
                            firestoreError = error ?: "Failed to save message"
                            showError = true
                        }
                    }
                }
            },
            enabled = Validators.isNonEmptyMessage(message.trim()) && !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Submit")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                navController.navigate(AppScreen.ThankYou.name)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Skip")
        }
    }
}
