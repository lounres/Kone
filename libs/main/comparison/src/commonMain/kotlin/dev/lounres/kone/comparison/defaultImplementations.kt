/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.comparison


internal object DefaultContext: Hashing<Any?> {
    override fun Any?.equalsTo(other: Any?): Boolean = this == other
    override fun Any?.hash(): Int = this.hashCode()
}

internal object AbsoluteContext: Hashing<Any?> {
    override fun Any?.equalsTo(other: Any?): Boolean = this === other
    override fun Any?.hash(): Int = this.hashCode()
}

@Suppress("UNCHECKED_CAST")
internal object DefaultOrderOnComparables: Order<Any?> {
    override fun Any?.compareWith(other: Any?): ComparisonResult =
        (this as Comparable<Any?>).compareTo(other).asComparisonResult()
}

@Suppress("UNCHECKED_CAST")
internal object DefaultComparatorOnComparables: Comparator<Any?> {
    override fun compare(left: Any?, right: Any?): ComparisonResult =
        (left as Comparable<Any?>).compareTo(right).asComparisonResult()
}