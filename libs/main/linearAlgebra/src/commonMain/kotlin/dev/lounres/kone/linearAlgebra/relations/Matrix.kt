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
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.relations.mdListEquality
import dev.lounres.kone.multidimensionalCollections.relations.mdListHashing


internal class MatrixEquality<N>(elementEquality: Equality<N>) : Equality<Matrix<N>> {
    private val mdListEquality: Equality<MDList2<N>> = mdListEquality(elementEquality)
    override fun Matrix<N>.equalsTo(other: Matrix<N>): Boolean = context(mdListEquality) { this.coefficients eq other.coefficients }
}

public fun <N> matrixEquality(elementEquality: Equality<N>): Equality<Matrix<N>> =
    MatrixEquality(elementEquality)

internal class MatrixHashing<N>(elementHashing: Hashing<N>) : Hashing<Matrix<N>> {
    private val mdListHashing: Hashing<MDList2<N>> = mdListHashing(elementHashing)
    override fun Matrix<N>.hash(): Int = context(mdListHashing) { this.coefficients.hash() }
}

public fun <N> matrixHashing(elementHashing: Hashing<N>): Hashing<Matrix<N>> =
    MatrixHashing(elementHashing)