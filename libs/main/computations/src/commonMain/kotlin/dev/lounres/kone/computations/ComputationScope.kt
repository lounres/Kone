/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations

import kotlin.coroutines.CoroutineContext


public interface ComputationScope {
    public val coroutineContext: CoroutineContext
}

public fun ComputationScope(context: CoroutineContext): ComputationScope =
    object : ComputationScope {
        override val coroutineContext: CoroutineContext =
            if (context[Computation] != null) context
            else context + Computation()
        override fun toString(): String = "ComputationScope(coroutineContext=${this.coroutineContext})"
    }