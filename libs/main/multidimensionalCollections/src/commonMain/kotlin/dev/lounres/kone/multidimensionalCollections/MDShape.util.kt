/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.relations.equality
import dev.lounres.kone.collections.array.relations.hashing
import dev.lounres.kone.collections.map.getOrSet
import dev.lounres.kone.collections.map.implementations.KoneHashResizableMap
import dev.lounres.kone.collections.utils.anyIndexed


@Suppress("FunctionName")
public fun ColumnMDShapeStrides(shape: MDShape): MDShapeStrides = MDShapeStrides(shape, order = KoneUIntArray(shape.size) { it })
@Suppress("FunctionName")
public fun RowMDShapeStrides(shape: MDShape): MDShapeStrides = MDShapeStrides(shape, order = KoneUIntArray(shape.size) { shape.size - 1u - it })

// TODO: Replace with concurrent map!!!
//@ThreadLocal
private val defaultStridesCache = KoneHashResizableMap<MDShape, MDShapeStrides>(keyEquality = MDShape.equality(), keyHashing = MDShape.hashing())

public fun MDShapeStrides(shape: MDShape): MDShapeStrides = defaultStridesCache.getOrSet(shape) { ColumnMDShapeStrides(shape) }

public fun requireShapeEquality(left: MDShape, right: MDShape) {
    if (
        left.size != right.size ||
        left.anyIndexed { index, value -> value != right[index] }
    ) shapeMismatchException(left = left, right = right)
}

public fun requireIndexInShape(index: KoneUIntArray, shape: MDShape) {
    if (
        index.size != shape.size ||
        index.anyIndexed { dim, value -> value >= shape[dim] }
    ) indexOutOfShapeException(shape = shape, index = index)
}