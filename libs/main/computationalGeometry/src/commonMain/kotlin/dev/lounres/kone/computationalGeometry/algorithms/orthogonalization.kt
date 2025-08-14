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
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.repeat


internal data class GramSchmidtOrthogonalizationIntermediateState<Number, VectorContent: MDList1<Number>>(
    val orthogonalizedBasis: KoneMutableList<Vector<Number, VectorContent>>,
    var product: Number,
    val exclusiveProducts: KoneMutableList<Number>,
)

internal fun <Number, VectorContent: MDList1<Number>> GramSchmidtOrthogonalizationIntermediateState<Number, VectorContent>.clone(maximalSubspaceDimension: UInt): GramSchmidtOrthogonalizationIntermediateState<Number, VectorContent> =
    GramSchmidtOrthogonalizationIntermediateState(
        orthogonalizedBasis = KoneArrayFixedCapacityList(maximalSubspaceDimension, orthogonalizedBasis.size) { orthogonalizedBasis[it] },
        product = product,
        exclusiveProducts = KoneArrayFixedCapacityList(maximalSubspaceDimension, exclusiveProducts.size) { exclusiveProducts[it] }
    )

context(_: Ring<Number>, _: EuclideanKategory<Number, VectorContent, *>)
internal fun <Number, VectorContent: MDList1<Number>> GramSchmidtOrthogonalizationIntermediateState<Number, VectorContent>.gramSchmidtOrthogonalizationUsage(newVector: Vector<Number, VectorContent>): Vector<Number, VectorContent> {
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

context(_: Ring<Number>, _: EuclideanKategory<Number, VectorContent, *>)
internal fun <Number, VectorContent: MDList1<Number>> GramSchmidtOrthogonalizationIntermediateState<Number, VectorContent>.gramSchmidtOrthogonalizationExtension(newOrthogonalizedVector: Vector<Number, VectorContent>) {
    val newIndex = orthogonalizedBasis.size
    orthogonalizedBasis.add(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    repeat(newIndex) { exclusiveProducts[it] *= currentNorm }
    exclusiveProducts.add(product)
    product *= currentNorm
}

context(_: Ring<Number>, _: EuclideanKategory<Number, VectorContent, *>)
internal fun <Number, VectorContent: MDList1<Number>> GramSchmidtOrthogonalizationIntermediateState<Number, VectorContent>.gramSchmidtOrthogonalizationStep(newVector: Vector<Number, VectorContent>) {
    gramSchmidtOrthogonalizationExtension(gramSchmidtOrthogonalizationUsage(newVector))
}

context(_: Ring<Number>, _: EuclideanKategory<Number, VectorContent, *>)
internal fun <Number, VectorContent: MDList1<Number>> KoneList<Vector<Number, VectorContent>>.gramSchmidtOrthogonalization(): KoneList<Vector<Number, VectorContent>> {
    val result = GramSchmidtOrthogonalizationIntermediateState<Number, VectorContent>(
        orthogonalizedBasis = KoneArrayFixedCapacityList(size),
        product = one,
        exclusiveProducts = KoneArrayFixedCapacityList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}