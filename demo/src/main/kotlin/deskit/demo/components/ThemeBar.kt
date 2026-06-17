package deskit.demo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import deskit.demo.model.PaletteStyle
import deskit.demo.model.PresetSeed
import deskit.demo.model.ThemeState

sealed interface ThemeEvent {
    data class SetDark(val isDark: Boolean) : ThemeEvent
    data class SetSeed(val color: Color) : ThemeEvent
    data class SetStyle(val style: PaletteStyle) : ThemeEvent
}

@Composable
fun ThemeBar(
    themeState: ThemeState,
    onThemeEvent: (ThemeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "deskit Showcase",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(24.dp))
            SeedColorDropdown(
                selected = themeState.seedColor,
                onSelect = { onThemeEvent(ThemeEvent.SetSeed(it)) }
            )
            Spacer(Modifier.width(16.dp))
            PaletteStyleDropdown(
                selected = themeState.paletteStyle,
                onSelect = { onThemeEvent(ThemeEvent.SetStyle(it)) }
            )
            Spacer(Modifier.width(16.dp))
            IconButton(onClick = { onThemeEvent(ThemeEvent.SetDark(!themeState.isDark)) }) {
                Icon(
                    imageVector = if (themeState.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (themeState.isDark) "Switch to light mode" else "Switch to dark mode"
                )
            }
        }
    }
}

@Composable
private fun SeedColorDropdown(
    selected: Color,
    onSelect: (Color) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPreset = PresetSeed.entries.first { it.color == selected }

    CustomDropdown(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        anchor = {
            TextButton(onClick = { expanded = true }) {
                Box(
                    Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(selected, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(selectedPreset.label, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    ) {
        PresetSeed.entries.forEach { preset ->
            val isSelected = selected == preset.color
            DropdownMenuItem(
                onClick = {
                    onSelect(preset.color)
                    expanded = false
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(preset.color, CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(preset.label, style = MaterialTheme.typography.bodyMedium)
                        if (isSelected) {
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun PaletteStyleDropdown(
    selected: PaletteStyle,
    onSelect: (PaletteStyle) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    CustomDropdown(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        anchor = {
            TextButton(onClick = { expanded = true }) {
                Text(selected.label, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    ) {
        PaletteStyle.entries.forEach { style ->
            val isSelected = selected == style
            DropdownMenuItem(
                onClick = {
                    onSelect(style)
                    expanded = false
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(style.label, style = MaterialTheme.typography.bodyMedium)
                        if (isSelected) {
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    }
}
