package deskit.utils.path

import java.io.File

private fun File.safeCanonical(): File = try {
    canonicalFile
} catch (_: Exception) {
    absoluteFile
}

class PathResolver @JvmOverloads constructor(
    private val mode: PathDisplayMode = PathDisplayMode.LOGICAL_HOME,
    private val allowSystemRootAccess: Boolean = false,
    private val os: OsType = OsType.detect(),
    private val userHome: File = System.getProperty("user.home").let(::File).safeCanonical()
) {
    private val userHomeContainer: File = userHome.parentFile?.safeCanonical() ?: userHome
    private val isContainerRoot: Boolean = userHomeContainer.absolutePath == "/"

    fun resolve(path: File): List<BreadcrumbSegment> {
        return when (mode) {
            PathDisplayMode.RAW_SYSTEM -> resolveRaw(path)
            PathDisplayMode.LOGICAL_HOME -> resolveLogical(path)
        }
    }

    fun isAccessible(path: File): Boolean {
        if (mode == PathDisplayMode.RAW_SYSTEM) return true
        val canonical = path.safeCanonical()
        if (isWithinHome(canonical)) return true
        if (isUnderContainer(canonical)) return true
        if (lookupMount(canonical) != null) return true
        if (os == OsType.WINDOWS) {
            if (isNonSystemWindowsDrive(canonical)) return true
            if (isWindowsNetworkPath(canonical)) return true
        }
        return allowSystemRootAccess
    }

    fun isWithinHome(path: File): Boolean {
        val canonical = path.safeCanonical()
        val homePath = userHome.absolutePath
        return canonical.absolutePath == homePath ||
                canonical.absolutePath.startsWith(homePath + separator)
    }

    fun isUnderContainer(path: File): Boolean {
        if (isContainerRoot) return false
        val canonical = path.safeCanonical()
        val containerPath = userHomeContainer.absolutePath
        return canonical.absolutePath == containerPath ||
                canonical.absolutePath.startsWith(containerPath + separator)
    }

    private val separator: String get() = File.separator

    private fun ancestorChain(file: File): List<File> {
        val path = file.safeCanonical().absolutePath
        val result = mutableListOf<File>()
        var current = path
        while (true) {
            result.add(File(current))
            val parent = parentPath(current) ?: break
            if (parent == current) break
            current = parent
        }
        return result.reversed()
    }

    private fun parentPath(path: String): String? {
        if (path == "/") return null
        if (path.length >= 2 && path[1] == ':') {
            if (path.length == 2 || (path.length == 3 && (path[2] == '/' || path[2] == '\\')))
                return null
        }
        if (path.startsWith("\\\\")) {
            val afterServer = path.indexOf('\\', 2)
            if (afterServer < 0) return null
        }
        val lastSep = path.lastIndexOfAny(charArrayOf('/', '\\'))
        if (lastSep < 0) return null
        if (lastSep == 0) return "/"
        val parent = path.substring(0, lastSep)
        return parent.ifEmpty { null }
    }

    private fun resolveRaw(path: File): List<BreadcrumbSegment> {
        val canonical = path.safeCanonical()
        val ancestors = ancestorChain(canonical)
        return ancestors.mapIndexed { index, file ->
            BreadcrumbSegment(
                label = file.name.ifBlank { "/" },
                file = file,
                type = SegmentType.NORMAL,
                isCurrentDirectory = index == ancestors.lastIndex
            )
        }
    }

    private fun resolveLogical(path: File): List<BreadcrumbSegment> {
        val canonical = path.safeCanonical()
        val ancestors = ancestorChain(canonical)

        val homeIdx = ancestors.indexOfFirst { it.absolutePath == userHome.absolutePath }
        if (homeIdx >= 0) {
            return buildHomeSegments(ancestors.drop(homeIdx))
        }

        if (!isContainerRoot) {
            val containerIdx = ancestors.indexOfFirst { it.absolutePath == userHomeContainer.absolutePath }
            if (containerIdx >= 0) {
                return buildContainerSegments(ancestors.drop(containerIdx))
            }
        }

        val mountInfo = lookupMount(canonical)
        if (mountInfo != null) {
            return buildMountSegments(ancestors, mountInfo)
        }

        if (os == OsType.WINDOWS) {
            val winSegments = resolveWindowsPath(canonical, ancestors)
            if (winSegments != null) return winSegments
        }

        return buildSystemSegments(ancestors)
    }

    private fun buildHomeSegments(segments: List<File>): List<BreadcrumbSegment> {
        return segments.mapIndexed { i, file ->
            BreadcrumbSegment(
                label = if (i == 0) "Home" else file.name,
                file = file,
                type = if (i == 0) SegmentType.HOME else SegmentType.NORMAL,
                isCurrentDirectory = i == segments.lastIndex
            )
        }
    }

    private fun buildContainerSegments(segments: List<File>): List<BreadcrumbSegment> {
        return segments.mapIndexed { i, file ->
            BreadcrumbSegment(
                label = if (i == 0) "Users" else file.name,
                file = file,
                type = if (i == 0) SegmentType.USER_CONTAINER else SegmentType.NORMAL,
                isCurrentDirectory = i == segments.lastIndex
            )
        }
    }

    private data class MountInfo(val root: File, val deviceName: String)

    private fun lookupMount(canonical: File): MountInfo? {
        val path = canonical.absolutePath
        return when (os) {
            OsType.LINUX -> lookupLinuxMount(path)
            OsType.MACOS -> lookupMacosMount(path)
            OsType.WINDOWS -> null
        }
    }

    private fun lookupLinuxMount(path: String): MountInfo? {
        val modernMedia = Regex("^/media/([^/]+)/([^/]+)(/.*)?$")
            .matchEntire(path)
        if (modernMedia != null) {
            val username = modernMedia.groupValues[1]
            val device = modernMedia.groupValues[2]
            return MountInfo(File("/media/$username/$device"), device)
        }
        val legacyMedia = Regex("^/media/([^/]+)(/.*)?$")
            .matchEntire(path)
        if (legacyMedia != null) {
            val device = legacyMedia.groupValues[1]
            return MountInfo(File("/media", device), device)
        }
        val mnt = Regex("^/mnt/([^/]+)(/.*)?$")
            .matchEntire(path)
        if (mnt != null) {
            val device = mnt.groupValues[1]
            return MountInfo(File("/mnt", device), device)
        }
        return null
    }

    private fun lookupMacosMount(path: String): MountInfo? {
        val vol = Regex("^/Volumes/([^/]+)(/.*)?$")
            .matchEntire(path)
        if (vol != null) {
            val device = vol.groupValues[1]
            return MountInfo(File("/Volumes", device), device)
        }
        return null
    }

    private fun buildMountSegments(
        ancestors: List<File>,
        mountInfo: MountInfo
    ): List<BreadcrumbSegment> {
        val rootPath = mountInfo.root.absolutePath
        val mountIdx = ancestors.indexOfFirst { it.absolutePath == rootPath }
        if (mountIdx < 0) {
            return listOf(
                BreadcrumbSegment(mountInfo.deviceName, mountInfo.root, SegmentType.MOUNTED_VOLUME, true)
            )
        }
        val relevant = ancestors.drop(mountIdx)
        return relevant.mapIndexed { i, file ->
            BreadcrumbSegment(
                label = if (i == 0) mountInfo.deviceName else file.name,
                file = file,
                type = if (i == 0) SegmentType.MOUNTED_VOLUME else SegmentType.NORMAL,
                isCurrentDirectory = i == relevant.lastIndex
            )
        }
    }

    private fun resolveWindowsPath(canonical: File, ancestors: List<File>): List<BreadcrumbSegment>? {
        val path = canonical.absolutePath

        val driveMatch = Regex("^([D-Z]):\\\\(.*)$", RegexOption.IGNORE_CASE)
            .matchEntire(path)
        if (driveMatch != null) {
            val driveLetter = driveMatch.groupValues[1].uppercase()
            val drivePath = "${driveLetter}:\\"
            val chain = ancestorChain(canonical)
            val idx = chain.indexOfFirst { it.absolutePath.equals(drivePath, ignoreCase = true) }
            if (idx >= 0) {
                val relevant = chain.drop(idx)
                return relevant.mapIndexed { i, file ->
                    BreadcrumbSegment(
                        label = if (i == 0) driveLetter else file.name,
                        file = file,
                        type = if (i == 0) SegmentType.MOUNTED_VOLUME else SegmentType.NORMAL,
                        isCurrentDirectory = i == relevant.lastIndex
                    )
                }
            }
        }

        if (path.startsWith("\\\\")) {
            val chain = ancestorChain(canonical)
            return chain.mapIndexed { i, file ->
                BreadcrumbSegment(
                    label = if (i == 0) "Network" else file.name,
                    file = file,
                    type = if (i == 0) SegmentType.NETWORK_SHARE else SegmentType.NORMAL,
                    isCurrentDirectory = i == chain.lastIndex
                )
            }
        }

        return null
    }

    private fun isNonSystemWindowsDrive(file: File): Boolean {
        val path = file.safeCanonical().absolutePath
        return Regex("^[D-Z]:", RegexOption.IGNORE_CASE).containsMatchIn(path)
    }

    private fun isWindowsNetworkPath(file: File): Boolean {
        return file.safeCanonical().absolutePath.startsWith("\\\\")
    }

    private fun buildSystemSegments(ancestors: List<File>): List<BreadcrumbSegment> {
        if (ancestors.isEmpty()) return emptyList()
        if (ancestors.size == 1 && ancestors.first().absolutePath == "/") {
            return listOf(
                BreadcrumbSegment("System", File("/"), SegmentType.SYSTEM_ROOT, true)
            )
        }
        val nonRoot = if (ancestors.first().absolutePath == "/")
            ancestors.drop(1) else ancestors
        return buildList {
            add(BreadcrumbSegment("System", nonRoot.first(), SegmentType.SYSTEM_ROOT, false))
            nonRoot.forEachIndexed { i, file ->
                add(BreadcrumbSegment(
                    label = file.name,
                    file = file,
                    type = SegmentType.NORMAL,
                    isCurrentDirectory = i == nonRoot.lastIndex
                ))
            }
        }
    }
}
