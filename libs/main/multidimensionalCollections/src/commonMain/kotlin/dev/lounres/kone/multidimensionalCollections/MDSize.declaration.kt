/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableUIntArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.asKoneUIntArray
import dev.lounres.kone.collections.array.fill
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.build
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.utils.*
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline


@OptIn(DelicateCollectionsInheritanceAPI::class)
@Serializable
@JvmInline
public value class MDSize(public val sizes: KoneUIntArray) : KoneList<UInt> by sizes {
    override fun toString(): String = "MDSize$sizes"
    
    public companion object
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
@Serializable
@JvmInline
public value class MDIndex(public val indices: KoneUIntArray) : KoneList<UInt> by indices {
    override fun toString(): String = "MDIndex$indices"
    
    public companion object
}

public fun MDSize.Companion.of(vararg dims: UInt): MDSize = MDSize(KoneUIntArray(dims))

public fun MDIndex.Companion.of(vararg dims: UInt): MDIndex = MDIndex(KoneUIntArray(dims))

public interface MDSizeIndexer : KoneIterable<MDIndex> {
    public val mdSize: MDSize
    override val size: UInt get() = mdSize.fold(1u) { acc, dim -> acc * dim }

    public fun getFirst(): MDIndex
    public fun MDIndex.hasNext(): Boolean
    public fun MDIndex.getNext(): MDIndex

    public override fun iterator(): KoneIterator<MDIndex> =
        KoneIterator.build {
            if (mdSize.any { it == 0u }) return@build
            var index = getFirst()
            while (true) {
                yield(index)
                if (!index.hasNext()) break
                index = index.getNext()
            }
        }
}

context(indexer: MDSizeIndexer)
public fun MDIndex.hasNext(): Boolean = with(indexer) { this@hasNext.hasNext() }
context(indexer: MDSizeIndexer)
public fun MDIndex.getNext(): MDIndex = with(indexer) { this@getNext.getNext() }

public interface MDSizeOffsetting: MDSizeIndexer {
    public fun offset(index: MDIndex): UInt
    public fun index(offset: UInt): MDIndex
}

public class MDSizeStrides(
    override val mdSize: MDSize,
    internal val order: KoneUIntArray,
): MDSizeOffsetting {
    override val size: UInt
    internal val strides: KoneUIntArray

    init {
        require(mdSize.size == order.size) { "Strides order array must have the same length as MD size."}
        val size: UInt
        strides = KoneMutableUIntArray.fill(order.size).apply {
            var last = 1u
            for (i in order) {
                this[i] = last
                last *= mdSize[i]
            }
            size = last
        }.asKoneUIntArray()
        this.size = size
    }

    override fun index(offset: UInt): MDIndex = KoneMutableUIntArray.fill(mdSize.size).apply {
        var remainder = offset
        for (i in mdSize.lastIndex downTo 0u) {
            this[order[i]] = remainder / strides[order[i]]
            remainder %= strides[order[i]]
        }
    }.asKoneUIntArray().let { MDIndex(it) }

    public override fun offset(index: MDIndex): UInt {
        var res = 0u
        index.forEachIndexed { i, value ->
            if (value !in 0u until mdSize[i]) throw IndexOutOfBoundsException("Index $value out of size bounds: (0, ${this.mdSize[i]})")
            res += value * strides[i]

        }
        return res
    }
    
    override fun getFirst(): MDIndex = MDIndex(KoneUIntArray.fill(mdSize.size))

    override fun MDIndex.hasNext(): Boolean = anyIndexed { index, value -> mdSize[index] > value + 1u }

    override fun MDIndex.getNext(): MDIndex =
        KoneMutableUIntArray.generate(mdSize.size) { this[it] }.apply {
            for (i in order) {
                if (this[i] == mdSize[i]-1u) {
                    this[i] = 0u
                } else {
                    this[i]++
                    return@apply
                }
            }
            throw IllegalArgumentException("There is no next index array")
        }.asKoneUIntArray().let { MDIndex(it) }
}