package greenberg.moviedbshell.logging

import timber.log.Timber

class DebuggingTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "sag:%s:%s",
            super.createStackElementTag(element),
            element.lineNumber
        )
    }
}
