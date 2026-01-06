/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.automata

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface SynchronousAutomaton<State, Transition, NoNextStateReason> {
    public val state: State
    @InternalAutomatonApi
    public fun checkTransition(previousState: State, transition: Transition): CheckResult<State, NoNextStateReason>
    @InternalAutomatonApi
    public fun acceptNewState(previousState: State, transition: Transition, nextState: State)
}

@OptIn(InternalAutomatonApi::class)
public inline fun <State, Transition, NoNextStateReason> SynchronousAutomaton(
    initialState: State,
    crossinline checkTransition: SynchronousAutomaton<State, Transition, NoNextStateReason>.(previousState: State, transition: Transition) -> CheckResult<State, NoNextStateReason>,
    crossinline onTransition: SynchronousAutomaton<State, Transition, NoNextStateReason>.(previousState: State, transition: Transition, nextState: State) -> Unit = { _, _, _ -> },
) : SynchronousAutomaton<State, Transition, NoNextStateReason> =
    object : SynchronousAutomaton<State, Transition, NoNextStateReason> {
        override var state: State = initialState
        override fun checkTransition(previousState: State, transition: Transition): CheckResult<State, NoNextStateReason> =
            checkTransition(this, previousState, transition)
        override fun acceptNewState(previousState: State, transition: Transition, nextState: State) {
            try {
                onTransition(this, previousState, transition, nextState)
            } finally {
                state = nextState
            }
        }
    }

@OptIn(InternalAutomatonApi::class)
@IgnorableReturnValue
public inline fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
> SynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybe(
    transition: (State) -> TransitionOrReason<Transition, NoTransitionReason>
): MovementMaybeResult<State, Transition, NoTransitionReason, NoNextStateReason> {
    contract {
        callsInPlace(transition, InvocationKind.EXACTLY_ONCE)
    }
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
}

@OptIn(InternalAutomatonApi::class)
@IgnorableReturnValue
public inline fun <
    State,
    Transition,
    NoNextStateReason,
> SynchronousAutomaton<State, Transition, NoNextStateReason>.move(
    transition: (State) -> Transition
): MovementResult<State, Transition, NoNextStateReason> {
    contract {
        callsInPlace(transition, InvocationKind.EXACTLY_ONCE)
    }
    val previousState = state
    val transition = transition(previousState)
    val nextState = when (val check = checkTransition(previousState, transition)) {
        is CheckResult.Failure<NoNextStateReason> -> return MovementResult.NoNextState(previousState, transition, check.reason)
        is CheckResult.Success<State> -> check.nextState
    }
    acceptNewState(previousState, transition, nextState)
    return MovementResult.Success(previousState, transition, nextState)
}

@IgnorableReturnValue
public fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
> SynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybe(
    transition: TransitionOrReason<Transition, NoTransitionReason>
): MovementMaybeResult<State, Transition, NoTransitionReason, NoNextStateReason> =
    moveMaybe { transition }

@IgnorableReturnValue
public fun <
    State,
    Transition,
    NoNextStateReason,
> SynchronousAutomaton<State, Transition, NoNextStateReason>.move(
    transition: Transition
): MovementResult<State, Transition, NoNextStateReason> =
    move { transition }

@OptIn(InternalAutomatonApi::class)
@IgnorableReturnValue
public inline fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
    Computation,
> SynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybeAndCompute(
    transition: (State) -> TransitionOrReasonAndComputation<Transition, NoTransitionReason, Computation>
): MovementMaybeAndComputationResult<State, Transition, NoTransitionReason, NoNextStateReason, Computation> {
    contract {
        callsInPlace(transition, InvocationKind.EXACTLY_ONCE)
    }
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
}

@OptIn(InternalAutomatonApi::class)
@IgnorableReturnValue
public inline fun <
    State,
    Transition,
    NoNextStateReason,
    Computation,
> SynchronousAutomaton<State, Transition, NoNextStateReason>.moveAndCompute(
    transition: (State) -> TransitionAndComputation<Transition, Computation>
): MovementAndComputationResult<State, Transition, NoNextStateReason, Computation> {
    contract {
        callsInPlace(transition, InvocationKind.EXACTLY_ONCE)
    }
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
}