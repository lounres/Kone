/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.svg4kt

import dev.jamesyox.svg4kt.TagConsumer
import dev.jamesyox.svg4kt.attr.attrs.transform
import dev.jamesyox.svg4kt.tags.G
import dev.jamesyox.svg4kt.tags.g
import dev.jamesyox.svg4kt.util.TransformBuilder
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.misc.canvas.KoneCanvasContextRegistry
import dev.lounres.kone.misc.canvas.KoneCanvasController
import dev.lounres.kone.misc.canvas.KoneCanvasControllerChildrenKey
import dev.lounres.kone.misc.canvas.KoneCanvasData
import dev.lounres.kone.misc.canvas.buildWithProvider
import dev.lounres.kone.misc.canvas.common.KoneCanvasCommonDrawer
import dev.lounres.kone.misc.canvas.common.KoneCanvasTargetKey
import dev.lounres.kone.misc.canvas.common.common
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.equality
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.registry.hashing


public fun interface KoneCanvasSvg4ktDrawer {
    context(_: TagConsumer<*>, _: G)
    public fun draw(data: KoneCanvasData)
    
    public companion object;
    
    public fun interface Transformation {
        context(_: TransformBuilder)
        public fun apply(data: KoneCanvasData)
        
        public companion object;
        
        public data object Key : RegistryKey<Transformation> {
            override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.KoneCanvasSvg4ktDrawer.Transformation.Key"
        }
    }
    
    public data object Key : RegistryKey<KoneCanvasSvg4ktDrawer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.KoneCanvasSvg4ktDrawer.Key"
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasSvg4ktDrawer.Transformation.Companion.set(
    transformation: KoneCanvasSvg4ktDrawer.Transformation,
) {
    KoneCanvasSvg4ktDrawer.Transformation.Key correspondsTo transformation
}

public class KoneCanvasSvg4ktGroupDrawer(private val controller: KoneCanvasController) : KoneCanvasSvg4ktDrawer {
    context(_: TagConsumer<*>, _: G)
    override fun draw(data: KoneCanvasData) {
        g {
            val transformation = controller.getOrNull(KoneCanvasSvg4ktDrawer.Transformation.Key)
            if (transformation != null) {
                transform {
                    transformation.apply(data)
                }
            }
            controller.getOrNull(BackgroundKey)?.apply { draw(data) }
            controller.getOrNull(KoneCanvasControllerChildrenKey)?.forEach {
                it.getOrNull(KoneCanvasSvg4ktDrawer.Key)?.apply { draw(data) }
            }
            controller.getOrNull(ForegroundKey)?.apply { draw(data) }
        }
    }
    
    public data object ForegroundKey : RegistryKey<KoneCanvasSvg4ktDrawer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.GroupSvg4ktDrawer.ForegroundKey"
    }
    
    public data object BackgroundKey : RegistryKey<KoneCanvasSvg4ktDrawer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.GroupSvg4ktDrawer.BackgroundKey"
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun KoneCanvasSvg4ktDrawer.Companion.setGroup() {
    KoneCanvasSvg4ktDrawer.Key correspondsTo RegisteredValueProvider.cached {
        KoneCanvasSvg4ktGroupDrawer(koneCanvasController.get())
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasSvg4ktDrawer.Companion.setGroupForeground(
    drawer: KoneCanvasSvg4ktDrawer,
) {
    KoneCanvasSvg4ktGroupDrawer.ForegroundKey correspondsTo drawer
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasSvg4ktDrawer.Companion.setGroupBackground(
    drawer: KoneCanvasSvg4ktDrawer,
) {
    KoneCanvasSvg4ktGroupDrawer.BackgroundKey correspondsTo drawer
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun KoneCanvasSvg4ktDrawer.Companion.setCustom(
    drawer: KoneCanvasSvg4ktDrawer,
) {
    KoneCanvasSvg4ktDrawer.Key correspondsTo RegisteredValueProvider.cached {
        val koneCanvasController = koneCanvasController.get()
        KoneCanvasSvg4ktDrawer { data ->
            g {
                val transformation = koneCanvasController.getOrNull(KoneCanvasSvg4ktDrawer.Transformation.Key)
                if (transformation != null) {
                    transform {
                        transformation.apply(data)
                    }
                }
                drawer.draw(data)
            }
        }
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun KoneCanvasSvg4ktDrawer.Companion.setViaCommon() {
    KoneCanvasSvg4ktDrawer.Key correspondsTo RegisteredValueProvider.cached {
        val koneCanvasController = koneCanvasController.get()
        val koneCanvasContextRegistryDrawer = koneCanvasController.getOrNull(KoneCanvasCommonDrawer.Key)
        KoneCanvasSvg4ktDrawer { data ->
            val newCanvasContext = KoneCanvasContextRegistry.buildWithProvider {
                KoneCanvasSvg4ktTagConsumerKey correspondsTo contextOf<TagConsumer<*>>()
                KoneCanvasTargetKey.SetKey correspondsTo KoneSet.of(
                    KoneCanvasTargetKey<KoneCanvasSvg4ktContext>(),
                    elementEquality = RegistryKey.equality(),
                    elementHashing = RegistryKey.hashing(),
                )
                KoneCanvasData.Key correspondsTo data
            }
            koneCanvasContextRegistryDrawer?.apply {
                val commonContext = newCanvasContext.common
                commonContext.draw(data)
            }
        }
    }
}