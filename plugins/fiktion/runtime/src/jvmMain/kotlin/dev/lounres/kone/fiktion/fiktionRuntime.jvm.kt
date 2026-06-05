/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.fiktion


@PublishedApi
internal object AtomicCallInterceptorThreadLocal : ThreadLocal<AtomicCallInterceptor>() {
    override fun initialValue(): AtomicCallInterceptor = AtomicCallInterceptor.Idle
}

@Fiktion
@Fiktion.AtomicCallInterceptorDelicateApi
public actual inline var atomicCallInterceptor: AtomicCallInterceptor
    get() = AtomicCallInterceptorThreadLocal.get()
    set(value) { AtomicCallInterceptorThreadLocal.set(value) }