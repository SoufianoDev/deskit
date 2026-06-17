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
package deskit.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import deskit.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


internal fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "%.1f KB".format(kb)
    val mb = kb / 1024.0
    if (mb < 1024) return "%.1f MB".format(mb)
    val gb = mb / 1024.0
    return "%.1f GB".format(gb)
}

internal fun calculateFolderSize(folder: File): Long {
    var size = 0L
    try {
        folder.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                calculateFolderSize(file)
            } else {
                file.length()
            }
        }
    } catch (e: Exception) {}
    return size
}

private fun String.stripSqlComments(): String {
    val out = StringBuilder()
    var i = 0
    while (i < length) {
        if (i + 1 < length && this[i] == '-' && this[i + 1] == '-') {
            while (i < length && this[i] != '\n') i++
        } else if (i + 1 < length && this[i] == '/' && this[i + 1] == '*') {
            i += 2
            while (i + 1 < length && !(this[i] == '*' && this[i + 1] == '/')) i++
            i += 2
        } else {
            out.append(this[i])
            i++
        }
    }
    return out.toString()
}

private fun String.containsWord(word: String): Boolean {
    var i = 0
    while (i <= length - word.length) {
        if (this[i] == word[0]) {
            var j = 0
            while (j < word.length && this[i + j] == word[j]) j++
            if (j == word.length) {
                val before = i == 0 || this[i - 1] < 'a' || this[i - 1] > 'z'
                val after = i + word.length >= length ||
                            this[i + word.length] < 'a' ||
                            this[i + word.length] > 'z'
                if (before && after) return true
            }
        }
        i++
    }
    return false
}

@Composable
fun getFileIcon(file: File): Painter {
    if (file.isDirectory) return painterResource(Res.drawable.folder)
    if (file.name == "Dockerfile") return painterResource(Res.drawable.docker)

    val name = file.name.lowercase()
    if (name == "makefile") return painterResource(Res.drawable.lang_makefile)
    if (name == "gradlew" || name == "gradlew.bat") return painterResource(Res.drawable.lang_gradle)
    if (name == ".gitattributes" || name == ".gitmodules" || name == ".gitkeep" || name == ".gitconfig" || name == ".gitignore") return painterResource(Res.drawable.git)
    if (name == ".editorconfig") return painterResource(Res.drawable.data_cog)
    if (name == ".dockerignore") return painterResource(Res.drawable.docker)
    if (name.endsWith(".pkg.tar.zst") || name.endsWith(".pkg.tar.xz")) return painterResource(Res.drawable.exe_arch)
    if (name.contains("archlinux") || name.startsWith("arch-")) return painterResource(Res.drawable.exe_arch)
    if (name.contains("alpine")) return painterResource(Res.drawable.exe_alpine)
    if (name.contains("fedora")) return painterResource(Res.drawable.exe_fedora)
    if (name.contains("linuxmint") || name.startsWith("mint-")) return painterResource(Res.drawable.exe_mint)
    if (name.contains("ubuntu")) return painterResource(Res.drawable.exe_ubuntu)
    if (name.contains("opensuse")) return painterResource(Res.drawable.exe_opensuse)
    if (name.contains("centos")) return painterResource(Res.drawable.exe_centos)
    if (name.contains("rockylinux")) return painterResource(Res.drawable.exe_rocky)
    if (name.contains("redhat") || name.contains("red-hat")) return painterResource(Res.drawable.exe_redhat)
    if (name.contains("manjaro")) return painterResource(Res.drawable.exe_manjaro)
    if (name.contains("flatpak")) return painterResource(Res.drawable.exe_flatpak)
    if (name.contains("snapcraft") || name.startsWith("snap-")) return painterResource(Res.drawable.exe_snap)
    if (name.contains("appimage")) return painterResource(Res.drawable.exe_appimage)
    if (name.contains("nixos")) return painterResource(Res.drawable.exe_nixos)
    if (name.contains("tailwind")) return painterResource(Res.drawable.web_tailwind)
    if (name == "project.godot") return painterResource(Res.drawable.lang_godot)
    if (name == "readme") return painterResource(Res.drawable.doc_readme)
    if (name == "changelog") return painterResource(Res.drawable.doc_changelog)
    if (name == "license") return painterResource(Res.drawable.doc_license)

    val extension = file.extension.lowercase()
    return when (extension) {
        // Images
        "webp", "ico", "ai", "tiff", "tif" -> painterResource(Res.drawable.img_image)
        "psd" -> painterResource(Res.drawable.img_psd)
        "png" -> painterResource(Res.drawable.img_png)
        "gif" -> painterResource(Res.drawable.img_gif)
        "bmp" -> painterResource(Res.drawable.img_bmp)
        "svg" -> painterResource(Res.drawable.img_svg)
        "jpg", "jpeg" -> painterResource(Res.drawable.img_jpg)

        // Docs
        "pdf" -> painterResource(Res.drawable.doc_pdf)
        "doc" -> painterResource(Res.drawable.doc_doc)
        "docx" -> painterResource(Res.drawable.doc_docx)
        "xls" -> painterResource(Res.drawable.doc_xls)
        "xlsx" -> painterResource(Res.drawable.doc_xlsx)
        "ppt" -> painterResource(Res.drawable.doc_ppt)
        "pptx" -> painterResource(Res.drawable.doc_pptx)
        "txt" -> painterResource(Res.drawable.doc_txt)
        "md" -> painterResource(Res.drawable.doc_markdown)
        "mdx" -> painterResource(Res.drawable.doc_mdx)

        // Code
        "rb" -> painterResource(Res.drawable.lang_ruby)
        "rs" -> painterResource(Res.drawable.lang_rust)
        "wasm" -> painterResource(Res.drawable.lang_wasm)
        "dll", "so", "dylib", "diff", "patch" -> painterResource(Res.drawable.lang_code)
        "go" -> painterResource(Res.drawable.lang_go)
        "o" -> painterResource(Res.drawable.exe_object)
        "m", "mm" -> painterResource(Res.drawable.lang_objectivec)
        "as", "asc" -> painterResource(Res.drawable.lang_assemblyscript)
        "swift" -> painterResource(Res.drawable.lang_swift)
        "dart" -> painterResource(Res.drawable.lang_dart)
        "lua" -> painterResource(Res.drawable.lang_lua)
        "scala" -> painterResource(Res.drawable.lang_scala)
        "r" -> painterResource(Res.drawable.lang_r)
        "groovy" -> painterResource(Res.drawable.lang_groovy)
        "pl", "pm" -> painterResource(Res.drawable.lang_perl)
        "hs", "lhs" -> painterResource(Res.drawable.lang_haskell)
        "ex", "exs" -> painterResource(Res.drawable.lang_elixir)
        "jl" -> painterResource(Res.drawable.lang_julia)
        "zig" -> painterResource(Res.drawable.lang_zig)
        "tf" -> painterResource(Res.drawable.lang_terraform)
        "prisma" -> painterResource(Res.drawable.lang_prisma)
        "clj", "cljs", "edn" -> painterResource(Res.drawable.lang_clojure)
        "coffee" -> painterResource(Res.drawable.lang_coffeescript)
        "erl", "hrl" -> painterResource(Res.drawable.lang_erlang)
        "gradle" -> {
            val head = try {
                file.useLines { it.take(30).joinToString("\n") }
            } catch (_: Exception) { "" }
            val isGroovyBuildScript = head.contains("task ") ||
                                      head.contains("plugins ") ||
                                      head.contains("dependencies ") ||
                                      head.contains("buildscript ") ||
                                      head.contains("apply ") ||
                                      head.contains("repositories ") ||
                                      head.contains("def ") ||
                                      head.contains("<<")
            painterResource(if (isGroovyBuildScript) Res.drawable.lang_groovy else Res.drawable.lang_gradle)
        }
        "kts" -> {
            if (file.name.lowercase().endsWith(".gradle.kts"))
                painterResource(Res.drawable.lang_gradle)
            else
                painterResource(Res.drawable.lang_kotlin)
        }
        "vue" -> painterResource(Res.drawable.lang_vue)
        "svelte" -> painterResource(Res.drawable.lang_svelte)
        "js" -> painterResource(Res.drawable.lang_javascript)
        "cs" -> painterResource(Res.drawable.lang_csharp)
        "php" -> painterResource(Res.drawable.lang_php)
        "ts" -> painterResource(Res.drawable.lang_typescript)
        "tsx" -> painterResource(Res.drawable.lang_react)
        "jsx" -> painterResource(Res.drawable.lang_react)
        "c" -> painterResource(Res.drawable.lang_c)
        "cpp" -> painterResource(Res.drawable.lang_cplusplus)
        "h" -> painterResource(Res.drawable.lang_h)
        "java" -> painterResource(Res.drawable.lang_java)
        "py" -> painterResource(Res.drawable.lang_python)
        "kt" -> painterResource(Res.drawable.lang_kotlin)
        "gd" -> painterResource(Res.drawable.lang_godot)
        "tscn", "tres" -> painterResource(Res.drawable.lang_godot)
        "gdnlib", "gdns" -> painterResource(Res.drawable.lang_godot)
        "gdextension" -> painterResource(Res.drawable.data_settings)
        "html", "htm" -> painterResource(Res.drawable.web_html5)
        "htmx" -> painterResource(Res.drawable.web_htmx)
        "css" -> {
            val head = try {
                file.useLines { it.take(30).joinToString("\n") }.lowercase()
            } catch (_: Exception) { "" }
            val isTailwind = head.contains("@tailwind") ||
                             head.contains("@apply")
            painterResource(if (isTailwind) Res.drawable.web_tailwind else Res.drawable.web_css)
        }
        "xml" -> painterResource(Res.drawable.web_xml)
        "yml", "yaml" -> painterResource(Res.drawable.web_yaml)
        "json" -> painterResource(Res.drawable.web_json)
        "scss", "sass" -> painterResource(Res.drawable.web_scss)
        "graphql", "gql" -> painterResource(Res.drawable.web_graphql)
        "astro" -> painterResource(Res.drawable.web_astro)
        "less" -> painterResource(Res.drawable.web_css)
        // Database
        "db3" -> painterResource(Res.drawable.sql_sqlite)
        "sqlite", "sqlite3" -> painterResource(Res.drawable.sql_sqlite)
        "psql", "pgsql" -> painterResource(Res.drawable.sql_postgresql)
        "sql", "db" -> {
            val head = try {
                file.useLines { it.take(30).joinToString("\n") }
                    .stripSqlComments()
                    .lowercase()
            } catch (_: Exception) { "" }

            val isPostgres = head.contains("::text") ||
                             head.contains("::integer") ||
                             head.contains("::varchar") ||
                             head.contains("::boolean") ||
                             head.contains("::bigint") ||
                             head.contains("::numeric") ||
                             head.contains("::json") ||
                             head.contains("::jsonb") ||
                             head.contains("pg_catalog.") ||
                             head.contains("->>'") ||
                             head.containsWord("returning") ||
                             head.contains("on conflict")

            val isSqlite = head.containsWord("pragma") ||
                           head.contains("autoincrement") ||
                           head.contains("without rowid") ||
                           head.contains("attach database") ||
                           head.contains("sqlite_version()") ||
                           head.contains("sqlite_")

            val icon = if (isPostgres) {
                Res.drawable.sql_postgresql
            } else if (isSqlite) {
                Res.drawable.sql_sqlite
            } else {
                Res.drawable.sql
            }
            painterResource(icon)
        }

        // Shell & Scripts
        "sh", "bash", "zsh", "fish" -> painterResource(Res.drawable.shell_bash)
        "bat", "cmd" -> painterResource(Res.drawable.shell_terminal)
        "ps1", "psm1", "psd1" -> painterResource(Res.drawable.shell_powershell)

        // Docker
        "dockerfile" -> painterResource(Res.drawable.docker)

        // Data & Config
        "csv" -> painterResource(Res.drawable.data_csv)
        "tsv" -> painterResource(Res.drawable.data_spreadsheet)
        "log" -> painterResource(Res.drawable.document)
        "lock" -> painterResource(Res.drawable.data_lock)
        "env" -> painterResource(Res.drawable.data_env)
        "ini", "cfg", "conf" -> painterResource(Res.drawable.data_cog)
        "toml" -> painterResource(Res.drawable.data_toml)

        // Archive
        "rar", "7z", "tar", "gz" -> painterResource(Res.drawable.arc_archive)
        "zip" -> painterResource(Res.drawable.arc_archive)
        "jar", "war", "ear" -> painterResource(Res.drawable.arc_archive)
        "iso" -> painterResource(Res.drawable.media_cd)
        "img" -> painterResource(Res.drawable.arc_archive)
        "ipa" -> painterResource(Res.drawable.arc_archive)

        // Audio
        "mp3", "wav", "flac", "aac", "ogg" -> painterResource(Res.drawable.media_audio)

        // Video
        "mp4", "avi", "mkv", "mov", "wmv", "flv" -> painterResource(Res.drawable.media_video)

        // Fonts
        "ttf", "otf", "woff", "woff2" -> painterResource(Res.drawable.font)

        // Executable
        "app" -> painterResource(Res.drawable.document_unknown)
        "exe" -> painterResource(Res.drawable.exe_exe)
        "msi" -> painterResource(Res.drawable.exe_msi)
        "rpm" -> painterResource(Res.drawable.exe_rpm)
        "dmg" -> painterResource(Res.drawable.exe_dmg)
        "deb" -> painterResource(Res.drawable.exe_debian)
        "apk" -> {
            val isAlpine = try {
                file.inputStream().use { it.read() == 0x1f && it.read() == 0x8b }
            } catch (_: Exception) { false }
            painterResource(if (isAlpine) Res.drawable.exe_alpine else Res.drawable.exe_android)
        }
        "flatpak", "flatpakref" -> painterResource(Res.drawable.exe_flatpak)
        "snap" -> painterResource(Res.drawable.exe_snap)
        "AppImage", "appimage" -> painterResource(Res.drawable.exe_appimage)
        "nix" -> painterResource(Res.drawable.exe_nixos)

        "" -> painterResource(Res.drawable.document_unknown)
        else -> painterResource(Res.drawable.document)
    }
}


@Composable
internal fun FileInfoDialog(
    file: File,
    onClose: () -> Unit
) {
    val fileSize = remember(file) {
        if (file.isFile) {
            formatFileSize(file.length())
        } else {
            "Folder"
        }
    }

    val lastModified = remember(file) {
        val date = Date(file.lastModified())
        SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(date)
    }

    val fileExtension = remember(file) {
        if (file.isFile && file.extension.isNotEmpty()) {
            ".${file.extension}"
        } else {
            "N/A"
        }
    }

    val folderSize = remember(file) {
        if (file.isDirectory) {
            calculateFolderSize(file)
        } else {
            0L
        }
    }

    val totalFiles = remember(file) {
        if (file.isDirectory) {
            file.listFiles()?.size ?: 0
        } else {
            0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClose)
        ) {}

        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 420.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = getFileIcon(file),
                        contentDescription = null,
                        tint = if (file.isDirectory) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                val scrollState = rememberScrollState()
                Box(modifier = Modifier.heightIn(max = 300.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoRow("Type", if (file.isDirectory) "Folder" else "File")
                        if (file.isFile) {
                            InfoRow("Extension", fileExtension)
                            InfoRow("Size", fileSize)
                        }
                        InfoRow("Location", file.parent ?: "Unknown")
                        AnimatedVisibility(visible = file.isDirectory) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                InfoRow("Total files", totalFiles.toString())
                                InfoRow("Folder size", formatFileSize(folderSize))
                            }
                        }
                        InfoRow("Modified", lastModified)
                    }
                    VerticalScrollbar(
                        modifier = Modifier
                            .fillMaxHeight(0.7f)
                            .align(Alignment.BottomEnd)
                            .pointerHoverIcon(PointerIcon.Hand),
                        adapter = rememberScrollbarAdapter(scrollState),
                        style = LocalScrollbarStyle.current.copy(
                            hoverColor = MaterialTheme.colorScheme.outline,
                            unhoverColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InfoRow(
    label: String,
    value: String
) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val isLocationLabel = label.contentEquals("Location")
    var isCopied by remember { mutableStateOf(false) }

    val locationModifier = if(isLocationLabel){
        Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable{
                clipboard.setContents(StringSelection(value), null)
                isCopied = true
            }
            .padding(5.dp)
    }else{
        Modifier
    }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    val displayMessage = if (isCopied) "Path Copied!!!" else value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
        if(isLocationLabel && value.length > 20){
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                state = rememberTooltipState(),
                tooltip = {
                    PlainTooltip {
                        Text(
                            text = if (isCopied) "Copied to clipboard!" else "Click to copy path"
                        )
                    }
                }
            ){
                Text(
                    text = displayMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCopied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = locationModifier.pointerHoverIcon(PointerIcon.Hand)
                )
            }
        }else{
            Text(
                text = displayMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCopied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = locationModifier
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LayoutViewToggle(
    isListView: Boolean,
    onListGridViewChange: () -> Unit
){
    TooltipBox(
        tooltip = {
            RichTooltip(
                modifier = Modifier.size(DpSize(10.dp, 7.dp)),
                shape = MaterialTheme.shapes.medium,
                title = {
                    Text(
                        text = if(isListView) "List view" else "Grid view",
                        fontWeight = FontWeight.Bold
                    )
                }
            ) {
                Text(
                    text = if(isListView) "Click to switch to grid view" else "Click to switch to list view",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        state = rememberTooltipState(isPersistent = true)
    ){
        IconButton(
            onClick = {onListGridViewChange()},
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
        ){
            Icon(
                imageVector = if (isListView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                contentDescription = null
            )
        }
    }
}