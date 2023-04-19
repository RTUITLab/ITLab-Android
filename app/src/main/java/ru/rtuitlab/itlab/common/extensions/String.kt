package ru.rtuitlab.itlab.common.extensions

fun String.unescaped() =
    this.replace("\\n", "\n")