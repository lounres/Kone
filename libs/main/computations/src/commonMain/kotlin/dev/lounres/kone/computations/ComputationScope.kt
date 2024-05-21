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
    ComputationScopeImpl(if (context[Computation] != null) context else context + CompletableComputation())

internal class ComputationScopeImpl(override val coroutineContext: CoroutineContext) : ComputationScope {
    override fun toString(): String = "ComputationScope(coroutineContext=${coroutineContext})"
}