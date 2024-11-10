/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.next
import dev.lounres.kone.computationalGeometry.EuclideanKategory
import dev.lounres.kone.computationalGeometry.MutablePolytopicConstruction
import dev.lounres.kone.computationalGeometry.Vector
import dev.lounres.kone.repeat


internal data class GramSchmidtOrthogonalizationIntermediateState<N>(
    val orthogonalizedBasis: KoneMutableList<Vector<N>>,
    var product: N,
    val exclusiveProducts: KoneMutableList<N>,
)

context(MutablePolytopicConstruction<N, *, *>)
internal fun <N> GramSchmidtOrthogonalizationIntermediateState<N>.clone(): GramSchmidtOrthogonalizationIntermediateState<N> =
    GramSchmidtOrthogonalizationIntermediateState(
        orthogonalizedBasis = KoneArrayFixedCapacityList(spaceDimension, orthogonalizedBasis.size) { orthogonalizedBasis[it] },
        product = product,
        exclusiveProducts = KoneArrayFixedCapacityList(spaceDimension, exclusiveProducts.size) { exclusiveProducts[it] }
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
    repeat(newIndex) { exclusiveProducts[it] *= currentNorm }
    exclusiveProducts.add(product)
    product *= currentNorm
}

context(A, EuclideanKategory<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState<N>.gramSchmidtOrthogonalizationStep(newVector: Vector<N>) {
    gramSchmidtOrthogonalizationExtension(gramSchmidtOrthogonalizationUsage(newVector))
}

context(A, EuclideanKategory<N>)
internal fun <N, A: Ring<N>> KoneList<Vector<N>>.gramSchmidtOrthogonalization(): KoneList<Vector<N>> {
    val result = GramSchmidtOrthogonalizationIntermediateState<N>(
        orthogonalizedBasis = KoneArrayFixedCapacityList(size),
        product = one,
        exclusiveProducts = KoneArrayFixedCapacityList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}