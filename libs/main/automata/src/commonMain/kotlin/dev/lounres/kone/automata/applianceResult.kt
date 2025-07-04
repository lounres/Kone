package dev.lounres.kone.automata


public sealed interface CheckResult<out State, out NoNextStateReason> {
    public data class Failure<out NoNextStateReason>(public val reason: NoNextStateReason) : CheckResult<Nothing, NoNextStateReason>
    public data class Success<out State>(public val nextState: State) : CheckResult<State, Nothing>
}

public sealed interface TransitionOrReason<out Transition, out NoTransitionReason> {
    public data class Failure<out NoTransitionReason>(public val reason: NoTransitionReason) : TransitionOrReason<Nothing, NoTransitionReason>
    public data class Success<out Transition>(public val transition: Transition) : TransitionOrReason<Transition, Nothing>
}

public data class TransitionAndComputation<out Transition, out Computation>(
    public val transition: Transition,
    public val computation: Computation,
)

public sealed interface TransitionOrReasonAndComputation<out Transition, out Reason, out Computation> {
    public val computation: Computation
    public data class Failure<out Reason, out Computation>(
        public val reason: Reason,
        override val computation: Computation,
    ) : TransitionOrReasonAndComputation<Nothing, Reason, Computation>
    public data class Success<out Transition, out Computation>(
        public val transition: Transition,
        override val computation: Computation,
    ) : TransitionOrReasonAndComputation<Transition, Nothing, Computation>
}

public sealed interface MovementMaybeResult<out State, out Transition, out NoTransitionReason, out NoNextStateReason> {
    public val previousState: State
    public data class NoTransition<out State, out NoTransitionReason>(
        override val previousState: State,
        public val noTransitionReason: NoTransitionReason,
    ) : MovementMaybeResult<State, Nothing, NoTransitionReason, Nothing>
    public data class NoNextState<out State, out Transition, out NoNextStateReason>(
        override val previousState: State,
        public val transition: Transition,
        public val noNextStateReason: NoNextStateReason,
    ) : MovementMaybeResult<State, Transition, Nothing, NoNextStateReason>
    public data class Success<out State, out Transition>(
        override val previousState: State,
        public val transition: Transition,
        public val nextState: State,
    ) : MovementMaybeResult<State, Transition, Nothing, Nothing>
}

public sealed interface MovementResult<out State, out Transition, out NoNextStateReason> {
    public val previousState: State
    public val transition: Transition
    public data class NoNextState<out State, out Transition, out NoNextStateReason>(
        override val previousState: State,
        override val transition: Transition,
        public val noNextStateReason: NoNextStateReason,
    ) : MovementResult<State, Transition, NoNextStateReason>
    public data class Success<out State, out Transition>(
        override val previousState: State,
        override val transition: Transition,
        public val nextState: State,
    ) : MovementResult<State, Transition, Nothing>
}

public sealed interface ConcurrentMovementMaybeResult<out State, out Transition, out NoTransitionReason, out NoNextStateReason> {
    public data object Blocked : ConcurrentMovementMaybeResult<Nothing, Nothing, Nothing, Nothing>
    public data class NoTransition<out State, out NoTransitionReason>(
        public val previousState: State,
        public val noTransitionReason: NoTransitionReason,
    ) : ConcurrentMovementMaybeResult<State, Nothing, NoTransitionReason, Nothing>
    public data class NoNextState<out State, out Transition, out NoNextStateReason>(
        public val previousState: State,
        public val transition: Transition,
        public val noNextStateReason: NoNextStateReason,
    ) : ConcurrentMovementMaybeResult<State, Transition, Nothing, NoNextStateReason>
    public data class Success<out State, out Transition>(
        public val previousState: State,
        public val transition: Transition,
        public val nextState: State,
    ) : ConcurrentMovementMaybeResult<State, Transition, Nothing, Nothing>
}

public sealed interface ConcurrentMovementResult<out State, out Transition, out NoNextStateReason> {
    public data object Blocked : ConcurrentMovementResult<Nothing, Nothing, Nothing>
    public data class NoNextState<out State, out Transition, out NoNextStateReason>(
        public val previousState: State,
        public val transition: Transition,
        public val noNextStateReason: NoNextStateReason,
    ) : ConcurrentMovementResult<State, Transition, NoNextStateReason>
    public data class Success<out State, out Transition>(
        public val previousState: State,
        public val transition: Transition,
        public val nextState: State,
    ) : ConcurrentMovementResult<State, Transition, Nothing>
}

public sealed interface MovementMaybeAndComputationResult<out State, out Transition, out NoTransitionReason, out NoNextStateReason, out Computation> {
    public val previousState: State
    public val computation: Computation
    public data class NoTransition<out State, out NoTransitionReason, out Computation>(
        override val previousState: State,
        public val noTransitionReason: NoTransitionReason,
        override val computation: Computation,
    ) : MovementMaybeAndComputationResult<State, Nothing, NoTransitionReason, Nothing, Computation>
    public data class NoNextState<out State, out Transition, out NoNextStateReason, out Computation>(
        override val previousState: State,
        public val transition: Transition,
        public val noNextStateReason: NoNextStateReason,
        override val computation: Computation,
    ) : MovementMaybeAndComputationResult<State, Transition, Nothing, NoNextStateReason, Computation>
    public data class Success<out State, out Transition, out Computation>(
        override val previousState: State,
        public val transition: Transition,
        public val nextState: State,
        override val computation: Computation,
    ) : MovementMaybeAndComputationResult<State, Transition, Nothing, Nothing, Computation>
}

public sealed interface MovementAndComputationResult<out State, out Transition, out NoNextStateReason, out Computation> {
    public val previousState: State
    public val transition: Transition
    public val computation: Computation
    public data class NoNextState<out State, out Transition, out NoNextStateReason, out Computation>(
        override val previousState: State,
        override val transition: Transition,
        public val noNextStateReason: NoNextStateReason,
        override val computation: Computation,
    ) : MovementAndComputationResult<State, Transition, NoNextStateReason, Computation>
    public data class Success<out State, out Transition, out Computation>(
        override val previousState: State,
        override val transition: Transition,
        public val nextState: State,
        override val computation: Computation,
    ) : MovementAndComputationResult<State, Transition, Nothing, Computation>
}