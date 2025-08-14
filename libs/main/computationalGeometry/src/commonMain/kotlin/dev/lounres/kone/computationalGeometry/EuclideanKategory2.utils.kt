/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.relations.Order
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.RowVector
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.minus
import dev.lounres.kone.linearAlgebra.plus
import dev.lounres.kone.linearAlgebra.times
import dev.lounres.kone.linearAlgebra.unaryMinus
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


private class EuclideanKategory2ViaVectorKategory<N, Content: MDList1<N>>(
    private val vectorKategory: VectorKategory<N, Content, *>,
): EuclideanKategory2<N, Content, Content> {
    override fun Vector2<N, Content>.unaryMinus(): Vector2<N, Content> = Vector2(vectorKategory { -ColumnVector(coordinates) }.coefficients)
    override fun Vector2<N, Content>.plus(other: Vector2<N, Content>): Vector2<N, Content> = Vector2(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Vector2<N, Content>.minus(other: Vector2<N, Content>): Vector2<N, Content> = Vector2(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    
    override fun Vector2<N, Content>.times(other: N): Vector2<N, Content> = Vector2(vectorKategory { ColumnVector(coordinates) * other }.coefficients)
    override fun N.times(other: Vector2<N, Content>): Vector2<N, Content> = Vector2(vectorKategory { this * ColumnVector(other.coordinates) }.coefficients)
    
    override fun Point2<N, Content>.plus(other: Vector2<N, Content>): Point2<N, Content> = Point2(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Point2<N, Content>.minus(other: Vector2<N, Content>): Point2<N, Content> = Point2(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    override fun Vector2<N, Content>.plus(other: Point2<N, Content>): Point2<N, Content> = Point2(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Point2<N, Content>.minus(other: Point2<N, Content>): Vector2<N, Content> = Vector2(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    
    override fun Vector2<N, Content>.lengthSquared(): N = vectorKategory { RowVector(this.coordinates) * ColumnVector(this.coordinates) }
    
    override fun Vector2<N, Content>.dot(other: Vector2<N, Content>): N = vectorKategory { RowVector(this.coordinates) * ColumnVector(other.coordinates) }
}

public fun <N, Content: MDList1<N>> EuclideanKategory2.Companion.viaVectorKategory(
    vectorKategory: VectorKategory<N, Content, *>,
): EuclideanKategory2<N, Content, Content> = EuclideanKategory2ViaVectorKategory(vectorKategory)

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>> RegistryBuilder<KoneContextRegistry>.setEuclideanKategory2For(
    numberType: SuppliedType,
    content1Type: SuppliedType,
    content2Type: SuppliedType,
) {
    this[EuclideanKategory2.Key<N, Content1, Content1>(numberType, content1Type, content1Type)] =
        EuclideanKategory2.viaVectorKategory(this[VectorKategory.Key<N, Content1, Content2>(numberType, content1Type, content2Type)])
}

public inline fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>, R> KoneContextRegistry.inEuclideanKategory2For(
    numberType: SuppliedType,
    vectorContentType: SuppliedType,
    pointContentType: SuppliedType,
    block: context(EuclideanKategory2<N, VectorContent, PointContent>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(contexts[EuclideanKategory2.Key<N, VectorContent, PointContent>(numberType, vectorContentType, pointContentType)])
}

public inline fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>, R> KoneContextRegistry.inEuclideanKategory2ScopeFor(
    numberType: SuppliedType,
    vectorContentType: SuppliedType,
    pointContentType: SuppliedType,
    block: context(Ring<N>, Order<N>, EuclideanKategory2<N, VectorContent, PointContent>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        contexts[Ring.Key(numberType)],
        contexts[Order.Key(numberType)],
        contexts[EuclideanKategory2.Key<N, VectorContent, PointContent>(numberType, vectorContentType, pointContentType)]
    )
}