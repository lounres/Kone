/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.computationalGeometry.relations.pointEquality
import dev.lounres.kone.computationalGeometry.relations.vectorEquality
import dev.lounres.kone.context
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.minus
import dev.lounres.kone.linearAlgebra.plus
import dev.lounres.kone.linearAlgebra.times
import dev.lounres.kone.linearAlgebra.unaryMinus
import dev.lounres.kone.linearAlgebra.vectorKategory
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


public interface EuclideanKategory4<N> : KoneContext {
    public val pointEquality: Equality<Point4<N>>
    public val vectorEquality: Equality<Vector4<N>>
    
    public operator fun Vector4<N>.unaryMinus(): Vector4<N>
    public operator fun Vector4<N>.plus(other: Vector4<N>): Vector4<N>
    public operator fun Vector4<N>.minus(other: Vector4<N>): Vector4<N>
    
    public operator fun Vector4<N>.times(other: N): Vector4<N>
    public operator fun N.times(other: Vector4<N>): Vector4<N>
    
    public operator fun Point4<N>.plus(other: Vector4<N>): Point4<N>
    public operator fun Point4<N>.minus(other: Vector4<N>): Point4<N>
    public operator fun Vector4<N>.plus(other: Point4<N>): Point4<N>
    public operator fun Point4<N>.minus(other: Point4<N>): Vector4<N>

    public val Vector4<N>.lengthSquared: N

    public infix fun Vector4<N>.dot(other: Vector4<N>): N
    
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<EuclideanKategory4<Number>> {
        override val typeKey: SuppliedType.Regular<EuclideanKategory4<Number>> =
            SuppliedType.Regular(
                kClass = EuclideanKategory4::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
    }
}

context(euclideanKategory: EuclideanKategory4<N>)
public operator fun <N> Vector4<N>.unaryMinus(): Vector4<N> = with(euclideanKategory) { -this@unaryMinus }
context(euclideanKategory: EuclideanKategory4<N>)
public operator fun <N> Vector4<N>.plus(other: Vector4<N>): Vector4<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory4<N>)
public operator fun <N> Vector4<N>.minus(other: Vector4<N>): Vector4<N> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory4<N>)
public operator fun <N> Vector4<N>.times(other: N): Vector4<N> = with(euclideanKategory) { this@times * other }
context(euclideanKategory: EuclideanKategory4<N>)
public operator fun <N> N.times(other: Vector4<N>): Vector4<N> = with(euclideanKategory) { this@times * other }

context(euclideanKategory: EuclideanKategory4<N>)
public operator fun <N> Point4<N>.plus(other: Vector4<N>): Point4<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory4<N>)
public operator fun <N> Point4<N>.minus(other: Vector4<N>): Point4<N> = with(euclideanKategory) { this@minus - other }
context(euclideanKategory: EuclideanKategory4<N>)
public operator fun <N> Vector4<N>.plus(other: Point4<N>): Point4<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory4<N>)
public operator fun <N> Point4<N>.minus(other: Point4<N>): Vector4<N> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory4<N>)
public val <N> Vector4<N>.lengthSquared: N get() = with(euclideanKategory) { this@lengthSquared.lengthSquared }

context(euclideanKategory: EuclideanKategory4<N>)
public infix fun <N> Vector4<N>.dot(other: Vector4<N>): N = with(euclideanKategory) { this@dot dot other }

internal class EuclideanKategory4WithNumberRingAndVectorKategory<N>(
    internal val numberRing: Ring<N>,
    private val vectorKategory: VectorKategory<N> = numberRing.vectorKategory(),
): EuclideanKategory4<N> {
    override val pointEquality: Equality<Point<N>> = pointEquality(numberRing)
    override val vectorEquality: Equality<Vector<N>> = vectorEquality(numberRing)
    
    override fun Vector4<N>.unaryMinus(): Vector4<N> = Vector4(context(vectorKategory) { -coordinates })
    override fun Vector4<N>.plus(other: Vector4<N>): Vector4<N> = Vector4(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Vector4<N>.minus(other: Vector4<N>): Vector4<N> = Vector4(context(vectorKategory) { this.coordinates - other.coordinates })
    
    override fun Vector4<N>.times(other: N): Vector4<N> = Vector4(context(vectorKategory) { coordinates * other })
    override fun N.times(other: Vector4<N>): Vector4<N> = Vector4(context(vectorKategory) { this * other.coordinates })
    
    override fun Point4<N>.plus(other: Vector4<N>): Point4<N> = Point4(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Point4<N>.minus(other: Vector4<N>): Point4<N> = Point4(context(vectorKategory) { this.coordinates - other.coordinates })
    override fun Vector4<N>.plus(other: Point4<N>): Point4<N> = Point4(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Point4<N>.minus(other: Point4<N>): Vector4<N> = Vector4(context(vectorKategory) { this.coordinates - other.coordinates })
    
    override val Vector4<N>.lengthSquared: N get() = context(numberRing) { this.x * this.x + this.y * this.y + this.z * this.z + this.t * this.t }
    
    override fun Vector4<N>.dot(other: Vector4<N>): N = context(numberRing) { this.x * other.x + this.y * other.y + this.z * other.z + this.t * other.t }
}