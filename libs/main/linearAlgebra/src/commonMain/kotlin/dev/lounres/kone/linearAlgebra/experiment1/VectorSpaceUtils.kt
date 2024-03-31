/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.feature.getFeature
import dev.lounres.kone.multidimensionalCollections.experiment1.SettableMDListTransformer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public inline operator fun <N, A: Ring<N>, R> VectorSpace<N, A>.invoke(block: context(A, SettableMDListTransformer, VectorSpace<N, A>) () -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.defaultSettableMdListTransformer, this)
}

public val <N, A: Ring<N>> A.vectorSpace: VectorSpace<N, A>
    get() = VectorSpace(this, SettableMDListTransformer())

public fun <N, NE: Equality<N>, A: Ring<N>> A.vectorSpace(
    mdListTransformer: SettableMDListTransformer = SettableMDListTransformer(),
): VectorSpace<N, A> =
    VectorSpace(this, mdListTransformer)

public inline fun <N, NE: Equality<N>, A: Ring<N>, R> A.vectorSpace(
    mdListTransformer: SettableMDListTransformer = SettableMDListTransformer(),
    block: context(A, SettableMDListTransformer, VectorSpace<N, A>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return VectorSpace(this, mdListTransformer).invoke(block)
}

context(VectorSpace<N, A>)
public val <N, A> Matrix<N>.isSymmetric: Boolean where A : Ring<N>
    get() = (this.getFeature<_, SymmetricMatrixFeature>() ?: throw IllegalArgumentException("Could not check symmetricity of the matrix")).value

context(VectorSpace<N, A>)
public val <N, A> Matrix<N>.isAntisymmetric: Boolean where A : Ring<N>
    get() = (this.getFeature<_, AntisymmetricMatrixFeature>() ?: throw IllegalArgumentException("Could not check antisymmetricity of the matrix")).value

context(VectorSpace<N, A>)
public val <N, A> Matrix<N>.transpose: Matrix<N> where A : Ring<N>
    get() = (this.getFeature<_, TransposeMatrixFeature<N>>() ?: throw IllegalArgumentException("Could not compute transposed matrix")).transpose

context(VectorSpace<N, A>)
public val <N, A> Matrix<N>.det: N where A : Ring<N>
    get() = (this.getFeature<_, DeterminantMatrixFeature<N>>() ?: throw IllegalArgumentException("Could not compute determinant")).determinant

context(VectorSpace<N, A>)
public val <N, A> Matrix<N>.reciprocal: Matrix<N> where A : Ring<N>
    get() = (this.getFeature<_, InvertibleMatrixFeature<N>>() ?: throw IllegalArgumentException("Could not compute reciprocal matrix")).inverseMatrix

context(VectorSpace<N, A>)
public val <N, A> Matrix<N>.adjugate: Matrix<N> where A : Ring<N>
    get() = (this.getFeature<_, AdjugateMatrixFeature<N>>() ?: throw IllegalArgumentException("Could not compute adjugate matrix")).adjugateMatrix

context(VectorSpace<N, A>)
public val <N, A> Matrix<N>.minor: MatrixMinorComputerFeature<N> where A : Ring<N>
    get() = this.getFeature<_, MatrixMinorComputerFeature<N>>() ?: throw IllegalArgumentException("Could not create minor computer of the matrix")