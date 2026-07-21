// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


fun interface Plus<in Left, in Right, out Result> : KoneContext {
    operator fun Left.plus(other: Right): Result
}

context(plus: Plus<Left, Right, Result>)
operator fun <Left, Right, Result> Left.plus(other: Right): Result = with(plus) { this@plus + other }

fun interface Minus<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.minus(other: Right): Result
}

context(minus: Minus<Left, Right, Result>)
operator fun <Left, Right, Result> Left.minus(other: Right): Result = with(minus) { this@minus - other }

interface Semigroup<Number> : KoneContext {
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
    val numberPlusLong: Plus<Number, Long, Number>
}

inline fun <Number> rightAddMultipliedByDoubling(base: Number, arg: Number, multiplier: Int, additionOp: (Number, Number) -> Number, rightSubtractionOp: (Number, Number) -> Number): Number = TODO()

context(ring: Ring<Number>)
fun <Number> Number.double(): Number {
    KoneContext.unwrap(ring)
    return this + this
}

context(ring: Ring<Number>)
fun <Number> test(arg: Number) {
    KoneContext.unwrap(ring)
    arg.double()
}