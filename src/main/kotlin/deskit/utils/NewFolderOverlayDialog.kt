package deskit.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun NewFolderOverlayDialog(
    visible: Boolean,
    folderName: String,
    onFolderNameChanged: (String) -> Unit,
    onCancel: () -> Unit,
    onCreateFolder: () -> Unit
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onCancel,
            icon = { Icon(Icons.Default.CreateNewFolder, contentDescription = null) },
            title = { Text("Create New Folder") },
            text = {
                OutlinedTextField(
                    value = folderName,
                    onValueChange = onFolderNameChanged,
                    label = { Text("Folder name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = onCreateFolder,
                    enabled = folderName.isNotBlank()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        )
    }
}
