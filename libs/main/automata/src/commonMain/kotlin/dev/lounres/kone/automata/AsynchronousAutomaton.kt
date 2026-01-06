/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.automata

import kotlinx.coroutines.sync.Mutex


public interface AsynchronousAutomaton<State, Transition, NoNextStateReason> {
    public val state: State
    @InternalAutomatonApi
    public suspend fun acquire()
    @InternalAutomatonApi
    public suspend fun checkTransition(previousState: State, transition: Transition): CheckResult<State, NoNextStateReason>
    @InternalAutomatonApi
    public suspend fun acceptNewState(previousState: State, transition: Transition, nextState: State)
    @InternalAutomatonApi
    public fun release()
}

@OptIn(InternalAutomatonApi::class)
public inline fun <State, Transition, NoNextStateReason> AsynchronousAutomaton(
    mutex: Mutex = Mutex(),
    initialState: State,
    crossinline checkTransition: suspend AsynchronousAutomaton<State, Transition, NoNextStateReason>.(previousState: State, transition: Transition) -> CheckResult<State, NoNextStateReason>,
    crossinline onTransition: suspend AsynchronousAutomaton<State, Transition, NoNextStateReason>.(previousState: State, transition: Transition, nextState: State) -> Unit = { _, _, _ -> },
) : AsynchronousAutomaton<State, Transition, NoNextStateReason> =
    object : AsynchronousAutomaton<State, Transition, NoNextStateReason> {
        override var state: State = initialState
        override suspend fun acquire() {
            mutex.lock()
        }
        override suspend fun checkTransition(previousState: State, transition: Transition): CheckResult<State, NoNextStateReason> =
            checkTransition(this, previousState, transition)
        override suspend fun acceptNewState(previousState: State, transition: Transition, nextState: State) {
            try {
                onTransition(this, previousState, transition, nextState)
            } finally {
                state = nextState
            }
        }
        override fun release() {
            mutex.unlock()
        }
    }

@OptIn(InternalAutomatonApi::class)
@IgnorableReturnValue
public suspend inline fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybe(
    transition: suspend (State) -> TransitionOrReason<Transition, NoTransitionReason>
): MovementMaybeResult<State, Transition, NoTransitionReason, NoNextStateReason> {
    acquire()
    try {
        val previousState = state
        val transition = when (val transitionOrReason = transition(previousState)) {
            is TransitionOrReason.Failure<NoTransitionReason> -> return MovementMaybeResult.NoTransition(previousState, transitionOrReason.reason)
            is TransitionOrReason.Success<Transition> -> transitionOrReason.transition
        }
        val nextState = when (val check = checkTransition(previousState, transition)) {
            is CheckResult.Failure<NoNextStateReason> -> return MovementMaybeResult.NoNextState(previousState, transition, check.reason)
            is CheckResult.Success<State> -> check.nextState
        }
        acceptNewState(previousState, transition, nextState)
        return MovementMaybeResult.Success(previousState, transition, nextState)
    } finally {
        release()
    }
}

@OptIn(InternalAutomatonApi::class)
@IgnorableReturnValue
public suspend inline fun <
    State,
    Transition,
    NoNextStateReason,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.move(
    transition: suspend (State) -> Transition
): MovementResult<State, Transition, NoNextStateReason> {
    acquire()
    try {
        val previousState = state
        val transition = transition(previousState)
        val nextState = when (val check = checkTransition(previousState, transition)) {
            is CheckResult.Failure<NoNextStateReason> -> return MovementResult.NoNextState(previousState, transition, check.reason)
            is CheckResult.Success<State> -> check.nextState
        }
        acceptNewState(previousState, transition, nextState)
        return MovementResult.Success(previousState, transition, nextState)
    } finally {
        release()
    }
}

@IgnorableReturnValue
public suspend fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybe(
    transition: TransitionOrReason<Transition, NoTransitionReason>
): MovementMaybeResult<State, Transition, NoTransitionReason, NoNextStateReason> =
    moveMaybe { transition }

@IgnorableReturnValue
public suspend fun <
    State,
    Transition,
    NoNextStateReason,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.move(
    transition: Transition
): MovementResult<State, Transition, NoNextStateReason> =
    move { transition }

@OptIn(InternalAutomatonApi::class)
@IgnorableReturnValue
public suspend inline fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
    Computation,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybeAndCompute(
    transition: suspend (State) -> TransitionOrReasonAndComputation<Transition, NoTransitionReason, Computation>
): MovementMaybeAndComputationResult<State, Transition, NoTransitionReason, NoNextStateReason, Computation> {
    acquire()
    try {
        val previousState = state
        val transitionResult = transition(previousState)
        val computation = transitionResult.computation
        val transition = when (transitionResult) {
            is TransitionOrReasonAndComputation.Failure<NoTransitionReason, Computation> ->
                return MovementMaybeAndComputationResult.NoTransition(previousState, transitionResult.reason, computation)
            is TransitionOrReasonAndComputation.Success<Transition, Computation> -> transitionResult.transition
        }
        val nextState = when (val check = checkTransition(previousState, transition)) {
            is CheckResult.Failure<NoNextStateReason> -> return MovementMaybeAndComputationResult.NoNextState(previousState, transition, check.reason, computation)
            is CheckResult.Success<State> -> check.nextState
        }
        acceptNewState(previousState, transition, nextState)
        return MovementMaybeAndComputationResult.Success(previousState, transition, nextState, computation)
    } finally {
        release()
    }
}

@OptIn(InternalAutomatonApi::class)
@IgnorableReturnValue
public suspend inline fun <
    State,
    Transition,
    NoNextStateReason,
    Computation,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.moveAndCompute(
    transition: suspend (State) -> TransitionAndComputation<Transition, Computation>
): MovementAndComputationResult<State, Transition, NoNextStateReason, Computation> {
    acquire()
    try {
        val previousState = state
        val transitionResult = transition(previousState)
        val computation = transitionResult.computation
        val transition = transitionResult.transition
        val nextState = when (val check = checkTransition(previousState, transition)) {
            is CheckResult.Failure<NoNextStateReason> -> return MovementAndComputationResult.NoNextState(previousState, transition, check.reason, computation)
            is CheckResult.Success<State> -> check.nextState
        }
        acceptNewState(previousState, transition, nextState)
        return MovementAndComputationResult.Success(previousState, transition, nextState, computation)
    } finally {
        release()
    }
}