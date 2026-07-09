/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.OwnedProviderRegistry
import dev.lounres.kone.registry.ProviderRegistry
import dev.lounres.kone.registry.ProviderRegistryWrapper
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.empty
import dev.lounres.kone.registry.wrapFor
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


@JvmInline
public value class KoneCanvasData(override val registry: OwnedProviderRegistry<KoneCanvasData>) : ProviderRegistryWrapper<KoneCanvasData> {
    public companion object;
    
    public fun interface Provider {
        public fun get(): KoneCanvasData
    }
    
    public data object Key : RegistryKey<KoneCanvasData> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasData.Key"
    }
}

public fun KoneCanvasData.Companion.empty(): KoneCanvasData = KoneCanvasData(OwnedProviderRegistry.empty())

public fun KoneCanvasData.Companion.wrap(providerRegistry: ProviderRegistry): KoneCanvasData = KoneCanvasData(OwnedProviderRegistry.wrapFor(providerRegistry))

public inline fun KoneCanvasData.Companion.build(block: MutableOwnedProviderRegistry<KoneCanvasData>.() -> Unit): KoneCanvasData {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneCanvasData(OwnedProviderRegistry.build { this.block() })
}

public inline fun KoneCanvasData.Companion.buildWithProvider(
    block: context(KoneCanvasData.Provider) MutableOwnedProviderRegistry<KoneCanvasData>.() -> Unit
): KoneCanvasData {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneCanvasData.Provider {
        var result: KoneCanvasData? = null
        override fun get(): KoneCanvasData =
            result ?: error("KoneCanvasData is not yet initialized but was requested by its properties.")
    }
    val result = KoneCanvasData(OwnedProviderRegistry.build { block(provider, this) })
    provider.result = result
    return result
}