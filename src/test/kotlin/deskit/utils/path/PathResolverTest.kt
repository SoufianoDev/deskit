package deskit.utils.path

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.io.File

class PathResolverLinuxTest {
    private val resolver = PathResolver(
        mode = PathDisplayMode.LOGICAL_HOME,
        os = OsType.LINUX,
        userHome = File("/home/alice")
    )

    @Test
    fun `home directory resolves to single Home segment`() {
        val segments = resolver.resolve(File("/home/alice"))
        assertEquals(1, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals(SegmentType.HOME, segments[0].type)
        assertTrue(segments[0].isCurrentDirectory)
    }

    @Test
    fun `home subdirectory shows Home prefix`() {
        val segments = resolver.resolve(File("/home/alice/Documents"))
        assertEquals(2, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals(SegmentType.HOME, segments[0].type)
        assertEquals("Documents", segments[1].label)
        assertEquals(SegmentType.NORMAL, segments[1].type)
        assertTrue(segments[1].isCurrentDirectory)
    }

    @Test
    fun `deep nesting under home`() {
        val segments = resolver.resolve(File("/home/alice/Documents/Work/2024"))
        assertEquals(4, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals("Documents", segments[1].label)
        assertEquals("Work", segments[2].label)
        assertEquals("2024", segments[3].label)
        assertEquals(SegmentType.HOME, segments[0].type)
        segments.drop(1).forEach { assertEquals(SegmentType.NORMAL, it.type) }
        assertTrue(segments.last().isCurrentDirectory)
    }

    @Test
    fun `user container shows Users`() {
        val segments = resolver.resolve(File("/home"))
        assertEquals(1, segments.size)
        assertEquals("Users", segments[0].label)
        assertEquals(SegmentType.USER_CONTAINER, segments[0].type)
    }

    @Test
    fun `other user directory shows Users prefix`() {
        val segments = resolver.resolve(File("/home/bob"))
        assertEquals(2, segments.size)
        assertEquals("Users", segments[0].label)
        assertEquals(SegmentType.USER_CONTAINER, segments[0].type)
        assertEquals("bob", segments[1].label)
        assertEquals(SegmentType.NORMAL, segments[1].type)
    }

    @Test
    fun `other user subdirectory`() {
        val segments = resolver.resolve(File("/home/bob/Documents"))
        assertEquals(3, segments.size)
        assertEquals("Users", segments[0].label)
        assertEquals("bob", segments[1].label)
        assertEquals("Documents", segments[2].label)
    }

    @Test
    fun `username never visible inside own home`() {
        val segments = resolver.resolve(File("/home/alice/Downloads/Projects"))
        assertEquals(3, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals("Downloads", segments[1].label)
        assertEquals("Projects", segments[2].label)
    }

    @Test
    fun `mounted volume at modern media path`() {
        val segments = resolver.resolve(File("/media/alice/USB"))
        assertEquals(1, segments.size)
        assertEquals("USB", segments[0].label)
        assertEquals(SegmentType.MOUNTED_VOLUME, segments[0].type)
    }

    @Test
    fun `mounted volume subdirectory`() {
        val segments = resolver.resolve(File("/media/alice/USB/Photos"))
        assertEquals(2, segments.size)
        assertEquals("USB", segments[0].label)
        assertEquals( SegmentType.MOUNTED_VOLUME, segments[0].type)
        assertEquals("Photos", segments[1].label)
        assertEquals(SegmentType.NORMAL, segments[1].type)
    }

    @Test
    fun `legacy media mount`() {
        val segments = resolver.resolve(File("/media/USB"))
        assertEquals(1, segments.size)
        assertEquals("USB", segments[0].label)
        assertEquals(SegmentType.MOUNTED_VOLUME, segments[0].type)
    }

    @Test
    fun `mnt mount point`() {
        val segments = resolver.resolve(File("/mnt/data"))
        assertEquals(1, segments.size)
        assertEquals("data", segments[0].label)
        assertEquals(SegmentType.MOUNTED_VOLUME, segments[0].type)
    }

    @Test
    fun `mnt mount subdirectory`() {
        val segments = resolver.resolve(File("/mnt/data/backup"))
        assertEquals(2, segments.size)
        assertEquals("data", segments[0].label)
        assertEquals(SegmentType.MOUNTED_VOLUME, segments[0].type)
        assertEquals("backup", segments[1].label)
        assertEquals(SegmentType.NORMAL, segments[1].type)
    }

    @Test
    fun `system directory blocked by default`() {
        assertFalse(resolver.isAccessible(File("/etc")))
        assertFalse(resolver.isAccessible(File("/usr")))
        assertFalse(resolver.isAccessible(File("/var")))
        assertFalse(resolver.isAccessible(File("/opt")))
    }

    @Test
    fun `system directory accessible when allowed`() {
        val permissiveResolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            allowSystemRootAccess = true,
            os = OsType.LINUX,
            userHome = File("/home/alice")
        )
        assertTrue(permissiveResolver.isAccessible(File("/etc")))

        val segments = permissiveResolver.resolve(File("/etc"))
        assertEquals(2, segments.size)
        assertEquals("System", segments[0].label)
        assertEquals(SegmentType.SYSTEM_ROOT, segments[0].type)
        assertEquals("etc", segments[1].label)
        assertEquals(SegmentType.NORMAL, segments[1].type)
    }

    @Test
    fun `system subdirectory with access`() {
        val permissiveResolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            allowSystemRootAccess = true,
            os = OsType.LINUX,
            userHome = File("/home/alice")
        )
        val segments = permissiveResolver.resolve(File("/usr/share"))
        assertEquals(3, segments.size)
        assertEquals("System", segments[0].label)
        assertEquals("usr", segments[1].label)
        assertEquals("share", segments[2].label)
    }

    @Test
    fun `home is accessible`() {
        assertTrue(resolver.isAccessible(File("/home/alice")))
        assertTrue(resolver.isAccessible(File("/home/alice/Documents")))
    }

    @Test
    fun `container is accessible`() {
        assertTrue(resolver.isAccessible(File("/home")))
        assertTrue(resolver.isAccessible(File("/home/bob")))
    }

    @Test
    fun `mounted volume is accessible`() {
        assertTrue(resolver.isAccessible(File("/media/alice/USB")))
        assertTrue(resolver.isAccessible(File("/mnt/data")))
    }

    @Test
    fun `root path shows as System when allowed`() {
        val permissiveResolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            allowSystemRootAccess = true,
            os = OsType.LINUX,
            userHome = File("/home/alice")
        )
        val segments = permissiveResolver.resolve(File("/"))
        assertEquals(1, segments.size)
        assertEquals("System", segments[0].label)
        assertEquals(SegmentType.SYSTEM_ROOT, segments[0].type)
    }

    @Test
    fun `isWithinHome correct for own home`() {
        assertTrue(resolver.isWithinHome(File("/home/alice")))
        assertTrue(resolver.isWithinHome(File("/home/alice/Documents")))
        assertFalse(resolver.isWithinHome(File("/home/bob")))
        assertFalse(resolver.isWithinHome(File("/etc")))
    }

    @Test
    fun `isUnderContainer correct`() {
        assertTrue(resolver.isUnderContainer(File("/home")))
        assertTrue(resolver.isUnderContainer(File("/home/alice")))
        assertTrue(resolver.isUnderContainer(File("/home/bob")))
        assertFalse(resolver.isUnderContainer(File("/")))
        assertFalse(resolver.isUnderContainer(File("/etc")))
    }

    @Test
    fun `breadcrumb resolution does not depend on user count`() {
        val segmentsAlice = resolver.resolve(File("/home/alice/Downloads"))
        assertEquals("Home", segmentsAlice[0].label)

        val segmentsBob = resolver.resolve(File("/home/bob/Downloads"))
        assertEquals("Users", segmentsBob[0].label)
    }

    @Test
    fun `raw system mode shows real paths`() {
        val rawResolver = PathResolver(
            mode = PathDisplayMode.RAW_SYSTEM,
            os = OsType.LINUX,
            userHome = File("/home/alice")
        )
        val segments = rawResolver.resolve(File("/home/alice/Downloads"))
        assertEquals("/", segments.first().label)
        assertEquals("home", segments[1].label)
        assertEquals("alice", segments[2].label)
        assertEquals("Downloads", segments[3].label)
    }

    @Test
    fun `raw system mode root shows slash`() {
        val rawResolver = PathResolver(
            mode = PathDisplayMode.RAW_SYSTEM,
            os = OsType.LINUX,
            userHome = File("/home/alice")
        )
        val segments = rawResolver.resolve(File("/"))
        assertEquals(1, segments.size)
        assertEquals("/", segments.first().label)
    }
}

class PathResolverMacosTest {
    private val resolver = PathResolver(
        mode = PathDisplayMode.LOGICAL_HOME,
        os = OsType.MACOS,
        userHome = File("/Users/alice")
    )

    @Test
    fun `home directory resolves to Home`() {
        val segments = resolver.resolve(File("/Users/alice"))
        assertEquals(1, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals(SegmentType.HOME, segments[0].type)
    }

    @Test
    fun `home subdirectory`() {
        val segments = resolver.resolve(File("/Users/alice/Documents"))
        assertEquals(2, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals("Documents", segments[1].label)
    }

    @Test
    fun `user container shows Users`() {
        val segments = resolver.resolve(File("/Users"))
        assertEquals(1, segments.size)
        assertEquals("Users", segments[0].label)
        assertEquals(SegmentType.USER_CONTAINER, segments[0].type)
    }

    @Test
    fun `other macos user`() {
        val segments = resolver.resolve(File("/Users/bob"))
        assertEquals(2, segments.size)
        assertEquals("Users", segments[0].label)
        assertEquals("bob", segments[1].label)
    }

    @Test
    fun `mounted volume at Volumes`() {
        val segments = resolver.resolve(File("/Volumes/Backup"))
        assertEquals(1, segments.size)
        assertEquals("Backup", segments[0].label)
        assertEquals(SegmentType.MOUNTED_VOLUME, segments[0].type)
    }

    @Test
    fun `mounted volume subdirectory`() {
        val segments = resolver.resolve(File("/Volumes/Backup/Projects"))
        assertEquals(2, segments.size)
        assertEquals("Backup", segments[0].label)
        assertEquals(SegmentType.MOUNTED_VOLUME, segments[0].type)
        assertEquals("Projects", segments[1].label)
    }

    @Test
    fun `username never visible inside own home on macos`() {
        val segments = resolver.resolve(File("/Users/alice/Desktop"))
        assertEquals(2, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals("Desktop", segments[1].label)
    }

    @Test
    fun `system directory blocked on macos`() {
        assertFalse(resolver.isAccessible(File("/System")))
        assertFalse(resolver.isAccessible(File("/Library")))
    }
}

class PathResolverWindowsTest {
    @Test
    fun `windows home directory resolves to Home`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            os = OsType.WINDOWS,
            userHome = File("/Users/Alice")
        )
        val segments = resolver.resolve(File("/Users/Alice"))
        assertEquals(1, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals(SegmentType.HOME, segments[0].type)
    }

    @Test
    fun `windows home subdirectory`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            os = OsType.WINDOWS,
            userHome = File("/Users/Alice")
        )
        val segments = resolver.resolve(File("/Users/Alice/Downloads"))
        assertEquals(2, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals("Downloads", segments[1].label)
    }

    @Test
    fun `windows user container shows Users`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            os = OsType.WINDOWS,
            userHome = File("/Users/Alice")
        )
        val segments = resolver.resolve(File("/Users"))
        assertEquals(1, segments.size)
        assertEquals("Users", segments[0].label)
        assertEquals(SegmentType.USER_CONTAINER, segments[0].type)
    }

    @Test
    fun `windows other user`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            os = OsType.WINDOWS,
            userHome = File("/Users/Alice")
        )
        val segments = resolver.resolve(File("/Users/Bob"))
        assertEquals(2, segments.size)
        assertEquals("Users", segments[0].label)
        assertEquals("Bob", segments[1].label)
    }

    @Test
    fun `windows raw system mode preserves path`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.RAW_SYSTEM,
            os = OsType.WINDOWS,
            userHome = File("/Users/Alice")
        )
        val segments = resolver.resolve(File("/Users/Alice/Desktop"))
        assertTrue(segments.isNotEmpty())
        assertTrue(segments.any { it.label == "Users" })
        assertTrue(segments.any { it.label == "Alice" })
        assertTrue(segments.any { it.label == "Desktop" })
    }

    @Test
    fun `windows system directory blocked`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            os = OsType.WINDOWS,
            userHome = File("/Users/Alice")
        )
        assertFalse(resolver.isAccessible(File("/Windows")))
    }

    @Test
    fun `windows username not visible inside home`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            os = OsType.WINDOWS,
            userHome = File("/Users/Alice")
        )
        val segments = resolver.resolve(File("/Users/Alice/Documents"))
        assertEquals("Home", segments[0].label)
    }
}

class PathResolverEdgeCaseTest {

    @Test
    fun `root user home is handled`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            os = OsType.LINUX,
            userHome = File("/root")
        )
        val segments = resolver.resolve(File("/root"))
        assertEquals(1, segments.size)
        assertEquals("Home", segments[0].label)
    }

    @Test
    fun `root user subdirectory`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            os = OsType.LINUX,
            userHome = File("/root")
        )
        val segments = resolver.resolve(File("/root/.config"))
        assertEquals(2, segments.size)
        assertEquals("Home", segments[0].label)
        assertEquals(".config", segments[1].label)
    }

    @Test
    fun `raw system mode makes everything accessible`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.RAW_SYSTEM,
            os = OsType.LINUX,
            userHome = File("/home/alice")
        )
        assertTrue(resolver.isAccessible(File("/etc")))
        assertTrue(resolver.isAccessible(File("/usr")))
        assertTrue(resolver.isAccessible(File("/home/alice")))
        assertTrue(resolver.isAccessible(File("/")))
    }

    @Test
    fun `current directory flag on last segment`() {
        val resolver = PathResolver(
            mode = PathDisplayMode.LOGICAL_HOME,
            os = OsType.LINUX,
            userHome = File("/home/alice")
        )
        val segments = resolver.resolve(File("/home/alice/Documents/Work"))
        segments.dropLast(1).forEach { assertFalse(it.isCurrentDirectory) }
        assertTrue(segments.last().isCurrentDirectory)
    }
}
