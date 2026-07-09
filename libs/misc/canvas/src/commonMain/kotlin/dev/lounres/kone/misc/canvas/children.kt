/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.registry.*
import kotlinx.coroutines.flow.StateFlow
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public data object KoneCanvasControllerChildrenKey : RegistryKey<KoneList<KoneCanvasController>> {
    override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasControllerChildrenKey"
}

public data object KoneCanvasControllerChildrenStateKey : RegistryKey<StateFlow<KoneList<KoneCanvasController>>> {
    override val impliedKeys: ImpliedKeysRegistry<StateFlow<KoneList<KoneCanvasController>>> = ImpliedKeysRegistry {
        KoneCanvasControllerChildrenKey implies { it.value }
    }
    override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasControllerChildrenStateKey"
}

public fun interface KoneCanvasControllerChildConfigurator {
    context(builder: MutableOwnedProviderRegistry<KoneCanvasController>, provider: KoneCanvasController.Provider)
    public fun configure(parent: KoneCanvasController)
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasControllerChildConfigurator> {
        override fun toString(): String = "KoneCanvasControllerChildConfigurator.Key"
    }
}

public inline fun KoneCanvasControllerChildConfigurator.Companion.build(
    crossinline configure: context(MutableOwnedProviderRegistry<KoneCanvasController>, KoneCanvasController.Provider) (KoneCanvasController) -> Unit = {}
): KoneCanvasControllerChildConfigurator = KoneCanvasControllerChildConfigurator {
    val parentFactory = it.getOrNull(KoneCanvasControllerChildConfigurator.Key)
    if (parentFactory != null) KoneCanvasControllerChildConfigurator.Key correspondsTo parentFactory
    configure(it)
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public inline fun KoneCanvasControllerChildConfigurator.Companion.set(
    crossinline configure: context(MutableOwnedProviderRegistry<KoneCanvasController>, KoneCanvasController.Provider) (KoneCanvasController) -> Unit = {}
) {
    KoneCanvasControllerChildConfigurator.Key correspondsTo RegisteredValueProvider.cached { build(configure) }
}

public inline fun KoneCanvasController.buildChildWithProvider(
    block: context(KoneCanvasController.Provider) MutableOwnedProviderRegistry<KoneCanvasController>.() -> Unit
): KoneCanvasController {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val parent = this
    return KoneCanvasController.buildWithProvider {
        parent.getOrNull(KoneCanvasControllerChildConfigurator.Key)?.configure(parent)
        block()
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun setChildren(
    childrenProvider: KoneCanvasController.() -> KoneList<KoneCanvasController>
) {
    KoneCanvasControllerChildrenKey correspondsTo {
        koneCanvasController.get().childrenProvider()
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun setChildrenCached(
    childrenProvider: KoneCanvasController.() -> KoneList<KoneCanvasController>
) {
    KoneCanvasControllerChildrenKey correspondsTo RegisteredValueProvider.cached {
        koneCanvasController.get().childrenProvider()
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun setChildrenState(
    childrenProvider: KoneCanvasController.() -> StateFlow<KoneList<KoneCanvasController>>
) {
    KoneCanvasControllerChildrenStateKey.withImpliedUsingFirst correspondsTo {
        koneCanvasController.get().childrenProvider()
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun setChildrenStateCached(
    childrenProvider: KoneCanvasController.() -> StateFlow<KoneList<KoneCanvasController>>
) {
    KoneCanvasControllerChildrenStateKey.withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        koneCanvasController.get().childrenProvider()
    }
}