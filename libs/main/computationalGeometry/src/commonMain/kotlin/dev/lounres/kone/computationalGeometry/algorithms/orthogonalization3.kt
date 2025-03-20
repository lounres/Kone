/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.computationalGeometry.*
import dev.lounres.kone.repeat


internal data class GramSchmidtOrthogonalizationIntermediateState3<Number>(
    val orthogonalizedBasis: KoneMutableList<Vector3<Number>>,
    var product: Number,
    val exclusiveProducts: KoneMutableList<Number>,
)

internal fun <Number> GramSchmidtOrthogonalizationIntermediateState3<Number>.clone(): GramSchmidtOrthogonalizationIntermediateState3<Number> =
    GramSchmidtOrthogonalizationIntermediateState3(
        orthogonalizedBasis = KoneArrayFixedCapacityList(3u, orthogonalizedBasis.size) { orthogonalizedBasis[it] },
        product = product,
        exclusiveProducts = KoneArrayFixedCapacityList(3u, exclusiveProducts.size) { exclusiveProducts[it] }
    )

context(_: NumberContext, _: EuclideanKategory3<Number>)
internal fun <Number, NumberContext: Ring<Number>> GramSchmidtOrthogonalizationIntermediateState3<Number>.gramSchmidtOrthogonalizationUsage(newVector: Vector3<Number>): Vector3<Number> {
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

context(_: A, _: EuclideanKategory3<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState3<N>.gramSchmidtOrthogonalizationExtension(newOrthogonalizedVector: Vector3<N>) {
    val newIndex = orthogonalizedBasis.size
    orthogonalizedBasis.add(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    repeat(newIndex) { exclusiveProducts[it] *= currentNorm }
    exclusiveProducts.add(product)
    product *= currentNorm
}

context(_: A, _: EuclideanKategory3<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState3<N>.gramSchmidtOrthogonalizationStep(newVector: Vector3<N>) {
    gramSchmidtOrthogonalizationExtension(gramSchmidtOrthogonalizationUsage(newVector))
}

context(_: A, _: EuclideanKategory3<N>)
internal fun <N, A: Ring<N>> KoneList<Vector3<N>>.gramSchmidtOrthogonalization(): KoneList<Vector3<N>> {
    val result = GramSchmidtOrthogonalizationIntermediateState3<N>(
        orthogonalizedBasis = KoneArrayFixedCapacityList(size),
        product = one,
        exclusiveProducts = KoneArrayFixedCapacityList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}