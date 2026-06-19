package deskit.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.launch
import deskit.demo.components.ThemeBar
import deskit.demo.components.ThemeEvent
import deskit.demo.model.PaletteStyle
import deskit.demo.model.PresetSeed
import deskit.demo.model.ThemeState
import deskit.demo.sections.ConfirmationSection
import deskit.demo.sections.FileChooserSection
import deskit.demo.sections.FileSaverSection
import deskit.demo.sections.FolderChooserSection
import deskit.demo.sections.InfoDialogSection
import deskit.demo.sections.IconTestSection
import deskit.demo.sections.SystemRootAccessSection
import deskit.demo.theme.DemoTheme
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(960.dp, 740.dp),
        position = WindowPosition.Aligned(Alignment.TopCenter)
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "deskit Showcase"
    ) {
        window.minimumSize = Dimension(640, 500)

        var themeState by remember {
            mutableStateOf(
                ThemeState(
                    isDark = true,
                    seedColor = PresetSeed.TERRACOTTA.color,
                    paletteStyle = PaletteStyle.TONAL_SPOT
                )
            )
        }

        DemoTheme(themeState = themeState) {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val onMessage: (String) -> Unit = { scope.launch { snackbarHostState.showSnackbar(it) } }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    ThemeBar(
                        themeState = themeState,
                        onThemeEvent = { event ->
                            themeState = when (event) {
                                is ThemeEvent.SetDark -> themeState.copy(isDark = event.isDark)
                                is ThemeEvent.SetSeed -> themeState.copy(seedColor = event.color)
                                is ThemeEvent.SetStyle -> themeState.copy(paletteStyle = event.style)
                            }
                        }
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        FileChooserSection(onMessage = onMessage)
                        FolderChooserSection(onMessage = onMessage)
                        FileSaverSection(onMessage = onMessage)
                        InfoDialogSection(onMessage = onMessage)
                        ConfirmationSection(onMessage = onMessage)
                        IconTestSection(onMessage = onMessage)
                        SystemRootAccessSection(onMessage = onMessage)
                        Spacer(Modifier.height(24.dp))
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
