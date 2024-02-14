/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.aliases.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.context.KoneContext


public typealias Shape = UIntArray

public fun Shape(vararg dims: UInt): Shape = koneUIntArrayOf(*dims)

public interface ShapeIndexer: KoneContext, Iterable<UIntArray> {
    public val shape: Shape

    public fun UIntArray.hasNext(): Boolean
    public fun UIntArray.next(): UIntArray

    public fun asSequence(): Sequence<UIntArray> = sequence {
        if (shape.any { it == 0u }) return@sequence
        var index = UIntArray(shape.size) { 0u }
        while (true) {
            yield(index)
            if (!index.hasNext()) break
            index = index.next()
        }
    }
    public override operator fun iterator(): Iterator<UIntArray> = asSequence().iterator()
}

public interface ShapeOffsetting: ShapeIndexer {
    public fun offset(index: UIntArray): UInt
    public fun index(offset: UInt): UIntArray
    public val linearSize: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }
}

public class ShapeStrides(
    override val shape: Shape,
    internal val order: UIntArray,
): ShapeOffsetting {
    internal val strides: UIntArray
    override val linearSize: UInt

    init {
        strides = MutableUIntArray(order.size).apply {
            var last = 1u
            for (i in order) {
                this[i] = last
                last *= shape[i]
            }
            linearSize = last
        }.toUIntArray()
    }

    override fun index(offset: UInt): UIntArray = MutableUIntArray(shape.size).apply {
        var remainder = offset
        for (i in shape.lastIndex downTo 0u) {
            this[order[i]] = remainder / strides[order[i]]
            remainder %= strides[order[i]]
        }
    }.toUIntArray()

    public override fun offset(index: UIntArray): UInt {
        var res = 0u
        index.forEachIndexed { i, value ->
            if (value !in 0u until shape[i]) throw IndexOutOfBoundsException("Index $value out of shape bounds: (0, ${this.shape[i]})")
            res += value * strides[i]

        }
        return res
    }

    override fun UIntArray.hasNext(): Boolean = anyIndexed { index, value -> shape[index] > value + 1u }

    override fun UIntArray.next(): UIntArray =
        MutableUIntArray(shape.size) { this[it] }.apply {
            for (i in order) {
                if (this[i] == shape[i]-1u) {
                    this[i] = 0u
                } else {
                    this[i]++
                    return@apply
                }
            }
            throw IllegalArgumentException("There is no next index array")
        }.toUIntArray()
}

public fun ColumnShapeStrides(shape: Shape): ShapeStrides = ShapeStrides(shape, order = UIntArray(shape.size) { it })
public fun RowShapeStrides(shape: Shape): ShapeStrides = ShapeStrides(shape, order = UIntArray(shape.size) { shape.size - 1u - it })

//@ThreadLocal
private val defaultStridesCache = HashMap<Shape, ShapeStrides>()

public fun ShapeStrides(shape: Shape): ShapeStrides = defaultStridesCache.getOrPut(shape) { ColumnShapeStrides(shape) }

public class ShapeMismatchException(public val left: Shape, public val right: Shape) :
    RuntimeException("Shapes $left and $right mismatch.")

public class IndexOutOfShapeException(public val shape: Shape, public val index: UIntArray) :
    RuntimeException("Index $index is out of shape $shape")

public fun requireShapeEquality(left: Shape, right: Shape) {
    if (
        left.size != right.size ||
        left.anyIndexed { index, value -> value != right[index] }
    ) throw ShapeMismatchException(left = left, right = right)
}

public fun requireIndexInShape(index: UIntArray, shape: Shape) {
    if (
        index.size != shape.size ||
        index.anyIndexed { dim, value -> value >= shape[dim] }
    ) throw IndexOutOfShapeException(shape = shape, index = index)
}

public interface Shaped {
    public val shape: Shape
}

public interface LinearlyShaped: Shaped {
    public val indexer: ShapeOffsetting
}