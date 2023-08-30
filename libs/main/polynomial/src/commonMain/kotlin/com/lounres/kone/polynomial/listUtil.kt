//    /*
// * Copyright © 2023 Gleb Minaev
// * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
// */
//
//    @file:Suppress("NOTHING_TO_INLINE")
//
//package com.lounres.kone.polynomial
//
//import com.lounres.kone.algebraic.Field
//import com.lounres.kone.algebraic.Ring
//import com.lounres.kone.annotations.UnstableKoneAPI
//import com.lounres.kone.context.invoke
//import kotlin.math.max
//import kotlin.math.pow
//
//
///**
// * Creates a [ListPolynomialSpace] over a received ring.
// */
//public inline val <C, A : Ring<C>> A.listPolynomialSpace: ListPolynomialSpace<C, A>
//    get() = ListPolynomialSpace()
//
///**
// * Creates a [ListPolynomialSpaceOverField] over a received field.
// */
//public inline val <C, A : Field<C>> A.listPolynomialSpace: ListPolynomialSpaceOverField<C, A>
//    get() = ListPolynomialSpaceOverField()
//
///**
// * Creates a [ListRationalFunctionSpace] over a received polynomial space.
// */
//public inline val <C, A : Ring<C>, PS: ListPolynomialSpace<C, A>> PS.listRationalFunctionSpace: DefaultListRationalFunctionSpace<C, A>
//    get() = constantRing { ListRationalFunctionSpace() }
//
///**
// * Creates a [ListRationalFunctionSpaceOverField] over a received polynomial space over field.
// */
//public inline val <C, A : Field<C>, PS: ListPolynomialSpaceOverField<C, A>> PS.listRationalFunctionSpace: DefaultListRationalFunctionSpaceOverField<C, A>
//    get() = constantRing { ListRationalFunctionSpaceOverField() }
//
///**
// * Creates a [ListRationalFunctionSpace] over a received ring.
// */
//public inline val <C, A : Ring<C>> A.listRationalFunctionSpace: DefaultListRationalFunctionSpace<C, A>
//    get() = this.listPolynomialSpace.listRationalFunctionSpace
//
///**
// * Creates a [ListRationalFunctionSpaceOverField] over a received field.
// */
//public inline val <C, A : Field<C>> A.listRationalFunctionSpace: DefaultListRationalFunctionSpaceOverField<C, A>
//    get() = this.listPolynomialSpace.listRationalFunctionSpace
//
//
///**
// * Evaluates value of [this] Double polynomial on provided Double argument.
// */
//public fun ListPolynomial<Double>.substitute(arg: Double): Double =
//    coefficients.reduceIndexedOrNull { index, acc, c ->
//        acc + c * arg.pow(index)
//    } ?: .0
//
///**
// * Evaluates value of [this] polynomial on provided argument.
// *
// * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
// */
//context(Ring<C>)
//public fun <C> ListPolynomial<C>.substitute(arg: C): C {
//    if (coefficients.isEmpty()) return zero
//    var result: C = coefficients.last()
//    for (j in coefficients.size - 2 downTo 0) {
//        result = (arg * result) + coefficients[j]
//    }
//    return result
//}
//context(ListPolynomialSpace<C, A>)
//public inline fun <C, A : Ring<C>> ListPolynomial<C>.substitute(argument: C): C = constantRing { substitute<C>(argument) }
///**
// * Evaluates value of [this] polynomial on provided argument.
// */
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline fun <C, A : Ring<C>> ListPolynomial<C>.substitute(argument: C): C = polynomialSpace.constantRing { substitute<C>(argument) }
//
///**
// * Substitutes provided polynomial [arg] into [this] polynomial.
// *
// * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
// */ // TODO: To optimize boxing
//context(Ring<C>)
//public fun <C> ListPolynomial<C>.substitute(arg: ListPolynomial<C>) : ListPolynomial<C> =
//    listPolynomialSpace {
//        if (coefficients.isEmpty()) return zero
//        var result: ListPolynomial<C> = coefficients.last().polynomialValue
//        for (j in coefficients.size - 2 downTo 0) {
//            result = (arg * result) + coefficients[j]
//        }
//        return result
//    }
//context(ListPolynomialSpace<C, A>)
//public inline fun <C, A : Ring<C>> ListPolynomial<C>.substitute(argument: ListPolynomial<C>): ListPolynomial<C> = constantRing { substitute<C>(argument) }
///**
// * Substitutes provided polynomial [argument] into [this] polynomial.
// */
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline fun <C, A : Ring<C>> ListPolynomial<C>.substitute(argument: ListPolynomial<C>): ListPolynomial<C> = polynomialSpace.constantRing { substitute<C>(argument) }
//
///**
// * Substitutes provided rational function [arg] into [this] polynomial.
// *
// * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
// */ // TODO: To optimize boxing
//context(Ring<C>)
//public fun <C> ListPolynomial<C>.substitute(arg: ListRationalFunction<C>) : ListRationalFunction<C> =
//    listRationalFunctionSpace {
//        if (coefficients.isEmpty()) return zero
//        var result: ListRationalFunction<C> = coefficients.last().rationalFunctionValue
//        for (j in coefficients.size - 2 downTo 0) {
//            result = (arg * result) + coefficients[j]
//        }
//        return result
//    }
///**
// * Substitutes provided rational function [argument] into [this] polynomial.
// */
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline fun <C, A: Ring<C>> ListPolynomial<C>.substitute(argument: ListRationalFunction<C>): ListRationalFunction<C> = polynomialSpace.constantRing { substitute<C>(argument) }
//
///**
// * Evaluates value of [this] Double rational function in provided Double argument.
// */
//public fun ListRationalFunction<Double>.substitute(arg: Double): Double =
//    numerator.substitute(arg) / denominator.substitute(arg)
//
///**
// * Evaluates value of [this] polynomial for provided argument.
// *
// * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
// */
//context(Field<C>)
//public fun <C> ListRationalFunction<C>.substitute(arg: C): C = numerator.substitute(arg) / denominator.substitute(arg)
//
///**
// * Substitutes provided polynomial [arg] into [this] rational function.
// */ // TODO: To optimize boxing
//context(Ring<C>)
//public fun <C> ListRationalFunction<C>.substitute(arg: ListPolynomial<C>) : ListRationalFunction<C> =
//    listRationalFunctionSpace {
//        numerator.substitute<C>(arg) / denominator.substitute<C>(arg)
//    }
///**
// * Substitutes provided polynomial [argument] into [this] rational function.
// */
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline fun <C, A: Ring<C>> ListRationalFunction<C>.substitute(argument: ListPolynomial<C>): ListRationalFunction<C> = polynomialSpace.constantRing { substitute<C>(argument) }
//
///**
// * Substitutes provided rational function [arg] into [this] rational function.
// */ // TODO: To optimize boxing
//context(Ring<C>)
//public fun <C> ListRationalFunction<C>.substitute(arg: ListRationalFunction<C>) : ListRationalFunction<C> =
//    listRationalFunctionSpace {
//        numerator.substitute<C>(arg) / denominator.substitute<C>(arg)
//    }
///**
// * Substitutes provided rational function [argument] into [this] rational function.
// */
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline fun <C, A: Ring<C>> ListRationalFunction<C>.substitute(argument: ListRationalFunction<C>): ListRationalFunction<C> = polynomialSpace.constantRing { substitute<C>(argument) }
//
///**
// * Represent [this] polynomial as a regular context-less function.
// */
//context(Ring<C>)
//public fun <C> ListPolynomial<C>.asFunction(): (C) -> C = { substitute(it) }
///**
// * Represent [this] polynomial as a regular context-less function.
// */
//context(ListPolynomialSpace<C, A>)
//public inline fun <C, A: Ring<C>> ListPolynomial<C>.asFunction(): (C) -> C = { substitute(it) }
///**
// * Represent [this] polynomial as a regular context-less function.
// */
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline fun <C, A: Ring<C>> ListPolynomial<C>.asFunction(): (C) -> C = { substitute(it) }
//
///**
// * Represent [this] polynomial as a regular context-less function.
// */
//context(Ring<C>)
//public fun <C> ListPolynomial<C>.asFunctionOfConstant(): (C) -> C = { substitute(it) }
//context(ListPolynomialSpace<C, A>)
//public inline fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOfConstant(): (C) -> C = constantRing { asFunctionOfConstant<C>() }
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOfConstant(): (C) -> C = polynomialSpace.constantRing { asFunctionOfConstant<C>() }
//context(ListPolynomialSpace<C, A>)
//public inline operator fun <C, A : Ring<C>> ListPolynomial<C>.invoke(argument: C): C = constantRing { substitute<C>(argument) }
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline operator fun <C, A : Ring<C>> ListPolynomial<C>.invoke(argument: C): C = polynomialSpace.constantRing { substitute<C>(argument) }
//
///**
// * Represent [this] polynomial as a regular context-less function.
// */
//context(Ring<C>)
//public fun <C> ListPolynomial<C>.asFunctionOfPolynomial(): (ListPolynomial<C>) -> ListPolynomial<C> = { substitute(it) }
//context(ListPolynomialSpace<C, A>)
//public inline fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOfPolynomial(): (ListPolynomial<C>) -> ListPolynomial<C> = constantRing { asFunctionOfPolynomial<C>() }
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOfPolynomial(): (ListPolynomial<C>) -> ListPolynomial<C> = polynomialSpace.constantRing { asFunctionOfPolynomial<C>() }
//context(ListPolynomialSpace<C, A>)
//public inline operator fun <C, A : Ring<C>> ListPolynomial<C>.invoke(argument: ListPolynomial<C>): ListPolynomial<C> = constantRing { substitute<C>(argument) }
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public inline operator fun <C, A : Ring<C>> ListPolynomial<C>.invoke(argument: ListPolynomial<C>): ListPolynomial<C> = polynomialSpace.constantRing { substitute<C>(argument) }
//
///**
// * Represent [this] polynomial as a regular context-less function.
// */
//context(Ring<C>)
//public fun <C> ListPolynomial<C>.asFunctionOfRationalFunction(): (ListRationalFunction<C>) -> ListRationalFunction<C> = { substitute(it) }
//context(ListPolynomialSpace<C, A>)
//public fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOfRationalFunction(): (ListRationalFunction<C>) -> ListRationalFunction<C> = constantRing { asFunctionOfRationalFunction<C>() }
//context(ListRationalFunctionSpace<C, A, ListPolynomialSpace<C, A>>)
//public fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOfRationalFunction(): (ListRationalFunction<C>) -> ListRationalFunction<C> = polynomialSpace.constantRing { asFunctionOfRationalFunction<C>() }
//
///**
// * Represent [this] rational function as a regular context-less function.
// */
//context(Field<C>)
//public fun <C> ListRationalFunction<C>.asFunction(): (C) -> C = { substitute(it) }
//
///**
// * Represent [this] rational function as a regular context-less function.
// */
//context(Field<C>)
//public fun <C> ListRationalFunction<C>.asFunctionOfConstant(): (C) -> C = { substitute(it) }
//
///**
// * Represent [this] rational function as a regular context-less function.
// */
//context(Ring<C>)
//public fun <C> ListRationalFunction<C>.asFunctionOfPolynomial(): (ListPolynomial<C>) -> ListRationalFunction<C> = { substitute(it) }
//
///**
// * Represent [this] rational function as a regular context-less function.
// */
//context(Ring<C>)
//public fun <C> ListRationalFunction<C>.asFunctionOfRationalFunction(): (ListRationalFunction<C>) -> ListRationalFunction<C> = { substitute(it) }
//
///**
// * Returns algebraic derivative of received polynomial.
// */
//context(Ring<C>)
//@UnstableKoneAPI
//public fun <C> ListPolynomial<C>.derivative(): ListPolynomial<C> =
//    ListPolynomial(
//        buildList(max(0, coefficients.size - 1)) {
//            for (deg in 1 .. coefficients.lastIndex) add(deg * coefficients[deg])
//        }
//    )
//
///**
// * Returns algebraic derivative of received polynomial of specified [order]. The [order] should be non-negative integer.
// */
//context(Ring<C>)
//@UnstableKoneAPI
//public fun <C> ListPolynomial<C>.nthDerivative(order: Int): ListPolynomial<C> {
//    require(order >= 0) { "Order of derivative must be non-negative" }
//    return ListPolynomial(
//        buildList(max(0, coefficients.size - order)) {
//            for (deg in order.. coefficients.lastIndex)
//                add((deg - order + 1 .. deg).fold(coefficients[deg]) { acc, d -> acc * d })
//        }
//    )
//}
//
///**
// * Returns algebraic antiderivative of received polynomial.
// */
//context(Field<C>)
//@UnstableKoneAPI
//public fun <C> ListPolynomial<C>.antiderivative(): ListPolynomial<C> =
//    ListPolynomial(
//        buildList(coefficients.size + 1) {
//            add(zero)
//            coefficients.mapIndexedTo(this) { index, t -> t / (index + 1) }
//        }
//    )
//
///**
// * Returns algebraic antiderivative of received polynomial of specified [order]. The [order] should be non-negative integer.
// */
//context(Field<C>)
//@UnstableKoneAPI
//public fun <C> ListPolynomial<C>.nthAntiderivative(order: Int): ListPolynomial<C> {
//    require(order >= 0) { "Order of antiderivative must be non-negative" }
//    return ListPolynomial(
//        buildList(coefficients.size + order) {
//            repeat(order) { add(zero) }
//            coefficients.mapIndexedTo(this) { index, c -> (1..order).fold(c) { acc, i -> acc / (index + i) } }
//        }
//    )
//}
//
///**
// * Computes a definite integral of [this] polynomial in the specified [range].
// */
//context(Field<C>)
//@UnstableKoneAPI
//public fun <C : Comparable<C>> ListPolynomial<C>.integrate(range: ClosedRange<C>): C {
//    val antiderivative = antiderivative()
//    return antiderivative.substitute(range.endInclusive) - antiderivative.substitute(range.start)
//}