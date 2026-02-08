/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks.algebraic

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.compareTo
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.hash
import dev.lounres.kone.contexts.invoke


// region Order
fun <T : Comparable<T>> tryComparableCompareTo(left: T, right: T) = left > right
fun <T> tryOrderCompareTo(left: T, right: T, order: Order<T>) = order { left > right }
fun <T> tryOrderCompareWith(left: T, right: T, order: Order<T>) = order { left gt right }
// endregion

// region Hashing
fun <T> tryAnyHashCode(element: T) = element.hashCode()
fun <T> tryHashingHash(element: T, hashing: Hashing<T>) = hashing { element.hash() }
//endregion