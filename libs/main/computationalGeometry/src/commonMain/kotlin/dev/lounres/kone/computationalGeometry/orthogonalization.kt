/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.common.KoneMutableIterableList
import dev.lounres.kone.collections.common.implementations.KoneFixedCapacityArrayList
import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.collections.contextual.KoneContextualMutableIterableList
import dev.lounres.kone.collections.contextual.implementations.KoneContextualFixedCapacityArrayList
import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order


internal data class GramSchmidtOrtogonalizationIntermediateState<N, NE: Equality<N>>(
    val orthogonalizedBasis: KoneContextualMutableIterableList<Vector<N>, Equality<Vector<N>>>,
    var product: N,
    val exclusiveProducts: KoneMutableIterableList<N>,
)

context(MutablePolytopicConstruction<N, *, *, *, *>)
internal fun <N, A> GramSchmidtOrtogonalizationIntermediateState<N, A>.clone(): GramSchmidtOrtogonalizationIntermediateState<N, A> where A: Ring<N>, A: Order<N> =
    GramSchmidtOrtogonalizationIntermediateState(
        orthogonalizedBasis = KoneContextualFixedCapacityArrayList(orthogonalizedBasis.size, spaceDimension) { orthogonalizedBasis[it] },
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
    orthogonalizedBasis.addAtTheEnd(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    for (j in 0u..<newIndex) this@EuclideanSpace {
        exclusiveProducts[j] *= currentNorm
    }
    exclusiveProducts.add(product)
    this@EuclideanSpace { product *= currentNorm }
}

context(EuclideanSpace<N, A>)
internal fun <N, A> KoneContextualIterableList<Vector<N>, *>.gramSchmidtOrtogonalization(): KoneContextualIterableList<Vector<N>, Equality<Vector<N>>> where A: Ring<N>, A: Order<N> {
    val result = GramSchmidtOrtogonalizationIntermediateState<N, A>(
        orthogonalizedBasis = KoneContextualFixedCapacityArrayList(size),
        product = numberRing.one,
        exclusiveProducts = KoneFixedCapacityArrayList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}