package deskit.demo.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import deskit.demo.model.ThemeState

@Composable
fun rememberDemoColorScheme(themeState: ThemeState): ColorScheme {
    return rememberDynamicColorScheme(
        seedColor = themeState.seedColor,
        isDark = themeState.isDark,
        style = when (themeState.paletteStyle) {
            deskit.demo.model.PaletteStyle.TONAL_SPOT -> PaletteStyle.TonalSpot
            deskit.demo.model.PaletteStyle.EXPRESSIVE -> PaletteStyle.Expressive
            deskit.demo.model.PaletteStyle.FIDELITY -> PaletteStyle.Fidelity
            deskit.demo.model.PaletteStyle.CONTENT -> PaletteStyle.Content
            deskit.demo.model.PaletteStyle.MONOCHROME -> PaletteStyle.Monochrome
            deskit.demo.model.PaletteStyle.VIBRANT -> PaletteStyle.Vibrant
            deskit.demo.model.PaletteStyle.NEUTRAL -> PaletteStyle.Neutral
            deskit.demo.model.PaletteStyle.RAINBOW -> PaletteStyle.Rainbow
            deskit.demo.model.PaletteStyle.FRUIT_SALAD -> PaletteStyle.FruitSalad
        }
    )
}
