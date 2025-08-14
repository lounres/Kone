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
import dev.lounres.kone.linearAlgebra.zero
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


private class EuclideanKategoryViaVectorKategory<N, Content: MDList1<N>>(
    private val vectorKategory: VectorKategory<N, Content, *>,
): EuclideanKategory<N, Content, Content> {
    override fun Vector.Companion.zero(dimension: UInt): Vector<N, Content> = Vector(vectorKategory { RowVector.zero(dimension) }.coefficients)
    
    override fun Vector<N, Content>.unaryMinus(): Vector<N, Content> = Vector(vectorKategory { -ColumnVector(coordinates) }.coefficients)
    override fun Vector<N, Content>.plus(other: Vector<N, Content>): Vector<N, Content> = Vector(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Vector<N, Content>.minus(other: Vector<N, Content>): Vector<N, Content> = Vector(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    
    override fun Vector<N, Content>.times(other: N): Vector<N, Content> = Vector(vectorKategory { ColumnVector(coordinates) * other }.coefficients)
    override fun N.times(other: Vector<N, Content>): Vector<N, Content> = Vector(vectorKategory { this * ColumnVector(other.coordinates) }.coefficients)
    
    override fun Point<N, Content>.plus(other: Vector<N, Content>): Point<N, Content> = Point(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Point<N, Content>.minus(other: Vector<N, Content>): Point<N, Content> = Point(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    override fun Vector<N, Content>.plus(other: Point<N, Content>): Point<N, Content> = Point(vectorKategory { ColumnVector(this.coordinates) + ColumnVector(other.coordinates) }.coefficients)
    override fun Point<N, Content>.minus(other: Point<N, Content>): Vector<N, Content> = Vector(vectorKategory { ColumnVector(this.coordinates) - ColumnVector(other.coordinates) }.coefficients)
    
    override fun Vector<N, Content>.lengthSquared(): N = vectorKategory { RowVector(coordinates) * ColumnVector(coordinates) }
    
    override fun Vector<N, Content>.dot(other: Vector<N, Content>): N = vectorKategory { RowVector(coordinates) * ColumnVector(other.coordinates) }
}

public fun <N, Content: MDList1<N>> EuclideanKategory.Companion.viaVectorKategory(
    vectorKategory: VectorKategory<N, Content, *>,
): EuclideanKategory<N, Content, Content> = EuclideanKategoryViaVectorKategory(vectorKategory)

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>> RegistryBuilder<KoneContextRegistry>.setEuclideanKategoryFor(
    numberType: SuppliedType,
    content1Type: SuppliedType,
    content2Type: SuppliedType,
) {
    this[EuclideanKategory.Key<N, Content1, Content1>(numberType, content1Type, content1Type)] =
        EuclideanKategory.viaVectorKategory(this[VectorKategory.Key<N, Content1, Content2>(numberType, content1Type, content2Type)])
}

public inline fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>, R> KoneContextRegistry.inEuclideanKategoryFor(
    numberType: SuppliedType,
    vectorContentType: SuppliedType,
    pointContentType: SuppliedType,
    block: context(EuclideanKategory<N, VectorContent, PointContent>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[EuclideanKategory.Key<N, VectorContent, PointContent>(numberType, vectorContentType, pointContentType)])
}

public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>, R> KoneContextRegistry.inEuclideanKategoryScopeFor(
    numberType: SuppliedType,
    vectorContentType: SuppliedType,
    pointContentType: SuppliedType,
    block: context(Ring<N>, Order<N>, EuclideanKategory<N, VectorContent, PointContent>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this.contexts[Ring.Key(numberType)],
        this.contexts[Order.Key(numberType)],
        this.contexts[EuclideanKategory.Key<N, VectorContent, PointContent>(numberType, vectorContentType, pointContentType)]
    )
}