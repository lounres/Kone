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
        
        data class PausingCompletion(val pausingJob: CompletableJob = Job()) : State
        data object PausedCompletion : State
        data class ActivatingCompletion(val activatingJob: CompletableJob = Job()) : State
        data object Completion: State
        
        data object Completed : State
        
        data object Cancelling : State
        data object Cancelled : State
    }

    override val isActive: Boolean
        get() = _state.value.let { it is State.Pausing || it === State.Active || it === State.Completion }
    override val isPaused: Boolean
        get() = _state.value.let { it === State.Paused || it is State.Activating }
    override val isCompleted: Boolean
        get() = _state.value.let { it === State.Completed || it === State.Cancelled }
    override val isCancelled: Boolean
        get() =  _state.value.let { it === State.Cancelling || it === State.Cancelled }

    private val lifecycleJob = Job()

    override fun resume() {
//        while (true) {
//            val currentState = _state.value
//            val newState = when (currentState) {
//                is State.Pausing -> return
//                State.Paused -> State.Active
//                is State.Activating -> currentState
//                State.Active -> return
//
//                is State.PausingCompletion -> return
//                State.PausedCompletion -> State.Cancelling
//                is State.ActivatingCompletion -> State.Cancelling
//                State.Completion -> State.Cancelling
//
//                State.Completed -> return
//
//                State.Cancelled -> return
//                State.Cancelling -> return
//            }
//            if (!_state.compareAndSet(currentState, newState)) continue
//
//            when (currentState) {
//                is State.Pausing -> error("Reached unreachable code")
//                State.Paused -> {}
//                is State.Activating -> currentState.activatingJob.cancel(cancellationException)
//                State.Active -> error("Reached unreachable code")
//
//                is State.PausingCompletion -> currentState.pausingJob.cancel(cancellationException)
//                State.PausedCompletion -> {}
//                is State.ActivatingCompletion -> currentState.activatingJob.cancel(cancellationException)
//                State.Completion -> {}
//
//                State.Completed -> error("Reached unreachable code")
//
//                State.Cancelled -> error("Reached unreachable code")
//                State.Cancelling -> error("Reached unreachable code")
//            }
//
//            lifecycleJob.cancel(cancellationException)
//
//            return
//        }
    }
    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun cancel(cause: CancellationException?) {
        while (true) {
            val currentState = _state.value
            val newState = when (currentState) {
                is State.Pausing -> State.Cancelling
                State.Paused -> State.Cancelling
                is State.Activating -> State.Cancelling
                State.Active -> State.Cancelling
                
                is State.PausingCompletion -> State.Cancelling
                State.PausedCompletion -> State.Cancelling
                is State.ActivatingCompletion -> State.Cancelling
                State.Completion -> State.Cancelling
                
                State.Completed -> return
                
                State.Cancelled -> return
                State.Cancelling -> return
            }
            if (!_state.compareAndSet(currentState, newState)) continue
            
            val cancellationException =
                if (cause != null) kotlinx.coroutines.CancellationException(cause.message, cause) else null

            when (currentState) {
                is State.Pausing -> currentState.pausingJob.cancel(cancellationException)
                State.Paused -> {}
                is State.Activating -> currentState.activatingJob.cancel(cancellationException)
                State.Active -> {}
                
                is State.PausingCompletion -> currentState.pausingJob.cancel(cancellationException)
                State.PausedCompletion -> {}
                is State.ActivatingCompletion -> currentState.activatingJob.cancel(cancellationException)
                State.Completion -> {}
                
                State.Completed -> error("Reached unreachable code")
                
                State.Cancelled -> error("Reached unreachable code")
                State.Cancelling -> error("Reached unreachable code")
            }
            
            lifecycleJob.cancel(cancellationException)
            
            return
        }
    }
    
    override fun complete(): Boolean {
        while (true) { // TODO: Include possible children
            val currentState = _state.value
            val newState = when (currentState) {
                is State.Pausing -> State.Completed
                State.Paused -> State.Completed
                is State.Activating -> State.Completed
                State.Active -> State.Completed
                
                is State.PausingCompletion -> return false
                State.PausedCompletion -> return false
                is State.ActivatingCompletion -> return false
                State.Completion -> return false
                
                State.Completed -> return false
                
                State.Cancelled -> return false
                State.Cancelling -> return false
            }
            if (!_state.compareAndSet(currentState, newState)) continue
            
            when (currentState) {
                is State.Pausing -> currentState.pausingJob.cancel()
                State.Paused -> {}
                is State.Activating -> currentState.activatingJob.cancel()
                State.Active -> {}
                
                is State.PausingCompletion -> error("Reached unreachable code")
                State.PausedCompletion -> error("Reached unreachable code")
                is State.ActivatingCompletion -> error("Reached unreachable code")
                State.Completion -> error("Reached unreachable code")
                
                State.Completed -> error("Reached unreachable code")
                
                State.Cancelled -> error("Reached unreachable code")
                State.Cancelling -> error("Reached unreachable code")
            }
            
            lifecycleJob.complete()
            
            return true
        }
    }
    override fun completeExceptionally(exception: Throwable): Boolean {
        while (true) {
            val currentState = _state.value
            val newState = when (currentState) {
                is State.Pausing -> State.Cancelling
                State.Paused -> State.Cancelling
                is State.Activating -> State.Cancelling
                State.Active -> State.Cancelling
                
                is State.PausingCompletion -> State.Cancelling
                State.PausedCompletion -> State.Cancelling
                is State.ActivatingCompletion -> State.Cancelling
                State.Completion -> State.Cancelling
                
                State.Completed -> return false
                
                State.Cancelled -> return false
                State.Cancelling -> return false
            }
            if (!_state.compareAndSet(currentState, newState)) continue
            
            val cancellationException = kotlinx.coroutines.CancellationException(exception.message, exception)
            
            when (currentState) {
                is State.Pausing -> currentState.pausingJob.cancel(cancellationException)
                State.Paused -> {}
                is State.Activating -> currentState.activatingJob.cancel(cancellationException)
                State.Active -> {}
                
                is State.PausingCompletion -> currentState.pausingJob.cancel(cancellationException)
                State.PausedCompletion -> {}
                is State.ActivatingCompletion -> currentState.activatingJob.cancel(cancellationException)
                State.Completion -> {}
                
                State.Completed -> error("Reached unreachable code")
                
                State.Cancelled -> error("Reached unreachable code")
                State.Cancelling -> error("Reached unreachable code")
            }
            
            lifecycleJob.cancel(cancellationException)
            
            return true
        }
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