/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.contexts

import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.RegistryBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


/**
 * Type-safe registry (a.k.a. type-safe map) made especially for contexts in Kone.
 */
@JvmInline
public value class KoneContextRegistry(
    /**
     * Underlying type-safe registry.
     */
    public val contexts: Registry
) : Registry by contexts

/**
 * Provides receiver for Kone context registry.
 */
public inline operator fun <R> KoneContextRegistry.invoke(block: KoneContextRegistry.() -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
    return block(this)
}

/**
 * Builder function for [KoneContextRegistry].
 */
public inline fun KoneContextRegistry(block: RegistryBuilder<KoneContextRegistry>.() -> Unit): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneContextRegistry(Registry { this.block() })
}