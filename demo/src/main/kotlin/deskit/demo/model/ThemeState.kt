package deskit.demo.model

import androidx.compose.ui.graphics.Color

enum class PaletteStyle(val label: String) {
    TONAL_SPOT("TonalSpot"),
    EXPRESSIVE("Expressive"),
    FIDELITY("Fidelity"),
    CONTENT("Content"),
    MONOCHROME("Monochrome"),
    VIBRANT("Vibrant"),
    NEUTRAL("Neutral"),
    RAINBOW("Rainbow"),
    FRUIT_SALAD("Fruit Salad")
}

enum class PresetSeed(val label: String, val color: Color) {
    TERRACOTTA("Terracotta", Color(0xFF8E4C40)),
    INDIGO("Indigo", Color(0xFF3F51B5)),
    TEAL("Teal", Color(0xFF00897B)),
    ROSE("Rose", Color(0xFFE91E63)),
    PINE("Pine", Color(0xFF2E7D32)),
    AMETHYST("Amethyst", Color(0xFF9B59B6)),
    CORAL("Coral", Color(0xFFE67E5A)),
    GOLD("Gold", Color(0xFFD4A017)),
    CRIMSON("Crimson", Color(0xFFB22222)),
    SAGE("Sage", Color(0xFF7A9B6A)),
    SKY_BLUE("Sky Blue", Color(0xFF4A90D9)),
    PLUM("Plum", Color(0xFF5E2B5E)),
    OCHRE("Ochre", Color(0xFFC77D2A)),
    MINT("Mint", Color(0xFF5ABF8A)),
    SLATE("Slate", Color(0xFF5A6A7A)),
    LAVENDER("Lavender", Color(0xFF9D8FC1)),
    COBALT("Cobalt", Color(0xFF3D5A99)),
    CARAMEL("Caramel", Color(0xFFB8864A)),
    FOREST("Forest", Color(0xFF2D6A3A)),
    DUSTY_ROSE("Dusty Rose", Color(0xFFB87A7A)),
    BURGUNDY("Burgundy", Color(0xFF7A2A3A)),
    LIME("Lime", Color(0xFF7AB84A)),
    STEEL("Steel", Color(0xFF5A7A8A)),
    PEACH("Peach", Color(0xFFE8A87A)),
    EGGPLANT("Eggplant", Color(0xFF5A3A6A))
}

data class ThemeState(
    val isDark: Boolean,
    val seedColor: Color,
    val paletteStyle: PaletteStyle
)
