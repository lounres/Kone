/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


public interface CompletableComputation: Computation {
    // TODO: Think if it is OK to call them in `Paused`/`Pausing` state.
    public fun complete(): Boolean
    public fun completeExceptionally(exception: Throwable): Boolean
}

public fun CompletableComputation(parent: Computation? = null): CompletableComputation {
    TODO("Not yet implemented")
}

internal class CompletableComputationImpl: CompletableComputation {
    override val key: CoroutineContext.Key<*> get() = Computation.Key

    override val parent: Computation? get() = null // TODO: Implement parent-child relations
    override val children: Sequence<Computation> get() = emptySequence() // TODO: Implement parent-child relations

    private val _state: AtomicRef<State> = atomic(State.Paused)

    internal sealed interface State {
        data class Pausing(val pausingJob: CompletableJob = Job()) : State
        data object Paused : State
        data class Activating(val activatingJob: CompletableJob = Job()) : State
        data object Active : State
        
        data object PausingCompletion : State
        data object PausedCompletion : State
        data object ActivatingCompletion : State
        data object Completion: State
        
        data object Completed : State
        
        data object Cancelling : State
        data object Cancelled : State
    }

    override val isActive: Boolean
        get() = _state.value.let { it is State.Pausing || it === State.Active || it === State.Completing }
    override val isPaused: Boolean
        get() = _state.value.let { it === State.Paused || it === State.Activating }
    override val isCompleted: Boolean
        get() = _state.value.let { it === State.Completed || it === State.Cancelled }
    override val isCancelled: Boolean
        get() =  _state.value.let { it === State.Cancelling || it === State.Cancelled }

    private val lifecycleJob = Job()

    override fun resume() {
        TODO("Not yet implemented")
    }
    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun cancel(cause: CancellationException?) {
        while (true) {
            val currentState = _state.value
            when (currentState) {
                State.Completed,
                State.Cancelling,
                State.Cancelled, -> return

                is State.Pausing,
                State.Paused,
                State.Activating,
                State.Active,
                State.Completing, -> {}
            }
            if (!_state.compareAndSet(currentState, State.Cancelling)) continue

            lifecycleJob.cancel(
                if (cause != null) kotlinx.coroutines.CancellationException(cause.message, cause)
                else null
            )

            TODO()

            return
        }
    }
    override fun complete(): Boolean {
        TODO("Not yet implemented")
    }
    override fun completeExceptionally(exception: Throwable): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun joinUntilResumed() {
        TODO("Not yet implemented")
    }
    override suspend fun joinUntilPaused() {
        TODO("Not yet implemented")
    }

    override suspend fun resumeJoining() {
        TODO("Not yet implemented")
    }
    override suspend fun pauseJoining() {
        TODO("Not yet implemented")
    }

    override suspend fun join() {
        lifecycleJob.join()
    }
}