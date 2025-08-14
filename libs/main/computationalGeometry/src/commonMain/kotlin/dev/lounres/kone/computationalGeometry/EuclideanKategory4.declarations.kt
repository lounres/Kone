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
import kotlin.reflect.KVariance.INVARIANT


public interface EuclideanKategory4<N, VectorContent: MDList1<N>, PointContent: MDList1<N>> : KoneContext {
    public operator fun Vector4<N, VectorContent>.unaryMinus(): Vector4<N, VectorContent>
    public operator fun Vector4<N, VectorContent>.plus(other: Vector4<N, VectorContent>): Vector4<N, VectorContent>
    public operator fun Vector4<N, VectorContent>.minus(other: Vector4<N, VectorContent>): Vector4<N, VectorContent>
    
    public operator fun Vector4<N, VectorContent>.times(other: N): Vector4<N, VectorContent>
    public operator fun N.times(other: Vector4<N, VectorContent>): Vector4<N, VectorContent>
    
    public operator fun Point4<N, PointContent>.plus(other: Vector4<N, VectorContent>): Point4<N, PointContent>
    public operator fun Point4<N, PointContent>.minus(other: Vector4<N, VectorContent>): Point4<N, PointContent>
    public operator fun Vector4<N, VectorContent>.plus(other: Point4<N, PointContent>): Point4<N, PointContent>
    public operator fun Point4<N, PointContent>.minus(other: Point4<N, PointContent>): Vector4<N, VectorContent>

    public fun Vector4<N, VectorContent>.lengthSquared(): N

    public infix fun Vector4<N, VectorContent>.dot(other: Vector4<N, VectorContent>): N
    
    public companion object;
    
    public class Key<Number, VectorContent: MDList1<Number>, PointContent: MDList1<Number>>(
        elementType: SuppliedType,
        vectorContentType: SuppliedType,
        pointContentType: SuppliedType,
    ) : RegistryKey<EuclideanKategory4<Number, VectorContent, PointContent>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.EuclideanKategory4",
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

context(euclideanKategory: EuclideanKategory4<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> Vector4<N, VectorContent>.unaryMinus(): Vector4<N, VectorContent> = with(euclideanKategory) { -this@unaryMinus }
context(euclideanKategory: EuclideanKategory4<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> Vector4<N, VectorContent>.plus(other: Vector4<N, VectorContent>): Vector4<N, VectorContent> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory4<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> Vector4<N, VectorContent>.minus(other: Vector4<N, VectorContent>): Vector4<N, VectorContent> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory4<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> Vector4<N, VectorContent>.times(other: N): Vector4<N, VectorContent> = with(euclideanKategory) { this@times * other }
context(euclideanKategory: EuclideanKategory4<N, VectorContent, *>)
public operator fun <N, VectorContent: MDList1<N>> N.times(other: Vector4<N, VectorContent>): Vector4<N, VectorContent> = with(euclideanKategory) { this@times * other }

context(euclideanKategory: EuclideanKategory4<N, VectorContent, PointContent>)
public operator fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Point4<N, PointContent>.plus(other: Vector4<N, VectorContent>): Point4<N, PointContent> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory4<N, VectorContent, PointContent>)
public operator fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Point4<N, PointContent>.minus(other: Vector4<N, VectorContent>): Point4<N, PointContent> = with(euclideanKategory) { this@minus - other }
context(euclideanKategory: EuclideanKategory4<N, VectorContent, PointContent>)
public operator fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Vector4<N, VectorContent>.plus(other: Point4<N, PointContent>): Point4<N, PointContent> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory4<N, VectorContent, PointContent>)
public operator fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Point4<N, PointContent>.minus(other: Point4<N, PointContent>): Vector4<N, VectorContent> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory4<N, VectorContent, *>)
public fun <N, VectorContent: MDList1<N>> Vector4<N, VectorContent>.lengthSquared(): N = with(euclideanKategory) { this@lengthSquared.lengthSquared() }

context(euclideanKategory: EuclideanKategory4<N, VectorContent, *>)
public infix fun <N, VectorContent: MDList1<N>> Vector4<N, VectorContent>.dot(other: Vector4<N, VectorContent>): N = with(euclideanKategory) { this@dot dot other }