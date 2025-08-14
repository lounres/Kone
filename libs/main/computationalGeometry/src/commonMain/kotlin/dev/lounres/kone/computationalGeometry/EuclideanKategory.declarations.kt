/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


public interface EuclideanKategory<N, VectorContent: MDList1<N>, PointContent: MDList1<N>> : KoneContext {
    public fun Vector.Companion.zero(dimension: UInt): Vector<N, VectorContent>
    
    public operator fun Vector<N, VectorContent>.unaryMinus(): Vector<N, VectorContent>
    public operator fun Vector<N, VectorContent>.plus(other: Vector<N, VectorContent>): Vector<N, VectorContent>
    public operator fun Vector<N, VectorContent>.minus(other: Vector<N, VectorContent>): Vector<N, VectorContent>

    public operator fun Vector<N, VectorContent>.times(other: N): Vector<N, VectorContent>
    public operator fun N.times(other: Vector<N, VectorContent>): Vector<N, VectorContent>

    public operator fun Point<N, PointContent>.plus(other: Vector<N, VectorContent>): Point<N, PointContent>
    public operator fun Point<N, PointContent>.minus(other: Vector<N, VectorContent>): Point<N, PointContent>
    public operator fun Vector<N, VectorContent>.plus(other: Point<N, PointContent>): Point<N, PointContent>
    public operator fun Point<N, PointContent>.minus(other: Point<N, PointContent>): Vector<N, VectorContent>

    public fun Vector<N, VectorContent>.lengthSquared(): N

    public infix fun Vector<N, VectorContent>.dot(other: Vector<N, VectorContent>): N
    
    public companion object;
    
    public class Key<Number, VectorContent: MDList1<Number>, PointContent: MDList1<Number>>(
        elementType: SuppliedType,
        vectorContentType: SuppliedType,
        pointContentType: SuppliedType,
    ) : RegistryKey<EuclideanKategory<Number, VectorContent, PointContent>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.EuclideanKategory",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = vectorContentType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = pointContentType
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(euclideanKategory: EuclideanKategory<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> Vector<N, VectorContent>.unaryMinus(): Vector<N, VectorContent> = with(euclideanKategory) { -this@unaryMinus }
context(euclideanKategory: EuclideanKategory<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> Vector<N, VectorContent>.plus(other: Vector<N, VectorContent>): Vector<N, VectorContent> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> Vector<N, VectorContent>.minus(other: Vector<N, VectorContent>): Vector<N, VectorContent> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> Vector<N, VectorContent>.times(other: N): Vector<N, VectorContent> = with(euclideanKategory) { this@times * other }
context(euclideanKategory: EuclideanKategory<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> N.times(other: Vector<N, VectorContent>): Vector<N, VectorContent> = with(euclideanKategory) { this@times * other }

context(euclideanKategory: EuclideanKategory<N, VectorContent, PointContent>)
public operator fun <N, PointContent: MDList1<N>, VectorContent: MDList1<N>> Point<N, PointContent>.plus(other: Vector<N, VectorContent>): Point<N, PointContent> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory<N, VectorContent, PointContent>)
public operator fun <N, PointContent: MDList1<N>, VectorContent: MDList1<N>> Point<N, PointContent>.minus(other: Vector<N, VectorContent>): Point<N, PointContent> = with(euclideanKategory) { this@minus - other }
context(euclideanKategory: EuclideanKategory<N, VectorContent, PointContent>)
public operator fun <N, PointContent: MDList1<N>, VectorContent: MDList1<N>> Vector<N, VectorContent>.plus(other: Point<N, PointContent>): Point<N, PointContent> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory<N, VectorContent, PointContent>)
public operator fun <N, PointContent: MDList1<N>, VectorContent: MDList1<N>> Point<N, PointContent>.minus(other: Point<N, PointContent>): Vector<N, VectorContent> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory<N, VectorContent, *>)
public fun <N, VectorContent: MDList1<N>> Vector<N, VectorContent>.lengthSquared(): N = with(euclideanKategory) { this@lengthSquared.lengthSquared() }

context(euclideanKategory: EuclideanKategory<N, VectorContent, *>)
public infix fun <N, VectorContent: MDList1<N>> Vector<N, VectorContent>.dot(other: Vector<N, VectorContent>): N = with(euclideanKategory) { this@dot dot other }