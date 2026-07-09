/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.registry.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


@JvmInline
public value class KoneCanvasContextRegistry(override val registry: OwnedProviderRegistry<KoneCanvasContextRegistry>) : ProviderRegistryWrapper<KoneCanvasContextRegistry> {
    public companion object;
    
    public fun interface Provider {
        public fun get(): KoneCanvasContextRegistry
    }
    
    public data object Key : RegistryKey<KoneCanvasContextRegistry> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasContextRegistry.Key"
    }
}

public fun KoneCanvasContextRegistry.Companion.empty(): KoneCanvasContextRegistry = KoneCanvasContextRegistry(OwnedProviderRegistry.empty())

public fun KoneCanvasContextRegistry.Companion.wrap(providerRegistry: ProviderRegistry): KoneCanvasContextRegistry = KoneCanvasContextRegistry(OwnedProviderRegistry.wrapFor(providerRegistry))

public inline fun KoneCanvasContextRegistry.Companion.build(block: MutableOwnedProviderRegistry<KoneCanvasContextRegistry>.() -> Unit): KoneCanvasContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneCanvasContextRegistry(OwnedProviderRegistry.build { this.block() })
}

public inline fun KoneCanvasContextRegistry.Companion.buildWithProvider(
    block: context(KoneCanvasContextRegistry.Provider) MutableOwnedProviderRegistry<KoneCanvasContextRegistry>.() -> Unit
): KoneCanvasContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneCanvasContextRegistry.Provider {
        var result: KoneCanvasContextRegistry? = null
        override fun get(): KoneCanvasContextRegistry =
            result ?: error("KoneCanvasContext is not yet initialized but was requested by its properties.")
    }
    val result = KoneCanvasContextRegistry(OwnedProviderRegistry.build { block(provider, this) })
    provider.result = result
    return result
}