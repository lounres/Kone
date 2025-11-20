/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.relations

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareWith
import kotlin.math.min


public enum class EmptinessGlobalPosition {
    MINIMUM, MAXIMUM,
}

internal class KoneListLexicographicOrder<Element>(
    val elementOrder: Order<Element>,
    val emptinessGlobalPosition: EmptinessGlobalPosition = EmptinessGlobalPosition.MINIMUM,
) : Order<KoneList<Element>> {
    override fun KoneList<Element>.compareWith(other: KoneList<Element>): ComparisonResult {
        for (i in 0u ..< minOf(this.size, other.size)) {
            val result = elementOrder { this[i] compareWith other[i] }
            if (result != ComparisonResult.Equal) return result
        }
        return when {
            this.size < other.size -> when (emptinessGlobalPosition) {
                EmptinessGlobalPosition.MINIMUM -> ComparisonResult.LeftIsLessThanRight
                EmptinessGlobalPosition.MAXIMUM -> ComparisonResult.LeftIsGreaterThanRight
            }
            this.size > other.size -> when (emptinessGlobalPosition) {
                EmptinessGlobalPosition.MINIMUM -> ComparisonResult.LeftIsGreaterThanRight
                EmptinessGlobalPosition.MAXIMUM -> ComparisonResult.LeftIsLessThanRight
            }
            else -> ComparisonResult.Equal
        }
    }
}

public fun <Element> KoneList.Companion.lexicographicOrder(
    elementOrder: Order<Element>,
    emptinessGlobalPosition: EmptinessGlobalPosition = EmptinessGlobalPosition.MINIMUM,
): Order<KoneList<Element>> = KoneListLexicographicOrder(elementOrder, emptinessGlobalPosition)

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

public fun <Element> KoneList.Companion.lengthLexicographicOrder(elementOrder: Order<Element>): Order<KoneList<Element>> =
    KoneListLengthLexicographicOrder(elementOrder)