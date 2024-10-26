/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.comparison

import dev.lounres.kone.context.invoke
import kotlin.Comparator as KotlinStdlibComparator


// TODO: Declare my own `Comparator` interface
/**
 * Describes result of two elements comparison. See [Order] for the main application.
 *
 * Names of the enumerated elements say for themselves.
 */
public enum class ComparisonResult {
    LeftIsGreaterThanRight, LeftIsLessThanRight, Equal;
}

/**
 * Describes a context that provides [linear (total) order](https://en.wikipedia.org/wiki/Total_order) as a [compareTo]
 * besides inherited [equalsTo] operator. This operator should return `0` iff [equalsTo] returns `true`
 *
 * Such contexts are used instead of usual [compareTo] operator defined right inside the [E] type for several reasons.
 * Some of them are:
 * - Following structural pattern, any behaviour *between* elements should not be a part of the elements' logic
 *   but a part of assumed context. (For example, summing two integers together, we assume that we are summing them
 *   as two integer, but not as a residue of some modulo.)
 * - Such separation of entities and operations over them brings modularity: you can change operations context
 *   leaving the entities the same.
 */
public interface Order<in E> : Equality<E> {
    public infix fun E.compareWith(other: E): ComparisonResult
}

/**
 * Provides comparison of two elements. Alternative of [KotlinStdlibComparator] but with result of type [ComparisonResult].
 */
public fun interface Comparator<in E> {
    public fun compare(left: E, right: E): ComparisonResult
}

/**
 * Shortcut to convert comparison result from [Comparable]'s and [KotlinStdlibComparator]'s terms to
 * [Order]'s and Kone [Comparator]'s terms.
 */
public fun Int.asComparisonResult(): ComparisonResult =
    when {
        this > 0 -> ComparisonResult.LeftIsGreaterThanRight
        this < 0 -> ComparisonResult.LeftIsLessThanRight
        else -> ComparisonResult.Equal
    }

/**
 * Shortcut to convert comparison result from [Comparable]'s and [KotlinStdlibComparator]'s terms to
 * [Order]'s and Kone [Comparator]'s terms.
 */
public fun ComparisonResult.asKotlinComparisonResult(): Int =
    when (this) {
        ComparisonResult.LeftIsGreaterThanRight -> 1
        ComparisonResult.LeftIsLessThanRight -> -1
        ComparisonResult.Equal -> 0
    }

public fun <E> Comparator<E>.asKotlinStdlib(): KotlinStdlibComparator<E> =
    KotlinStdlibComparator { left, right -> compare(left, right).asKotlinComparisonResult() }

public fun <E> KotlinStdlibComparator<E>.asKotlinStdlib(): Comparator<E> =
    Comparator { left, right -> compare(left, right).asComparisonResult() }

/**
 * Compares [this] and [other] elements but in terms of the built-in language `compareTo` operator.
 *
 * The only usage is to import to make `<`, `<=`, `>`, and `>=` work in [Order] context.
 */
context(Order<E>)
public operator fun <E> E.compareTo(other: E): Int = this.compareWith(other).asKotlinComparisonResult()

/**
 * Alternative notation to `>` operator that uses [Order.compareTo] for comparison.
 */
context(Order<E>)
public inline infix fun <E> E.greaterThan(other: E): Boolean = this.compareWith(other) == ComparisonResult.LeftIsGreaterThanRight
/**
 * Alternative notation to `>=` operator that uses [Order.compareTo] for comparison.
 */
context(Order<E>)
public inline infix fun <E> E.greaterThanOrEqual(other: E): Boolean = this.compareWith(other) != ComparisonResult.LeftIsLessThanRight
/**
 * Alternative notation to `<` operator that uses [Order.compareTo] for comparison.
 */
context(Order<E>)
public inline infix fun <E> E.lessThen(other: E): Boolean = this.compareWith(other) == ComparisonResult.LeftIsLessThanRight
/**
 * Alternative notation to `<=` operator that uses [Order.compareTo] for comparison.
 */
context(Order<E>)
public inline infix fun <E> E.lessThenOrEqual(other: E): Boolean = this.compareWith(other) != ComparisonResult.LeftIsGreaterThanRight
/**
 * Alternative notation to `>` operator that uses [Order.compareTo] for comparison.
 */
context(Order<E>)
public inline infix fun <E> E.gt(other: E): Boolean = this greaterThan other
/**
 * Alternative notation to `>=` operator that uses [Order.compareTo] for comparison.
 */
context(Order<E>)
public inline infix fun <E> E.geq(other: E): Boolean = this greaterThanOrEqual other
/**
 * Alternative notation to `<` operator that uses [Order.compareTo] for comparison.
 */
context(Order<E>)
public inline infix fun <E> E.lt(other: E): Boolean = this lessThen other
/**
 * Alternative notation to `<=` operator that uses [Order.compareTo] for comparison.
 */
context(Order<E>)
public inline infix fun <E> E.leq(other: E): Boolean = this lessThenOrEqual other

/**
 * Returns the smaller of two values [a] and [b].
 */
context(Order<E>)
public fun <E> min(a: E, b: E): E = if (a <= b) a else b
/**
 * Returns the greater of two values [a] and [b].
 */
context(Order<E>)
public fun <E> max(a: E, b: E): E = if (a >= b) a else b
/**
 * Returns the smallest value from [elements]. If [elements] is empty throws [IllegalArgumentException].
 *
 * @throws IllegalArgumentException If [elements] is empty.
 */
context(Order<E>)
public fun <E> min(vararg elements: E): E {
    if (elements.isEmpty()) throw IllegalArgumentException("Cannot calculate minimum of an empty collection of elements")
    return elements.reduce { a, b -> min(a, b) }
}
/**
 * Returns the greatest value from [elements]. If [elements] is empty throws [IllegalArgumentException].
 *
 * @throws IllegalArgumentException If [elements] is empty.
 */
context(Order<E>)
public fun <E> max(vararg elements: E): E {
    if (elements.isEmpty()) throw IllegalArgumentException("Cannot calculate maximum of an empty collection of elements")
    return elements.reduce { a, b -> max(a, b) }
}

/**
 * [Order] builder from a [equalizer] that checks equality of the `left` and `right` elements and [comparator]
 * that compares the `left` and `right` elements to each other.
 */
public inline fun <E> Order(crossinline equalizer: (left: E, right: E) -> Boolean, crossinline comparator: (left: E, right: E) -> ComparisonResult): Order<E> =
    object : Order<E> {
        override fun E.equalsTo(other: E): Boolean = equalizer(this, other)
        override fun E.compareWith(other: E): ComparisonResult = comparator(this, other)
    }

/**
 * [Order] builder from a [equalizer] that checks equality of the `left` and `right` elements and [comparator]
 * that compares the `left` and `right` elements to each other.
 */
public inline fun <E> Order(crossinline equalizer: (left: E, right: E) -> Boolean, comparator: Comparator<E>): Order<E> =
    object : Order<E> {
        override fun E.equalsTo(other: E): Boolean = equalizer(this, other)
        override fun E.compareWith(other: E): ComparisonResult = comparator.compare(this, other)
    }

/**
 * [Order] builder from a [equalizer] that checks equality of the `left` and `right` elements and [comparator]
 * that compares the `left` and `right` elements to each other.
 */
public inline fun <E> Order(equalizer: Equality<E>, crossinline comparator: (left: E, right: E) -> ComparisonResult): Order<E> =
    object : Order<E> {
        override fun E.equalsTo(other: E): Boolean = equalizer { this eq other }
        override fun E.compareWith(other: E): ComparisonResult = comparator(this, other)
    }

/**
 * [Order] builder from a [equalizer] that checks equality of the `left` and `right` elements and [comparator]
 * that compares the `left` and `right` elements to each other.
 */
public inline fun <E> Order(equalizer: Equality<E>, comparator: Comparator<E>): Order<E> =
    object : Order<E> {
        override fun E.equalsTo(other: E): Boolean = equalizer { this eq other }
        override fun E.compareWith(other: E): ComparisonResult = comparator.compare(this, other)
    }

/**
 * Returns [Order] instance which [Equality.equalsTo] operator just uses [Any.equals] operator's result as a return value
 * and which [Order.compareTo] operator just uses [Comparable.compareTo] operator's result as a return value.
 */
public fun <E: Comparable<E>> defaultOrder(): Order<E> = DefaultOrderOnComparables
/**
 * Returns [Comparator] instance which [Comparator.compare] operator just uses [Comparable.compareTo] operator's result as a return value.
 */
public inline fun <E: Comparable<E>> defaultComparator(): Comparator<E> = DefaultComparatorOnComparables
/**
 * Converts provided [Order] receiver into [Comparator] that delegates its [Comparator.compare] operator to
 * [Order.compareTo] operator.
 */
public fun <E> Order<E>.asComparator(): Comparator<E> = Comparator { left, right -> left.compareWith(right) }
/**
 * Converts provided [Order] context receiver into [Comparator] that delegates its [Comparator.compare] operator to
 * [Order.compareTo] operator.
 */
context(Order<E>)
public val <E> comparator: Comparator<E> get() = Comparator { left, right -> left.compareWith(right) }
/**
 * Creates a comparator using the sequence of functions to calculate a result of comparison.
 * The functions are called sequentially, receive the given values `a` and `b` and return objects comparable via
 * [Order] context receiver. As soon as the instances returned by a function for `a` and `b` values do not
 * compare as equal, the result of that comparison is returned from the [Comparator]. Otherwise `0` is returned.
 *
 * Such order is usually called [lexicographic order](https://en.wikipedia.org/wiki/Lexicographic_order#Cartesian_products)
 * with respect to the provided orders.
 */
context(Order<E>)
public fun <T, E> compareByOrdered(vararg selectors: (T) -> E): Comparator<T> = Comparator { a, b ->
    for (s in selectors) {
        val comparisonResult = s(a).compareWith(s(b))
        if (comparisonResult != ComparisonResult.Equal) return@Comparator comparisonResult
    }
    return@Comparator ComparisonResult.Equal
}

// TODO: Replace with multifield value classes when KT-72538 will be fixed
/**
 * A wrapper data class that contains values [start] and [endInclusive] to be used by [ClosedRange.contains] operator that checks
 * if the provided value lies in a closed interval `[start; endInclusive]`.
 */
//@JvmInline
public data class ClosedRange<out E>(public val start: E, public val endInclusive: E)
/**
 * A wrapper data class that contains values [start] and [endExclusive] to be used by [RightOpenRange.contains] operator that checks
 * if the provided value lies in a right-open interval `[start; endExclusive)`.
 */
//@JvmInline
public data class RightOpenRange<out E>(public val start: E, public val endExclusive: E)

/**
 * Creates [ClosedRange] instance to be used by [ClosedRange.contains] operator that checks if the provided value
 * lies in a closed interval from [this] to [other].
 */
public operator fun <E> E.rangeTo(other: E): ClosedRange<E> = ClosedRange(this, other)
/**
 * Creates [RightOpenRange] instance to be used by [RightOpenRange.contains] operator that checks if the provided value
 * lies in a right-open interval from [this] to [other].
 */
public operator fun <E> E.rangeUntil(other: E): RightOpenRange<E> = RightOpenRange(this, other)

/**
 * Checks if the provided [element] lies in a closed interval from [ClosedRange.start] to [ClosedRange.endInclusive]
 * with respect to contextual order.
 */
context(Order<E>)
public operator fun <E> ClosedRange<E>.contains(element: E): Boolean = element >= start && element <= endInclusive
/**
 * Checks if the provided [element] lies in a right-open interval from [RightOpenRange.start] to [RightOpenRange.endExclusive]
 * with respect to contextual order.
 */
context(Order<E>)
public operator fun <E> RightOpenRange<E>.contains(element: E): Boolean = element >= start && element < endExclusive