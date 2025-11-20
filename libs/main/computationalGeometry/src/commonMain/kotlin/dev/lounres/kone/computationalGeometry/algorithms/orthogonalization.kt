/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.generate
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.repeat


internal data class GramSchmidtOrthogonalizationIntermediateState<Number, Vector>(
    val orthogonalizedBasis: KoneMutableList<Vector>,
    var product: Number,
    val exclusiveProducts: KoneMutableList<Number>,
)

internal fun <Number, Vector> GramSchmidtOrthogonalizationIntermediateState<Number, Vector>.clone(maximalSubspaceDimension: UInt): GramSchmidtOrthogonalizationIntermediateState<Number, Vector> =
    GramSchmidtOrthogonalizationIntermediateState(
        orthogonalizedBasis = KoneArrayFixedCapacityList.generate(maximalSubspaceDimension, orthogonalizedBasis.size) { orthogonalizedBasis[it] },
        product = product,
        exclusiveProducts = KoneArrayFixedCapacityList.generate(maximalSubspaceDimension, exclusiveProducts.size) { exclusiveProducts[it] }
    )

context(_: Ring<Number>, _: EuclideanSpaceOverRing<Number, Vector, *>)
internal fun <Number, Vector> GramSchmidtOrthogonalizationIntermediateState<Number, Vector>.gramSchmidtOrthogonalizationUsage(newVector: Vector): Vector {
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

context(_: Ring<Number>, _: EuclideanSpaceOverRing<Number, Vector, *>)
internal fun <Number, Vector> GramSchmidtOrthogonalizationIntermediateState<Number, Vector>.gramSchmidtOrthogonalizationExtension(newOrthogonalizedVector: Vector) {
    val newIndex = orthogonalizedBasis.size
    orthogonalizedBasis.add(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    repeat(newIndex) { exclusiveProducts[it] *= currentNorm }
    exclusiveProducts.add(product)
    product *= currentNorm
}

context(_: Ring<Number>, _: EuclideanSpaceOverRing<Number, Vector, *>)
internal fun <Number, Vector> GramSchmidtOrthogonalizationIntermediateState<Number, Vector>.gramSchmidtOrthogonalizationStep(newVector: Vector) {
    gramSchmidtOrthogonalizationExtension(gramSchmidtOrthogonalizationUsage(newVector))
}

context(ring: Ring<Number>, _: EuclideanSpaceOverRing<Number, Vector, *>)
internal fun <Number, Vector> KoneList<Vector>.gramSchmidtOrthogonalization(): KoneList<Vector> {
    val result = GramSchmidtOrthogonalizationIntermediateState<Number, Vector>(
        orthogonalizedBasis = KoneArrayFixedCapacityList(size),
        product = ring.one,
        exclusiveProducts = KoneArrayFixedCapacityList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}