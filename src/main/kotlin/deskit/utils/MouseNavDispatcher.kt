package deskit.utils

import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File

class MouseNavDispatcher {
    var onNavigate: ((File) -> Unit)? = null
    var currentSupplier: (() -> File)? = null

    private val backStack = ArrayDeque<File>()
    private val forwardStack = ArrayDeque<File>()

    private val adapter = object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            val current = currentSupplier?.invoke() ?: return
            when (e.button) {
                4, 6 -> goBack(current)
                5, 7 -> goForward(current)
            }
        }
    }

    private fun goBack(current: File) {
        val dir = backStack.removeLastOrNull() ?: return
        forwardStack.addLast(current)
        onNavigate?.invoke(dir)
    }

    private fun goForward(current: File) {
        val dir = forwardStack.removeLastOrNull() ?: return
        backStack.addLast(current)
        onNavigate?.invoke(dir)
    }

    fun navigateTo(dir: File) {
        val current = currentSupplier?.invoke()
        if (current?.absolutePath == dir.absolutePath) return
        current?.let { backStack.addLast(it) }
        forwardStack.clear()
        onNavigate?.invoke(dir)
    }

    fun clearHistory() {
        backStack.clear()
        forwardStack.clear()
    }

    fun installOn(window: Window) {
        window.addMouseListener(adapter)
    }

    fun uninstallFrom(window: Window) {
        window.removeMouseListener(adapter)
    }
}
