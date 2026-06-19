package deskit.demo.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import deskit.dialogs.file.filechooser.FileChooserDialog
import deskit.dialogs.file.filesaver.FileSaverDialog
import deskit.dialogs.file.folderchooser.FolderChooserDialog
import deskit.utils.path.PathDisplayMode

@Composable
fun SystemRootAccessSection(onMessage: (String) -> Unit = {}) {
    var showFCNormal by remember { mutableStateOf(false) }
    var showFCSystem by remember { mutableStateOf(false) }
    var showFONormal by remember { mutableStateOf(false) }
    var showFOSystem by remember { mutableStateOf(false) }
    var showFSNormal by remember { mutableStateOf(false) }
    var showFSSystem by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "System Root Access",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text(
                text = "Compare default LOGICAL_HOME behavior with system root access (starts at /).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            GroupHeader(icon = Icons.Default.FileOpen, label = "File Chooser")
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { showFCNormal = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Normal")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { showFCSystem = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Root Access")
                }
            }

            Spacer(Modifier.height(12.dp))
            GroupHeader(icon = Icons.Default.FolderOpen, label = "Folder Chooser")
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { showFONormal = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Normal")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { showFOSystem = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Root Access")
                }
            }

            Spacer(Modifier.height(12.dp))
            GroupHeader(icon = Icons.Default.Save, label = "File Saver")
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { showFSNormal = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Normal")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { showFSSystem = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Root Access")
                }
            }
        }
    }

    if (showFCNormal) {
        FileChooserDialog(
            title = "File Chooser (Normal)",
            onFileSelected = {
                showFCNormal = false
                onMessage("FC Normal — Selected: ${it.absolutePath}")
            },
            onCancel = {
                showFCNormal = false
                onMessage("FC Normal — Cancelled")
            }
        )
    }
    if (showFCSystem) {
        FileChooserDialog(
            title = "File Chooser (Root Access)",
            pathDisplayMode = PathDisplayMode.RAW_SYSTEM,
            allowSystemRootAccess = true,
            onFileSelected = {
                showFCSystem = false
                onMessage("FC Root — Selected: ${it.absolutePath}")
            },
            onCancel = {
                showFCSystem = false
                onMessage("FC Root — Cancelled")
            }
        )
    }

    if (showFONormal) {
        FolderChooserDialog(
            title = "Folder Chooser (Normal)",
            onFolderSelected = {
                showFONormal = false
                onMessage("FO Normal — Selected: ${it.absolutePath}")
            },
            onCancel = {
                showFONormal = false
                onMessage("FO Normal — Cancelled")
            }
        )
    }
    if (showFOSystem) {
        FolderChooserDialog(
            title = "Folder Chooser (Root Access)",
            pathDisplayMode = PathDisplayMode.RAW_SYSTEM,
            allowSystemRootAccess = true,
            onFolderSelected = {
                showFOSystem = false
                onMessage("FO Root — Selected: ${it.absolutePath}")
            },
            onCancel = {
                showFOSystem = false
                onMessage("FO Root — Cancelled")
            }
        )
    }

    if (showFSNormal) {
        FileSaverDialog(
            title = "File Saver (Normal)",
            extension = "",
            onSave = {
                showFSNormal = false
                onMessage("FS Normal — Saved: ${it.absolutePath}")
            },
            onCancel = {
                showFSNormal = false
                onMessage("FS Normal — Cancelled")
            }
        )
    }
    if (showFSSystem) {
        FileSaverDialog(
            title = "File Saver (Root Access)",
            pathDisplayMode = PathDisplayMode.RAW_SYSTEM,
            allowSystemRootAccess = true,
            extension = "",
            onSave = {
                showFSSystem = false
                onMessage("FS Root — Saved: ${it.absolutePath}")
            },
            onCancel = {
                showFSSystem = false
                onMessage("FS Root — Cancelled")
            }
        )
    }
}

@Composable
private fun GroupHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Spacer(Modifier.height(6.dp))
}
