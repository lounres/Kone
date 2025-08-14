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
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.repeat


internal data class GramSchmidtOrthogonalizationIntermediateState3<Number, VectorContent: MDList1<Number>>(
    val orthogonalizedBasis: KoneMutableList<Vector3<Number, VectorContent>>,
    var product: Number,
    val exclusiveProducts: KoneMutableList<Number>,
)

internal fun <Number, VectorContent: MDList1<Number>> GramSchmidtOrthogonalizationIntermediateState3<Number, VectorContent>.clone(): GramSchmidtOrthogonalizationIntermediateState3<Number, VectorContent> =
    GramSchmidtOrthogonalizationIntermediateState3(
        orthogonalizedBasis = KoneArrayFixedCapacityList(3u, orthogonalizedBasis.size) { orthogonalizedBasis[it] },
        product = product,
        exclusiveProducts = KoneArrayFixedCapacityList(3u, exclusiveProducts.size) { exclusiveProducts[it] }
    )

context(_: Ring<Number>, _: EuclideanKategory3<Number, VectorContent, *>)
internal fun <Number, VectorContent: MDList1<Number>> GramSchmidtOrthogonalizationIntermediateState3<Number, VectorContent>.gramSchmidtOrthogonalizationUsage(newVector: Vector3<Number, VectorContent>): Vector3<Number, VectorContent> {
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

context(_: Ring<Number>, _: EuclideanKategory3<Number, VectorContent, *>)
internal fun <Number, VectorContent: MDList1<Number>> GramSchmidtOrthogonalizationIntermediateState3<Number, VectorContent>.gramSchmidtOrthogonalizationExtension(newOrthogonalizedVector: Vector3<Number, VectorContent>) {
    val newIndex = orthogonalizedBasis.size
    orthogonalizedBasis.add(newOrthogonalizedVector)
    val currentNorm = newOrthogonalizedVector dot newOrthogonalizedVector
    repeat(newIndex) { exclusiveProducts[it] *= currentNorm }
    exclusiveProducts.add(product)
    product *= currentNorm
}

context(_: Ring<Number>, _: EuclideanKategory3<Number, VectorContent, *>)
internal fun <Number, VectorContent: MDList1<Number>> GramSchmidtOrthogonalizationIntermediateState3<Number, VectorContent>.gramSchmidtOrthogonalizationStep(newVector: Vector3<Number, VectorContent>) {
    gramSchmidtOrthogonalizationExtension(gramSchmidtOrthogonalizationUsage(newVector))
}

context(_: Ring<Number>, _: EuclideanKategory3<Number, VectorContent, *>)
internal fun <Number, VectorContent: MDList1<Number>> KoneList<Vector3<Number, VectorContent>>.gramSchmidtOrthogonalization(): KoneList<Vector3<Number, VectorContent>> {
    val result = GramSchmidtOrthogonalizationIntermediateState3<Number, VectorContent>(
        orthogonalizedBasis = KoneArrayFixedCapacityList(size),
        product = one,
        exclusiveProducts = KoneArrayFixedCapacityList(size),
    )

    for (vector in this) result.gramSchmidtOrthogonalizationStep(vector)

    return result.orthogonalizedBasis
}