package deskit.demo.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import deskit.demo.components.ConfigRow
import deskit.demo.components.DemoCard
import deskit.demo.components.ThemeColorRef
import deskit.demo.components.resolve
import deskit.demo.components.ThemeColorSelector
import deskit.demo.model.IconChoice
import deskit.demo.model.toPainter
import deskit.dialogs.confirmation.ConfirmationDialog
import deskit.dialogs.defaults.ConfirmationDialogDefaults

@Composable
fun ConfirmationSection(onMessage: (String) -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("Confirmation") }
    var message by remember { mutableStateOf("Are you sure you want to proceed?") }
    var width by remember { mutableFloatStateOf(450f) }
    var height by remember { mutableFloatStateOf(230f) }
    var resizable by remember { mutableStateOf(false) }
    var iconChoice by remember { mutableStateOf(IconChoice.QUESTION) }
    var iconSize by remember { mutableFloatStateOf(64f) }
    var confirmText by remember { mutableStateOf("Confirm") }
    var cancelText by remember { mutableStateOf("Cancel") }
    var useCustomColors by remember { mutableStateOf(false) }
    var iconTintRef by remember { mutableStateOf(ThemeColorRef.PRIMARY) }
    var confirmButtonColorRef by remember { mutableStateOf(ThemeColorRef.PRIMARY) }
    var confirmButtonTextColorRef by remember { mutableStateOf(ThemeColorRef.ON_PRIMARY) }
    var cancelButtonColorRef by remember { mutableStateOf(ThemeColorRef.ERROR_CONTAINER) }
    var cancelButtonTextColorRef by remember { mutableStateOf(ThemeColorRef.ON_ERROR_CONTAINER) }

    DemoCard(
        title = "Confirmation",
        icon = Icons.Default.QuestionMark,
        onLaunch = {
            showDialog = true
            onMessage("Confirmation dialog opened")
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
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
        ConfigRow("Confirm") {
            OutlinedTextField(
                value = confirmText,
                onValueChange = { confirmText = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        ConfigRow("Cancel") {
            OutlinedTextField(
                value = cancelText,
                onValueChange = { cancelText = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        ConfigRow("Custom colors") {
            Switch(checked = useCustomColors, onCheckedChange = { useCustomColors = it })
        }
        AnimatedVisibility(visible = useCustomColors) {
            Column {
                ThemeColorSelector("Icon tint", iconTintRef, { iconTintRef = it })
                ThemeColorSelector("Confirm bg", confirmButtonColorRef, { confirmButtonColorRef = it })
                ThemeColorSelector("Confirm text", confirmButtonTextColorRef, { confirmButtonTextColorRef = it })
                ThemeColorSelector("Cancel bg", cancelButtonColorRef, { cancelButtonColorRef = it })
                ThemeColorSelector("Cancel text", cancelButtonTextColorRef, { cancelButtonTextColorRef = it })
            }
        }
    }

    if (showDialog) {
        ConfirmationDialog(
            title = title,
            message = message,
            width = width.dp,
            height = height.dp,
            resizable = resizable,
            icon = iconChoice.toPainter(),
            iconSize = DpSize(iconSize.dp, iconSize.dp),
            confirmButtonText = confirmText,
            cancelButtonText = cancelText,
            colors = if (useCustomColors) {
                ConfirmationDialogDefaults.colors(
                    iconTint = iconTintRef.resolve(),
                    confirmButtonColor = confirmButtonColorRef.resolve(),
                    confirmButtonTextColor = confirmButtonTextColorRef.resolve(),
                    cancelButtonColor = cancelButtonColorRef.resolve(),
                    cancelButtonTextColor = cancelButtonTextColorRef.resolve()
                )
            } else null,
            onConfirm = {
                showDialog = false
                onMessage("Confirmed")
            },
            onCancel = {
                showDialog = false
                onMessage("Cancelled")
            },
            onClose = {
                showDialog = false
                onMessage("Closed via window close")
            }
        )
    }
}
