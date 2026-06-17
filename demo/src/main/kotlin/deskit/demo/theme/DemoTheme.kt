package deskit.demo.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import deskit.demo.model.ThemeState

@Composable
fun DemoTheme(
    themeState: ThemeState,
    content: @Composable () -> Unit
) {
    val colorScheme = rememberDemoColorScheme(themeState)
    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            content()
        }
    }
}
