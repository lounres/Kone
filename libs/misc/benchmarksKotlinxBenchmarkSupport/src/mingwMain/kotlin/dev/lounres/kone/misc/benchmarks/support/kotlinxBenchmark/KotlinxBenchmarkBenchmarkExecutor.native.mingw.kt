/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv
import platform.posix.mkdir


@OptIn(ExperimentalForeignApi::class)
internal actual val tempDirectory: String by lazy {
    val temp = getenv("TEMP")?.toKString()
    if (!temp.isNullOrEmpty()) return@lazy temp
    val tmp = getenv("TMP")?.toKString()
    if (!tmp.isNullOrEmpty()) return@lazy tmp
    val userProfile = getenv("USERPROFILE")?.toKString()
    if (!userProfile.isNullOrEmpty()) return@lazy "$userProfile\\AppData\\Local\\Temp"
    return@lazy "C:\\Temp"
}

internal actual fun String.makeDir() {
    mkdir(this)
}