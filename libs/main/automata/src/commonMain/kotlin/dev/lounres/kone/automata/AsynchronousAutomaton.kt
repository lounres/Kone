package dev.lounres.kone.automata

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


public class AsynchronousAutomaton<State, Transition, NoNextStateReason>(
    @PublishedApi
    internal val mutex: Mutex = Mutex(),
    initialState: State,
    @PublishedApi
    internal val checkTransition: suspend AsynchronousAutomaton<State, Transition, NoNextStateReason>.(State, Transition) -> CheckResult<State, NoNextStateReason>,
    @PublishedApi
    internal val onTransition: suspend AsynchronousAutomaton<State, Transition, NoNextStateReason>.(previousState: State, transition: Transition, nextState: State) -> Unit = { _, _, _ -> },
) {
    public val state: State get() = _state
    @PublishedApi
    internal var _state: State = initialState
}

public suspend inline fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybe(
    transition: suspend (State) -> TransitionOrReason<Transition, NoTransitionReason>
): MovementMaybeResult<State, Transition, NoTransitionReason, NoNextStateReason> =
    mutex.withLock {
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
        MovementMaybeResult.Success(previousState, transition, nextState)
    }

public suspend inline fun <
    State,
    Transition,
    NoNextStateReason,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.move(
    transition: suspend (State) -> Transition
): MovementResult<State, Transition, NoNextStateReason> =
    mutex.withLock {
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
        MovementResult.Success(previousState, transition, nextState)
    }

public suspend fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybe(
    transition: TransitionOrReason<Transition, NoTransitionReason>
): MovementMaybeResult<State, Transition, NoTransitionReason, NoNextStateReason> =
    moveMaybe { transition }

public suspend fun <
    State,
    Transition,
    NoNextStateReason,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.move(
    transition: Transition
): MovementResult<State, Transition, NoNextStateReason> =
    move { transition }

public suspend inline fun <
    State,
    Transition,
    NoTransitionReason,
    NoNextStateReason,
    Computation,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.moveMaybeAndCompute(
    transition: suspend (State) -> TransitionOrReasonAndComputation<Transition, NoTransitionReason, Computation>
): MovementMaybeAndComputationResult<State, Transition, NoTransitionReason, NoNextStateReason, Computation> =
    mutex.withLock {
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
        MovementMaybeAndComputationResult.Success(previousState, transition, nextState, computation)
    }

public suspend inline fun <
    State,
    Transition,
    NoNextStateReason,
    Computation,
> AsynchronousAutomaton<State, Transition, NoNextStateReason>.moveAndCompute(
    transition: suspend (State) -> TransitionAndComputation<Transition, Computation>
): MovementAndComputationResult<State, Transition, NoNextStateReason, Computation> =
    mutex.withLock {
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
        MovementAndComputationResult.Success(previousState, transition, nextState, computation)
    }