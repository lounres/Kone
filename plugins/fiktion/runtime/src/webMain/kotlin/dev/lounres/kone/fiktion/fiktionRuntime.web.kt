/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.fiktion


@PublishedApi
internal var webAtomicCallInterceptor: AtomicCallInterceptor = AtomicCallInterceptor.Idle

@Fiktion
@Fiktion.AtomicCallInterceptorDelicateApi
public actual inline var atomicCallInterceptor: AtomicCallInterceptor
    get() = webAtomicCallInterceptor
    set(value) { webAtomicCallInterceptor = value }