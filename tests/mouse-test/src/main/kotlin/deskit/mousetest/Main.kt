package deskit.mousetest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension
import java.awt.MouseInfo
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val windowState = rememberWindowState(width = 600.dp, height = 600.dp)

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Mouse Button Test - Click any button"
    ) {
        window.minimumSize = Dimension(500, 400)

        var composeIndex by remember { mutableIntStateOf(-1) }
        var awtButton by remember { mutableIntStateOf(-1) }
        val logs = remember { mutableListOf<String>() }
        var logText by remember { mutableStateOf("") }

        DisposableEffect(Unit) {
            val listener = object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    val msg = "AWT: button=${e.button}, modifiersEx=0x${e.modifiersEx.toString(16)}"
                    println(msg)
                    awtButton = e.button
                    logs.add(msg)
                    if (logs.size > 50) logs.removeFirst()
                    logText = logs.joinToString("\n")
                }
            }
            window.addMouseListener(listener)
            onDispose { window.removeMouseListener(listener) }
        }

        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .onPointerEvent(
                            eventType = PointerEventType.Press,
                            pass = PointerEventPass.Initial
                        ) { event ->
                            val idx = event.button?.index ?: -1
                            val msg = "Compose: buttonIndex=$idx"
                            println(msg)
                            composeIndex = idx
                            logs.add(msg)
                            if (logs.size > 50) logs.removeFirst()
                            logText = logs.joinToString("\n")
                        }
                ) {
                    Text(
                        text = "Mouse Button Diagnostic Tool",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Click any mouse button in this window.", style = MaterialTheme.typography.bodyMedium)
                    Text("Watch the terminal for raw output.", style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Last Compose index: ${if (composeIndex >= 0) composeIndex.toString() else "—"}",
                        style = MaterialTheme.typography.titleMedium,
                        color = when (composeIndex) {
                            0 -> Color.Gray
                            1 -> Color(0xFF4CAF50)
                            2 -> Color(0xFF2196F3)
                            3 -> Color(0xFFFF9800)
                            4 -> Color(0xFF9C27B0)
                            else -> Color.Unspecified
                        }
                    )
                    Text(
                        text = "Last AWT button: ${if (awtButton >= 0) awtButton.toString() else "—"}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Unspecified
                    )

                    if (composeIndex in listOf(3, 5, 6)) {
                        Text(
                            text = "✓ BACK BUTTON DETECTED (index=$composeIndex)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text("Button Legend:", fontWeight = FontWeight.Bold)
                    Text("  Compose 0 = Left     |  AWT 1 = Left")
                    Text("  Compose 1 = Right    |  AWT 3 = Right")
                    Text("  Compose 2 = Middle   |  AWT 2 = Middle")
                    Text("  Compose 3 = BACK     |  AWT 4 = Back", color = Color(0xFFFF9800))
                    Text("  Compose 4 = Forward  |  AWT 5 = Forward", color = Color(0xFF9C27B0))
                    Text("  Compose 5/6 = alt    |  AWT 6/7 = alt back")
                    Text("  Compose 7+ = unknown |  AWT 8+ = unknown")

                    Spacer(Modifier.height(12.dp))
                    Text("Log (last 50 events):", fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color(0xFFF5F5F5))
                            .padding(6.dp)
                    ) {
                        Text(
                            text = logText.ifEmpty { "Waiting..." },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        }
    }
}
