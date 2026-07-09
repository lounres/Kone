/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.registry.RegistryKey
import kotlinx.coroutines.CoroutineScope


public data object KoneCanvasCoroutineScopeKey : RegistryKey<CoroutineScope> {
    override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasCoroutineScopeKey"
}