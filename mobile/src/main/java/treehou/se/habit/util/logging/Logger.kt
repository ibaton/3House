package treehou.se.habit.util.logging


interface Logger {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
    fun w(tag: String, message: String, error: Throwable)
    fun e(tag: String, message: String)
    fun e(tag: String, message: String, error: Throwable)
}
