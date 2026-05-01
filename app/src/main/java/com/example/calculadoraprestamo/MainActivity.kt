package com.example.calculadoraprestamo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadoraprestamo.ui.theme.CalculadoraPrestamoTheme
import kotlin.math.pow

val Green = Color(0xFF2ECC71)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraPrestamoTheme {
                LoanCalculatorApp()
            }
        }
    }
}

data class LoanResult(
    val monthlyPayment: Double,
    val principal: Double,
    val totalInterest: Double
)

@Composable
fun LoanCalculatorApp() {
    var currentScreen by remember { mutableStateOf("calculator") }
    var loanResult by remember { mutableStateOf<LoanResult?>(null) }

    when (currentScreen) {
        "calculator" -> LoanCalculatorScreen(
            onCalculate = { result ->
                loanResult = result
                currentScreen = "result"
            }
        )
        "result" -> ResultScreen(
            result = loanResult!!,
            onBack = { currentScreen = "calculator" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculatorScreen(onCalculate: (LoanResult) -> Unit) {
    var loanAmount by remember { mutableStateOf("5000") }
    var loanTerm by remember { mutableStateOf("5") }
    var isYears by remember { mutableStateOf(true) }
    var interestRate by remember { mutableStateOf("4.5") }
    var sliderValue by remember { mutableFloatStateOf(5000f) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculate Loan") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // --- Monto del préstamo ---
            Text("Loan amount", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            OutlinedTextField(
                value = loanAmount,
                onValueChange = { value ->
                    loanAmount = value
                    value.toFloatOrNull()?.let { f ->
                        if (f in 1000f..50000f) sliderValue = f
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Min \$1000", fontSize = 11.sp, color = Color.Gray)
                Text("Max \$50000", fontSize = 11.sp, color = Color.Gray)
            }
            Slider(
                value = sliderValue,
                onValueChange = { value ->
                    sliderValue = value
                    loanAmount = value.toInt().toString()
                },
                valueRange = 1000f..50000f,
                colors = SliderDefaults.colors(
                    thumbColor = Green,
                    activeTrackColor = Green
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // --- Plazo ---
            Text("Loan term", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            OutlinedTextField(
                value = loanTerm,
                onValueChange = { loanTerm = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { isYears = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isYears) Green else Color(0xFFBDBDBD),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(
                        topStart = 8.dp, bottomStart = 8.dp,
                        topEnd = 0.dp, bottomEnd = 0.dp
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Years", fontSize = 13.sp)
                }
                Button(
                    onClick = { isYears = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isYears) Green else Color(0xFFBDBDBD),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(
                        topStart = 0.dp, bottomStart = 0.dp,
                        topEnd = 8.dp, bottomEnd = 8.dp
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Monthly", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- Tasa de interés ---
            Text("Interest rate per year", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            OutlinedTextField(
                value = interestRate,
                onValueChange = { interestRate = it },
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text("%") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // --- Error ---
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Botón calcular ---
            Button(
                onClick = {
                    errorMessage = ""
                    val amount = loanAmount.trim().toDoubleOrNull()
                    val term = loanTerm.trim().toDoubleOrNull()
                    val rate = interestRate.trim().toDoubleOrNull()

                    when {
                        loanAmount.isBlank() || amount == null ->
                            errorMessage = "Ingrese un monto del préstamo válido."
                        loanTerm.isBlank() || term == null ->
                            errorMessage = "Ingrese un plazo válido."
                        interestRate.isBlank() || rate == null ->
                            errorMessage = "Ingrese una tasa de interés válida."
                        else -> {
                            val months = if (isYears) term * 12.0 else term
                            val monthlyRate = rate / 12.0 / 100.0
                            val cuota = if (monthlyRate == 0.0) {
                                amount / months
                            } else {
                                amount * (monthlyRate * (1 + monthlyRate).pow(months)) /
                                        ((1 + monthlyRate).pow(months) - 1)
                            }
                            val totalPagado = cuota * months
                            val interesTotal = totalPagado - amount
                            onCalculate(LoanResult(cuota, amount, interesTotal))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Calculate", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(result: LoanResult, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculation result") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ResultItem(label = "Monthly Rate", value = "$%.2f".format(result.monthlyPayment))
            Spacer(modifier = Modifier.height(32.dp))
            ResultItem(label = "Principal amount", value = "$%.2f".format(result.principal))
            Spacer(modifier = Modifier.height(32.dp))
            ResultItem(label = "Total Interest", value = "$%.2f".format(result.totalInterest))
            Spacer(modifier = Modifier.height(56.dp))
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Green)
            ) {
                Text("Compare loan rates", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ResultItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )
    }
}
