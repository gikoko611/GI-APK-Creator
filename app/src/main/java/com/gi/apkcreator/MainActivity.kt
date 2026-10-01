package com.gi.apkcreator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TerminalBg = Color(0xFF05070A)
private val TerminalPanel = Color(0xFF0B0F14)
private val TerminalGreen = Color(0xFF00FF88)
private val TerminalText = Color(0xFFE6EDF3)
private val TerminalMuted = Color(0xFF718096)
private val TerminalBorder = Color(0xFF1D2935)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TerminalBg
                ) {
                    APKCreatorScreen()
                }
            }
        }
    }
}

@Composable
fun APKCreatorScreen() {
    var prompt by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("waiting for command_") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "G.I // APK CREATOR",
                color = TerminalGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp
            )

            Text(
                text = "v0.1.0",
                color = TerminalMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }

        TerminalDivider()

        // System status
        TerminalPanel {
            StatusLine("SYSTEM", "ONLINE")
            StatusLine("AI", "READY")
            StatusLine("BUILD", "IDLE")
        }

        // Project
        TerminalPanel {
            Text(
                text = "┌─ PROJECT",
                color = TerminalGreen,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "> new-project",
                color = TerminalText,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "> Android / Jetpack Compose",
                color = TerminalMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }

        // AI terminal
        TerminalPanel {
            Text(
                text = "┌─ AI TERMINAL",
                color = TerminalGreen,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "$ describe your application...",
                color = TerminalMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TerminalText,
                    fontFamily = FontFamily.Monospace
                ),
                label = {
                    Text(
                        "> prompt",
                        color = TerminalMuted
                    )
                },
                placeholder = {
                    Text(
                        "create a notes app with dark mode...",
                        color = TerminalMuted
                    )
                },
                minLines = 5
            )
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TerminalButton(
                text = "GENERATE",
                modifier = Modifier.weight(1f)
            ) {
                status = "analyzing request..."
            }

            TerminalButton(
                text = "PREVIEW",
                modifier = Modifier.weight(1f)
            ) {
                status = "preview unavailable"
            }
        }

        TerminalButton(
            text = "BUILD APK",
            modifier = Modifier.fillMaxWidth()
        ) {
            status = "build requested..."
        }

        // Console
        TerminalPanel {
            Text(
                text = "┌─ SYSTEM LOG",
                color = TerminalGreen,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "[SYSTEM] ONLINE",
                color = TerminalText,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )

            Text(
                text = "[AI] READY",
                color = TerminalText,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )

            Text(
                text = "[BUILD] IDLE",
                color = TerminalText,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )

            Text(
                text = "> G.I AI :: $status",
                color = TerminalGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "G.I APK Creator // Cloud Build Ready",
            color = TerminalMuted,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
        )
    }
}

@Composable
fun TerminalPanel(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                TerminalPanel,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                TerminalBorder,
                RoundedCornerShape(8.dp)
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        content()
    }
}

@Composable
fun StatusLine(
    name: String,
    status: String
) {
    Row {
        Text(
            text = "[$name]",
            color = TerminalMuted,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = status,
            color = TerminalGreen,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun TerminalButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF101820),
            contentColor = TerminalGreen
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TerminalDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(TerminalBorder)
    )
}
