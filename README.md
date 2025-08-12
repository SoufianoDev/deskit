# Deskit

[![](https://jitpack.io/v/zahid4kh/deskit.svg)](https://jitpack.io/#zahid4kh/deskit) [![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-blue.svg?logo=kotlin)](https://kotlinlang.org/docs/releases.html#release-details) [![Compose](https://img.shields.io/badge/Compose-1.8.0-blue.svg?logo=jetpackcompose)](https://github.com/JetBrains/compose-jb)

A library for desktop components designed for *Compose for Desktop* applications using Kotlin.

[**Latest release 1.4.0**](https://github.com/zahid4kh/deskit/wiki/1.4.0)

## What's New in 1.4.0

- **Layout View Toggle**: Switch between grid and list views in all file dialogs with interactive toggle button
- **Text Wrapping Controls**: New `allowSoftWrapFolderName` and `allowSoftWrapFileName` parameters for better text handling
- **Enhanced Compose**: Updated to Compose plugin version 1.9.0-alpha03

See the complete [v1.4.0 Release Notes](https://github.com/zahid4kh/deskit/wiki/1.4.0) for detailed changes.

### Demo Video

FolderChooserDialog in action:

<video src="https://github.com/user-attachments/assets/9abeeb1c-91d7-43ff-b800-d163214973e0" width="640" controls></video>

### Screenshots

*FileChooserDialog showing file type filtering, breadcrumb navigation with scrollbar, and folder matching counts*

![File Chooser Dialog with File Extension Filtering](screenshots/filechooser3.png)

*InfoDialog with custom icon and styled message*

![Information Dialog with Custom Icon](screenshots/infodialog.png)

*FolderChooserDialog displaying both files (dimmed) and folders with scrollbars*

![Folder Chooser Dialog with Files](screenshots/folderchooser1.png)

*FileChooserDialog with badge and tooltip, showing how many files match the required extension*

![File Chooser Dialog with Badge and Tooltip](screenshots/filechooser2.png)

*ConfirmationDialog with warning icon*

![Confirmation Dialog with Warning Icon](screenshots/confirmationdialog1.png)

---

## Installation

Add Deskit to your Compose for Desktop project:

```kotlin
implementation("com.github.zahid4kh:deskit:1.4.0")
```

For detailed setup instructions, see the [Installation Guide](https://github.com/zahid4kh/deskit/wiki/Installation).

---

## Quick Start

Get started with a simple file chooser dialog:

```kotlin
@Composable
fun FileChooserExample() {
    var showFileDialog by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<File?>(null) }

    Button(onClick = { showFileDialog = true }) {
        Text("Choose File")
    }

    if (showFileDialog) {
        FileChooserDialog(
            title = "Select a Document",
            allowedExtensions = listOf("txt", "pdf", "md"),
            resizableFileInfoDialog = true,
            allowSoftWrapFileName = false, // New in 1.4.0
            colors = FileChooserDefaults.colors(),
            onFileSelected = { file ->
                selectedFile = file
                showFileDialog = false
            },
            onCancel = { showFileDialog = false }
        )
    }
}
```

For complete examples of all dialog types, visit the [Quick Start Guide](https://github.com/zahid4kh/deskit/wiki/Quick-Start).

---

## Custom Content

Create rich, interactive dialogs with custom content:

```kotlin
InfoDialog(
    title = "Processing Files",
    content = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Processing your files...")
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text("75% Complete")
        }
    },
    onClose = { }
)
```

Learn more about creating custom dialog content in the [Custom Content Guide](https://github.com/zahid4kh/deskit/wiki/Custom-Content-in-Dialogs).

---

## Features

- 🗂️ **File System Dialogs**: Choose files, save files, and select folders with scrollbars and smart navigation
- ✅ **Confirmation Dialogs**: User-friendly confirmation prompts with customizable messages and optional icons
- ℹ️ **Information Dialogs**: Clean information display with single-action acknowledgment and custom icons
- 📋 **File Information**: Comprehensive metadata display with copy-to-clipboard functionality
- 🧭 **Breadcrumb Navigation**: Intuitive path navigation with clickable segments and horizontal scrolling
- 🎨 **Rich Visual Experience**: Custom icons, hover effects, and smooth animations

---

## Documentation

For complete documentation, visit the [Deskit Wiki](https://github.com/zahid4kh/deskit/wiki).

---

## Requirements for building from source

- **Kotlin**: 2.1.20+
- **Compose Multiplatform**: 1.8.0+
- **JVM**: 17+

For detailed requirements and compatibility information, see [Requirements](https://github.com/zahid4kh/deskit/wiki/Installation#requirements).

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.