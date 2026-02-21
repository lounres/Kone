package dev.lounres.kone.collections.progression

import dev.lounres.kone.algebraic.Monoid
import dev.lounres.kone.algebraic.Semiring
import dev.lounres.kone.algebraic.Sign
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.ClosedRange
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.OpenEndRange
import dev.lounres.kone.relations.geq
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.leq
import dev.lounres.kone.relations.lt


public infix fun <Number> ClosedRange<Number>.withStep(step: Number): ClosedRangeArithmeticProgression<Number> =
    ClosedRangeArithmeticProgression(
        startInclusive = this.startInclusive,
        endInclusive = this.endInclusive,
        step = step,
    )
public infix fun <Number> OpenEndRange<Number>.withStep(step: Number): OpenEndRangeArithmeticProgression<Number> =
    OpenEndRangeArithmeticProgression(
        startInclusive = this.startInclusive,
        endExclusive = this.endExclusive,
        step = step,
    )

private class ClosedRangeArithmeticProgressionIterator<Number>(
    private var currentNumber: Number,
    private val endInclusive: Number,
    private val step: Number,
    private val monoid: Monoid<Number>,
    private val order: Order<Number>,
) : KoneIterator<Number> {
    private val isItIncreasing = context(monoid, order) {
        when (step.sign()) {
            Sign.Negative -> false
            Sign.Zero -> throw IllegalArgumentException("Cannot iterate over closed range arithmetic progression with zero step.")
            Sign.Positive -> true
        }
    }
    
    override fun hasNext(): Boolean =
        order { if (isItIncreasing) currentNumber leq endInclusive else currentNumber geq endInclusive }
    override fun getNext(): Number =
        if (hasNext()) currentNumber else noNextElementInIteratorException()
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        monoid { currentNumber += step }
    }
}

context(semiring: Semiring<Number>, order: Order<Number>)
public operator fun <Number> ClosedRange<Number>.iterator(): KoneIterator<Number> =
    ClosedRangeArithmeticProgressionIterator(
        currentNumber = this.startInclusive,
        endInclusive = this.endInclusive,
        step = semiring.one,
        monoid = semiring,
        order = order,
    )

context(monoid: Monoid<Number>, order: Order<Number>)
public operator fun <Number> ClosedRangeArithmeticProgression<Number>.iterator(): KoneIterator<Number> =
    ClosedRangeArithmeticProgressionIterator(
        currentNumber = this.startInclusive,
        endInclusive = this.endInclusive,
        step = this.step,
        monoid = monoid,
        order = order,
    )

private class RightOpenRangeArithmeticProgressionIterator<Number>(
    private var currentNumber: Number,
    private val endExclusive: Number,
    private val step: Number,
    private val monoid: Monoid<Number>,
    private val order: Order<Number>,
) : KoneIterator<Number> {
    private val isItIncreasing = context(monoid, order) {
        when (step.sign()) {
            Sign.Negative -> false
            Sign.Zero -> throw IllegalArgumentException("Cannot iterate over closed range arithmetic progression with zero step.")
            Sign.Positive -> true
        }
    }
    
    override fun hasNext(): Boolean =
        order { if (isItIncreasing) currentNumber lt endExclusive else currentNumber gt endExclusive }
    override fun getNext(): Number =
        if (hasNext()) currentNumber else noNextElementInIteratorException()
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        monoid { currentNumber += step }
    }
}

context(semiring: Semiring<Number>, order: Order<Number>)
public operator fun <Number> OpenEndRange<Number>.iterator(): KoneIterator<Number> =
    RightOpenRangeArithmeticProgressionIterator(
        currentNumber = this.startInclusive,
        endExclusive = this.endExclusive,
        step = semiring.one,
        monoid = semiring,
        order = order,
    )

context(monoid: Monoid<Number>, order: Order<Number>)
public operator fun <Number> OpenEndRangeArithmeticProgression<Number>.iterator(): KoneIterator<Number> =
    RightOpenRangeArithmeticProgressionIterator(
        currentNumber = this.startInclusive,
        endExclusive = this.endExclusive,
        step = this.step,
        monoid = monoid,
        order = order,
    )

context(_: Semiring<Number>, _: Order<Number>)
public fun <Number> ClosedRange<Number>.asKoneSequence(): KoneSequence<Number> = KoneSequence { iterator() }

context(_: Monoid<Number>, _: Order<Number>)
public fun <Number> ClosedRangeArithmeticProgression<Number>.asKoneSequence(): KoneSequence<Number> = KoneSequence { iterator() }

context(_: Semiring<Number>, _: Order<Number>)
public fun <Number> OpenEndRange<Number>.asKoneSequence(): KoneSequence<Number> = KoneSequence { iterator() }

context(_: Monoid<Number>, _: Order<Number>)
public fun <Number> OpenEndRangeArithmeticProgression<Number>.asKoneSequence(): KoneSequence<Number> = KoneSequence { iterator() }