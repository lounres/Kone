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


internal data class GramSchmidtOrthogonalizationIntermediateState<N>(
    val orthogonalizedBasis: KoneMutableIterableList<Vector<N>>,
    var product: N,
    val exclusiveProducts: KoneMutableIterableList<N>,
)

context(MutablePolytopicConstruction<N, *, *>)
internal fun <N> GramSchmidtOrthogonalizationIntermediateState<N>.clone(): GramSchmidtOrthogonalizationIntermediateState<N> =
    GramSchmidtOrthogonalizationIntermediateState(
        orthogonalizedBasis = KoneFixedCapacityArrayList(orthogonalizedBasis.size, spaceDimension) { orthogonalizedBasis[it] },
        product = product,
        exclusiveProducts = KoneFixedCapacityArrayList(exclusiveProducts.size, spaceDimension) { exclusiveProducts[it] }
    )

context(A, EuclideanKategory<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState<N>.gramSchmidtOrthogonalizationUsage(newVector: Vector<N>): Vector<N> {
    // FIXME: KT-67840
//    (0u..<orthogonalizedBasis.size).fold(newVector * product) { acc, index ->
//        val previousOrthogonalizedVector = orthogonalizedBasis[index]
//        acc - previousOrthogonalizedVector * (previousOrthogonalizedVector dot newVector) * exclusiveProducts[index]
//    }
    var result = newVector * product
    var index = 0u
    while (index < orthogonalizedBasis.size) {
        val previousOrthogonalizedVector = orthogonalizedBasis[index]
        result -= previousOrthogonalizedVector * (previousOrthogonalizedVector dot newVector) * exclusiveProducts[index]
        index++
    }
    return result
}

context(A, EuclideanKategory<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState<N>.gramSchmidtOrthogonalizationExtension(newOrthogonalizedVector: Vector<N>) {
    val newIndex = orthogonalizedBasis.size
    orthogonalizedBasis.add(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    for (j in 0u..<newIndex) exclusiveProducts[j] *= currentNorm
    exclusiveProducts.add(product)
    product *= currentNorm
}

context(A, EuclideanKategory<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState<N>.gramSchmidtOrthogonalizationStep(newVector: Vector<N>) {
    gramSchmidtOrthogonalizationExtension(gramSchmidtOrthogonalizationUsage(newVector))
}

context(A, EuclideanKategory<N>)
internal fun <N, A: Ring<N>> KoneIterableList<Vector<N>>.gramSchmidtOrthogonalization(): KoneIterableList<Vector<N>> {
    val result = GramSchmidtOrthogonalizationIntermediateState<N>(
        orthogonalizedBasis = KoneFixedCapacityArrayList(size),
        product = one,
        exclusiveProducts = KoneFixedCapacityArrayList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}