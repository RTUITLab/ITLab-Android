package ru.rtuitlab.itlab.utils

class RunnableHolder {
    var runnable: Runnable? = null
    fun run() = runnable?.run()
}