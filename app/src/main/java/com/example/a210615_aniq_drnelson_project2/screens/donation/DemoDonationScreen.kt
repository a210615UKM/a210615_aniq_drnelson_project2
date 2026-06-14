package com.example.a210615_aniq_drnelson_project2.screens.donation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
import com.example.a210615_aniq_drnelson_project2.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoDonationScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    val campaign = viewModel.selectedCampaign
    val campaignName = campaign?.name ?: "Demo Donation"

    val quickAmounts = listOf("25", "50", "100", "250", "500")
    val tipOptions = listOf(0, 10, 15, 20)

    var amount by remember { mutableStateOf("25") }
    var isMonthly by remember { mutableStateOf(false) }
    var tipPercent by remember { mutableStateOf(15) }
    var paymentMethod by remember { mutableStateOf("Card") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }

    val baseAmount = amount.toDoubleOrNull() ?: 0.0
    val tipAmount = baseAmount * tipPercent / 100.0
    val total = baseAmount + tipAmount

    val canPay = baseAmount > 0.0 && firstName.isNotBlank() &&
        email.contains("@") && cardNumber.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
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
                text = "Complete Donation (Demo)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Test mode — no real money is charged. Fill the form and tap Pay to " +
                        "simulate a completed donation.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Donation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { new ->
                    if (new.isEmpty() || new.matches(Regex("^\\d*\\.?\\d*$"))) amount = new
                },
                label = { Text("Amount") },
                prefix = { Text("$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                quickAmounts.forEach { value ->
                    FilterChip(
                        selected = amount == value,
                        onClick = { amount = value },
                        label = { Text("$$value") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !isMonthly,
                    onClick = { isMonthly = false },
                    label = { Text("Give once") }
                )
                FilterChip(
                    selected = isMonthly,
                    onClick = { isMonthly = true },
                    label = { Text("Monthly") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Support Pledge 💜", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = "Every tip helps provide seamless, secure giving for nonprofits and donors.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tipOptions.forEach { pct ->
                    FilterChip(
                        selected = tipPercent == pct,
                        onClick = { tipPercent = pct },
                        label = { Text("$pct%") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Payment information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Card", "PayPal", "Bank").forEach { method ->
                    FilterChip(
                        selected = paymentMethod == method,
                        onClick = { paymentMethod = method },
                        label = { Text(method) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 19) cardNumber = it },
                label = { Text("Card number") },
                placeholder = { Text("MM / YY  CVC") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amt = baseAmount.toString()
                    viewModel.setDonationAmount(amt)
                    viewModel.recordDonationLocally(campaignName, "demo_${System.currentTimeMillis()}", baseAmount)
                    viewModel.completeDonation(amt, campaignName)
                    navController.navigate(AppScreen.Summary.name)
                },
                enabled = canPay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Pay $%.2f".format(total), fontWeight = FontWeight.Bold)
            }

            if (!canPay) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter an amount, first name, a valid email, and card number to continue.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "powered by Pledge (demo)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
