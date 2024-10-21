/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.implementations.KoneResizableHashMap
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.repeat


public typealias Shape = KoneUIntArray

public fun Shape(vararg dims: UInt): Shape = koneUIntArrayOf(*dims)

public interface ShapeIndexer: KoneContext, Iterable<KoneUIntArray> {
    public val shape: Shape

    public fun KoneUIntArray.hasNext(): Boolean
    public fun KoneUIntArray.next(): KoneUIntArray

    public fun asSequence(): Sequence<KoneUIntArray> = sequence {
        if (shape.any { it == 0u }) return@sequence
        var index = KoneUIntArray(shape.size) { 0u }
        while (true) {
            yield(index)
            if (!index.hasNext()) break
            index = index.next()
        }
    }
    public override operator fun iterator(): Iterator<KoneUIntArray> = asSequence().iterator()
}

public interface ShapeOffsetting: ShapeIndexer {
    public fun offset(index: KoneUIntArray): UInt
    public fun index(offset: UInt): KoneUIntArray
    public val linearSize: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }
}

public class ShapeStrides(
    override val shape: Shape,
    internal val order: KoneUIntArray,
): ShapeOffsetting {
    internal val strides: KoneUIntArray
    override val linearSize: UInt

    init {
        strides = KoneMutableUIntArray(order.size).apply {
            var last = 1u
            for (i in order) {
                this[i] = last
                last *= shape[i]
            }
            linearSize = last
        }.toKoneUIntArray()
    }

    override fun index(offset: UInt): KoneUIntArray = KoneMutableUIntArray(shape.size).apply {
        var remainder = offset
        for (i in shape.lastIndex downTo 0u) {
            this[order[i]] = remainder / strides[order[i]]
            remainder %= strides[order[i]]
        }
    }.toKoneUIntArray()

    public override fun offset(index: KoneUIntArray): UInt {
        var res = 0u
        index.forEachIndexed { i, value ->
            if (value !in 0u until shape[i]) throw IndexOutOfBoundsException("Index $value out of shape bounds: (0, ${this.shape[i]})")
            res += value * strides[i]

        }
        return res
    }

    override fun KoneUIntArray.hasNext(): Boolean = anyIndexed { index, value -> shape[index] > value + 1u }

    override fun KoneUIntArray.next(): KoneUIntArray =
        KoneMutableUIntArray(shape.size) { this[it] }.apply {
            for (i in order) {
                if (this[i] == shape[i]-1u) {
                    this[i] = 0u
                } else {
                    this[i]++
                    return@apply
                }
            }
            throw IllegalArgumentException("There is no next index array")
        }.toKoneUIntArray()
}

@Suppress("FunctionName")
public fun ColumnShapeStrides(shape: Shape): ShapeStrides = ShapeStrides(shape, order = KoneUIntArray(shape.size) { it })
@Suppress("FunctionName")
public fun RowShapeStrides(shape: Shape): ShapeStrides = ShapeStrides(shape, order = KoneUIntArray(shape.size) { shape.size - 1u - it })

private object ShapeHashing: Hashing<Shape> {
    override fun Shape.equalsTo(other: Shape): Boolean {
        if (this.size != other.size) return false

        for (index in 0u ..< this.size) if (this[index] != other[index]) return false

        return true
    }

    override fun Shape.hash(): Int {
        var hashCode = 1
        repeat(size) {
            hashCode = 31 * hashCode + this[it].toInt()
        }
        return hashCode
    }
}

//@ThreadLocal
private val defaultStridesCache = KoneResizableHashMap<Shape, _, ShapeStrides, _>(keyContext = ShapeHashing, valueContext = defaultEquality())

public fun ShapeStrides(shape: Shape): ShapeStrides = defaultStridesCache.getOrSet(shape) { ColumnShapeStrides(shape) }

public class ShapeMismatchException(public val left: Shape, public val right: Shape) :
    RuntimeException("Shapes $left and $right mismatch.")

public class IndexOutOfShapeException(public val shape: Shape, public val index: KoneUIntArray) :
    RuntimeException("Index $index is out of shape $shape")

public fun requireShapeEquality(left: Shape, right: Shape) {
    if (
        left.size != right.size ||
        left.anyIndexed { index, value -> value != right[index] }
    ) throw ShapeMismatchException(left = left, right = right)
}

public fun requireIndexInShape(index: KoneUIntArray, shape: Shape) {
    if (
        index.size != shape.size ||
        index.anyIndexed { dim, value -> value >= shape[dim] }
    ) throw IndexOutOfShapeException(shape = shape, index = index)
}