package ru.rtuitlab.itlab.common.extensions

/**
 * Returns 1 if true, 0 otherwise
 */
fun Boolean.toInt() =
    if (this) 1 else 0