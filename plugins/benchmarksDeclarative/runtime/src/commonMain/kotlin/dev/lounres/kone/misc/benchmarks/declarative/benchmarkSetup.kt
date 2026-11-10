/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.declarative


public inline fun <Value> setupValue(crossinline block: () -> Value): Value = error("... intrinsic ...")

public interface KoneDisposableValue<out T> {
    public val value: T
    public fun dispose()
}

public fun <T> KoneDisposableValue(value: T, dispose: () -> Unit): KoneDisposableValue<T> =
    object : KoneDisposableValue<T> {
        override val value: T = value
        override fun dispose() { dispose() }
    }

public inline fun <Value> setupDisposableValue(crossinline block: () -> KoneDisposableValue<Value>): Value = error("... intrinsic ...")