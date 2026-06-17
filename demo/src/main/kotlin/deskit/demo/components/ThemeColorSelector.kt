package deskit.demo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ThemeColorRef(val label: String) {
    PRIMARY("primary"),
    ON_PRIMARY("onPrimary"),
    PRIMARY_CONTAINER("primaryContainer"),
    ON_PRIMARY_CONTAINER("onPrimaryContainer"),
    SECONDARY("secondary"),
    ON_SECONDARY("onSecondary"),
    SECONDARY_CONTAINER("secondaryContainer"),
    ON_SECONDARY_CONTAINER("onSecondaryContainer"),
    TERTIARY("tertiary"),
    ON_TERTIARY("onTertiary"),
    TERTIARY_CONTAINER("tertiaryContainer"),
    ON_TERTIARY_CONTAINER("onTertiaryContainer"),
    ERROR("error"),
    ON_ERROR("onError"),
    ERROR_CONTAINER("errorContainer"),
    ON_ERROR_CONTAINER("onErrorContainer"),
    SURFACE("surface"),
    ON_SURFACE("onSurface"),
    SURFACE_VARIANT("surfaceVariant"),
    ON_SURFACE_VARIANT("onSurfaceVariant"),
    BACKGROUND("background"),
    ON_BACKGROUND("onBackground"),
    OUTLINE("outline"),
    OUTLINE_VARIANT("outlineVariant"),
    INVERSE_PRIMARY("inversePrimary"),
    INVERSE_SURFACE("inverseSurface"),
    INVERSE_ON_SURFACE("inverseOnSurface")
}

@Composable
fun ThemeColorRef.resolve(): Color = when (this) {
    ThemeColorRef.PRIMARY -> MaterialTheme.colorScheme.primary
    ThemeColorRef.ON_PRIMARY -> MaterialTheme.colorScheme.onPrimary
    ThemeColorRef.PRIMARY_CONTAINER -> MaterialTheme.colorScheme.primaryContainer
    ThemeColorRef.ON_PRIMARY_CONTAINER -> MaterialTheme.colorScheme.onPrimaryContainer
    ThemeColorRef.SECONDARY -> MaterialTheme.colorScheme.secondary
    ThemeColorRef.ON_SECONDARY -> MaterialTheme.colorScheme.onSecondary
    ThemeColorRef.SECONDARY_CONTAINER -> MaterialTheme.colorScheme.secondaryContainer
    ThemeColorRef.ON_SECONDARY_CONTAINER -> MaterialTheme.colorScheme.onSecondaryContainer
    ThemeColorRef.TERTIARY -> MaterialTheme.colorScheme.tertiary
    ThemeColorRef.ON_TERTIARY -> MaterialTheme.colorScheme.onTertiary
    ThemeColorRef.TERTIARY_CONTAINER -> MaterialTheme.colorScheme.tertiaryContainer
    ThemeColorRef.ON_TERTIARY_CONTAINER -> MaterialTheme.colorScheme.onTertiaryContainer
    ThemeColorRef.ERROR -> MaterialTheme.colorScheme.error
    ThemeColorRef.ON_ERROR -> MaterialTheme.colorScheme.onError
    ThemeColorRef.ERROR_CONTAINER -> MaterialTheme.colorScheme.errorContainer
    ThemeColorRef.ON_ERROR_CONTAINER -> MaterialTheme.colorScheme.onErrorContainer
    ThemeColorRef.SURFACE -> MaterialTheme.colorScheme.surface
    ThemeColorRef.ON_SURFACE -> MaterialTheme.colorScheme.onSurface
    ThemeColorRef.SURFACE_VARIANT -> MaterialTheme.colorScheme.surfaceVariant
    ThemeColorRef.ON_SURFACE_VARIANT -> MaterialTheme.colorScheme.onSurfaceVariant
    ThemeColorRef.BACKGROUND -> MaterialTheme.colorScheme.background
    ThemeColorRef.ON_BACKGROUND -> MaterialTheme.colorScheme.onBackground
    ThemeColorRef.OUTLINE -> MaterialTheme.colorScheme.outline
    ThemeColorRef.OUTLINE_VARIANT -> MaterialTheme.colorScheme.outlineVariant
    ThemeColorRef.INVERSE_PRIMARY -> MaterialTheme.colorScheme.inversePrimary
    ThemeColorRef.INVERSE_SURFACE -> MaterialTheme.colorScheme.inverseSurface
    ThemeColorRef.INVERSE_ON_SURFACE -> MaterialTheme.colorScheme.inverseOnSurface
}

@Composable
fun ThemeColorSelector(
    label: String,
    selectedRef: ThemeColorRef,
    onColorChange: (ThemeColorRef) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val resolvedColor = selectedRef.resolve()

    ConfigRow(label = label, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(resolvedColor, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text(
                        selectedRef.label,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ThemeColorRef.entries.forEach { ref ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(ref.resolve(), CircleShape)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(ref.label, style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                onColorChange(ref)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
