/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.linearAlgebra.*
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


private class EuclideanKategory3ViaVectorKategory<N, Content: MDList1<N>>(
    private val vectorKategory: VectorKategory<N, Content, *>,
): EuclideanKategory3<N, Content, Content> {
    override fun Vector3<N, Content>.unaryMinus(): Vector3<N, Content> = Vector3(vectorKategory { -ColumnVector(coordinates) }.coefficients)
    override fun Vector3<N, Content>.plus(other: Vector3<N, Content>): Vector3<N, Content> = Vector3(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Vector3<N, Content>.minus(other: Vector3<N, Content>): Vector3<N, Content> = Vector3(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    
    override fun Vector3<N, Content>.times(other: N): Vector3<N, Content> = Vector3(vectorKategory { ColumnVector(coordinates) * other }.coefficients)
    override fun N.times(other: Vector3<N, Content>): Vector3<N, Content> = Vector3(vectorKategory { this * ColumnVector(other.coordinates) }.coefficients)
    
    override fun Point3<N, Content>.plus(other: Vector3<N, Content>): Point3<N, Content> = Point3(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Point3<N, Content>.minus(other: Vector3<N, Content>): Point3<N, Content> = Point3(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    override fun Vector3<N, Content>.plus(other: Point3<N, Content>): Point3<N, Content> = Point3(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Point3<N, Content>.minus(other: Point3<N, Content>): Vector3<N, Content> = Vector3(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    
    override fun Vector3<N, Content>.lengthSquared(): N = vectorKategory { RowVector(coordinates) * ColumnVector(coordinates) }
    
    override fun Vector3<N, Content>.dot(other: Vector3<N, Content>): N = vectorKategory { RowVector(coordinates) * ColumnVector(other.coordinates) }
}

public fun <N, Content: MDList1<N>> EuclideanKategory3.Companion.viaVectorKategory(
    vectorKategory: VectorKategory<N, Content, *>,
): EuclideanKategory3<N, Content, Content> = EuclideanKategory3ViaVectorKategory(vectorKategory)

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>> RegistryBuilder<KoneContextRegistry>.setEuclideanKategory3For(
    numberType: SuppliedType,
    content1Type: SuppliedType,
    content2Type: SuppliedType,
) {
    this[EuclideanKategory3.Key<N, Content1, Content1>(numberType, content1Type, content1Type)] =
        EuclideanKategory3.viaVectorKategory(this[VectorKategory.Key<N, Content1, Content2>(numberType, content1Type, content2Type)])
}

public inline fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>, R> KoneContextRegistry.inEuclideanKategory3For(
    numberType: SuppliedType,
    vectorContentType: SuppliedType,
    pointContentType: SuppliedType,
    block: context(EuclideanKategory3<N, VectorContent, PointContent>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(contexts[EuclideanKategory3.Key<N, VectorContent, PointContent>(numberType, vectorContentType, pointContentType)])
}

public inline fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>, R> KoneContextRegistry.inEuclideanKategory3ScopeFor(
    numberType: SuppliedType,
    vectorContentType: SuppliedType,
    pointContentType: SuppliedType,
    block: context(Ring<N>, Order<N>, EuclideanKategory3<N, VectorContent, PointContent>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        contexts[Ring.Key<N>(numberType)],
        contexts[Order.Key<N>(numberType)],
        contexts[EuclideanKategory3.Key<N, VectorContent, PointContent>(numberType, vectorContentType, pointContentType)]
    )
}