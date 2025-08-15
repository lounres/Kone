/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.relations

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.getOrDefault
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.jvm.JvmField
import kotlin.jvm.JvmInline
import kotlin.Comparator as KotlinStdlibComparator


/**
 * Describes result of two elements comparison. See [Order] for the main application.
 *
 * Names of the enumerated elements say for themselves.
 */
public enum class ComparisonResult(@JvmField internal val kotlinComparisonResult: Int) {
    LeftIsGreaterThanRight(1), LeftIsLessThanRight(-1), Equal(0);
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
 */
public interface Order<in Element> : KoneContext {
    /**
     * Compares [this] and [other] elements.
     */
    public infix fun Element.compareWith(other: Element): ComparisonResult
    
    public companion object;
    
    /**
     * Registry key for [Order] interface in [KoneContextRegistry].
     */
    public class Key<Element>(
        elementType: SuppliedType,
    ) : RegistryKey<Order<Element>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.relations.Order",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = IN,
                        type = elementType
                    )
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

/**
 * Shortcut for getting [Order] context for the given [suppliedElementType].
 * Throws if there is no such context in the registry.
 */
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> Order.Companion.getFor(suppliedElementType: SuppliedType): Order<Element> =
    koneContextRegistry[Order.Key(suppliedElementType)]
/**
 * Shortcut for getting [Order] context for the given [suppliedElementType]
 * or `null` if there is no such context in the registry.
 */
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> Order.Companion.getForOrNull(suppliedElementType: SuppliedType): Order<Element>? =
    koneContextRegistry.getOrNull(Order.Key(suppliedElementType))
/**
 * Shortcut for getting [Order] context for the given [suppliedElementType]
 * or [default] context if there is no such context in the registry.
 */
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> Order.Companion.getForOrDefault(suppliedElementType: SuppliedType, default: Order<Element>): Order<Element> =
    koneContextRegistry.getOrDefault(Order.Key(suppliedElementType), default)
/**
 * Shortcut for getting [Order] context for the given [suppliedElementType]
 * or compute [block] to get such context if there is no such context in the registry.
 */
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> Order.Companion.getForOrElse(suppliedElementType: SuppliedType, block: () -> Order<Element>): Order<Element> =
    koneContextRegistry.getOrElse(Order.Key(suppliedElementType), block)

context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun <Element: Comparable<Element>> Order.Companion.setDefaultFor(suppliedElementType: SuppliedType) {
    koneContextRegistryBuilder[Order.Key<Element>(suppliedElementType)] = Order.defaultFor<Element>()
}

/**
 * Provides comparison of two elements. Alternative of [Kotlin stlib Comparator][KotlinStdlibComparator] but with result of type [ComparisonResult].
 */
public fun interface Comparator<in Element> {
    /**
     * Compares [left] and [right] elements.
     */
    public fun compare(left: Element, right: Element): ComparisonResult
    
    public companion object
}

/**
 * Shortcut to convert comparison result from [Comparable]'s and [Kotlin stlib Comparator][KotlinStdlibComparator]'s terms to
 * [Order]'s and Kone [Comparator]'s terms.
 */
public fun Int.asComparisonResult(): ComparisonResult =
    when {
        this > 0 -> ComparisonResult.LeftIsGreaterThanRight
        this < 0 -> ComparisonResult.LeftIsLessThanRight
        else -> ComparisonResult.Equal
    }

/**
 * Shortcut to convert comparison result from [Comparable]'s and [Order]'s terms to
 * [Kotlin stlib Comparator][KotlinStdlibComparator]'s and Kone [Comparator]'s terms.
 */
public fun ComparisonResult.asKotlinComparisonResult(): Int = kotlinComparisonResult

/**
 * Converts Kone [Comparator] to [Kotlin stlib Comparator][KotlinStdlibComparator].
 */
public fun <Element> Comparator<Element>.asKotlinStdlib(): KotlinStdlibComparator<Element> =
    KotlinStdlibComparator { left, right -> compare(left, right).asKotlinComparisonResult() }

/**
 * Converts [Kotlin stlib Comparator][KotlinStdlibComparator] to Kone [Comparator].
 */
public fun <Element> KotlinStdlibComparator<Element>.asKone(): Comparator<Element> =
    Comparator { left, right -> compare(left, right).asComparisonResult() }

/**
 * Compares [this] and [other] elements.
 *
 * A bridge contextual function for [Order.compareWith].
 */
context(order: Order<Element>)
public infix fun <Element> Element.compareWith(other: Element): ComparisonResult = with(order) { this@compareWith.compareWith(other) }

/**
 * Compares [this] and [other] elements but in terms of the built-in language `compareTo` operator.
 *
 * The only usage is to import to make `<`, `<=`, `>`, and `>=` work in [Order] context.
 */
context(_: Order<Element>)
public infix operator fun <Element> Element.compareTo(other: Element): Int = this.compareWith(other).asKotlinComparisonResult()

/**
 * Alternative notation to `==` operator that uses [Order.compareTo] for comparison.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.coincidesWith(other: Element): Boolean = this.compareWith(other) == ComparisonResult.Equal
/**
 * Alternative notation to `!=` operator that uses [Order.compareTo] for comparison.
 */
// FIXME: KT-5351
context(_: Order<Element>)
public inline infix fun <Element> Element.notCoincidesWith(other: Element): Boolean = this.compareWith(other) != ComparisonResult.Equal
/**
 * Alternative notation to `>` operator that uses [Order.compareTo] for comparison.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.greaterThan(other: Element): Boolean = this.compareWith(other) == ComparisonResult.LeftIsGreaterThanRight
/**
 * Alternative notation to `>=` operator that uses [Order.compareTo] for comparison.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.greaterThanOrEqual(other: Element): Boolean = this.compareWith(other) != ComparisonResult.LeftIsLessThanRight
/**
 * Alternative notation to `<` operator that uses [Order.compareTo] for comparison.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.lessThen(other: Element): Boolean = this.compareWith(other) == ComparisonResult.LeftIsLessThanRight
/**
 * Alternative notation to `<=` operator that uses [Order.compareTo] for comparison.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.lessThenOrEqual(other: Element): Boolean = this.compareWith(other) != ComparisonResult.LeftIsGreaterThanRight
/**
 * Alternative notation to `>` operator that uses [Order.compareTo] for comparison.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.gt(other: Element): Boolean = this greaterThan other
/**
 * Alternative notation to `>=` operator that uses [Order.compareTo] for comparison.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.geq(other: Element): Boolean = this greaterThanOrEqual other
/**
 * Alternative notation to `<` operator that uses [Order.compareTo] for comparison.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.lt(other: Element): Boolean = this lessThen other
/**
 * Alternative notation to `<=` operator that uses [Order.compareTo] for comparison.
 */
context(_: Order<Element>)
public inline infix fun <Element> Element.leq(other: Element): Boolean = this lessThenOrEqual other

/**
 * Returns the smaller of two values [a] and [b].
 */
context(_: Order<Element>)
public fun <Element> minOf(a: Element, b: Element): Element = if (a leq b) a else b
/**
 * Returns the greater of two values [a] and [b].
 */
context(_: Order<Element>)
public fun <Element> maxOf(a: Element, b: Element): Element = if (a geq b) a else b
/**
 * Returns the smallest value from [elements]. If [elements] is empty throws [IllegalArgumentException].
 *
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
 * @throws IllegalArgumentException If [elements] is empty.
 */
context(_: Order<Element>)
public fun <Element> maxOf(vararg elements: Element): Element {
    require(elements.isNotEmpty()) { "Cannot calculate maximum of an empty collection of elements" }
    return elements.reduce { a, b -> maxOf(a, b) }
}

/**
 * [Order] builder from a [comparator] that compares the `left` and `right` elements to each other.
 */
public inline fun <Element> Order(crossinline comparator: (left: Element, right: Element) -> ComparisonResult): Order<Element> =
    object : Order<Element> {
        override fun Element.compareWith(other: Element): ComparisonResult = comparator(this, other)
    }

/**
 * [Order] builder from a [comparator] that compares the `left` and `right` elements to each other.
 */
public fun <Element> Comparator<Element>.asOrder(): Order<Element> =
    object : Order<Element> {
        override fun Element.compareWith(other: Element): ComparisonResult = this@asOrder.compare(this, other)
    }

/**
 * Returns [Order] instance which [Order.compareTo] operator just uses [Comparable.compareTo] operator's result as a return value.
 */
public fun <Element: Comparable<Element>> Order.Companion.defaultFor(): Order<Element> = DefaultOrderOnComparables
/**
 * Returns [Comparator] instance which [Comparator.compare] operator just uses [Comparable.compareTo] operator's result as a return value.
 */
public fun <Element: Comparable<Element>> Comparator.Companion.defaultFor(): Comparator<Element> = DefaultComparatorOnComparables
/**
 * Converts provided [Order] receiver into [Comparator] that delegates its [Comparator.compare] operator to
 * [Order.compareTo] operator.
 */
public fun <Element> Order<Element>.asComparator(): Comparator<Element> = Comparator { left, right -> left.compareWith(right) }
/**
 * Converts provided [Order] context receiver into [Comparator] that delegates its [Comparator.compare] operator to
 * [Order.compareTo] operator.
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
 */
context(_: Order<Element>)
public fun <Target, Element> Comparator.Companion.byOrdered(vararg selectors: (Target) -> Element): Comparator<Target> = Comparator { a, b ->
    for (s in selectors) {
        val comparisonResult = s(a).compareWith(s(b))
        if (comparisonResult != ComparisonResult.Equal) return@Comparator comparisonResult
    }
    return@Comparator ComparisonResult.Equal
}

/**
 * A wrapper data class that contains values [start] and [endInclusive] to be used by [ClosedRange.contains] operator that checks
 * if the provided value lies in a closed interval `[start; endInclusive]`.
 */
@JvmInline // There might be a problem with the MFVC and context parameters. See KT-72538 for more.
public value class ClosedRange<out Element>(public val start: Element, public val endInclusive: Element)
/**
 * A wrapper data class that contains values [start] and [endExclusive] to be used by [RightOpenRange.contains] operator that checks
 * if the provided value lies in a right-open interval `[start; endExclusive)`.
 */
@JvmInline // There might be a problem with the MFVC and context parameters. See KT-72538 for more.
public value class RightOpenRange<out Element>(public val start: Element, public val endExclusive: Element)

/**
 * Creates [ClosedRange] instance to be used by [ClosedRange.contains] operator that checks if the provided value
 * lies in a closed interval from [this] to [other].
 */
public operator fun <Element> Element.rangeTo(other: Element): ClosedRange<Element> = ClosedRange(this, other)
/**
 * Creates [RightOpenRange] instance to be used by [RightOpenRange.contains] operator that checks if the provided value
 * lies in a right-open interval from [this] to [other].
 */
public operator fun <Element> Element.rangeUntil(other: Element): RightOpenRange<Element> = RightOpenRange(this, other)

/**
 * Checks if the provided [element] lies in a closed interval from [ClosedRange.start] to [ClosedRange.endInclusive]
 * with respect to contextual order.
 */
context(_: Order<Element>)
public operator fun <Element> ClosedRange<Element>.contains(element: Element): Boolean = element geq start && element leq endInclusive
/**
 * Checks if the provided [element] lies in a right-open interval from [RightOpenRange.start] to [RightOpenRange.endExclusive]
 * with respect to contextual order.
 */
context(_: Order<Element>)
public operator fun <Element> RightOpenRange<Element>.contains(element: Element): Boolean = element geq start && element lt endExclusive