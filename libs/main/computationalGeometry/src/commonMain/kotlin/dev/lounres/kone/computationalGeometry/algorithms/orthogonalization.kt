/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.computationalGeometry.EuclideanKategory
import dev.lounres.kone.computationalGeometry.Vector
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.times
import dev.lounres.kone.repeat


internal data class GramSchmidtOrthogonalizationIntermediateState<Number>(
    val orthogonalizedBasis: KoneMutableList<Vector<Number>>,
    var product: Number,
    val exclusiveProducts: KoneMutableList<Number>,
)

internal fun <Number> GramSchmidtOrthogonalizationIntermediateState<Number>.clone(maximalSubspaceDimension: UInt): GramSchmidtOrthogonalizationIntermediateState<Number> =
    GramSchmidtOrthogonalizationIntermediateState(
        orthogonalizedBasis = KoneArrayFixedCapacityList(maximalSubspaceDimension, orthogonalizedBasis.size) { orthogonalizedBasis[it] },
        product = product,
        exclusiveProducts = KoneArrayFixedCapacityList(maximalSubspaceDimension, exclusiveProducts.size) { exclusiveProducts[it] }
    )

context(_: NumberContext, _: EuclideanKategory<Number>)
internal fun <Number, NumberContext: Ring<Number>> GramSchmidtOrthogonalizationIntermediateState<Number>.gramSchmidtOrthogonalizationUsage(newVector: Vector<Number>): Vector<Number> {
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

context(_: A, _: EuclideanKategory<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState<N>.gramSchmidtOrthogonalizationExtension(newOrthogonalizedVector: Vector<N>) {
    val newIndex = orthogonalizedBasis.size
    orthogonalizedBasis.add(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    repeat(newIndex) { exclusiveProducts[it] *= currentNorm }
    exclusiveProducts.add(product)
    product *= currentNorm
}

context(_: A, _: EuclideanKategory<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState<N>.gramSchmidtOrthogonalizationStep(newVector: Vector<N>) {
    gramSchmidtOrthogonalizationExtension(gramSchmidtOrthogonalizationUsage(newVector))
}

context(_: A, _: EuclideanKategory<N>)
internal fun <N, A: Ring<N>> KoneList<Vector<N>>.gramSchmidtOrthogonalization(): KoneList<Vector<N>> {
    val result = GramSchmidtOrthogonalizationIntermediateState<N>(
        orthogonalizedBasis = KoneArrayFixedCapacityList(size),
        product = one,
        exclusiveProducts = KoneArrayFixedCapacityList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}