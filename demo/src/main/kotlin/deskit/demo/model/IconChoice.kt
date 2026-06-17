package deskit.demo.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter

enum class IconChoice(val label: String) {
    NONE("None"),
    INFO("Info"),
    WARNING("Warning"),
    ERROR("Error"),
    QUESTION("Question"),
    CUSTOM("Custom")
}

@Composable
fun IconChoice.toPainter(): Painter? = when (this) {
    IconChoice.NONE -> null
    IconChoice.INFO -> rememberVectorPainter(Icons.Default.Info)
    IconChoice.WARNING -> rememberVectorPainter(Icons.Default.Warning)
    IconChoice.ERROR -> rememberVectorPainter(Icons.Default.Error)
    IconChoice.QUESTION -> rememberVectorPainter(Icons.Default.QuestionMark)
    IconChoice.CUSTOM -> rememberVectorPainter(Icons.Default.Bookmark)
}
