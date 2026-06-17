package deskit.demo.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import deskit.demo.components.ConfigRow
import deskit.demo.components.DemoCard
import deskit.demo.components.ThemeColorRef
import deskit.demo.components.resolve
import deskit.demo.components.ThemeColorSelector
import deskit.dialogs.defaults.FileSaverDefaults
import deskit.dialogs.file.filesaver.FileSaverDialog

@Composable
fun FileSaverSection(onMessage: (String) -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("Save As") }
    var suggestedFileName by remember { mutableStateOf("untitled") }
    var extension by remember { mutableStateOf(".txt") }
    var allowSoftWrapFolder by remember { mutableStateOf(false) }
    var allowSoftWrapFile by remember { mutableStateOf(false) }
    var useCustomColors by remember { mutableStateOf(false) }

    var folderIconColorRef by remember { mutableStateOf(ThemeColorRef.PRIMARY) }
    var fileIconColorRef by remember { mutableStateOf(ThemeColorRef.PRIMARY) }
    var listBgRef by remember { mutableStateOf(ThemeColorRef.SECONDARY_CONTAINER) }
    var tooltipColorRef by remember { mutableStateOf(ThemeColorRef.TERTIARY) }

    DemoCard(
        title = "File Saver",
        icon = Icons.Default.Save,
        onLaunch = {
            showDialog = true
            onMessage("File saver opened")
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
        ConfigRow("File name") {
            OutlinedTextField(
                value = suggestedFileName,
                onValueChange = { suggestedFileName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        ConfigRow("Extension") {
            OutlinedTextField(
                value = extension,
                onValueChange = { extension = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text(".ext") }
            )
        }
        ConfigRow("Soft wrap folder") {
            Switch(checked = allowSoftWrapFolder, onCheckedChange = { allowSoftWrapFolder = it })
        }
        ConfigRow("Soft wrap file") {
            Switch(checked = allowSoftWrapFile, onCheckedChange = { allowSoftWrapFile = it })
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        ConfigRow("Custom colors") {
            Switch(checked = useCustomColors, onCheckedChange = { useCustomColors = it })
        }
        AnimatedVisibility(visible = useCustomColors) {
            Column {
                ThemeColorSelector("Folder icon", folderIconColorRef, { folderIconColorRef = it })
                ThemeColorSelector("File icon", fileIconColorRef, { fileIconColorRef = it })
                ThemeColorSelector("List bg", listBgRef, { listBgRef = it })
                ThemeColorSelector("Tooltip bg", tooltipColorRef, { tooltipColorRef = it })
            }
        }
    }

    if (showDialog) {
        FileSaverDialog(
            title = title,
            suggestedFileName = suggestedFileName,
            extension = extension,
            colors = if (useCustomColors) {
                FileSaverDefaults.colors(
                    folderIconColor = folderIconColorRef.resolve(),
                    fileIconColor = fileIconColorRef.resolve(),
                    fileAndFolderListBG = listBgRef.resolve(),
                    tooltipColor = tooltipColorRef.resolve()
                )
            } else null,
            allowSoftWrapFolderName = allowSoftWrapFolder,
            allowSoftWrapFileName = allowSoftWrapFile,
            onSave = {
                showDialog = false
                onMessage("Saved: ${it.absolutePath}")
            },
            onCancel = {
                showDialog = false
                onMessage("Cancelled")
            }
        )
    }
}
