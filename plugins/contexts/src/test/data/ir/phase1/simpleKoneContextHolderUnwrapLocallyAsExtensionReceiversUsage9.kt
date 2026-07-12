// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


fun interface Plus<in Left, in Right, out Result> : KoneContext {
    operator fun Left.plus(other: Right): Result
}

fun interface Minus<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.minus(other: Right): Result
}

interface Semigroup<Number> : KoneContextHolder {
    @KoneContextHolderInclude
    val numberPlusNumber: Plus<Number, Number, Number>
}

interface Group<Number> : Semigroup<Number> {
    @KoneContextHolderInclude
    val numberMinusNumber: Minus<Number, Number, Number>
}

interface Semiring<Number> : Semigroup<Number> {
    val one: Number
}

interface Ring<Number> : Semiring<Number>, Group<Number> {
    @KoneContextHolderInclude
    public val numberPlusLong: Plus<Number, Long, Number>
}

public inline fun <Number> rightAddMultipliedByDoubling(base: Number, arg: Number, multiplier: Int, additionOp: (Number, Number) -> Number, rightSubtractionOp: (Number, Number) -> Number): Number = TODO()

context(ring: Ring<Number>)
public infix fun <Number> Number.doublingPlus(other: Int): Number {
    KoneContextHolder.unwrapLocallyAsExtensionReceivers(ring)
    return rightAddMultipliedByDoubling<Number>(this@doublingPlus, ring.one, other, { left, right -> left + right }, { left, right -> left - right })
}