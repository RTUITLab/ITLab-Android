package ru.rtuitlab.itlab.presentation.utils

class RunnableHolder {
    var runnable: Runnable? = null
    fun run() = runnable?.run()
}