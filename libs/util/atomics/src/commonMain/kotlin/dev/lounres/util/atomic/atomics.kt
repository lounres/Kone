/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.util.atomic

import kotlin.concurrent.atomics.AtomicReference


public inline var <T> AtomicReference<T>.value: T
    get() = load()
    set(value) { store(value) }

public inline fun <T> AtomicReference<T>.update(update: (T) -> T) {
    while (true) {
        val currentValue = load()
        val newValue = update(currentValue)
        if (compareAndSet(currentValue, newValue)) return
    }
}

public inline fun <T> AtomicReference<T>.updateAndGet(update: (T) -> T): T {
    while (true) {
        val currentValue = load()
        val newValue = update(currentValue)
        if (compareAndSet(currentValue, newValue)) return newValue
    }
}

public inline fun <T> AtomicReference<T>.getAndUpdate(update: (T) -> T): T {
    while (true) {
        val currentValue = load()
        val newValue = update(currentValue)
        if (compareAndSet(currentValue, newValue)) return currentValue
    }
}