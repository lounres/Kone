/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(DelicateImmutableArrayConstructor::class)

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.DelicateImmutableArrayConstructor
import dev.lounres.kone.collections.array.KoneMutableUIntArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.asKoneUIntArray
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.utils.*


public typealias MDShape = KoneUIntArray

@OptIn(DelicateImmutableArrayConstructor::class)
public fun MDShape(vararg dims: UInt): MDShape = KoneUIntArray(dims)

// TODO: Move to `collections` module
internal class MDShapeIndexerIterator(
    val iterator: Iterator<KoneUIntArray>
) : KoneIterator<KoneUIntArray> {
    var currentElement: KoneUIntArray? = null
    override fun hasNext(): Boolean = currentElement != null || iterator.hasNext()
    override fun getNext(): KoneUIntArray {
        if (!hasNext()) noNextElementInIteratorException()
        return currentElement ?: iterator.next().also { currentElement = it }
    }
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        if (currentElement != null) currentElement = null
        else iterator.next()
    }
}

public interface MDShapeIndexer : KoneIterable<KoneUIntArray> {
    public val shape: MDShape
    override val size: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }

    public fun KoneUIntArray.hasNext(): Boolean
    public fun KoneUIntArray.next(): KoneUIntArray

    public fun asSequence(): Sequence<KoneUIntArray> = sequence {
        if (shape.any { it == 0u }) return@sequence
        var index = KoneUIntArray(shape.size) { 0u } // TODO: Think about moving starting index to the interface level
        while (true) {
            yield(index)
            if (!index.hasNext()) break
            index = index.next()
        }
    }
    public override fun iterator(): KoneIterator<KoneUIntArray> = MDShapeIndexerIterator(asSequence().iterator()) // TODO: Reimplement `iterator { ... }` builder for Kone
}

context(indexer: MDShapeIndexer)
public fun KoneUIntArray.hasNext(): Boolean = with(indexer) { this@hasNext.hasNext() }
context(indexer: MDShapeIndexer)
public fun KoneUIntArray.next(): KoneUIntArray = with(indexer) { this@next.next() }

public interface MDShapeOffsetting: MDShapeIndexer {
    public fun offset(index: KoneUIntArray): UInt
    public fun index(offset: UInt): KoneUIntArray
}

public class MDShapeStrides(
    override val shape: MDShape,
    internal val order: KoneUIntArray,
): MDShapeOffsetting {
    override val size: UInt
    internal val strides: KoneUIntArray

    init {
        val size: UInt
        strides = KoneMutableUIntArray(order.size).apply {
            var last = 1u
            for (i in order) {
                this[i] = last
                last *= shape[i]
            }
            size = last
        }.asKoneUIntArray()
        this.size = size
    }

    override fun index(offset: UInt): KoneUIntArray = KoneMutableUIntArray(shape.size).apply {
        var remainder = offset
        for (i in shape.lastIndex downTo 0u) {
            this[order[i]] = remainder / strides[order[i]]
            remainder %= strides[order[i]]
        }
    }.asKoneUIntArray()

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
        }.asKoneUIntArray()
}