/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.compareTo
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.hash
import dev.lounres.kone.relations.reify
import dev.lounres.kone.relations.reifyMaybe
import dev.lounres.kone.relations.reifyOrNull
import dev.lounres.kone.contexts.invoke
import kotlinx.benchmark.Blackhole


// region Reification
fun tryReificationContains(element: Any?, reification: Reification<*>, blackhole: Blackhole) {
    blackhole.consume(element in reification)
}

fun tryReificationReifyMaybe(element: Any?, reification: Reification<*>, blackhole: Blackhole) {
    blackhole.consume(reification { reifyMaybe(element) })
}

fun tryReificationReifyOrNull(element: Any?, reification: Reification<*>, blackhole: Blackhole) {
    blackhole.consume(reification { reifyOrNull(element) })
}

fun tryReificationReify(element: Any?, reification: Reification<*>, blackhole: Blackhole) {
    blackhole.consume(reification { reify(element) })
}
// endregion

// region Equality
fun <T> tryAnyEquals(left: T, right: T, blackhole: Blackhole) {
    blackhole.consume(left == right)
}

fun <T> tryEqualityEqualsTo(left: T, right: T, equality: Equality<T>, blackhole: Blackhole) {
    blackhole.consume(equality { left eq right })
}
// endregion

// region Order
fun <T: Comparable<T>> tryComparableCompareTo(left: T, right: T, blackhole: Blackhole) {
    blackhole.consume(left > right)
}

fun <T> tryOrderCompareTo(left: T, right: T, order: Order<T>, blackhole: Blackhole) {
    blackhole.consume(order { left > right })
}

fun <T> tryOrderCompareWith(left: T, right: T, order: Order<T>, blackhole: Blackhole) {
    blackhole.consume(order { left gt right })
}
// endregion

// region Hashing
fun <T> tryAnyHashCode(element: T, blackhole: Blackhole) {
    blackhole.consume(element.hashCode())
}

fun <T> tryHashingHash(element: T, hashing: Hashing<T>, blackhole: Blackhole) {
    blackhole.consume(hashing { element.hash() })
}
//endregion