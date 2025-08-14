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


private class EuclideanKategory4ViaVectorKategory<N, Content: MDList1<N>>(
    private val vectorKategory: VectorKategory<N, Content, *>,
): EuclideanKategory4<N, Content, Content> {
    override fun Vector4<N, Content>.unaryMinus(): Vector4<N, Content> = Vector4(vectorKategory { -ColumnVector(coordinates) }.coefficients)
    override fun Vector4<N, Content>.plus(other: Vector4<N, Content>): Vector4<N, Content> = Vector4(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Vector4<N, Content>.minus(other: Vector4<N, Content>): Vector4<N, Content> = Vector4(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    
    override fun Vector4<N, Content>.times(other: N): Vector4<N, Content> = Vector4(vectorKategory { ColumnVector(coordinates) * other }.coefficients)
    override fun N.times(other: Vector4<N, Content>): Vector4<N, Content> = Vector4(vectorKategory { this * ColumnVector(other.coordinates) }.coefficients)
    
    override fun Point4<N, Content>.plus(other: Vector4<N, Content>): Point4<N, Content> = Point4(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Point4<N, Content>.minus(other: Vector4<N, Content>): Point4<N, Content> = Point4(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    override fun Vector4<N, Content>.plus(other: Point4<N, Content>): Point4<N, Content> = Point4(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Point4<N, Content>.minus(other: Point4<N, Content>): Vector4<N, Content> = Vector4(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    
    override fun Vector4<N, Content>.lengthSquared(): N = vectorKategory { RowVector(coordinates) * ColumnVector(coordinates) }
    
    override fun Vector4<N, Content>.dot(other: Vector4<N, Content>): N = vectorKategory { RowVector(coordinates) * ColumnVector(other.coordinates) }
}

public fun <N, Content: MDList1<N>> EuclideanKategory4.Companion.viaVectorKategory(
    vectorKategory: VectorKategory<N, Content, *>,
): EuclideanKategory4<N, Content, Content> = EuclideanKategory4ViaVectorKategory(vectorKategory)

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>> RegistryBuilder<KoneContextRegistry>.setEuclideanKategory4For(
    numberType: SuppliedType,
    content1Type: SuppliedType,
    content2Type: SuppliedType,
) {
    this[EuclideanKategory4.Key<N, Content1, Content1>(numberType, content1Type, content1Type)] =
        EuclideanKategory4.viaVectorKategory(this[VectorKategory.Key<N, Content1, Content2>(numberType, content1Type, content2Type)])
}

public inline fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>, R> KoneContextRegistry.inEuclideanKategory4For(
    numberType: SuppliedType,
    vectorContentType: SuppliedType,
    pointContentType: SuppliedType,
    block: context(EuclideanKategory4<N, VectorContent, PointContent>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(contexts[EuclideanKategory4.Key<N, VectorContent, PointContent>(numberType, vectorContentType, pointContentType)])
}

public inline fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>, R> KoneContextRegistry.inEuclideanKategoryScope4For(
    numberType: SuppliedType,
    vectorContentType: SuppliedType,
    pointContentType: SuppliedType,
    block: context(Ring<N>, Order<N>, EuclideanKategory4<N, VectorContent, PointContent>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        contexts[Ring.Key<N>(numberType)],
        contexts[Order.Key<N>(numberType)],
        contexts[EuclideanKategory4.Key<N, VectorContent, PointContent>(numberType, vectorContentType, pointContentType)]
    )
}