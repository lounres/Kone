/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.common

import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.misc.canvas.KoneCanvasController
import dev.lounres.kone.misc.canvas.KoneCanvasControllerChildrenKey
import dev.lounres.kone.misc.canvas.KoneCanvasData
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrNull


public fun interface KoneCanvasCommonDrawer {
    public fun KoneCanvasCommonContext.draw(data: KoneCanvasData)
    
    public companion object;
    
    public fun interface Transformation {
        public fun KoneCanvasTransformationContext.apply(data: KoneCanvasData)
        
        public companion object;
        
        public data object Key : RegistryKey<Transformation> {
            override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.KoneCanvasCommonDrawer.Transformation.Key"
        }
    }
    
    public data object Key : RegistryKey<KoneCanvasCommonDrawer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasCommonDrawer.Key"
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasCommonDrawer.Transformation.Companion.set(
    transformation: KoneCanvasCommonDrawer.Transformation,
) {
    KoneCanvasCommonDrawer.Transformation.Key correspondsTo transformation
}

public class KoneCanvasCommonGroupDrawer(private val controller: KoneCanvasController) : KoneCanvasCommonDrawer {
    override fun KoneCanvasCommonContext.draw(data: KoneCanvasData) {
        context(controller) {
            val transformation = controller.getOrNull(KoneCanvasCommonDrawer.Transformation.Key)
            group(
                transformationBlock = { transformation?.apply { apply(data) } }
            ) {
                controller.getOrNull(BackgroundKey)?.apply { draw(data) }
                controller.getOrNull(KoneCanvasControllerChildrenKey)?.forEach {
                    it.getOrNull(KoneCanvasCommonDrawer.Key)?.apply { draw(data) }
                }
                controller.getOrNull(ForegroundKey)?.apply { draw(data) }
            }
        }
    }
    
    public data object ForegroundKey : RegistryKey<KoneCanvasCommonDrawer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.GroupCommonDrawer.ForegroundKey"
    }
    
    public data object BackgroundKey : RegistryKey<KoneCanvasCommonDrawer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.GroupCommonDrawer.BackgroundKey"
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, controller: KoneCanvasController.Provider)
public fun KoneCanvasCommonDrawer.Companion.setGroup() {
    KoneCanvasCommonDrawer.Key correspondsTo RegisteredValueProvider.cached {
        KoneCanvasCommonGroupDrawer(controller.get())
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasCommonDrawer.Companion.setGroupForeground(
    drawer: KoneCanvasCommonDrawer,
) {
    KoneCanvasCommonGroupDrawer.ForegroundKey correspondsTo drawer
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, controller: KoneCanvasController.Provider)
public fun KoneCanvasCommonDrawer.Companion.setGroupForeground(
    drawer: context(KoneCanvasController) KoneCanvasCommonContext.(data: KoneCanvasData) -> Unit,
) {
    KoneCanvasCommonGroupDrawer.ForegroundKey correspondsTo RegisteredValueProvider.cached {
        val controller = controller.get()
        KoneCanvasCommonDrawer { data -> context(controller) { drawer(data) } }
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasCommonDrawer.Companion.setGroupBackground(
    drawer: KoneCanvasCommonDrawer,
) {
    KoneCanvasCommonGroupDrawer.BackgroundKey correspondsTo drawer
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, controller: KoneCanvasController.Provider)
public fun KoneCanvasCommonDrawer.Companion.setGroupBackground(
    drawer: context(KoneCanvasController) KoneCanvasCommonContext.(data: KoneCanvasData) -> Unit,
) {
    KoneCanvasCommonGroupDrawer.BackgroundKey correspondsTo RegisteredValueProvider.cached {
        val controller = controller.get()
        KoneCanvasCommonDrawer { data -> context(controller) { drawer(data) } }
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, controller: KoneCanvasController.Provider)
public fun KoneCanvasCommonDrawer.Companion.setCustom(
    drawer: KoneCanvasCommonDrawer,
) {
    KoneCanvasCommonDrawer.Key correspondsTo RegisteredValueProvider.cached {
        val controller = controller.get()
        KoneCanvasCommonDrawer { data ->
            context(controller) {
                val transformation = controller.getOrNull(KoneCanvasCommonDrawer.Transformation.Key)
                group(
                    transformationBlock = { transformation?.apply { apply(data) } }
                ) {
                    drawer.apply { draw(data) }
                }
            }
        }
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, controller: KoneCanvasController.Provider)
public fun KoneCanvasCommonDrawer.Companion.setCustom(
    drawer: context(KoneCanvasController) KoneCanvasCommonContext.(data: KoneCanvasData) -> Unit,
) {
    KoneCanvasCommonDrawer.Key correspondsTo RegisteredValueProvider.cached {
        val controller = controller.get()
        KoneCanvasCommonDrawer { data ->
            context(controller) {
                val transformation = controller.getOrNull(KoneCanvasCommonDrawer.Transformation.Key)
                group(
                    transformationBlock = { transformation?.apply { apply(data) } }
                ) {
                    drawer(data)
                }
            }
        }
    }
}