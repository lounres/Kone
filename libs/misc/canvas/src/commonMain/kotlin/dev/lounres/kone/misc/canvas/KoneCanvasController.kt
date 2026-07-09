/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.OwnedProviderRegistry
import dev.lounres.kone.registry.ProviderRegistry
import dev.lounres.kone.registry.ProviderRegistryWrapper
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.empty
import dev.lounres.kone.registry.wrapFor
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


@JvmInline
public value class KoneCanvasController(override val registry: OwnedProviderRegistry<KoneCanvasController>) : ProviderRegistryWrapper<KoneCanvasController> {
    public companion object;
    
    public fun interface Provider {
        public fun get(): KoneCanvasController
    }
}

public fun KoneCanvasController.Companion.empty(): KoneCanvasController = KoneCanvasController(OwnedProviderRegistry.empty())

public fun KoneCanvasController.Companion.wrap(providerRegistry: ProviderRegistry): KoneCanvasController = KoneCanvasController(OwnedProviderRegistry.wrapFor(providerRegistry))

public inline fun KoneCanvasController.Companion.build(block: MutableOwnedProviderRegistry<KoneCanvasController>.() -> Unit): KoneCanvasController {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneCanvasController(OwnedProviderRegistry.build { this.block() })
}

public inline fun KoneCanvasController.Companion.buildWithProvider(
    block: context(KoneCanvasController.Provider) MutableOwnedProviderRegistry<KoneCanvasController>.() -> Unit
): KoneCanvasController {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneCanvasController.Provider {
        var result: KoneCanvasController? = null
        override fun get(): KoneCanvasController =
            result ?: error("KoneCanvasController is not yet initialized but was requested by its properties.")
    }
    val result = KoneCanvasController(OwnedProviderRegistry.build { block(provider, this) })
    provider.result = result
    return result
}