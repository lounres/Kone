/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.aliases.*
import dev.lounres.kone.collections.any
import dev.lounres.kone.collections.anyIndexed
import dev.lounres.kone.collections.fold
import dev.lounres.kone.collections.forEachIndexed
import dev.lounres.kone.context.KoneContext


public typealias Shape = UIntArray

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

public interface ShapeOffseter: ShapeIndexer {
    public fun offset(index: UIntArray): UInt
    public fun index(offset: UInt): UIntArray
    public val linearSize: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }
}

public abstract class ShapeStrides: ShapeOffseter {
    internal abstract val strides: UIntArray

    public override fun offset(index: UIntArray): UInt {
        var res = 0u
        index.forEachIndexed { i, value ->
            if (value !in 0u until shape[i]) throw IndexOutOfBoundsException("Index $value out of shape bounds: (0, ${this.shape[i]})")
            res += value * strides[i]

        }
        return res
    }
}

//public class ColumnStrides(override val shape: ShapeND): ShapeStrides() {
//    override val linearSize: UInt get() = strides[shape.size]
//
//    override val strides: UIntArray = shape.runningFold(1u) { acc, dim -> acc * dim }
//
//    override fun index(offset: Int): IntArray {
//        val res = IntArray(shape.size.toInt())
//        var current = offset
//        var strideIndex = strides.size - 2
//
//        while (strideIndex >= 0) {
//            res[strideIndex] = (current / strides[strideIndex])
//            current %= strides[strideIndex]
//            strideIndex--
//        }
//
//        return res
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is ColumnStrides) return false
//        return shape.contentEquals(other.shape)
//    }
//
//    override fun hashCode(): Int = shape.contentHashCode()
//
//
//    public companion object
//}
//
//public class RowStrides(override val shape: ShapeND) : Strides() {
//
//    override val strides: IntArray = run {
//        val nDim = shape.size
//        val res = IntArray(nDim)
//        if (nDim == 0) return@run res
//
//        var current = nDim - 1
//        res[current] = 1
//
//        while (current > 0) {
//            res[current - 1] = max(1, shape[current]) * res[current]
//            current--
//        }
//        res
//    }
//
//    override fun index(offset: Int): IntArray {
//        val res = IntArray(shape.size)
//        var current = offset
//        var strideIndex = 0
//
//        while (strideIndex < shape.size) {
//            res[strideIndex] = (current / strides[strideIndex])
//            current %= strides[strideIndex]
//            strideIndex++
//        }
//        return res
//    }
//
//    override val linearSize: Int get() = shape.linearSize
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is RowStrides) return false
//        return shape.contentEquals(other.shape)
//    }
//
//    override fun hashCode(): Int = shape.contentHashCode()
//
//    public companion object
//
//}
//
//@ThreadLocal
//private val defaultStridesCache = HashMap<ShapeND, Strides>()
//
//public fun Strides(shape: ShapeND): Strides = defaultStridesCache.getOrPut(shape) { RowStrides(shape) }

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

public interface WithShape {
    public val shape: Shape
    public val indexer: ShapeIndexer
}