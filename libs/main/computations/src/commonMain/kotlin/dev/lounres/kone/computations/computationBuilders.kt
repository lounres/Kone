/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalTypeInference::class)

package dev.lounres.kone.computations

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.experimental.ExperimentalTypeInference


public fun runBlockingComputation(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend ComputationScope.() -> Unit
): Computation {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    TODO("Not yet implemented")
}

public fun ComputationScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: ComputationStart = ComputationStart.ACTIVE,
    onResume: suspend ComputationScope.() -> Unit = {},
    onPause: suspend ComputationScope.() -> Unit = {},
    logic: suspend ComputationScope.() -> Unit,
): Computation {
    TODO("Not yet implemented")
}

public fun <R> ComputationScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: ComputationStart = ComputationStart.ACTIVE,
    onResume: suspend ComputationScope.() -> Unit = {},
    onPause: suspend ComputationScope.() -> Unit = {},
    logic: suspend ComputationScope.() -> R,
): ResultComputation<R> {
    TODO("Not yet implemented")
}

public fun <R> ComputationScope.channelComputation(
    context: CoroutineContext = EmptyCoroutineContext,
    start: ComputationStart = ComputationStart.ACTIVE,
    @BuilderInference onResume: suspend ResultProducingComputationScope<R>.() -> Unit = {},
    @BuilderInference onPause: suspend ResultProducingComputationScope<R>.() -> Unit = {},
    @BuilderInference logic: suspend ResultProducingComputationScope<R>.() -> Unit,
): ChannelComputation<R> {
    TODO("Not yet implemented")
}

public fun <R> ComputationScope.flowComputation(
    context: CoroutineContext = EmptyCoroutineContext,
    start: ComputationStart = ComputationStart.ACTIVE,
    @BuilderInference onResume: suspend ResultProducingComputationScope<R>.() -> Unit = {},
    @BuilderInference onPause: suspend ResultProducingComputationScope<R>.() -> Unit = {},
    @BuilderInference logic: suspend ResultProducingComputationScope<R>.() -> Unit,
): FlowComputation<R> {
    TODO("Not yet implemented")
}