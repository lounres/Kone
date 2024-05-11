/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


public interface ComputationJob: Job {
    public val computation: Computation
}

public fun CoroutineScope.launchComputation(
    context: CoroutineContext = EmptyCoroutineContext,
    start: ComputationStart = ComputationStart.ACTIVE,
    block: suspend ComputationScope.() -> Unit,
): ComputationJob {
    TODO("Not yet implemented")
}