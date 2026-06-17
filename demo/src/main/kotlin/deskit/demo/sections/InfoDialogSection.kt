package deskit.demo.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import deskit.demo.components.ConfigRow
import deskit.demo.components.DemoCard
import deskit.demo.components.ThemeColorRef
import deskit.demo.components.resolve
import deskit.demo.components.ThemeColorSelector
import deskit.demo.model.IconChoice
import deskit.demo.model.toPainter
import deskit.dialogs.defaults.InfoDialogDefaults
import deskit.dialogs.info.InfoDialog

@Composable
fun InfoDialogSection(onMessage: (String) -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("Information") }
    var message by remember { mutableStateOf("Operation completed successfully.") }
    var width by remember { mutableFloatStateOf(450f) }
    var height by remember { mutableFloatStateOf(230f) }
    var resizable by remember { mutableStateOf(false) }
    var iconChoice by remember { mutableStateOf(IconChoice.INFO) }
    var iconSize by remember { mutableFloatStateOf(64f) }
    var useCustomContent by remember { mutableStateOf(false) }
    var useCustomColors by remember { mutableStateOf(false) }
    var iconTintRef by remember { mutableStateOf(ThemeColorRef.PRIMARY) }
    var okButtonColorRef by remember { mutableStateOf(ThemeColorRef.PRIMARY) }
    var okButtonTextColorRef by remember { mutableStateOf(ThemeColorRef.ON_PRIMARY) }

    DemoCard(
        title = "Info Dialog",
        icon = Icons.Default.Info,
        onLaunch = {
            showDialog = true
            onMessage("Info dialog opened")
        }
    ) {
        ConfigRow("Title") {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        ConfigRow("Message") {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        ConfigRow("Width") {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Text("${width.toInt()}dp", style = MaterialTheme.typography.bodySmall)
                }
                Slider(value = width, onValueChange = { width = it }, valueRange = 300f..700f)
            }
        }
        ConfigRow("Height") {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Text("${height.toInt()}dp", style = MaterialTheme.typography.bodySmall)
                }
                Slider(value = height, onValueChange = { height = it }, valueRange = 150f..500f)
            }
        }
        ConfigRow("Resizable") {
            Switch(checked = resizable, onCheckedChange = { resizable = it })
        }
        ConfigRow("Icon") {
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
                IconChoice.entries.forEach { choice ->
                    FilterChip(
                        selected = iconChoice == choice,
                        onClick = { iconChoice = choice },
                        label = { Text(choice.label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
        ConfigRow("Icon size") {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Text("${iconSize.toInt()}dp", style = MaterialTheme.typography.bodySmall)
                }
                Slider(value = iconSize, onValueChange = { iconSize = it }, valueRange = 32f..96f)
            }
        }
        ConfigRow("Custom content") {
            Switch(checked = useCustomContent, onCheckedChange = { useCustomContent = it })
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        ConfigRow("Custom colors") {
            Switch(checked = useCustomColors, onCheckedChange = { useCustomColors = it })
        }
        AnimatedVisibility(visible = useCustomColors) {
            Column {
                ThemeColorSelector("Icon tint", iconTintRef, { iconTintRef = it })
                ThemeColorSelector("OK button", okButtonColorRef, { okButtonColorRef = it })
                ThemeColorSelector("OK text", okButtonTextColorRef, { okButtonTextColorRef = it })
            }
        }
    }

    if (showDialog) {
        InfoDialog(
            title = title,
            message = message,
            width = width.dp,
            height = height.dp,
            resizable = resizable,
            icon = iconChoice.toPainter(),
            iconSize = androidx.compose.ui.unit.DpSize(iconSize.dp, iconSize.dp),
            colors = if (useCustomColors) {
                InfoDialogDefaults.colors(
                    iconTint = iconTintRef.resolve(),
                    okButtonColor = okButtonColorRef.resolve(),
                    okButtonTextColor = okButtonTextColorRef.resolve()
                )
            } else null,
            onClose = {
                showDialog = false
                onMessage("Dialog closed")
            },
            content = if (useCustomContent) {
                {
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Text("Processing files...", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        androidx.compose.material3.LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            } else {
                {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}
