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
public value class KoneCanvasHoverData(override val registry: OwnedProviderRegistry<KoneCanvasHoverData>) : ProviderRegistryWrapper<KoneCanvasHoverData> {
    public companion object;
    
    public fun interface Provider {
        public fun get(): KoneCanvasHoverData
    }
    
    public data object Key : RegistryKey<KoneCanvasHoverData> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasHoverData.Key"
    }
}

public fun KoneCanvasHoverData.Companion.empty(): KoneCanvasHoverData = KoneCanvasHoverData(OwnedProviderRegistry.empty())

public fun KoneCanvasHoverData.Companion.wrap(providerRegistry: ProviderRegistry): KoneCanvasHoverData = KoneCanvasHoverData(OwnedProviderRegistry.wrapFor(providerRegistry))

public inline fun KoneCanvasHoverData.Companion.build(block: MutableOwnedProviderRegistry<KoneCanvasHoverData>.() -> Unit): KoneCanvasHoverData {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneCanvasHoverData(OwnedProviderRegistry.build { this.block() })
}

public inline fun KoneCanvasHoverData.Companion.buildWithProvider(
    block: context(KoneCanvasHoverData.Provider) MutableOwnedProviderRegistry<KoneCanvasHoverData>.() -> Unit
): KoneCanvasHoverData {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneCanvasHoverData.Provider {
        var result: KoneCanvasHoverData? = null
        override fun get(): KoneCanvasHoverData =
            result ?: error("KoneCanvasHoverData is not yet initialized but was requested by its properties.")
    }
    val result = KoneCanvasHoverData(OwnedProviderRegistry.build { block(provider, this) })
    provider.result = result
    return result
}

public fun interface KoneCanvasHoverController {
    public fun hover(canvasData: KoneCanvasData): KoneCanvasHoverData
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasHoverController> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasHoverController.Key"
    }
}

public fun interface KoneCanvasHoverConsumer {
    public fun onHover(canvasData: KoneCanvasData)
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasHoverConsumer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasHoverConsumer.Key"
    }
}