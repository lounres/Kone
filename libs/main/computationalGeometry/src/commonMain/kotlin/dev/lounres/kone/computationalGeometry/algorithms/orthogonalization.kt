/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.generate
import dev.lounres.kone.computationalGeometry.EuclideanVectorSpaceOverRing
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextHolder
import dev.lounres.kone.contexts.unwrapLocallyAsExtensionReceivers
import dev.lounres.kone.contexts.useLocallyAsExtensionReceivers
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

context(ring: Ring<Number>, euclideanSpace: EuclideanVectorSpaceOverRing<Number, Vector>)
internal fun <Number, Vector> GramSchmidtOrthogonalizationIntermediateState<Number, Vector>.gramSchmidtOrthogonalizationUsage(newVector: Vector): Vector {
    KoneContextHolder.unwrapLocallyAsExtensionReceivers(ring, euclideanSpace)
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

context(ring: Ring<Number>, euclideanSpace: EuclideanVectorSpaceOverRing<Number, Vector>)
internal fun <Number, Vector> GramSchmidtOrthogonalizationIntermediateState<Number, Vector>.gramSchmidtOrthogonalizationExtension(newOrthogonalizedVector: Vector) {
    KoneContext.useLocallyAsExtensionReceivers(ring.numberTimesNumber, euclideanSpace.vectorDotVector)
    val newIndex = orthogonalizedBasis.size
    orthogonalizedBasis.add(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    repeat(newIndex) { exclusiveProducts[it] *= currentNorm }
    exclusiveProducts.add(product)
    product *= currentNorm
}

context(_: Ring<Number>, euclideanSpace: EuclideanVectorSpaceOverRing<Number, Vector>)
internal fun <Number, Vector> GramSchmidtOrthogonalizationIntermediateState<Number, Vector>.gramSchmidtOrthogonalizationStep(newVector: Vector) {
    KoneContext.useLocallyAsExtensionReceivers(euclideanSpace.numberIsZero)
    val orthogonalizedVector = gramSchmidtOrthogonalizationUsage(newVector)
    if (orthogonalizedVector.isNotZero()) gramSchmidtOrthogonalizationExtension(orthogonalizedVector)
}

context(ring: Ring<Number>, _: EuclideanVectorSpaceOverRing<Number, Vector>)
internal fun <Number, Vector> KoneList<Vector>.gramSchmidtOrthogonalization(): KoneList<Vector> {
    val result = GramSchmidtOrthogonalizationIntermediateState<Number, Vector>(
        orthogonalizedBasis = KoneArrayFixedCapacityList(size),
        product = ring.one,
        exclusiveProducts = KoneArrayFixedCapacityList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}