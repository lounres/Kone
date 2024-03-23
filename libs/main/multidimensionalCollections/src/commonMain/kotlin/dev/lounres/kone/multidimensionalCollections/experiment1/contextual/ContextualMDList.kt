/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual

import dev.lounres.kone.collections.contextual.KoneContextualCollection
import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.contentEquals
import dev.lounres.kone.collections.common.utils.fold
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.Shaped
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.utils.fold


public interface ContextualMDList<out E, in EE: Equality<@UnsafeVariance E>>: Shaped, KoneContextualCollection<E, EE> {
    public val dimension: UInt get() = shape.size
    public operator fun get(index: KoneUIntArray): E

    override val size: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }
    context(EE)
    override fun contains(element: @UnsafeVariance E): Boolean = ShapeStrides(shape).asSequence().any { element == get(it) }
}

public operator fun <E, EE: Equality<E>> ContextualMDList<E, EE>.get(vararg index: UInt): E = get(KoneUIntArray(index))

public interface ContextualSettableMDList<E, in EE: Equality<E>>: ContextualMDList<E, EE> {
    public operator fun set(index: KoneUIntArray, element: E)
}

public operator fun <E, EE: Equality<E>> ContextualSettableMDList<E, EE>.set(vararg index: UInt, element: E) {
    set(KoneUIntArray(index), element)
}

internal class ContextualMDListEquality<E>(val elementEquality: Equality<E>) : Equality<ContextualMDList<E, *>> {
    override fun ContextualMDList<E, *>.equalsTo(other: ContextualMDList<E, *>): Boolean {
        if (this === other) return true
        if (!(this.shape contentEquals other.shape)) return false

        for (index in ShapeStrides(this.shape)) if (elementEquality { this[index] neq other[index] }) return false

        return true
    }
}

public fun <E, EE: Equality<E>> contextualMDListEquality(elementEquality: EE): Equality<ContextualMDList<E, EE>> =
    ContextualMDListEquality(elementEquality)

internal class ContextualMDListHashing<E>(val elementHashing: Hashing<E>) : Hashing<ContextualMDList<E, *>> {
    override fun ContextualMDList<E, *>.equalsTo(other: ContextualMDList<E, *>): Boolean {
        if (this === other) return true
        if (!(this.shape contentEquals other.shape)) return false

        for (index in ShapeStrides(this.shape)) if (elementHashing { this[index] neq other[index] }) return false

        return true
    }

    override fun ContextualMDList<E, *>.hash(): Int = this.fold(0) { acc, element -> acc xor elementHashing { element.hash() } } // Maybe replace with `foldIndexed` with more complex hashing
}

public fun <E, EE: Hashing<E>> contextualMDListHashing(elementHashing: EE): Hashing<ContextualMDList<E, *>> =
    ContextualMDListHashing(elementHashing)