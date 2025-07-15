package dev.lounres.kone.automata

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


public class BlockingAutomaton<State, Transition, NoNextStateReason>(
    initialState: State,
    @PublishedApi
    internal val checkTransition: BlockingAutomaton<State, Transition, NoNextStateReason>.(previousState: State, transition: Transition) -> CheckResult<State, NoNextStateReason>,
    @PublishedApi
    internal val onTransition: BlockingAutomaton<State, Transition, NoNextStateReason>.(previousState: State, transition: Transition, nextState: State) -> Unit = { _, _, _ -> },
) : SynchronizedObject() {
    public val state: State get() = _state
    @PublishedApi
    internal var _state: State = initialState
}

public inline fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
> BlockingAutomaton<State, Transition, NoNextStateReason>.moveMaybe(
    transition: (State) -> TransitionOrReason<Transition, NoTransitionReason>
): MovementMaybeResult<State, Transition, NoTransitionReason, NoNextStateReason> =
    synchronized(this) {
        val previousState = _state
        val transition = transition(previousState).let {
            when (it) {
                is TransitionOrReason.Failure<NoTransitionReason> -> return MovementMaybeResult.NoTransition(previousState, it.reason)
                is TransitionOrReason.Success<Transition> -> it.transition
            }
        }
        val nextState = checkTransition(previousState, transition).let {
            when (it) {
                is CheckResult.Failure<NoNextStateReason> -> return MovementMaybeResult.NoNextState(previousState, transition, it.reason)
                is CheckResult.Success<State> -> it.nextState
            }
        }
        try {
            onTransition(previousState, transition, nextState)
        } finally {
            _state = nextState
        }
        MovementMaybeResult.Success(previousState, transition, nextState)
    }

public inline fun <
    State,
    Transition,
    NoNextStateReason,
> BlockingAutomaton<State, Transition, NoNextStateReason>.move(
    transition: (State) -> Transition
): MovementResult<State, Transition, NoNextStateReason> =
    synchronized(this) {
        val previousState = _state
        val transition = transition(previousState)
        val nextState = checkTransition(previousState, transition).let {
            when (it) {
                is CheckResult.Failure<NoNextStateReason> -> return MovementResult.NoNextState(previousState, transition, it.reason)
                is CheckResult.Success<State> -> it.nextState
            }
        }
        try {
            onTransition(previousState, transition, nextState)
        } finally {
            _state = nextState
        }
        MovementResult.Success(previousState, transition, nextState)
    }

public fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
> BlockingAutomaton<State, Transition, NoNextStateReason>.moveMaybe(
    transition: TransitionOrReason<Transition, NoTransitionReason>
): MovementMaybeResult<State, Transition, NoTransitionReason, NoNextStateReason> = moveMaybe { transition }

public fun <
    State,
    Transition,
    NoNextStateReason,
> BlockingAutomaton<State, Transition, NoNextStateReason>.move(
    transition: Transition
): MovementResult<State, Transition, NoNextStateReason> = move { transition }

public inline fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
    Computation,
> BlockingAutomaton<State, Transition, NoNextStateReason>.moveMaybeAndCompute(
    transition: (State) -> TransitionOrReasonAndComputation<Transition, NoTransitionReason, Computation>
): MovementMaybeAndComputationResult<State, Transition, NoTransitionReason, NoNextStateReason, Computation> =
    synchronized(this) {
        val previousState = _state
        val transitionResult = transition(previousState)
        val computation = transitionResult.computation
        val transition = transitionResult.let {
            when (it) {
                is TransitionOrReasonAndComputation.Failure<NoTransitionReason, Computation> ->
                    return MovementMaybeAndComputationResult.NoTransition(previousState, it.reason, computation)
                is TransitionOrReasonAndComputation.Success<Transition, Computation> -> it.transition
            }
        }
        val nextState = checkTransition(previousState, transition).let {
            when (it) {
                is CheckResult.Failure<NoNextStateReason> -> return MovementMaybeAndComputationResult.NoNextState(previousState, transition, it.reason, computation)
                is CheckResult.Success<State> -> it.nextState
            }
        }
        try {
            onTransition(previousState, transition, nextState)
        } finally {
            _state = nextState
        }
        MovementMaybeAndComputationResult.Success(previousState, transition, nextState, computation)
    }

public inline fun <
    State,
    Transition,
    NoNextStateReason,
    Computation,
> BlockingAutomaton<State, Transition, NoNextStateReason>.moveAndCompute(
    transition: (State) -> TransitionAndComputation<Transition, Computation>
): MovementAndComputationResult<State, Transition, NoNextStateReason, Computation> =
    synchronized(this) {
        val previousState = _state
        val transitionResult = transition(previousState)
        val computation = transitionResult.computation
        val transition = transitionResult.transition
        val nextState = checkTransition(previousState, transition).let {
            when (it) {
                is CheckResult.Failure<NoNextStateReason> -> return MovementAndComputationResult.NoNextState(previousState, transition, it.reason, computation)
                is CheckResult.Success<State> -> it.nextState
            }
        }
        try {
            onTransition(previousState, transition, nextState)
        } finally {
            _state = nextState
        }
        MovementAndComputationResult.Success(previousState, transition, nextState, computation)
    }