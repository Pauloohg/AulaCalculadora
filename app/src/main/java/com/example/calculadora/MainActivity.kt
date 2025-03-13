package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var mostrarFuncoesEspeciais by rememberSaveable { mutableStateOf(false) }

    CalculadoraTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Display(displayText)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Botao(texto = "C", cor = Color.Red, onClick = {
                    primeiroValor = ""
                    segundoValor = ""
                    operador = ""
                    displayText = ZERO
                })
                Botao(texto = "E", cor = Color.Gray, onClick = {
                    mostrarFuncoesEspeciais = !mostrarFuncoesEspeciais
                })
            }
            Teclado(mostrarFuncoesEspeciais, onButtonClick = { input ->
                when {
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
            })
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
fun Botao(texto: String, cor: Color = Color.DarkGray, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(texto) },
        colors = ButtonDefaults.buttonColors(containerColor = cor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .size(70.dp)
            .padding(4.dp)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun Teclado(mostrarFuncoesEspeciais: Boolean, onButtonClick: (String) -> Unit) {
    val botoes = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "=", "+")
    )
    val funcoesEspeciais = listOf("√", "%")

    Column {
        for (linha in botoes) {
            Row(modifier = Modifier.padding(4.dp)) {
                for (botao in linha) {
                    val cor = when (botao) {
                        in "0".."9" -> Color.DarkGray
                        in listOf("+", "-", "*", "/", "=") -> Color(0xFFFFA500)
                        else -> Color.Gray
                    }
                    val tamanho = when (botao) {
                        "0" -> Modifier.weight(2f)
                        "=" -> Modifier.weight(2f)
                        else -> Modifier.weight(1f)
                    }
                    Button(
                        onClick = { onButtonClick(botao) },
                        colors = ButtonDefaults.buttonColors(containerColor = cor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = tamanho.padding(4.dp)
                    ) {
                        Text(text = botao, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
        if (mostrarFuncoesEspeciais) {
            Row(modifier = Modifier.padding(4.dp)) {
                for (botao in funcoesEspeciais) {
                    Botao(texto = botao, cor = Color.Blue, onClick = onButtonClick)
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
