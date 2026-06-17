package deskit.demo.sections

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import deskit.demo.components.DemoCard
import deskit.dialogs.file.filechooser.FileChooserDialog
import deskit.dialogs.file.folderchooser.FolderChooserDialog
import deskit.utils.getFileIcon
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconTestSection(onMessage: (String) -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var showFolderDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }
    var selectedFiles by remember { mutableStateOf<List<File>?>(null) }

    val startDir = remember {
        var dir = File(System.getProperty("user.dir"))
        val testDir = generateSequence(dir) { it.parentFile }
            .map { File(it, "tests/icons") }
            .firstOrNull { it.exists() }
        testDir ?: File(System.getProperty("user.home") + "/Downloads")
    }

    DemoCard(
        title = "Icon Test",
        icon = Icons.Default.Image,
        onLaunch = {
            showModeDialog = true
            onMessage("Icon test opened")
        }
    ) {
        if (selectedFiles != null) {
            val count = selectedFiles!!.size
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$count file${if (count != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { selectedFiles = null }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                FlowRow(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedFiles!!.sortedBy { it.name.lowercase() }.forEach { file ->
                        Column(
                            modifier = Modifier.width(80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = getFileIcon(file),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = file.name,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Select a file or folder to preview icons",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showModeDialog) {
        AlertDialog(
            onDismissRequest = { showModeDialog = false },
            title = { Text("Icon Test Mode") },
            text = { Text("Select a single file to preview its icon, or choose a folder to display all file icons.") },
            confirmButton = {
                TextButton(onClick = {
                    showModeDialog = false
                    showDialog = true
                }) { Text("Single file") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showModeDialog = false
                    showFolderDialog = true
                }) { Text("Folder") }
            }
        )
    }

    if (showDialog) {
        FileChooserDialog(
            title = "Select a file to preview its icon",
            startDirectory = startDir,
            allowedExtensions = listOf("all"),
            onFileSelected = { file ->
                showDialog = false
                selectedFiles = listOf(file)
                onMessage("Selected: ${file.absolutePath}")
            },
            onCancel = {
                showDialog = false
                onMessage("Cancelled")
            }
        )
    }

    if (showFolderDialog) {
        FolderChooserDialog(
            title = "Select a folder to preview icons",
            startDirectory = startDir,
            onFolderSelected = { folder ->
                showFolderDialog = false
                selectedFiles = folder.listFiles()
                    ?.filter { !it.name.startsWith(".") }
                    ?.sortedBy { it.name }
                onMessage("Folder selected: ${folder.absolutePath}")
            },
            onCancel = {
                showFolderDialog = false
                onMessage("Cancelled")
            }
        )
    }
}
