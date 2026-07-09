/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrNull


public fun interface KoneCanvasControllerInitializer {
    public fun initialize()
    
    public data object Key : RegistryKey<KoneCanvasControllerInitializer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasControllerInitializer.Key"
    }
}

public fun KoneCanvasController.initialize() {
    getOrNull(KoneCanvasControllerInitializer.Key)?.initialize()
}

public fun MutableOwnedProviderRegistry<KoneCanvasController>.onInitialize(action: KoneCanvasControllerInitializer) {
    KoneCanvasControllerInitializer.Key correspondsTo action
}