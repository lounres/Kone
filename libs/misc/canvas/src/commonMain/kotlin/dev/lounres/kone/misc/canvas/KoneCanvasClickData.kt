/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.OwnedProviderRegistry
import dev.lounres.kone.registry.ProviderRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.RegistryWrapper
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.empty
import dev.lounres.kone.registry.wrapFor
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


@JvmInline
public value class KoneCanvasClickData(override val registry: OwnedProviderRegistry<KoneCanvasClickData>) : RegistryWrapper<KoneCanvasClickData> {
    public companion object;
    
    public fun interface Provider {
        public fun get(): KoneCanvasClickData
    }
    
    public data object Key : RegistryKey<KoneCanvasClickData> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasClickData.Key"
    }
}

public fun KoneCanvasClickData.Companion.empty(): KoneCanvasClickData = KoneCanvasClickData(OwnedProviderRegistry.empty())

public fun KoneCanvasClickData.Companion.wrap(providerRegistry: ProviderRegistry): KoneCanvasClickData = KoneCanvasClickData(OwnedProviderRegistry.wrapFor(providerRegistry))

public inline fun KoneCanvasClickData.Companion.build(block: MutableOwnedProviderRegistry<KoneCanvasClickData>.() -> Unit): KoneCanvasClickData {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneCanvasClickData(OwnedProviderRegistry.build { this.block() })
}

public inline fun KoneCanvasClickData.Companion.buildWithProvider(
    block: context(KoneCanvasClickData.Provider) MutableOwnedProviderRegistry<KoneCanvasClickData>.() -> Unit
): KoneCanvasClickData {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneCanvasClickData.Provider {
        var result: KoneCanvasClickData? = null
        override fun get(): KoneCanvasClickData =
            result ?: error("KoneCanvasClickData is not yet initialized but was requested by its properties.")
    }
    val result = KoneCanvasClickData(OwnedProviderRegistry.build { block(provider, this) })
    provider.result = result
    return result
}

public fun interface KoneCanvasClickController {
    public fun click(canvasData: KoneCanvasData): KoneCanvasClickData
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasClickController> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasClickController.Key"
    }
}

public fun interface KoneCanvasClickConsumer {
    public fun onClick(canvasData: KoneCanvasData)
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasClickConsumer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasClickConsumer.Key"
    }
}