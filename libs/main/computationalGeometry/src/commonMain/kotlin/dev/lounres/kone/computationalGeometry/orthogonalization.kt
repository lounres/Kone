/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneMutableIterableList
import dev.lounres.kone.collections.implementations.KoneFixedCapacityArrayList
import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order


internal data class GramSchmidtOrtogonalizationIntermediateState<N, NE: Equality<N>>(
    val orthogonalizedBasis: KoneMutableIterableList<Vector<N>>,
    var product: N,
    val exclusiveProducts: KoneMutableIterableList<N>,
)

context(MutablePolytopicConstruction<N, *, *>)
internal fun <N, A> GramSchmidtOrtogonalizationIntermediateState<N, A>.clone(): GramSchmidtOrtogonalizationIntermediateState<N, A> where A: Ring<N>, A: Order<N> =
    GramSchmidtOrtogonalizationIntermediateState(
        orthogonalizedBasis = KoneFixedCapacityArrayList(orthogonalizedBasis.size, spaceDimension) { orthogonalizedBasis[it] },
        product = product,
        exclusiveProducts = KoneFixedCapacityArrayList(exclusiveProducts.size, spaceDimension) { exclusiveProducts[it] }
    )

context(EuclideanSpace<N, A>)
internal fun <N, A> GramSchmidtOrtogonalizationIntermediateState<N, A>.gramSchmidtOrthogonalizationStep(newVector: Vector<N>) where A: Ring<N>, A: Order<N> {
    val newIndex = orthogonalizedBasis.size
    val newOrthogonalizedVector = (0u..<newIndex).fold(newVector * product) { acc, index ->
        val previousOrthogonalizedVector = orthogonalizedBasis[index]
        acc - previousOrthogonalizedVector * (previousOrthogonalizedVector dot newVector) * exclusiveProducts[index]
    }
    orthogonalizedBasis.add(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    for (j in 0u..<newIndex) this@EuclideanSpace {
        exclusiveProducts[j] *= currentNorm
    }
    exclusiveProducts.add(product)
    this@EuclideanSpace { product *= currentNorm }
}

context(EuclideanSpace<N, A>)
internal fun <N, A> KoneIterableList<Vector<N>>.gramSchmidtOrtogonalization(): KoneIterableList<Vector<N>> where A: Ring<N>, A: Order<N> {
    val result = GramSchmidtOrtogonalizationIntermediateState<N, A>(
        orthogonalizedBasis = KoneFixedCapacityArrayList(size),
        product = numberRing.one,
        exclusiveProducts = KoneFixedCapacityArrayList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}