/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.KoneMutableUIntArray
import kotlin.math.max
import dev.lounres.kone.collections.aliases.*
import dev.lounres.kone.collections.aliases.UIntArray
import dev.lounres.kone.context.KoneContext


public interface ShapeIndexer: KoneContext, Iterable<UIntArray> {
    public val shape: ShapeND

    public fun UIntArray.hasNext()
    public fun UIntArray.next()

    public fun asSequence(): Sequence<UIntArray> = sequence {
        var index = KoneMutableUIntArray(shape.size) { 0u }
        TODO()
    }
    public override operator fun iterator(): Iterator<UIntArray> = asSequence().iterator()
}

public interface ShapeOffsets: ShapeIndexer {
    public fun offset(index: UIntArray): UInt
    public fun index(offset: UInt): UIntArray
    public val linearSize: UInt
}

public abstract class ShapeStrides : ShapeOffsets {
    internal abstract val strides: UIntArray

    public override fun offset(index: UIntArray): UInt {
        var res = 0
        index.forEachIndexed { i, value ->
            if (value !in 0u until shape[i.toUInt()]) throw IndexOutOfBoundsException("Index $value out of shape bounds: (0, ${this.shape[i]})")
            res += value * strides[i]

        }
        return res
    }

    // TODO introduce a fast way to calculate index of the next element?

    /**
     * Iterate over ND indices in a natural order
     */
    public override fun asSequence(): Sequence<UIntArray> = (0u until linearSize).asSequence().map(::index)

}

/**
 * Column-first [Strides]. Columns are represented as continuous arrays
 */
public class ColumnStrides(override val shape: ShapeND) : Strides() {
    override val linearSize: UInt get() = strides[shape.size]

    /**
     * Strides for memory access
     */
    override val strides: UIntArray = sequence {
        var current = 1u
        yield(1u)

        shape.forEach {
            current *= it
            yield(current)
        }
    }.toList().toUIntArray()

    override fun index(offset: Int): IntArray {
        val res = IntArray(shape.size)
        var current = offset
        var strideIndex = strides.size - 2

        while (strideIndex >= 0) {
            res[strideIndex] = (current / strides[strideIndex])
            current %= strides[strideIndex]
            strideIndex--
        }

        return res
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ColumnStrides) return false
        return shape.contentEquals(other.shape)
    }

    override fun hashCode(): Int = shape.contentHashCode()


    public companion object
}

/**
 * This [Strides] implementation follows the last dimension first convention
 * For more information: https://numpy.org/doc/stable/reference/generated/numpy.ndarray.strides.html
 *
 * @param shape the shape of the tensor.
 */
public class RowStrides(override val shape: ShapeND) : Strides() {

    override val strides: IntArray = run {
        val nDim = shape.size
        val res = IntArray(nDim)
        if (nDim == 0) return@run res

        var current = nDim - 1
        res[current] = 1

        while (current > 0) {
            res[current - 1] = max(1, shape[current]) * res[current]
            current--
        }
        res
    }

    override fun index(offset: Int): IntArray {
        val res = IntArray(shape.size)
        var current = offset
        var strideIndex = 0

        while (strideIndex < shape.size) {
            res[strideIndex] = (current / strides[strideIndex])
            current %= strides[strideIndex]
            strideIndex++
        }
        return res
    }

    override val linearSize: Int get() = shape.linearSize

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RowStrides) return false
        return shape.contentEquals(other.shape)
    }

    override fun hashCode(): Int = shape.contentHashCode()

    public companion object

}

@ThreadLocal
private val defaultStridesCache = HashMap<ShapeND, Strides>()

/**
 * Cached builder for default strides
 */
public fun Strides(shape: ShapeND): Strides = defaultStridesCache.getOrPut(shape) { RowStrides(shape) }
