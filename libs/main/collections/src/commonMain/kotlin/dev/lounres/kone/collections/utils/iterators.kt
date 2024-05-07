/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.*
import kotlin.jvm.JvmInline


@Suppress("NOTHING_TO_INLINE")
public inline operator fun <E> KoneIterator<E>.iterator(): KoneIterator<E> = this

public fun <E> KoneReversibleIterator<E>.reversed(): KoneReversibleIterator<E> = KoneReversedIteratorImpl(this)

@JvmInline
internal value class KoneReversedIteratorImpl<E>(override val iterator: KoneReversibleIterator<E>) : KoneReversedIterator<E>
internal interface KoneReversedIterator<E> : KoneReversibleIterator<E> {
    val iterator: KoneReversibleIterator<E>

    override fun hasNext(): Boolean = iterator.hasPrevious()
    override fun getNext(): E = iterator.getPrevious()
    override fun moveNext() {
        iterator.movePrevious()
    }

    override fun hasPrevious(): Boolean = iterator.hasNext()
    override fun getPrevious(): E = iterator.getNext()
    override fun movePrevious() {
        iterator.moveNext()
    }
}

public fun <E> KoneReversibleSettableIterator<E>.reversed(): KoneReversibleSettableIterator<E> = KoneReversedSettableIteratorImpl(this)

@JvmInline
internal value class KoneReversedSettableIteratorImpl<E>(override val iterator: KoneReversibleSettableIterator<E>) : KoneReversedSettableIterator<E>
internal interface KoneReversedSettableIterator<E> : KoneReversedIterator<E>, KoneReversibleSettableIterator<E> {
    override val iterator: KoneReversibleSettableIterator<E>

    override fun setNext(element: E) {
        iterator.setPrevious(element)
    }

    override fun setPrevious(element: E) {
        iterator.setNext(element)
    }
}

public fun <E> KoneReversibleExtendableIterator<E>.reversed(): KoneReversibleExtendableIterator<E> = KoneReversedExtendableIteratorImpl(this)

@JvmInline
internal value class KoneReversedExtendableIteratorImpl<E>(override val iterator: KoneReversibleExtendableIterator<E>) : KoneReversedExtendableIterator<E>
internal interface KoneReversedExtendableIterator<E> : KoneReversedIterator<E>, KoneReversibleExtendableIterator<E> {
    override val iterator: KoneReversibleExtendableIterator<E>

    override fun addNext(element: E) {
        iterator.addPrevious(element)
    }

    override fun addPrevious(element: E) {
        iterator.addNext(element)
    }
}

public fun <E> KoneReversibleRemovableIterator<E>.reversed(): KoneReversibleRemovableIterator<E> = KoneReversedRemovableIteratorImpl(this)

@JvmInline
internal value class KoneReversedRemovableIteratorImpl<E>(override val iterator: KoneReversibleRemovableIterator<E>) : KoneReversedRemovableIterator<E>
internal interface KoneReversedRemovableIterator<E> : KoneReversedIterator<E>, KoneReversibleRemovableIterator<E> {
    override val iterator: KoneReversibleRemovableIterator<E>

    override fun removeNext() {
        iterator.removePrevious()
    }

    override fun removePrevious() {
        iterator.removeNext()
    }
}

public fun <E> KoneReversibleMutableIterator<E>.reversed(): KoneReversibleMutableIterator<E> = KoneReversedMutableIteratorImpl(this)

@JvmInline
internal value class KoneReversedMutableIteratorImpl<E>(override val iterator: KoneReversibleMutableIterator<E>) : KoneReversedMutableIterator<E>
internal interface KoneReversedMutableIterator<E> : KoneReversedSettableIterator<E>, KoneReversedExtendableIterator<E>, KoneReversedRemovableIterator<E>, KoneReversibleMutableIterator<E> {
    override val iterator: KoneReversibleMutableIterator<E>
}

public fun <E> KoneLinearIterator<E>.reversed(): KoneLinearIterator<E> = KoneReversedLinearIteratorImpl(this)

@JvmInline
internal value class KoneReversedLinearIteratorImpl<E>(override val iterator: KoneLinearIterator<E>) : KoneReversedLinearIterator<E>
internal interface KoneReversedLinearIterator<E> : KoneReversedIterator<E>, KoneLinearIterator<E> {
    override val iterator: KoneLinearIterator<E>

    override fun nextIndex(): UInt = iterator.previousIndex()

    override fun previousIndex(): UInt = iterator.nextIndex()
}

public fun <E> KoneSettableLinearIterator<E>.reversed(): KoneSettableLinearIterator<E> = KoneReversedSettableLinearIteratorImpl(this)

@JvmInline
internal value class KoneReversedSettableLinearIteratorImpl<E>(override val iterator: KoneSettableLinearIterator<E>) : KoneReversedSettableLinearIterator<E>
internal interface KoneReversedSettableLinearIterator<E> : KoneReversedLinearIterator<E>, KoneReversedSettableIterator<E>, KoneSettableLinearIterator<E> {
    override val iterator: KoneSettableLinearIterator<E>
}

public fun <E> KoneExtendableLinearIterator<E>.reversed(): KoneExtendableLinearIterator<E> = KoneReversedExtendableLinearIteratorImpl(this)

@JvmInline
internal value class KoneReversedExtendableLinearIteratorImpl<E>(override val iterator: KoneExtendableLinearIterator<E>) : KoneReversedExtendableLinearIterator<E>
internal interface KoneReversedExtendableLinearIterator<E> : KoneReversedLinearIterator<E>, KoneReversedExtendableIterator<E>, KoneExtendableLinearIterator<E> {
    override val iterator: KoneExtendableLinearIterator<E>
}

public fun <E> KoneRemovableLinearIterator<E>.reversed(): KoneRemovableLinearIterator<E> = KoneReversedRemovableLinearIteratorImpl(this)

@JvmInline
internal value class KoneReversedRemovableLinearIteratorImpl<E>(override val iterator: KoneRemovableLinearIterator<E>) : KoneReversedRemovableLinearIterator<E>
internal interface KoneReversedRemovableLinearIterator<E> : KoneReversedLinearIterator<E>, KoneReversedRemovableIterator<E>, KoneRemovableLinearIterator<E> {
    override val iterator: KoneRemovableLinearIterator<E>
}

public fun <E> KoneMutableLinearIterator<E>.reversed(): KoneMutableLinearIterator<E> = KoneReversedMutableLinearIteratorImpl(this)

@JvmInline
internal value class KoneReversedMutableLinearIteratorImpl<E>(override val iterator: KoneMutableLinearIterator<E>) : KoneReversedMutableLinearIterator<E>
internal interface KoneReversedMutableLinearIterator<E> : KoneReversedMutableIterator<E>, KoneReversedSettableLinearIterator<E>, KoneReversedExtendableLinearIterator<E>, KoneReversedRemovableLinearIterator<E>, KoneMutableLinearIterator<E> {
    override val iterator: KoneMutableLinearIterator<E>
}