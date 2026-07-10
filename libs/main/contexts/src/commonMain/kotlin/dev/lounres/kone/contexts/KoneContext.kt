/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.contexts

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneContextHolder {
    public companion object;
}

/**
 * Marker interface for Kone's contexts.
 */
public interface KoneContext {
    public companion object;
}

/**
 * Simple provider of [this] context as a context parameter in the following [block].
 */
public inline operator fun <KoneContextType: KoneContext, Result> KoneContextType.invoke(block: context(KoneContextType) () -> Result): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this)
}