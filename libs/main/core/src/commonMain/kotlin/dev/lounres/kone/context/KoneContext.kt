/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.context


/**
 * Marker interface for Kone's contexts.
 */
public interface KoneContext

/**
 * Simple provider of [this] context as a context parameter in the following [block].
 */
public inline operator fun <KoneContextType: KoneContext, Result> KoneContextType.invoke(block: context(KoneContextType) () -> Result): Result {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
    return block(this)
}