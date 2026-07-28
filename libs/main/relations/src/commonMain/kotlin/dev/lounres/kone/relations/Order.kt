/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.relations

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf
import kotlin.jvm.JvmField
import kotlin.Comparator as KotlinStdlibComparator
import kotlin.ranges.ClosedRange as KotlinClosedRange
import kotlin.ranges.OpenEndRange as KotlinOpenEndRange


/**
 * Describes result of two elements comparison. See [Order] for the main application.
 *
 * Names of the enumerated elements say for themselves.
 *
 * @property kotlinComparisonResult The corresponding integer value for use with Kotlin's standard comparison.
 *        `1` for [LeftIsGreaterThanRight], `-1` for [LeftIsLessThanRight], `0` for [Equal].
 */
public enum class ComparisonResult(@JvmField internal val kotlinComparisonResult: Int) {
    /** Indicates that the left element is greater than the right element. */
    LeftIsGreaterThanRight(1),
    /** Indicates that the left element is less than the right element. */
    LeftIsLessThanRight(-1),
    /** Indicates that the left and right elements are equal. */
    Equal(0);
}

/**
 * Describes a context that provides [linear (total) order](https://en.wikipedia.org/wiki/Total_order) as a [compareTo].
 *
 * Such contexts are used instead of usual [compareTo] operator defined right inside the [Element] type for several reasons.
 * Some of them are:
 * - Following structural pattern, any behaviour *between* elements should not be a part of the elements' logic
 *   but a part of assumed context. (For example, summing two integers together, we assume that we are summing them
 *   as two integer, but not as a residue of some modulo.)
 * - Such separation of entities and operations over them brings modularity: you can change operations context
 *   leaving the entities the same.
 *
 * @param Element The type of elements for which this order context is defined.
 */
public interface Order<in Element> : KoneContext {
    /**
     * Compares [this] and [other] elements.
     *
     * @receiver The left element in the comparison.
     * @param other The right element to compare with this element.
     * @return A [ComparisonResult] indicating the relative order of the two elements.
     */
    public infix fun Element.compareWith(other: Element): ComparisonResult
    
    /**
     * Companion object for [Order] interface providing factory methods
     * and constants for creating [Order] instances.
     */
    public companion object;
    
    /**
     * Registry key for [Order] interface in [KoneContextRegistry].
     *
     * This key is used to register and retrieve [Order] instances for specific types
     * in the context registry.
     *
     * @param Element The type of elements for which this order context is registered.
     */
    @Suppliable
    public class Key<@Supply Element> : SuppliedTypeRegistryKey<Order<Element>>() {
        override fun toString(): String = "dev.lounres.kone.relations.Order.Key<${suppliedTypeOf<Element>()}>"
    }
}

/**
 * Provides comparison of two elements. Alternative of [Kotlin stdlib Comparator][KotlinStdlibComparator] but with result of type [ComparisonResult].
 *
 * @param Element The type of elements this comparator can compare.
 */
public fun interface Comparator<in Element> {
    /**
     * Compares [left] and [right] elements.
     *
     * @param left The left element in the comparison.
     * @param right The right element in the comparison.
     * @return A [ComparisonResult] indicating the relative order of the two elements.
     */
    public fun compare(left: Element, right: Element): ComparisonResult
    
    /**
     * Companion object for [Comparator] interface providing factory methods
     * for creating [Comparator] instances.
     */
    public companion object
}

/**
 * Shortcut to convert comparison result from [Comparable]'s and [Kotlin stdlib Comparator][KotlinStdlibComparator]'s terms to
 * [Order]'s and Kone [Comparator]'s terms.
 *
 * Converts an integer comparison result (as returned by [Comparable.compareTo] or [KotlinStdlibComparator.compare])
 * to a [ComparisonResult] value.
 *
 * @receiver The integer comparison result to convert.
 * @return The corresponding [ComparisonResult] value.
 */
public fun Int.asComparisonResult(): ComparisonResult =
    when {
        this > 0 -> ComparisonResult.LeftIsGreaterThanRight
        this < 0 -> ComparisonResult.LeftIsLessThanRight
        else -> ComparisonResult.Equal
    }

/**
 * Shortcut to convert comparison result from [Comparable]'s and [Order]'s terms to
 * [Kotlin stdlib Comparator][KotlinStdlibComparator]'s and Kone [Comparator]'s terms.
 *
 * Converts a [ComparisonResult] value to an integer comparison result for use with
 * Kotlin's standard comparison mechanisms.
 *
 * @receiver The comparison result to convert.
 * @return The corresponding integer value: `1` for [LeftIsGreaterThanRight][ComparisonResult.LeftIsGreaterThanRight],
 *         `-1` for [LeftIsLessThanRight][ComparisonResult.LeftIsLessThanRight], `0` for [Equal][ComparisonResult.Equal].
 */
public fun ComparisonResult.asKotlinComparisonResult(): Int = kotlinComparisonResult

/**
 * Converts Kone [Comparator] to [Kotlin stdlib Comparator][KotlinStdlibComparator].
 *
 * Creates a Kotlin standard library comparator that delegates to this Kone comparator
 * and converts the result to an integer.
 *
 * @receiver The Kone comparator to convert.
 * @return A [KotlinStdlibComparator] that wraps this Kone comparator.
 */
public fun <Element> Comparator<Element>.asKotlinStdlib(): KotlinStdlibComparator<Element> =
    KotlinStdlibComparator { left, right -> compare(left, right).asKotlinComparisonResult() }

/**
 * Converts [Kotlin stdlib Comparator][KotlinStdlibComparator] to Kone [Comparator].
 *
 * Creates a Kone comparator that delegates to the Kotlin standard library comparator
 * and converts the integer result to a [ComparisonResult].
 *
 * @receiver The Kotlin standard library comparator to convert.
 * @return A [Comparator] that wraps the Kotlin standard library comparator.
 */
public fun <Element> KotlinStdlibComparator<Element>.asKone(): Comparator<Element> =
    Comparator { left, right -> compare(left, right).asComparisonResult() }

/**
 * Compares [this] and [other] elements.
 *
 * A bridge contextual function for [Order.compareWith].
 *
 * @receiver The left element in the comparison.
 * @param order The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return A [ComparisonResult] indicating the relative order of the two elements.
 */
context(order: Order<Element>)
public infix fun <Element> Element.compareWith(other: Element): ComparisonResult = with(order) { this@compareWith.compareWith(other) }

/**
 * Compares [this] and [other] elements but in terms of the built-in language `compareTo` operator.
 *
 * The only usage is to import to make `<`, `<=`, `>`, and `>=` work in [Order] context.
 *
 * @receiver The left element in the comparison.
 * @param Element The type of elements being compared.
 * @param order The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return An integer comparison result compatible with Kotlin's standard comparison operators.
 */
context(order: Order<Element>)
public infix operator fun <Element> Element.compareTo(other: Element): Int = this.compareWith(other).asKotlinComparisonResult()

/**
 * Alternative notation to `==` operator that uses [Order.compareTo] for comparison.
 *
 * @receiver The left element in the comparison.
 * @param Element The type of elements being compared.
 * @param order The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element equals the other element according to the order context.
 */
context(order: Order<Element>)
public inline infix fun <Element> Element.coincidesWith(other: Element): Boolean = this.compareWith(other) == ComparisonResult.Equal
/**
 * Alternative notation to `!=` operator that uses [Order.compareTo] for comparison.
 *
 * @receiver The left element in the comparison.
 * @param Element The type of elements being compared.
 * @param order The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element does not equal the other element according to the order context.
 */
// FIXME: KT-5351
context(order: Order<Element>)
public inline infix fun <Element> Element.notCoincidesWith(other: Element): Boolean = this.compareWith(other) != ComparisonResult.Equal
/**
 * Alternative notation to `>` operator that uses [Order.compareTo] for comparison.
 *
 * @receiver The left element in the comparison.
 * @param Element The type of elements being compared.
 * @param order The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element is greater than the other element according to the order context.
 */
context(order: Order<Element>)
public inline infix fun <Element> Element.greaterThan(other: Element): Boolean = this.compareWith(other) == ComparisonResult.LeftIsGreaterThanRight
/**
 * Alternative notation to `>=` operator that uses [Order.compareTo] for comparison.
 *
 * @receiver The left element in the comparison.
 * @param Element The type of elements being compared.
 * @param order The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element is greater than or equal to the other element according to the order context.
 */
context(order: Order<Element>)
public inline infix fun <Element> Element.greaterThanOrEqual(other: Element): Boolean = this.compareWith(other) != ComparisonResult.LeftIsLessThanRight
/**
 * Alternative notation to `<` operator that uses [Order.compareTo] for comparison.
 *
 * @receiver The left element in the comparison.
 * @param _ The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element is less than the other element according to the order context.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.lessThen(other: Element): Boolean = this.compareWith(other) == ComparisonResult.LeftIsLessThanRight
/**
 * Alternative notation to `<=` operator that uses [Order.compareTo] for comparison.
 *
 * @receiver The left element in the comparison.
 * @param _ The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element is less than or equal to the other element according to the order context.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.lessThenOrEqual(other: Element): Boolean = this.compareWith(other) != ComparisonResult.LeftIsGreaterThanRight
/**
 * Alternative notation to `>` operator that uses [Order.compareTo] for comparison.
 * Shorthand for [greaterThan].
 *
 * @receiver The left element in the comparison.
 * @param _ The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element is greater than the other element according to the order context.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.gt(other: Element): Boolean = this greaterThan other
/**
 * Alternative notation to `>=` operator that uses [Order.compareTo] for comparison.
 * Shorthand for [greaterThanOrEqual].
 *
 * @receiver The left element in the comparison.
 * @param _ The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element is greater than or equal to the other element according to the order context.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.geq(other: Element): Boolean = this greaterThanOrEqual other
/**
 * Alternative notation to `<` operator that uses [Order.compareTo] for comparison.
 * Shorthand for [lessThen].
 *
 * @receiver The left element in the comparison.
 * @param _ The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element is less than the other element according to the order context.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.lt(other: Element): Boolean = this lessThen other
/**
 * Alternative notation to `<=` operator that uses [Order.compareTo] for comparison.
 * Shorthand for [lessThenOrEqual].
 *
 * @receiver The left element in the comparison.
 * @param _ The order context in which to perform the comparison.
 * @param other The right element to compare with this element.
 * @return `true` if this element is less than or equal to the other element according to the order context.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.leq(other: Element): Boolean = this lessThenOrEqual other

/**
 * Returns the smaller of two values [a] and [b].
 *
 * @param _ The order context in which to perform the comparison.
 * @param a The first value to compare.
 * @param b The second value to compare.
 * @return The smaller of the two values according to the order context.
 */
context(_: Order<Element>)
public fun <Element> minOf(a: Element, b: Element): Element = if (a leq b) a else b
/**
 * Returns the greater of two values [a] and [b].
 *
 * @param _ The order context in which to perform the comparison.
 * @param a The first value to compare.
 * @param b The second value to compare.
 * @return The greater of the two values according to the order context.
 */
context(_: Order<Element>)
public fun <Element> maxOf(a: Element, b: Element): Element = if (a geq b) a else b
/**
 * Returns the smallest value from [elements]. If [elements] is empty throws [IllegalArgumentException].
 *
 * @param _ The order context in which to perform the comparison.
 * @param elements The vararg array of elements from which to find the minimum.
 * @return The smallest element from the array according to the order context.
 * @throws IllegalArgumentException If [elements] is empty.
 */
context(_: Order<Element>)
public fun <Element> minOf(vararg elements: Element): Element {
    require(elements.isNotEmpty()) { "Cannot calculate minimum of an empty collection of elements" }
    return elements.reduce { a, b -> minOf(a, b) }
}
/**
 * Returns the greatest value from [elements]. If [elements] is empty throws [IllegalArgumentException].
 *
 * @param _ The order context in which to perform the comparison.
 * @param elements The vararg array of elements from which to find the maximum.
 * @return The greatest element from the array according to the order context.
 * @throws IllegalArgumentException If [elements] is empty.
 */
context(_: Order<Element>)
public fun <Element> maxOf(vararg elements: Element): Element {
    require(elements.isNotEmpty()) { "Cannot calculate maximum of an empty collection of elements" }
    return elements.reduce { a, b -> maxOf(a, b) }
}

/**
 * [Order] builder from a [comparator] that compares the `left` and `right` elements to each other.
 *
 * Creates a custom [Order] context using the provided comparison logic.
 *
 * @param Element The type of elements this order context will handle.
 * @param comparator A function that takes two elements and returns their [ComparisonResult].
 * @return An [Order] instance that uses the provided [comparator] function for comparisons.
 */
public inline fun <Element> Order(crossinline comparator: (left: Element, right: Element) -> ComparisonResult): Order<Element> =
    object : Order<Element> {
        override fun Element.compareWith(other: Element): ComparisonResult = comparator(this, other)
    }

/**
 * [Order] builder from a [Comparator] that compares the `left` and `right` elements to each other.
 *
 * Creates an [Order] context that delegates to the provided [Comparator] for comparisons.
 *
 * @receiver The comparator to convert to an order context.
 * @return An [Order] instance that delegates to this comparator.
 */
public fun <Element> Comparator<Element>.asOrder(): Order<Element> =
    object : Order<Element> {
        override fun Element.compareWith(other: Element): ComparisonResult = this@asOrder.compare(this, other)
    }

/**
 * Creates an [Order] instance that compares elements of type [Target] by comparing
 * the results of applying the provided [selectors] to each element.
 *
 * The comparison is performed lexicographically: the first selector that produces
 * a non-equal comparison result determines the overall result.
 *
 * @receiver The order companion object.
 * @param _ The order context for the element type.
 * @param Target The type of elements to be ordered.
 * @param Element The type of values produced by the selectors.
 * @param selectors The sequence of selector functions to apply to elements before comparison.
 * @return An [Order] instance that compares [Target] elements by their selected [Element] values.
 */
context(_: Order<Element>)
public fun <Target, Element> Order.Companion.byOrdered(vararg selectors: (Target) -> Element): Order<Target> = Order { left, right ->
    for (selector in selectors) {
        val comparisonResult = selector(left).compareWith(selector(right))
        if (comparisonResult != Equal) return@Order comparisonResult
    }
    return@Order Equal
}

/**
 * Returns an [Order] context for nullable elements where `null` is considered the least value.
 *
 * In the resulting order:
 * - Two null values are considered equal
 * - A null value is considered less than any non-null value
 * - Two non-null values are compared using this order context
 *
 * @receiver The order context for non-nullable elements.
 * @return An [Order] instance where null is treated as the minimum value.
 */
public val <Element: Any> Order<Element>.withNullAsLeast: Order<Element?> get() = Order<Element?> { left, right ->
    when {
        left == null && right == null -> Equal
        left == null -> LeftIsLessThanRight
        right == null -> LeftIsGreaterThanRight
        else -> with(this) { left compareWith right }
    }
}

/**
 * Returns an [Order] context for nullable elements where `null` is considered the greatest value.
 *
 * In the resulting order:
 * - Two null values are considered equal
 * - A null value is considered greater than any non-null value
 * - Two non-null values are compared using this order context
 *
 * @receiver The order context for non-nullable elements.
 * @return An [Order] instance where null is treated as the maximum value.
 */
public val <Element: Any> Order<Element>.withNullAsGreatest: Order<Element?> get() = Order<Element?> { left, right ->
    when {
        left == null && right == null -> Equal
        left == null -> LeftIsGreaterThanRight
        right == null -> LeftIsLessThanRight
        else -> with(this) { left compareWith right }
    }
}

/**
 * Returns a [Comparator] for nullable elements where `null` is considered the least value.
 *
 * In the resulting comparator:
 * - Two null values are considered equal
 * - A null value is considered less than any non-null value
 * - Two non-null values are compared using this comparator
 *
 * @receiver The comparator for non-nullable elements.
 * @return A [Comparator] instance where null is treated as the minimum value.
 */
public val <Element: Any> Comparator<Element>.withNullAsLeast: Comparator<Element?> get() = Comparator { left, right ->
    when {
        left == null && right == null -> Equal
        left == null -> LeftIsLessThanRight
        right == null -> LeftIsGreaterThanRight
        else -> this.compare(left, right)
    }
}

/**
 * Returns a [Comparator] for nullable elements where `null` is considered the greatest value.
 *
 * In the resulting comparator:
 * - Two null values are considered equal
 * - A null value is considered greater than any non-null value
 * - Two non-null values are compared using this comparator
 *
 * @receiver The comparator for non-nullable elements.
 * @return A [Comparator] instance where null is treated as the maximum value.
 */
public val <Element: Any> Comparator<Element>.withNullAsGreatest: Comparator<Element?> get() = Comparator { left, right ->
    when {
        left == null && right == null -> Equal
        left == null -> LeftIsGreaterThanRight
        right == null -> LeftIsLessThanRight
        else -> this.compare(left, right)
    }
}

/**
 * Returns [Order] instance which [Order.compareTo] operator just uses [Comparable.compareTo] operator's result as a return value.
 *
 * This provides a default order context for types that implement [Comparable].
 *
 * @receiver The order companion object.
 * @param Element The comparable element type for which to create the default order context.
 * @return An [Order] instance that uses the natural ordering of comparable elements.
 */
public fun <Element: Comparable<Element>> Order.Companion.defaultFor(): Order<Element> = DefaultOrderOnComparables
// TODO: Remove the checker when KT-73135 will be fixed
/**
 * Container object for suppliable top-level functions related to [Order] context registration.
 *
 * These functions are used within DSL builders to register default order contexts.
 */
public object OrderDefaultForSuppliableTopLevelFunctions {
    /**
     * Sets default [Order] context for the given supplied type [Element] (which must implement [Comparable])
     * into context registry builder.
     *
     * The registered order context uses the natural ordering of comparable elements.
     *
     * @receiver The order companion object.
     * @param _ The mutable owned provider registry to register into.
     * @param Element The supplied element type (must implement [Comparable]) for which to set the default order context.
     */
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Element : Comparable<Element>> Order.Companion.setDefaultFor() {
        Order.Key<Element>() correspondsTo Order.defaultFor<Element>()
    }
}
/**
 * Returns [Comparator] instance which [Comparator.compare] operator just uses [Comparable.compareTo] operator's result as a return value.
 *
 * This provides a default comparator for types that implement [Comparable].
 *
 * @receiver The comparator companion object.
 * @param Element The comparable element type for which to create the default comparator.
 * @return A [Comparator] instance that uses the natural ordering of comparable elements.
 */
public fun <Element: Comparable<Element>> Comparator.Companion.defaultFor(): Comparator<Element> = DefaultComparatorOnComparables
/**
 * Converts provided [Order] receiver into [Comparator] that delegates its [Comparator.compare] operator to
 * [Order.compareTo] operator.
 *
 * @receiver The order context to convert.
 * @return A [Comparator] that delegates to this order context.
 */
public fun <Element> Order<Element>.asComparator(): Comparator<Element> = Comparator { left, right -> left.compareWith(right) }
/**
 * Converts provided [Order] context receiver into [Comparator] that delegates its [Comparator.compare] operator to
 * [Order.compareTo] operator.
 *
 * @param _ The order context in which to create the comparator.
 * @return A [Comparator] that uses the provided order context for comparisons.
 */
context(_: Order<Element>)
public val <Element> comparator: Comparator<Element> get() = Comparator { left, right -> left.compareWith(right) }
/**
 * Creates a comparator using the sequence of functions to calculate a result of comparison.
 * The functions are called sequentially, receive the given values `a` and `b` and return objects comparable via
 * [Order] context receiver. As soon as the instances returned by a function for `a` and `b` values do not
 * compare as equal, the result of that comparison is returned from the [Comparator]. Otherwise `0` is returned.
 *
 * Such order is usually called [lexicographic order](https://en.wikipedia.org/wiki/Lexicographic_order#Cartesian_products)
 * with respect to the provided orders.
 *
 * @receiver The comparator companion object.
 * @param _ The order context for the element type.
 * @param Target The type of elements to be compared.
 * @param Element The type of values produced by the selectors.
 * @param selectors The sequence of selector functions to apply to elements before comparison.
 * @return A [Comparator] that compares [Target] elements by their selected [Element] values.
 */
context(_: Order<Element>)
public fun <Target, Element> Comparator.Companion.byOrdered(vararg selectors: (Target) -> Element): Comparator<Target> = Comparator { left, right ->
    for (selector in selectors) {
        val comparisonResult = selector(left).compareWith(selector(right))
        if (comparisonResult != Equal) return@Comparator comparisonResult
    }
    return@Comparator Equal
}

/**
 * A wrapper data class that contains values [startInclusive] and [endInclusive] to be used by [ClosedRange.contains] operator that checks
 * if the provided value lies in a closed interval `[startInclusive; endInclusive]`.
 *
 * @param startInclusive The start of the interval (inclusive).
 * @param endInclusive The end of the interval (inclusive).
 */
//@JvmInline
public /*value*/ data class ClosedRange<out Element>(public val startInclusive: Element, public val endInclusive: Element)
/**
 * A wrapper data class that contains values [startInclusive] and [endExclusive] to be used by [OpenEndRange.contains] operator that checks
 * if the provided value lies in a right-open interval `[startInclusive; endExclusive)`.
 *
 * @param startInclusive The start of the interval (inclusive).
 * @param endExclusive The end of the interval (exclusive).
 */
//@JvmInline
public /*value*/ data class OpenEndRange<out Element>(public val startInclusive: Element, public val endExclusive: Element)

/**
 * Creates [ClosedRange] instance to be used by [ClosedRange.contains] operator that checks if the provided value
 * lies in a closed interval from [this] to [other].
 *
 * @receiver The start value of the interval (inclusive).
 * @param other The end value of the interval (inclusive).
 * @return A [ClosedRange] instance representing the interval from this value to the other value.
 */
public operator fun <Element> Element.rangeTo(other: Element): ClosedRange<Element> = ClosedRange(this, other)
/**
 * Creates [OpenEndRange] instance to be used by [OpenEndRange.contains] operator that checks if the provided value
 * lies in a right-open interval from [this] to [other].
 *
 * @receiver The start value of the interval (inclusive).
 * @param other The end value of the interval (exclusive).
 * @return An [OpenEndRange] instance representing the interval from this value to the other value.
 */
public operator fun <Element> Element.rangeUntil(other: Element): OpenEndRange<Element> = OpenEndRange(this, other)

/**
 * Checks if the provided [element] lies in a closed interval from [ClosedRange.startInclusive] to [ClosedRange.endInclusive]
 * with respect to contextual order.
 *
 * @receiver The closed range to check against.
 * @param _ The order context in which to perform the comparison.
 * @param element The element to check for containment in the interval.
 * @return `true` if the element lies in the closed interval, `false` otherwise.
 */
context(_: Order<Element>)
public operator fun <Element> ClosedRange<Element>.contains(element: Element): Boolean = element geq startInclusive && element leq endInclusive
/**
 * Checks if the provided [element] lies in a right-open interval from [OpenEndRange.startInclusive] to [OpenEndRange.endExclusive]
 * with respect to contextual order.
 *
 * @receiver The open-end range to check against.
 * @param _ The order context in which to perform the comparison.
 * @param element The element to check for containment in the interval.
 * @return `true` if the element lies in the right-open interval, `false` otherwise.
 */
context(_: Order<Element>)
public operator fun <Element> OpenEndRange<Element>.contains(element: Element): Boolean = element geq startInclusive && element lt endExclusive

/**
 * Converts a Kotlin standard library closed range to a Kone [ClosedRange].
 *
 * This allows using Kotlin's standard range syntax (`a..b`) with Kone's contextual comparison functions.
 *
 * @receiver The Kotlin closed range to convert.
 * @return A [ClosedRange] with the same bounds as this Kotlin range.
 */
public fun <Element : Comparable<Element>> KotlinClosedRange<Element>.toKoneClosedRange(): ClosedRange<Element> =
    ClosedRange(startInclusive = start, endInclusive = endInclusive)

/**
 * Converts a Kotlin standard library open-end range to a Kone [OpenEndRange].
 *
 * This allows using Kotlin's standard range syntax (`a..<b`) with Kone's contextual comparison functions.
 *
 * @receiver The Kotlin open-end range to convert.
 * @return An [OpenEndRange] with the same bounds as this Kotlin range.
 */
public fun <Element : Comparable<Element>> KotlinOpenEndRange<Element>.toKoneOpenEndRange(): OpenEndRange<Element> =
    OpenEndRange(startInclusive = start, endExclusive = endExclusive)