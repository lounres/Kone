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


public interface EuclideanKategory2<N> {
    public val pointEquality: Equality<Point2<N>>
    public val vectorEquality: Equality<Vector2<N>>
    
    public operator fun Vector2<N>.unaryMinus(): Vector2<N>
    public operator fun Vector2<N>.plus(other: Vector2<N>): Vector2<N>
    public operator fun Vector2<N>.minus(other: Vector2<N>): Vector2<N>
    
    public operator fun Vector2<N>.times(other: N): Vector2<N>
    public operator fun N.times(other: Vector2<N>): Vector2<N>
    
    public operator fun Point2<N>.plus(other: Vector2<N>): Point2<N>
    public operator fun Point2<N>.minus(other: Vector2<N>): Point2<N>
    public operator fun Vector2<N>.plus(other: Point2<N>): Point2<N>
    public operator fun Point2<N>.minus(other: Point2<N>): Vector2<N>

    public val Vector2<N>.lengthSquared: N

    public infix fun Vector2<N>.dot(other: Vector2<N>): N
    
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<EuclideanKategory2<Number>> {
        override val typeKey: SuppliedType.Regular<EuclideanKategory2<Number>> =
            SuppliedType.Regular(
                kClass = EuclideanKategory2::class,
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

context(euclideanKategory: EuclideanKategory2<N>)
public operator fun <N> Vector2<N>.unaryMinus(): Vector2<N> = with(euclideanKategory) { -this@unaryMinus }
context(euclideanKategory: EuclideanKategory2<N>)
public operator fun <N> Vector2<N>.plus(other: Vector2<N>): Vector2<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory2<N>)
public operator fun <N> Vector2<N>.minus(other: Vector2<N>): Vector2<N> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory2<N>)
public operator fun <N> Vector2<N>.times(other: N): Vector2<N> = with(euclideanKategory) { this@times * other }
context(euclideanKategory: EuclideanKategory2<N>)
public operator fun <N> N.times(other: Vector2<N>): Vector2<N> = with(euclideanKategory) { this@times * other }

context(euclideanKategory: EuclideanKategory2<N>)
public operator fun <N> Point2<N>.plus(other: Vector2<N>): Point2<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory2<N>)
public operator fun <N> Point2<N>.minus(other: Vector2<N>): Point2<N> = with(euclideanKategory) { this@minus - other }
context(euclideanKategory: EuclideanKategory2<N>)
public operator fun <N> Vector2<N>.plus(other: Point2<N>): Point2<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory2<N>)
public operator fun <N> Point2<N>.minus(other: Point2<N>): Vector2<N> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory2<N>)
public val <N> Vector2<N>.lengthSquared: N get() = with(euclideanKategory) { this@lengthSquared.lengthSquared }

context(euclideanKategory: EuclideanKategory2<N>)
public infix fun <N> Vector2<N>.dot(other: Vector2<N>): N = with(euclideanKategory) { this@dot dot other }

internal class EuclideanKategory2WithNumberRingAndVectorKategory<N>(
    internal val numberRing: Ring<N>,
    private val vectorKategory: VectorKategory<N> = numberRing.vectorKategory(),
): EuclideanKategory2<N> {
    override val pointEquality: Equality<Point<N>> = pointEquality(numberRing)
    override val vectorEquality: Equality<Vector<N>> = vectorEquality(numberRing)
    
    override fun Vector2<N>.unaryMinus(): Vector2<N> = Vector2(context(vectorKategory) { -coordinates })
    override fun Vector2<N>.plus(other: Vector2<N>): Vector2<N> = Vector2(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Vector2<N>.minus(other: Vector2<N>): Vector2<N> = Vector2(context(vectorKategory) { this.coordinates - other.coordinates })
    
    override fun Vector2<N>.times(other: N): Vector2<N> = Vector2(context(vectorKategory) { coordinates * other })
    override fun N.times(other: Vector2<N>): Vector2<N> = Vector2(context(vectorKategory) { this * other.coordinates })
    
    override fun Point2<N>.plus(other: Vector2<N>): Point2<N> = Point2(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Point2<N>.minus(other: Vector2<N>): Point2<N> = Point2(context(vectorKategory) { this.coordinates - other.coordinates })
    override fun Vector2<N>.plus(other: Point2<N>): Point2<N> = Point2(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Point2<N>.minus(other: Point2<N>): Vector2<N> = Vector2(context(vectorKategory) { this.coordinates - other.coordinates })
    
    override val Vector2<N>.lengthSquared: N get() = context(numberRing) { this.x * this.x + this.y * this.y }
    
    override fun Vector2<N>.dot(other: Vector2<N>): N = context(numberRing) { this.x * other.x + this.y * other.y }
}