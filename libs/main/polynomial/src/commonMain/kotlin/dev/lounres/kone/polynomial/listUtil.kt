/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.rem
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.algebraic.zero
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.iterables.isEmpty
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.build
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.utils.count
import dev.lounres.kone.collections.utils.filter
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.mapIndexedTo
import dev.lounres.kone.relations.Order
import dev.lounres.kone.repeat


/**
 * Creates a [ListPolynomialSpace] over a received ring.
 */
public inline val <Number> Ring<Number>.listPolynomialSpace: ListPolynomialSpace<Number>
    get() = ListPolynomialSpace(this)

/**
 * Creates a [ListPolynomialSpaceOverField] over a received field.
 */
public inline val <Number> Field<Number>.listPolynomialSpace: ListPolynomialSpaceOverField<Number>
    get() = ListPolynomialSpaceOverField(this)

public inline val <Number, NumberContext: Ring<Number>> NumberContext.listPolynomialSpaceScope: PolynomialSpaceScope<Number, ListPolynomial<Number>, NumberContext, ListPolynomialSpace<Number>>
    get() = PolynomialSpaceScope(numberContext = this, polynomialSpace = this.listPolynomialSpace)

public inline val <Number, NumberContext: Field<Number>> NumberContext.listPolynomialSpaceScope: PolynomialSpaceScope<Number, ListPolynomial<Number>, NumberContext, ListPolynomialSpaceOverField<Number>>
    get() = PolynomialSpaceScope(numberContext = this, polynomialSpace = this.listPolynomialSpace)

/**
 * Creates a [ListRationalFunctionSpace] over a received polynomial space.
 */
public inline val <C> ListPolynomialSpace<C>.listRationalFunctionSpace: ListRationalFunctionSpace<C>
    get() = ListRationalFunctionSpace(this)

public inline val <Number, NumberContext: Ring<Number>> NumberContext.listRationalFunctionSpaceScope: RationalFunctionSpaceScope<Number, ListPolynomial<Number>, ListRationalFunction<Number>, NumberContext, ListPolynomialSpace<Number>, ListRationalFunctionSpace<Number>>
    get() {
        val polynomialSpace = this.listPolynomialSpace
        return RationalFunctionSpaceScope(numberContext = this, polynomialSpace = polynomialSpace, rationalFunctionSpace = polynomialSpace.listRationalFunctionSpace)
    }

public inline val <Number, NumberContext: Field<Number>> NumberContext.listRationalFunctionSpaceScope: RationalFunctionSpaceScope<Number, ListPolynomial<Number>, ListRationalFunction<Number>, NumberContext, ListPolynomialSpaceOverField<Number>, ListRationalFunctionSpace<Number>>
    get() {
        val polynomialSpace = this.listPolynomialSpace
        return RationalFunctionSpaceScope(numberContext = this, polynomialSpace = polynomialSpace, rationalFunctionSpace = polynomialSpace.listRationalFunctionSpace)
    }


/**
 * Evaluates value of [this] polynomial on provided argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
context(_: Ring<Number>)
public fun <Number> ListPolynomial<Number>.substitute(arg: Number): Number {
    if (coefficients.isEmpty()) return zero
    var result: Number = coefficients.last()
    if (coefficients.size >= 2u) for (j in coefficients.size - 2u downTo 0u) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

/**
 * Substitutes provided polynomial [arg] into [this] polynomial.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */ // TODO: To optimize boxing
context(_: ListPolynomialSpace<C>)
public fun <C> ListPolynomial<C>.substitute(arg: ListPolynomial<C>) : ListPolynomial<C> {
    if (coefficients.isEmpty()) return zero
    var result: ListPolynomial<C> = coefficients.last().polynomialValue
    if (coefficients.size >= 2u) for (j in coefficients.size - 2u downTo 0u) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

/**
 * Substitutes provided rational function [arg] into [this] polynomial.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
// TODO: To optimize boxing
// TODO: Improve denominator computation: it should have degree as small as possible
context(_: ListRationalFunctionSpace<Number>)
public fun <Number> ListPolynomial<Number>.substitute(arg: ListRationalFunction<Number>) : ListRationalFunction<Number> {
    if (coefficients.isEmpty()) return zero
    var result: ListRationalFunction<Number> = coefficients.last().rationalFunctionValue
    if (coefficients.size >= 2u) for (j in coefficients.size - 2u downTo 0u) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

/**
 * Evaluates value of [this] polynomial for provided argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
context(_: Field<Number>)
public fun <Number> ListRationalFunction<Number>.substitute(arg: Number): Number = numerator.substitute(arg) / denominator.substitute(arg)

/**
 * Substitutes provided polynomial [arg] into [this] rational function.
 */
// TODO: To optimize boxing
context(_: ListPolynomialSpace<Number>, _: ListRationalFunctionSpace<Number>)
public fun <Number> ListRationalFunction<Number>.substitute(arg: ListPolynomial<Number>) : ListRationalFunction<Number> =
    numerator.substitute<Number>(arg) / denominator.substitute<Number>(arg)

/**
 * Substitutes provided rational function [arg] into [this] rational function.
 */
// TODO: To optimize boxing
// TODO: Improve denominators computation: they should have degree as small as possible
context(_: ListRationalFunctionSpace<Number>)
public fun <Number> ListRationalFunction<Number>.substitute(arg: ListRationalFunction<Number>) : ListRationalFunction<Number> =
    numerator.substitute<Number>(arg) / denominator.substitute<Number>(arg)


/**
 * Returns algebraic derivative of received polynomial.
 */
context(_: Ring<C>, _: ListPolynomialSpace<C>)
public fun <C> ListPolynomial<C>.derivative(): ListPolynomial<C> =
    if (coefficients.isEmpty()) polynomialZero
    else ListPolynomial(
        KoneList.build(coefficients.size - 1u) {
            for (deg in 1u .. coefficients.lastIndex) +(deg * coefficients[deg])
        }
    )

/**
 * Returns algebraic derivative of received polynomial of specified [order]. The [order] should be non-negative integer.
 */
context(_: Ring<C>, _: ListPolynomialSpace<C>)
public fun <C> ListPolynomial<C>.nthDerivative(order: UInt): ListPolynomial<C> {
    if (coefficients.size < order) return polynomialZero
    return ListPolynomial(
        KoneList.build(coefficients.size - order) {
            for (deg in order.. coefficients.lastIndex)
                +(deg - order + 1u .. deg).fold(coefficients[deg]) { acc, d -> acc * d }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
context(_: Field<C>)
public fun <C> ListPolynomial<C>.antiderivative(): ListPolynomial<C> =
    ListPolynomial(
        KoneList.build(coefficients.size + 1u) {
            +zero
            coefficients.mapIndexedTo(this) { index, t -> t / (index + 1u) }
        }
    )

/**
 * Returns algebraic antiderivative of received polynomial of specified [order]. The [order] should be non-negative integer.
 */
context(_: Field<C>)
public fun <C> ListPolynomial<C>.nthAntiderivative(order: UInt): ListPolynomial<C> {
    return ListPolynomial(
        KoneList.build(coefficients.size + order) {
            repeat(order) { +zero }
            coefficients.mapIndexedTo(this) { index, coef -> (1u..order).fold(coef) { acc, i -> acc / (index + i) } }
        }
    )
}

context(_: Field<Number>, _: ListPolynomialSpaceOverField<Number>)
internal fun <Number> ListPolynomial<Number>.sturmSeries(): KoneList<ListPolynomial<Number>> =
    if (this.isZero()) KoneList.empty()
    else KoneList.build {
        +this@sturmSeries
        var last = this@sturmSeries
        var next = this@sturmSeries.derivative()
        
        while (next.isNotZero()) {
            last = next.also { next = -(last % next) }
            +last
        }
    }

context(_: Field<Number>, _: Order<Number>, _: ListPolynomialSpaceOverField<Number>)
internal fun <Number> ListPolynomial<Number>.sturmNumberOfSignVariationsAt(point: Number): UInt {
    val sturmSigns = sturmSeries().map { it.substitute(point).sign }.filter { it != 0 }
    return (0u ..< sturmSigns.lastIndex).toKoneList().count { sturmSigns[it] != sturmSigns[it + 1u] }
}

// "from" excluded, "to" included
context(_: Field<Number>, _: Order<Number>, _: ListPolynomialSpaceOverField<Number>)
public fun <Number> ListPolynomial<Number>.numberOfRootsBySturm(from: Number, to: Number): UInt {
    val sturmSeries = sturmSeries()
    val sturmSignsAtFromPoint = sturmSeries.map { it.substitute(from).sign }.filter { it != 0 }
    val sturmSignsAtToPoint = sturmSeries.map { it.substitute(to).sign }.filter { it != 0 }
    val sturmNumberOfSignVariationsAtFromPoint = (0u ..< sturmSignsAtFromPoint.lastIndex).toKoneList().count { sturmSignsAtFromPoint[it] != sturmSignsAtFromPoint[it + 1u] }
    val sturmNumberOfSignVariationsAtToPoint = (0u ..< sturmSignsAtToPoint.lastIndex).toKoneList().count { sturmSignsAtToPoint[it] != sturmSignsAtToPoint[it + 1u] }
    return sturmNumberOfSignVariationsAtFromPoint - sturmNumberOfSignVariationsAtToPoint
}