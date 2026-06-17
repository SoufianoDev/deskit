package deskit.demo.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import deskit.dialogs.defaults.FileChooserDefaults
import deskit.dialogs.file.filechooser.FileChooserDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FileChooserSection(onMessage: (String) -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("Choose File") }
    var extensions by remember { mutableStateOf(listOf("txt", "kt", "md", "json", "png", "jpg")) }
    var newExt by remember { mutableStateOf("") }
    var allowSoftWrapFolder by remember { mutableStateOf(false) }
    var allowSoftWrapFile by remember { mutableStateOf(false) }
    var useCustomColors by remember { mutableStateOf(false) }

    var folderIconColorRef by remember { mutableStateOf(ThemeColorRef.PRIMARY) }
    var fileIconColorRef by remember { mutableStateOf(ThemeColorRef.PRIMARY) }
    var listBgRef by remember { mutableStateOf(ThemeColorRef.SECONDARY_CONTAINER) }
    var badgeColorRef by remember { mutableStateOf(ThemeColorRef.PRIMARY) }
    var tooltipColorRef by remember { mutableStateOf(ThemeColorRef.TERTIARY) }

    DemoCard(
        title = "File Chooser",
        icon = Icons.Default.FileOpen,
        onLaunch = {
            showDialog = true
            onMessage("File chooser opened")
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
        ConfigRow("Extensions") {
            Column {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    extensions.forEach { ext ->
                        InputChip(
                            selected = false,
                            onClick = { extensions = extensions - ext },
                            label = { Text(".${ext}") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newExt,
                        onValueChange = { newExt = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Add extension") },
                        singleLine = true
                    )
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = {
                        if (newExt.isNotBlank()) {
                            extensions = extensions + newExt.trim().lowercase()
                            newExt = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add extension")
                    }
                }
            }
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
                ThemeColorSelector("Badge bg", badgeColorRef, { badgeColorRef = it })
                ThemeColorSelector("Tooltip bg", tooltipColorRef, { tooltipColorRef = it })
            }
        }
    }

    if (showDialog) {
        FileChooserDialog(
            title = title,
            allowedExtensions = extensions.ifEmpty { null },
            colors = if (useCustomColors) {
                FileChooserDefaults.colors(
                    folderIconColor = folderIconColorRef.resolve(),
                    fileIconColor = fileIconColorRef.resolve(),
                    fileAndFolderListBG = listBgRef.resolve(),
                    badgeColor = badgeColorRef.resolve(),
                    tooltipColor = tooltipColorRef.resolve()
                )
            } else null,
            allowSoftWrapFolderName = allowSoftWrapFolder,
            allowSoftWrapFileName = allowSoftWrapFile,
            onFileSelected = {
                showDialog = false
                onMessage("Selected: ${it.absolutePath}")
            },
            onCancel = {
                showDialog = false
                onMessage("Cancelled")
            }
        )
    }
}
