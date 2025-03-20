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


internal data class GramSchmidtOrthogonalizationIntermediateState4<Number>(
    val orthogonalizedBasis: KoneMutableList<Vector4<Number>>,
    var product: Number,
    val exclusiveProducts: KoneMutableList<Number>,
)

internal fun <Number> GramSchmidtOrthogonalizationIntermediateState4<Number>.clone(): GramSchmidtOrthogonalizationIntermediateState4<Number> =
    GramSchmidtOrthogonalizationIntermediateState4(
        orthogonalizedBasis = KoneArrayFixedCapacityList(4u, orthogonalizedBasis.size) { orthogonalizedBasis[it] },
        product = product,
        exclusiveProducts = KoneArrayFixedCapacityList(4u, exclusiveProducts.size) { exclusiveProducts[it] }
    )

context(_: NumberContext, _: EuclideanKategory4<Number>)
internal fun <Number, NumberContext: Ring<Number>> GramSchmidtOrthogonalizationIntermediateState4<Number>.gramSchmidtOrthogonalizationUsage(newVector: Vector4<Number>): Vector4<Number> {
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

context(_: A, _: EuclideanKategory4<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState4<N>.gramSchmidtOrthogonalizationExtension(newOrthogonalizedVector: Vector4<N>) {
    val newIndex = orthogonalizedBasis.size
    orthogonalizedBasis.add(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    repeat(newIndex) { exclusiveProducts[it] *= currentNorm }
    exclusiveProducts.add(product)
    product *= currentNorm
}

context(_: A, _: EuclideanKategory4<N>)
internal fun <N, A: Ring<N>> GramSchmidtOrthogonalizationIntermediateState4<N>.gramSchmidtOrthogonalizationStep(newVector: Vector4<N>) {
    gramSchmidtOrthogonalizationExtension(gramSchmidtOrthogonalizationUsage(newVector))
}

context(_: A, _: EuclideanKategory4<N>)
internal fun <N, A: Ring<N>> KoneList<Vector4<N>>.gramSchmidtOrthogonalization(): KoneList<Vector4<N>> {
    val result = GramSchmidtOrthogonalizationIntermediateState4<N>(
        orthogonalizedBasis = KoneArrayFixedCapacityList(size),
        product = one,
        exclusiveProducts = KoneArrayFixedCapacityList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}