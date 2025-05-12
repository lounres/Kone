/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.relations

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.hash
import dev.lounres.kone.context
import dev.lounres.kone.linearAlgebra.RowVector
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.relations.mdListEquality
import dev.lounres.kone.multidimensionalCollections.relations.mdListHashing


internal class RowVectorEquality<N>(elementEquality: Equality<N>) : Equality<RowVector<N>> {
    private val mdListEquality: Equality<MDList1<N>> = mdListEquality(elementEquality)
    override fun RowVector<N>.equalsTo(other: RowVector<N>): Boolean = context(mdListEquality) { this.coefficients eq other.coefficients }
}

public fun <N> rowVectorEquality(elementEquality: Equality<N>): Equality<RowVector<N>> =
    RowVectorEquality(elementEquality)

internal class RowVectorHashing<N>(elementHashing: Hashing<N>) : Hashing<RowVector<N>> {
    private val mdListHashing: Hashing<MDList1<N>> = mdListHashing(elementHashing)
    override fun RowVector<N>.hash(): Int = context(mdListHashing) { this.coefficients.hash() }
}

public fun <N> rowVectorHashing(elementHashing: Hashing<N>): Hashing<RowVector<N>> =
    RowVectorHashing(elementHashing)