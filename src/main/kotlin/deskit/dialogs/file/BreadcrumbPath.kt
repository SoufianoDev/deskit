package deskit.dialogs.file

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import deskit.resources.*
import deskit.utils.path.BreadcrumbSegment
import deskit.utils.path.SegmentType
import org.jetbrains.compose.resources.painterResource

private const val MAX_VISIBLE_SEGMENTS = 5

private sealed interface DisplayItem {
    data class Segment(val segment: BreadcrumbSegment) : DisplayItem
    data class Collapsed(val collapsedSegments: List<BreadcrumbSegment>) : DisplayItem
}

private fun buildDisplayList(segments: List<BreadcrumbSegment>): List<DisplayItem> {
    if (segments.size <= MAX_VISIBLE_SEGMENTS) {
        return segments.map { DisplayItem.Segment(it) }
    }
    val first = segments.take(1)
    val last = segments.takeLast(3)
    val middle = segments.drop(1).dropLast(3)
    return buildList {
        addAll(first.map { DisplayItem.Segment(it) })
        add(DisplayItem.Collapsed(middle))
        addAll(last.map { DisplayItem.Segment(it) })
    }
}

@Composable
fun BreadcrumbPath(
    segments: List<BreadcrumbSegment>,
    onSegmentSelected: (BreadcrumbSegment) -> Unit,
    modifier: Modifier = Modifier,
    showHomeIcon: Boolean = false,
    separator: String = "\u203A",
    skipFirstSeparator: Boolean = false
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(segments.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val displayItems = remember(segments) { buildDisplayList(segments) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(bottom = 8.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            displayItems.forEachIndexed { index, item ->
                when (item) {
                    is DisplayItem.Segment -> {
                        SegmentView(
                            segment = item.segment,
                            isLast = index == displayItems.lastIndex,
                            showHomeIcon = showHomeIcon,
                            onClick = { onSegmentSelected(item.segment) }
                        )
                        if (index != displayItems.lastIndex && !(skipFirstSeparator && index == 0)) {
                            SeparatorView(separator)
                        }
                    }
                    is DisplayItem.Collapsed -> {
                        CollapsedView(
                            collapsedSegments = item.collapsedSegments,
                            onSelect = onSegmentSelected
                        )
                        if (index != displayItems.lastIndex) {
                            SeparatorView(separator)
                        }
                    }
                }
            }
        }
        HorizontalScrollbar(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(end = 12.dp)
                .pointerHoverIcon(PointerIcon.Hand),
            adapter = rememberScrollbarAdapter(scrollState),
            style = LocalScrollbarStyle.current.copy(
                hoverColor = MaterialTheme.colorScheme.outline,
                unhoverColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun SegmentView(
    segment: BreadcrumbSegment,
    isLast: Boolean,
    showHomeIcon: Boolean,
    onClick: () -> Unit
) {
    val colors = if (isLast) MaterialTheme.colorScheme.primary else LocalContentColor.current
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(8.dp)
            .pointerHoverIcon(PointerIcon.Hand)
    ) {
        if (showHomeIcon && segment.type == SegmentType.HOME) {
            Icon(
                painter = painterResource(Res.drawable.ic_home_filled),
                contentDescription = "Home",
                tint = colors,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = segment.label,
                color = colors,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}

@Composable
private fun SeparatorView(separator: String) {
    Text(
        text = separator,
        color = LocalContentColor.current.copy(alpha = 0.4f),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
private fun CollapsedView(
    collapsedSegments: List<BreadcrumbSegment>,
    onSelect: (BreadcrumbSegment) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            text = "\u2026",
            color = LocalContentColor.current.copy(alpha = 0.6f),
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable { expanded = true }
                .padding(8.dp)
                .pointerHoverIcon(PointerIcon.Hand),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            collapsedSegments.forEach { segment ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = segment.label,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelect(segment)
                    }
                )
            }
        }
    }
}
