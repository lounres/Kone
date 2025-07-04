package dev.lounres.kone.automata

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public class SynchronousAutomaton<State, Transition, NoNextStateReason>(
    initialState: State,
    @PublishedApi
    internal val checkTransition: SynchronousAutomaton<State, Transition, NoNextStateReason>.(State, Transition) -> CheckResult<State, NoNextStateReason>,
    @PublishedApi
    internal val onTransition: SynchronousAutomaton<State, Transition, NoNextStateReason>.(previousState: State, transition: Transition, nextState: State) -> Unit = { _, _, _ -> },
) {
    public val state: State get() = _state
    @PublishedApi
    internal var _state: State = initialState
}

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
    onTransition(previousState, transition, nextState)
    _state = nextState
    return MovementMaybeResult.Success(previousState, transition, nextState)
}

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
    val previousState = _state
    val transition = transition(previousState)
    val nextState = checkTransition(previousState, transition).let {
        when (it) {
            is CheckResult.Failure<NoNextStateReason> -> return MovementResult.NoNextState(previousState, transition, it.reason)
            is CheckResult.Success<State> -> it.nextState
        }
    }
    onTransition(previousState, transition, nextState)
    _state = nextState
    return MovementResult.Success(previousState, transition, nextState)
}

public fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
> SynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybe(
    transition: TransitionOrReason<Transition, NoTransitionReason>
): MovementMaybeResult<State, Transition, NoTransitionReason, NoNextStateReason> =
    moveMaybe { transition }

public fun <
    State,
    Transition,
    NoNextStateReason,
> SynchronousAutomaton<State, Transition, NoNextStateReason>.move(
    transition: Transition
): MovementResult<State, Transition, NoNextStateReason> =
    move { transition }

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
    onTransition(previousState, transition, nextState)
    _state = nextState
    return MovementMaybeAndComputationResult.Success(previousState, transition, nextState, computation)
}

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
    onTransition(previousState, transition, nextState)
    _state = nextState
    return MovementAndComputationResult.Success(previousState, transition, nextState, computation)
}