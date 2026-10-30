/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks


@Deprecated(
    message = "",
    level = HIDDEN,
)
public data class InjectedValue<out T>(
    val disposableInjection: DisposableInjection<T>,
    val value: T
)