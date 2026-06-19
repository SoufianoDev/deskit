package deskit.utils.path

enum class OsType {
    LINUX,
    MACOS,
    WINDOWS;

    companion object {
        fun detect(): OsType {
            val name = System.getProperty("os.name").lowercase()
            return when {
                name.contains("mac") || name.contains("darwin") -> MACOS
                name.contains("windows") -> WINDOWS
                else -> LINUX
            }
        }
    }
}
