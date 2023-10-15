/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.aliases.*
import dev.lounres.kone.collections.fold
import dev.lounres.kone.collections.forEachIndexed
import dev.lounres.kone.context.KoneContext


public typealias ShapeND = UIntArray

public interface ShapeIndexer: KoneContext, Iterable<UIntArray> {
    public val shape: ShapeND

    public fun UIntArray.hasNext()
    public fun UIntArray.next(): UIntArray

    public fun asSequence(): Sequence<UIntArray>
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

    public override fun asSequence(): Sequence<UIntArray> = (0u until linearSize).asSequence().map(::index)
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

public interface WithShape {
    public val shape: ShapeND
    public val indexer: ShapeIndexer
}