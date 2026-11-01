/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.compose

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.withTransform
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.misc.canvas.*
import dev.lounres.kone.misc.canvas.common.KoneCanvasCommonDrawer
import dev.lounres.kone.misc.canvas.common.KoneCanvasTargetKey
import dev.lounres.kone.misc.canvas.common.common
import dev.lounres.kone.registry.*


public fun interface KoneCanvasComposeMultiplatformDrawer {
    public fun DrawScope.draw(data: KoneCanvasData)
    
    public companion object;
    
    public fun interface Transformation {
        public fun DrawTransform.apply(data: KoneCanvasData)
        
        public companion object;
        
        public data object Key : RegistryKey<Transformation> {
            override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.KoneCanvasComposeMultiplatformDrawer.Transformation.Key"
        }
    }
    
    public data object Key : RegistryKey<KoneCanvasComposeMultiplatformDrawer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.KoneCanvasComposeMultiplatformDrawer.Key"
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasComposeMultiplatformDrawer.Transformation.Companion.set(
    transformation: KoneCanvasComposeMultiplatformDrawer.Transformation,
) {
    KoneCanvasComposeMultiplatformDrawer.Transformation.Key correspondsTo transformation
}

public class KoneCanvasComposeMultiplatformGroupDrawer(private val controller: KoneCanvasController) : KoneCanvasComposeMultiplatformDrawer {
    override fun DrawScope.draw(data: KoneCanvasData) {
        val transformation = controller.getOrNull(KoneCanvasComposeMultiplatformDrawer.Transformation.Key)
        withTransform(
            transformBlock = {
                transformation?.apply { apply(data) }
            }
        ) {
            controller.getOrNull(BackgroundKey)?.apply { draw(data) }
            controller.getOrNull(KoneCanvasControllerChildrenKey)?.forEach {
                it.getOrNull(KoneCanvasComposeMultiplatformDrawer.Key)?.apply { draw(data) }
            }
            controller.getOrNull(ForegroundKey)?.apply { draw(data) }
        }
    }
    
    public data object ForegroundKey : RegistryKey<KoneCanvasComposeMultiplatformDrawer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.GroupComposeMultiplatformDrawer.ForegroundKey"
    }
    
    public data object BackgroundKey : RegistryKey<KoneCanvasComposeMultiplatformDrawer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.GroupComposeMultiplatformDrawer.BackgroundKey"
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun KoneCanvasComposeMultiplatformDrawer.Companion.setGroup() {
    KoneCanvasComposeMultiplatformDrawer.Key correspondsTo RegisteredValueProvider.cached {
        KoneCanvasComposeMultiplatformGroupDrawer(koneCanvasController.get())
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasComposeMultiplatformDrawer.Companion.setGroupForeground(
    drawer: KoneCanvasComposeMultiplatformDrawer,
) {
    KoneCanvasComposeMultiplatformGroupDrawer.ForegroundKey correspondsTo drawer
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasComposeMultiplatformDrawer.Companion.setGroupBackground(
    drawer: KoneCanvasComposeMultiplatformDrawer,
) {
    KoneCanvasComposeMultiplatformGroupDrawer.BackgroundKey correspondsTo drawer
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun KoneCanvasComposeMultiplatformDrawer.Companion.setCustom(
    drawer: KoneCanvasComposeMultiplatformDrawer,
) {
    KoneCanvasComposeMultiplatformDrawer.Key correspondsTo RegisteredValueProvider.cached {
        val koneCanvasController = koneCanvasController.get()
        KoneCanvasComposeMultiplatformDrawer { data ->
            contextOf<DrawScope>().withTransform(
                transformBlock = {
                    koneCanvasController.getOrNull(KoneCanvasComposeMultiplatformDrawer.Transformation.Key)?.apply { apply(data) }
                }
            ) {
                drawer.apply { draw(data) }
            }
        }
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>, koneCanvasController: KoneCanvasController.Provider)
public fun KoneCanvasComposeMultiplatformDrawer.Companion.setViaCommon() {
    KoneCanvasComposeMultiplatformDrawer.Key correspondsTo RegisteredValueProvider.cached {
        val koneCanvasController = koneCanvasController.get()
        val koneCanvasContextRegistryDrawer = koneCanvasController.getOrNull(KoneCanvasCommonDrawer.Key)
        KoneCanvasComposeMultiplatformDrawer { data ->
            val newCanvasContext = KoneCanvasContextRegistry.buildWithProvider {
                KoneCanvasComposeMultiplatformStack.Key correspondsTo KoneCanvasComposeMultiplatformStack(
                    drawScope = this@KoneCanvasComposeMultiplatformDrawer,
                    sizes = KoneMutableList.of(),
                )
                KoneCanvasTargetKey.SetKey correspondsTo KoneSet.of(
                    KoneCanvasTargetKey<KoneCanvasComposeMultiplatformContext>(),
                    elementEquality = RegistryKey.equality(),
                    elementHashing = RegistryKey.hashing(),
                )
                KoneCanvasData.Key correspondsTo data
            }
            koneCanvasContextRegistryDrawer?.apply { newCanvasContext.common.draw(data) }
        }
    }
}