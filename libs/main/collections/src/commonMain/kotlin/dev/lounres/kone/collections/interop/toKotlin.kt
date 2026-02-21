/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.interop

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.isEmpty
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality


// region Conversion
public fun <Element> KoneIterable<Element>.toList(): List<Element> {
    val iterator = iterator()
    return List(size.toInt()) { iterator.getAndMoveNext() }
}
// endregion

// region Wrapping
internal class KoneIteratorAsKotlinIteratorWrapper<Element>(val iterator: KoneIterator<Element>): Iterator<Element> {
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): Element = iterator.getAndMoveNext()
}

public fun <Element> KoneIterator<Element>.asKotlinIterator(): Iterator<Element> = KoneIteratorAsKotlinIteratorWrapper(this)

internal class KoneIterableAsKotlinCollectionWrapper<Element>(
    val iterable: KoneIterable<Element>,
): Collection<Element> {
    override val size: Int get() = iterable.size.toInt()
    override fun isEmpty(): Boolean = iterable.isEmpty()
    
    override fun iterator(): Iterator<Element> = KoneIteratorAsKotlinIteratorWrapper(iterable.iterator())
    
    override fun contains(element: Element): Boolean = (Equality.defaultFor<Element>()) { iterable.contains(element) }
    override fun containsAll(elements: Collection<Element>): Boolean = elements.all { contains(it) }
}

public fun <Element> KoneIterable<Element>.asKotlinCollection(): Collection<Element> = KoneIterableAsKotlinCollectionWrapper(this)
// endregion