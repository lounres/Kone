/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.registry.RegistryKey

@Suppress("EqualsOrHashCode")
public object KoneCanvasTrigger {
    override fun equals(other: Any?): Boolean = false
}

public fun interface KoneCanvasTriggerConsumer {
    public suspend fun onTrigger(block: suspend () -> Unit)
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasTriggerConsumer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasTriggerConsumer.Key"
    }
}

public fun interface KoneCanvasTriggerProducer {
    public fun trigger()
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasTriggerProducer> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasTriggerProducer.Key"
    }
}