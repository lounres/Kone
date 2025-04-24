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
import dev.lounres.kone.computationalGeometry.utils.fold
import dev.lounres.kone.context
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.linearAlgebra.*
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


public interface EuclideanKategory<N> : KoneContext {
    public val pointEquality: Equality<Point<N>>
    public val vectorEquality: Equality<Vector<N>>
    
    public operator fun Vector<N>.unaryMinus(): Vector<N>
    public operator fun Vector<N>.plus(other: Vector<N>): Vector<N>
    public operator fun Vector<N>.minus(other: Vector<N>): Vector<N>

    public operator fun Vector<N>.times(other: N): Vector<N>
    public operator fun N.times(other: Vector<N>): Vector<N>

    public operator fun Point<N>.plus(other: Vector<N>): Point<N>
    public operator fun Point<N>.minus(other: Vector<N>): Point<N>
    public operator fun Vector<N>.plus(other: Point<N>): Point<N>
    public operator fun Point<N>.minus(other: Point<N>): Vector<N>
    
    public operator fun Vector2<N>.unaryMinus(): Vector2<N> = Vector2((this as Vector<N>).unaryMinus().coordinates)
    public operator fun Vector2<N>.plus(other: Vector2<N>): Vector2<N> = Vector2((this as Vector<N>).plus(other as Vector<N>).coordinates)
    public operator fun Vector2<N>.minus(other: Vector2<N>): Vector2<N> = Vector2((this as Vector<N>).minus(other as Vector<N>).coordinates)
    
    public operator fun Vector2<N>.times(other: N): Vector2<N> = Vector2((this as Vector<N>).times(other).coordinates)
    public operator fun N.times(other: Vector2<N>): Vector2<N> = Vector2(this.times(other as Vector<N>).coordinates)
    
    public operator fun Point2<N>.plus(other: Vector2<N>): Point2<N> = Point2((this as Point<N>).plus(other as Vector<N>).coordinates)
    public operator fun Point2<N>.minus(other: Vector2<N>): Point2<N> = Point2((this as Point<N>).minus(other as Vector<N>).coordinates)
    public operator fun Vector2<N>.plus(other: Point2<N>): Point2<N> = Point2((this as Vector<N>).plus(other as Point<N>).coordinates)
    public operator fun Point2<N>.minus(other: Point2<N>): Vector2<N> = Vector2((this as Point<N>).minus(other as Point<N>).coordinates)

    public val Vector<N>.lengthSquared: N

    public infix fun Vector<N>.dot(other: Vector<N>): N
    
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<EuclideanKategory<Number>> {
        override val typeKey: SuppliedType.Regular<EuclideanKategory<Number>> =
            SuppliedType.Regular(
                kClass = EuclideanKategory::class,
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

context(euclideanKategory: EuclideanKategory<N>)
public operator fun <N> Vector<N>.unaryMinus(): Vector<N> = with(euclideanKategory) { -this@unaryMinus }
context(euclideanKategory: EuclideanKategory<N>)
public operator fun <N> Vector<N>.plus(other: Vector<N>): Vector<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory<N>)
public operator fun <N> Vector<N>.minus(other: Vector<N>): Vector<N> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory<N>)
public operator fun <N> Vector<N>.times(other: N): Vector<N> = with(euclideanKategory) { this@times * other }
context(euclideanKategory: EuclideanKategory<N>)
public operator fun <N> N.times(other: Vector<N>): Vector<N> = with(euclideanKategory) { this@times * other }

context(euclideanKategory: EuclideanKategory<N>)
public operator fun <N> Point<N>.plus(other: Vector<N>): Point<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory<N>)
public operator fun <N> Point<N>.minus(other: Vector<N>): Point<N> = with(euclideanKategory) { this@minus - other }
context(euclideanKategory: EuclideanKategory<N>)
public operator fun <N> Vector<N>.plus(other: Point<N>): Point<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory<N>)
public operator fun <N> Point<N>.minus(other: Point<N>): Vector<N> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory<N>)
public val <N> Vector<N>.lengthSquared: N get() = with(euclideanKategory) { this@lengthSquared.lengthSquared }

context(euclideanKategory: EuclideanKategory<N>)
public infix fun <N> Vector<N>.dot(other: Vector<N>): N = with(euclideanKategory) { this@dot dot other }

internal class EuclideanKategoryWithNumberRingAndVectorKategory<N>(
    internal val numberRing: Ring<N>,
    private val vectorKategory: VectorKategory<N> = numberRing.vectorKategory(),
): EuclideanKategory<N> {
    override val pointEquality: Equality<Point<N>> = pointEquality(numberRing)
    override val vectorEquality: Equality<Vector<N>> = vectorEquality(numberRing)
    
    override fun Vector<N>.unaryMinus(): Vector<N> = Vector(context(vectorKategory) { -coordinates })
    override fun Vector<N>.plus(other: Vector<N>): Vector<N> = Vector(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Vector<N>.minus(other: Vector<N>): Vector<N> = Vector(context(vectorKategory) { this.coordinates - other.coordinates })
    
    override fun Vector<N>.times(other: N): Vector<N> = Vector(context(vectorKategory) { coordinates * other })
    override fun N.times(other: Vector<N>): Vector<N> = Vector(context(vectorKategory) { this * other.coordinates })
    
    override fun Point<N>.plus(other: Vector<N>): Point<N> = Point(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Point<N>.minus(other: Vector<N>): Point<N> = Point(context(vectorKategory) { this.coordinates - other.coordinates })
    override fun Vector<N>.plus(other: Point<N>): Point<N> = Point(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Point<N>.minus(other: Point<N>): Vector<N> = Vector(context(vectorKategory) { this.coordinates - other.coordinates })
    
    override val Vector<N>.lengthSquared: N get() = fold(numberRing.zero) { acc, n -> context(numberRing) { acc + n * n } }
    
    override fun Vector<N>.dot(other: Vector<N>): N {
        requireShapeEquality(this.coordinates, other.coordinates)
        val size = this.coordinates.size
        // FIXME: KT-67840
//        return (0u..<size).fold(numberRing.zero) { acc, index -> numberRing { acc + this.coordinates[index] * other.coordinates[index] } }
        var result = numberRing.zero
        var index = 0u
        while (index < size) {
            result = context(numberRing) { result + this.coordinates[index] * other.coordinates[index] }
            index++
        }
        return result
    }
}