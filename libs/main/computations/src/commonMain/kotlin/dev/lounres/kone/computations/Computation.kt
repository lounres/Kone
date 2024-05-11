/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations

import kotlin.coroutines.CoroutineContext


public interface Computation: CoroutineContext.Element {
    public companion object Key : CoroutineContext.Key<Computation>

    public val parent: Computation?
    public val children: Sequence<Computation>

    public val isActive: Boolean
    public val isCompleted: Boolean
    public val isPaused: Boolean
    public val isCancelled: Boolean

    public fun resume()
    public fun pause()
    public fun cancel(cause: CancellationException? = null)

    public suspend fun joinUntilResumed()
    public suspend fun joinUntilPaused()

    // TODO: Not sure that the operation should not be a part of `Computation` interface
    //  because in implementation
    //  ```
    //  resume()
    //  joinUntilResumed()
    //  ```
    //  between the operations several state changes could happen.
    public suspend fun resumeJoining()
    public suspend fun pauseJoining()

    public suspend fun join()

    // Possible internal methods like in `Job`
//    @InternalComputationsApi
//    public fun attachChild(child: ???): ???
//    @InternalComputationsApi
//    public fun getCancellationException(): ???
//    @InternalComputationsApi
//    public fun invokeOnCompletion(handler: ???): ???
//    @InternalComputationsApi
//    public fun invokeOnCompletion(onCancelling: Boolean = false, invokeImmediately: Boolean = true, handler: ???): ???
}

public fun Computation(parent: Computation? = null): Computation {
    TODO("Not yet implemented")
}