/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.relations

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.hash
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.relations.mdListEquality
import dev.lounres.kone.multidimensionalCollections.relations.mdListHashing


internal class ColumnVectorEquality<N>(elementEquality: Equality<N>) : Equality<ColumnVector<N>> {
    private val mdListEquality: Equality<MDList1<N>> = mdListEquality(elementEquality)
    override fun ColumnVector<N>.equalsTo(other: ColumnVector<N>): Boolean = context(mdListEquality) { this.coefficients eq other.coefficients }
}

public fun <N> columnVectorEquality(elementEquality: Equality<N>): Equality<ColumnVector<N>> =
    ColumnVectorEquality(elementEquality)

internal class ColumnVectorHashing<N>(elementHashing: Hashing<N>) : Hashing<ColumnVector<N>> {
    private val mdListHashing: Hashing<MDList1<N>> = mdListHashing(elementHashing)
    override fun ColumnVector<N>.hash(): Int = context(mdListHashing) { this.coefficients.hash() }
}

public fun <N> columnVectorHashing(elementHashing: Hashing<N>): Hashing<ColumnVector<N>> =
    ColumnVectorHashing(elementHashing)