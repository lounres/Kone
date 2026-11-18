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
    val tmpdir = getenv("TMPDIR")?.toKString()
    if (!tmpdir.isNullOrEmpty()) return@lazy tmpdir
    return@lazy "/tmp"
}

internal actual fun String.makeDir() {
    mkdir(this, 755u)
}