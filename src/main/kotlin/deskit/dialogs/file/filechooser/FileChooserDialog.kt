/*
Copyright 2025 Zahid Khalilov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package deskit.dialogs.file.filechooser


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import deskit.dialogs.defaults.FileChooserColors
import deskit.dialogs.defaults.FileChooserDefaults
import deskit.dialogs.file.BreadcrumbPath
import deskit.utils.MouseNavDispatcher
import deskit.utils.FileInfoDialog
import deskit.utils.LayoutViewToggle
import deskit.utils.NewFolderOverlayDialog
import deskit.utils.SearchBarSection
import deskit.utils.path.PathDisplayMode
import deskit.utils.path.PathResolver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.io.File

/**
 * Displays a file selection dialog with smart file type icons and optional extension filtering.
 *
 * This dialog provides file system navigation with visual file type identification through
 * contextual icons. Files can be filtered by extension, and the dialog includes a breadcrumb
 * navigation trail.
 *
 * @param title The title text displayed in the dialog window's title bar. Defaults to "Choose File".
 * @param startDirectory The initial directory to display when the dialog opens. Defaults to the user's Downloads folder.
 * @param allowedExtensions Optional list of file extensions to filter by (e.g., ["txt", "pdf"]).
 *                          If null, all files are shown. Extensions are case-insensitive.
 * @param colors The colors to be used for the folder chooser dialog. See [FileChooserColors] for more details.

 * @param allowSoftWrapFolderName Whether to allow soft wrapping for folder names if they are too long. Defaults to `false`.
 * @param allowSoftWrapFileName Whether to allow soft wrapping for file names if they are too long. Defaults to `false`.
 * @param onFileSelected Callback function invoked with the selected File when the user clicks a file.
 * @param onCancel Callback function invoked when the user cancels the operation.
 *
 * @sample deskit.dialogs.file.filechooser.FileChooserDialogSample
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileChooserDialog(
    title: String = "Choose File",
    startDirectory: File = File(System.getProperty("user.home") + "/Downloads"),
    allowedExtensions: List<String>? = null,
    pathDisplayMode: PathDisplayMode = PathDisplayMode.LOGICAL_HOME,
    allowSystemRootAccess: Boolean = false,
    colors: FileChooserColors? = null,
    colorScheme: ColorScheme? = null,
    allowSoftWrapFolderName: Boolean = false,
    allowSoftWrapFileName: Boolean = false,
    onFileSelected: (File) -> Unit,
    onCancel: () -> Unit
) {
    val resolvedColorScheme = colorScheme ?: MaterialTheme.colorScheme
    val coroutineScope = rememberCoroutineScope()
    var currentDir by remember { mutableStateOf(startDirectory) }

    var searchQuery by remember { mutableStateOf("") }
    val showAll = allowedExtensions?.any { it.equals("all", ignoreCase = true) } == true

    val files = remember(currentDir) {
        currentDir.listFiles()
            ?.filter {
                !it.name.startsWith(".") &&
                        (it.isDirectory || allowedExtensions == null || showAll || allowedExtensions.any {
                                ext -> it.name.endsWith(ext, ignoreCase = true)
                        })
            }
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?: emptyList()
    }

    val filteredFiles = remember(files, searchQuery) {
        if (searchQuery.isBlank()) files
        else files.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    var selectedFileForInfo by remember { mutableStateOf<File?>(null) }

    var isListView by remember { mutableStateOf(true) }
    var creatingNewFolder by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var systemAccessWarning by remember { mutableStateOf<String?>(null) }

    val resolver = remember { PathResolver(pathDisplayMode, allowSystemRootAccess) }
    val breadcrumbSegments = remember(currentDir) { resolver.resolve(currentDir) }

    LaunchedEffect(systemAccessWarning) {
        if (systemAccessWarning != null) {
            delay(3000)
            systemAccessWarning = null
        }
    }

    val dialogState = rememberDialogState(position = WindowPosition(Alignment.Center))

    DialogWindow(
        title = title,
        state = dialogState,
        onCloseRequest = onCancel
    ) {
        window.minimumSize = Dimension(660, 600)
        window.maximumSize = Dimension(900, 600)
        window.undecoratedResizerThickness = 2.dp
        val nav = remember { MouseNavDispatcher() }
        nav.onNavigate = { dir -> currentDir = dir; searchQuery = "" }
        nav.currentSupplier = { currentDir }

        DisposableEffect(Unit) {
            nav.installOn(window)
            onDispose { nav.uninstallFrom(window) }
        }

        MaterialTheme(colorScheme = resolvedColorScheme) {
            val resolvedColors = colors ?: FileChooserDefaults.colors()
            Surface(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(Modifier.padding(16.dp)) {

                    FileFilterSection(
                    allowedExtensions = allowedExtensions
                )

                Spacer(Modifier.height(8.dp))

                BreadcrumbPath(
                    segments = breadcrumbSegments,
                    onSegmentSelected = { segment ->
                        if (resolver.isAccessible(segment.file)) {
                            nav.navigateTo(segment.file)
                        } else {
                            systemAccessWarning = "System folders are not accessible in Home mode."
                        }
                    },
                    showHomeIcon = true,
                    separator = if (pathDisplayMode == PathDisplayMode.RAW_SYSTEM) " ${File.separator} " else "\u203A",
                    skipFirstSeparator = pathDisplayMode == PathDisplayMode.RAW_SYSTEM && File.separator == "/"
                )

                systemAccessWarning?.let { warning ->
                    Text(
                        text = warning,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                    SearchBarSection(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it }
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,){
                        if (currentDir.parentFile != null) {
                            IconButton(
                                onClick = {
                                    currentDir.parentFile?.let { parent ->
                                        nav.navigateTo(parent)
                                    }
                                },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowCircleLeft,
                                    contentDescription = "Back"
                                )
                            }
                        }
                        Spacer(Modifier.width(3.dp))
                        Text("Current Directory", style = MaterialTheme.typography.labelLarge)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { creatingNewFolder = true }, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)) {
                            Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder")
                        }
                        LayoutViewToggle(isListView, onListGridViewChange = {isListView = !isListView})
                    }
                }

                Spacer(Modifier.height(4.dp))

                FileAndFolderSection(
                    files = filteredFiles,
                    allowedExtensions = allowedExtensions,
                    onDirectorySelected = { nav.navigateTo(it) },
                    onShowFileInfo = { file ->
                        selectedFileForInfo = file
                    },
                    colors = resolvedColors,
                    onFileSelected = onFileSelected,
                    modifier = Modifier.weight(1f),
                    isListView = isListView,
                    allowSoftWrapFolderName = allowSoftWrapFolderName,
                    allowSoftWrapFileName = allowSoftWrapFileName
                )


                Spacer(Modifier.height(8.dp))

                    Row(Modifier.align(Alignment.End)) {
                        TextButton(onClick = onCancel, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)) {
                            Text("Cancel", color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val selectedFile = currentDir.listFiles()?.firstOrNull { !it.isDirectory }
                                if (selectedFile != null) {
                                    onFileSelected(selectedFile)
                                }
                            },
                            enabled = files.any { !it.isDirectory },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Text("Select")
                        }
                    }
                }

                selectedFileForInfo?.let { file ->
                    FileInfoDialog(
                        file = file,
                        onClose = { selectedFileForInfo = null }
                    )
                }

                NewFolderOverlayDialog(
                    visible = creatingNewFolder,
                    folderName = newFolderName,
                    onFolderNameChanged = { newFolderName = it },
                    onCancel = {
                        creatingNewFolder = false
                        newFolderName = ""
                    },
                    onCreateFolder = {
                        val newFolder = File(currentDir, newFolderName)
                        if (!newFolder.exists()) {
                            newFolder.mkdir()
                            nav.navigateTo(newFolder)
                        }
                        creatingNewFolder = false
                        newFolderName = ""
                    }
                )
            }
            }
        }
    }
}

/**
 * A sample composable function demonstrating the usage of the [FileChooserDialog].
 */
@Composable
fun FileChooserDialogSample(){
    var showFileChooserDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                showFileChooserDialog = true
                text = "File chooser dialog is shown"
            }
        ) {
            Text("Show File chooser Dialog")
        }

        Text(text)
    }


    if(showFileChooserDialog){
        FileChooserDialog(
            title = "Open File",
            allowedExtensions = listOf("txt", "md", "json", "kt", "py", "js", "html", "css", "png", "jpg"),
            onFileSelected = {
                showFileChooserDialog = false
                text = "Selected file: ${it.absolutePath}"
            },
            onCancel = { showFileChooserDialog = false; text = "File chooser dialog was closed" }
        )
    }
}