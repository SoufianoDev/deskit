package deskit.utils.path

import java.io.File

data class BreadcrumbSegment(
    val label: String,
    val file: File,
    val type: SegmentType,
    val isCurrentDirectory: Boolean = false
)
