package deskit.dialogs.file.filesaver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import deskit.dialogs.defaults.FileSaverColors
import deskit.dialogs.defaults.FileSaverDefaults
import deskit.dialogs.info.InfoDialog
import deskit.utils.FileInfoDialog
import deskit.utils.NewFolderOverlayDialog
import deskit.utils.SearchBarSection
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.io.File


/**
 * Displays a file save dialog with directory navigation, folder creation, and file naming capabilities.
 *
 * This dialog allows users to navigate through the file system, create new folders, and specify
 * a filename for saving. It includes file existence checking and animated folder creation UI.
 *
 * @param title The title text displayed in the dialog window's title bar. Defaults to "Save As".
 * @param startDirectory Default directory to save a file.
 * @param suggestedFileName The initial filename to populate in the text field. Can be empty.
 * @param extension The file extension to append to the saved file (e.g., ".txt", ".pdf").
 * @param allowSoftWrapFolderName Whether to allow soft wrapping for folder names if they are too long. Defaults to `false`.
 * @param allowSoftWrapFileName Whether to allow soft wrapping for file names if they are too long. Defaults to `false`.
 * @param colors The colors to be used for the folder chooser dialog. See [FileSaverColors] for more details.
 * @param onSave Callback function invoked with the selected File when the user clicks Save.
 * @param onCancel Callback function invoked when the user cancels the operation.
 *
 * @sample deskit.dialogs.file.filesaver.FileSaverDialogSample
 */
@Composable
fun FileSaverDialog(
    title: String = "Save As",
    startDirectory: File = File(System.getProperty("user.home") + "/Downloads"),
    suggestedFileName: String = "",
    extension: String,
    allowSoftWrapFolderName: Boolean = false,
    allowSoftWrapFileName: Boolean = false,
    colors: FileSaverColors? = null,
    colorScheme: ColorScheme? = null,
    onSave: (File) -> Unit,
    onCancel: () -> Unit
) {
    val resolvedColorScheme = colorScheme ?: MaterialTheme.colorScheme
    var fileName by remember { mutableStateOf(suggestedFileName) }
    var showFileExistsDialog by remember { mutableStateOf(false) }
    var currentDir by remember { mutableStateOf(startDirectory) }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var selectedFileForInfo by remember { mutableStateOf<File?>(null) }
    var isListView by remember { mutableStateOf(true) }

    val pathSegments = generateSequence(currentDir) { it.parentFile }
        .toList()
        .asReversed()

    var creatingNewFolder by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    val items = remember(currentDir) {
        currentDir.listFiles()
            ?.filter { !it.name.startsWith(".") }
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?: emptyList()
    }

    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) items
        else items.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val dialogState = rememberDialogState(size = DpSize(600.dp, 600.dp), position = WindowPosition(Alignment.Center))
    val pathScrollState = rememberScrollState()

    LaunchedEffect(pathSegments) {
        pathScrollState.animateScrollTo(pathScrollState.maxValue)
    }

    DialogWindow(
        title = title,
        state = dialogState,
        onCloseRequest = onCancel
    ) {
        window.minimumSize = Dimension(600, 600)
        window.undecoratedResizerThickness = 2.dp
        MaterialTheme(colorScheme = resolvedColorScheme) {
            val resolvedColors = colors ?: FileSaverDefaults.colors()
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                    ) {
                        Text("Saving as", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(16.dp))

                        // Path segments with scrollbar
                        PathSegmentsSection(
                            pathScrollState = pathScrollState,
                            pathSegments = pathSegments,
                            onFolderSelected = { currentDir = it; searchQuery = "" }
                        )

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                            SearchBarSection(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it }
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Navigation row with Back button and New Folder button
                        NavigationButtonsSection(
                            coroutineScope = coroutineScope,
                            pathScrollState = pathScrollState,
                            currentDir = currentDir,
                            onBackClicked = { currentDir = it; searchQuery = "" },
                            onNewFolderClicked = { creatingNewFolder = true },
                            isListView = isListView,
                            onListGridViewChange = {isListView = !isListView}
                        )



                        // Files and folders list with scrollbar
                        FilesAndFoldersListSection(
                            items = filteredItems,
                            onFolderClicked = {
                                currentDir = it
                                searchQuery = ""
                                coroutineScope.launch {
                                    pathScrollState.animateScrollTo(pathScrollState.maxValue)
                                }
                            },
                            onShowFileInfo = {file ->
                                selectedFileForInfo = file
                            },
                            isListView = isListView,
                            allowSoftWrapFileName = allowSoftWrapFileName,
                            allowSoftWrapFolderName = allowSoftWrapFolderName,
                            colors = resolvedColors,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(Modifier.height(16.dp))

                        // Bottom Section: Input Field and Action Buttons Row
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FileNameInputSection(
                                    fileName = fileName,
                                    extension = extension,
                                    onFileNameChanged = { newValue ->
                                        fileName = if (newValue.endsWith(extension, ignoreCase = true)) {
                                            newValue.dropLast(extension.length)
                                        } else {
                                            newValue
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Spacer(Modifier.width(16.dp))
                                
                                TextButton(onClick = onCancel, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)) {
                                    Text(
                                        text = "Cancel",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val finalFileName = if (fileName.endsWith(extension, ignoreCase = true)) {
                                            fileName
                                        } else {
                                            fileName + extension
                                        }
                                        val finalFile = File(currentDir, finalFileName)
                                        if (finalFile.exists()) {
                                            showFileExistsDialog = true
                                        } else {
                                            onSave(finalFile)
                                        }
                                    },
                                    enabled = fileName.isNotBlank(),
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                ) {
                                    Text("Save", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            
                            Spacer(Modifier.height(4.dp))
                            
                            // Note text properly placed entirely below the actionable row
                            Text(
                                text = "Note: Extension $extension will be added automatically",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                                currentDir = newFolder
                                searchQuery = ""
                            }
                            creatingNewFolder = false
                            newFolderName = ""
                        }
                    )
                }
            }
        }
    }

    if (showFileExistsDialog) {
        val displayFileName = if (fileName.endsWith(extension, ignoreCase = true)) {
            fileName
        } else {
            fileName + extension
        }

        InfoDialog(
            title = "File already exists",
            message = "A file named \"$displayFileName\" already exists in this folder. Please choose a different name.",
            onClose = { showFileExistsDialog = false }
        )
    }
}


/**
 * A sample composable function demonstrating the usage of the [FileSaverDialog].
 */
@Composable
fun FileSaverDialogSample(){
    var showFileSaverDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                showFileSaverDialog = true
                text = "File saver dialog is shown"
            }
        ) {
            Text("Show File Saver Dialog")
        }

        Text(text)
    }


    if(showFileSaverDialog){
        FileSaverDialog(
            title = "Save As",
            suggestedFileName = "newfile",
            extension = ".md",
            onSave = {
                it.writeText("# Kotlin is fun")
                showFileSaverDialog = false; text = "File was saved and dialog was closed"
            },
            onCancel = { showFileSaverDialog = false; text = "File saver dialog was closed" }
        )
    }
}
