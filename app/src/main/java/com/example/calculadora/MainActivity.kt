package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculadora.ui.theme.CalculadoraTheme

const val ZERO = "0"
const val MENSAGEM_ERRO = "Erro"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraScreen()
        }
    }
}

@Composable
fun CalculadoraScreen() {
    var primeiroValor by rememberSaveable { mutableStateOf("") }
    var segundoValor by rememberSaveable { mutableStateOf("") }
    var operador by rememberSaveable { mutableStateOf("") }
    var displayText by rememberSaveable { mutableStateOf(ZERO) }

    CalculadoraTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Display(displayText)
            Teclado(
                onButtonClick = { input ->
                    when {
                        input == "C" -> {
                            primeiroValor = ""
                            segundoValor = ""
                            operador = ""
                            displayText = ZERO
                        }
                        input in listOf("+", "-", "*", "/") -> {
                            if (primeiroValor.isNotEmpty()) {
                                operador = input
                            }
                        }
                        input == "=" -> {
                            if (primeiroValor.isNotEmpty() && segundoValor.isNotEmpty() && operador.isNotEmpty()) {
                                displayText = processarEntrada(primeiroValor, segundoValor, operador)
                                primeiroValor = displayText
                                segundoValor = ""
                                operador = ""
                            }
                        }
                        else -> {
                            if (operador.isEmpty()) {
                                primeiroValor += input
                                displayText = primeiroValor
                            } else {
                                segundoValor += input
                                displayText = segundoValor
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun Display(displayText: String) {
    Text(
        text = displayText,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun Botao(texto: String, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(texto) },
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun Teclado(onButtonClick: (String) -> Unit) {
    val botoes = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "=", "+"),
        listOf("C")
    )

    Column {
        for (linha in botoes) {
            Row(modifier = Modifier.padding(8.dp)) {
                for (botao in linha) {
                    Botao(texto = botao, onClick = onButtonClick)
                }
            }
        }
    }
}

fun processarEntrada(primeiroValor: String?, segundoValor: String?, operador: String): String {
    if (primeiroValor.isNullOrEmpty() || segundoValor.isNullOrEmpty()) {
        return MENSAGEM_ERRO
    }
    return try {
        when (operador) {
            "+" -> (primeiroValor.toDouble() + segundoValor.toDouble()).toString()
            "-" -> (primeiroValor.toDouble() - segundoValor.toDouble()).toString()
            "*" -> (primeiroValor.toDouble() * segundoValor.toDouble()).toString()
            "/" -> {
                if (segundoValor == ZERO) MENSAGEM_ERRO
                else (primeiroValor.toDouble() / segundoValor.toDouble()).toString()
            }
            else -> MENSAGEM_ERRO
        }
    } catch (e: Exception) {
        MENSAGEM_ERRO
    }
}
