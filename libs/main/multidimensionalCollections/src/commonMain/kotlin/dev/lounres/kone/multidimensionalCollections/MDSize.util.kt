/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.contentEquals
import dev.lounres.kone.collections.array.contentHashCode
import dev.lounres.kone.collections.map.getOrSet
import dev.lounres.kone.collections.map.implementations.KoneHashResizableMap
import dev.lounres.kone.collections.utils.anyIndexed
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing


public infix fun MDSize.contentEquals(other: MDSize): Boolean = this.sizes contentEquals other.sizes

public fun MDSize.contentHashCode(): Int = this.sizes.contentHashCode()

@Suppress("FunctionName")
public fun ColumnMDSizeStrides(size: MDSize): MDSizeStrides = MDSizeStrides(size, order = KoneUIntArray(size.size) { it })
@Suppress("FunctionName")
public fun RowMDSizeStrides(size: MDSize): MDSizeStrides = MDSizeStrides(size, order = KoneUIntArray(size.size) { size.size - 1u - it })

// TODO: Replace with concurrent map!!!
//@ThreadLocal
private val defaultStridesCache = KoneHashResizableMap<MDSize, MDSizeStrides>(keyEquality = MDSize.equality(), keyHashing = MDSize.hashing())

public fun MDSizeStrides(size: MDSize): MDSizeStrides = defaultStridesCache.getOrSet(size) { ColumnMDSizeStrides(size) }

public fun requireMDSizeEquality(left: MDSize, right: MDSize) {
    if (!(left.sizes contentEquals right.sizes)) mdSizeMismatchException(left = left, right = right)
}

public fun requireIndexInSize(index: MDIndex, size: MDSize) {
    if (
        index.size != size.size ||
        index.anyIndexed { dim, value -> value >= size[dim] }
    ) mdIndexOutOfSizeException(size = size, index = index)
}