/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.relations

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.getMaybe
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.relations.withNoneAsLeast
import kotlin.jvm.JvmName


internal class KoneListLexicographicOrder<Element>(
    val elementOrder: Order<Maybe<Element>>,
) : Order<KoneList<Element>> {
    override fun KoneList<Element>.compareWith(other: KoneList<Element>): ComparisonResult {
        for (i in 0u ..< maxOf(this.size, other.size)) {
            val result = elementOrder { this.getMaybe(i) compareWith other.getMaybe(i) }
            if (result != ComparisonResult.Equal) return result
        }
        return ComparisonResult.Equal
    }
}

/**
 * Returns an [Order] instance for [KoneList] comparing lists lexicographically based on the given [elementOrder].
 *
 * Lists are compared element by element. When the lists have different lengths, an element missing in one of the
 * lists is represented as [None] via [getMaybe], and the comparison of a present element with a missing one is
 * determined by the given [elementOrder].
 *
 * @param Element The element type.
 * @receiver The list companion object.
 * @param elementOrder The order context for elements wrapped in [Maybe] used to compare corresponding elements of
 *        the lists, including cases where an element is missing in one of the lists.
 * @return An [Order] instance for lists comparing them lexicographically.
 */
public fun <Element> KoneList.Companion.lexicographicOrder(elementOrder: Order<Maybe<Element>>): Order<KoneList<Element>> =
    KoneListLexicographicOrder(elementOrder)

/**
 * Returns an [Order] instance for [KoneList] comparing lists lexicographically based on the given [elementOrder].
 *
 * Lists are compared element by element. When the lists have different lengths, a missing element is considered
 * to be less than any present element (see [withNoneAsLeast]).
 *
 * @param Element The element type.
 * @receiver The list companion object.
 * @param elementOrder The order context for elements used to compare corresponding elements of the lists.
 * @return An [Order] instance for lists comparing them lexicographically.
 */
@JvmName("lexicographicOrderWithNoneAsLeast")
public fun <Element> KoneList.Companion.lexicographicOrder(elementOrder: Order<Element>): Order<KoneList<Element>> =
    lexicographicOrder(elementOrder.withNoneAsLeast)

internal class KoneListLengthLexicographicOrder<Element>(
    val elementOrder: Order<Element>,
) : Order<KoneList<Element>> {
    override fun KoneList<Element>.compareWith(other: KoneList<Element>): ComparisonResult {
        when {
            this.size < other.size -> return ComparisonResult.LeftIsLessThanRight
            this.size > other.size -> return ComparisonResult.LeftIsGreaterThanRight
        }
        for (i in 0u ..< minOf(this.size, other.size)) {
            val result = elementOrder { this[i] compareWith other[i] }
            if (result != ComparisonResult.Equal) return result
        }
        return ComparisonResult.Equal
    }
}

/**
 * Returns an [Order] instance for [KoneList] comparing lists first by length and then lexicographically
 * based on the given [elementOrder].
 *
 * A shorter list is considered to be less than a longer one. If the lists have equal lengths, they are compared
 * element by element.
 *
 * @param Element The element type.
 * @receiver The list companion object.
 * @param elementOrder The order context for elements used to compare corresponding elements of the lists.
 * @return An [Order] instance for lists comparing them by length and then lexicographically.
 */
public fun <Element> KoneList.Companion.lengthLexicographicOrder(elementOrder: Order<Element>): Order<KoneList<Element>> =
    KoneListLengthLexicographicOrder(elementOrder)