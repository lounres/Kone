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
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


public interface EuclideanKategory3<N> : KoneContext {
    public val pointEquality: Equality<Point3<N>>
    public val vectorEquality: Equality<Vector3<N>>
    
    public operator fun Vector3<N>.unaryMinus(): Vector3<N>
    public operator fun Vector3<N>.plus(other: Vector3<N>): Vector3<N>
    public operator fun Vector3<N>.minus(other: Vector3<N>): Vector3<N>
    
    public operator fun Vector3<N>.times(other: N): Vector3<N>
    public operator fun N.times(other: Vector3<N>): Vector3<N>
    
    public operator fun Point3<N>.plus(other: Vector3<N>): Point3<N>
    public operator fun Point3<N>.minus(other: Vector3<N>): Point3<N>
    public operator fun Vector3<N>.plus(other: Point3<N>): Point3<N>
    public operator fun Point3<N>.minus(other: Point3<N>): Vector3<N>

    public val Vector3<N>.lengthSquared: N

    public infix fun Vector3<N>.dot(other: Vector3<N>): N
    
    public class Key<Number>(
        elementType: SuppliedType,
    ) : RegistryKey<EuclideanKategory3<Number>> {
        override val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.EuclideanKategory3",
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

context(euclideanKategory: EuclideanKategory3<N>)
public operator fun <N> Vector3<N>.unaryMinus(): Vector3<N> = with(euclideanKategory) { -this@unaryMinus }
context(euclideanKategory: EuclideanKategory3<N>)
public operator fun <N> Vector3<N>.plus(other: Vector3<N>): Vector3<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory3<N>)
public operator fun <N> Vector3<N>.minus(other: Vector3<N>): Vector3<N> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory3<N>)
public operator fun <N> Vector3<N>.times(other: N): Vector3<N> = with(euclideanKategory) { this@times * other }
context(euclideanKategory: EuclideanKategory3<N>)
public operator fun <N> N.times(other: Vector3<N>): Vector3<N> = with(euclideanKategory) { this@times * other }

context(euclideanKategory: EuclideanKategory3<N>)
public operator fun <N> Point3<N>.plus(other: Vector3<N>): Point3<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory3<N>)
public operator fun <N> Point3<N>.minus(other: Vector3<N>): Point3<N> = with(euclideanKategory) { this@minus - other }
context(euclideanKategory: EuclideanKategory3<N>)
public operator fun <N> Vector3<N>.plus(other: Point3<N>): Point3<N> = with(euclideanKategory) { this@plus + other }
context(euclideanKategory: EuclideanKategory3<N>)
public operator fun <N> Point3<N>.minus(other: Point3<N>): Vector3<N> = with(euclideanKategory) { this@minus - other }

context(euclideanKategory: EuclideanKategory3<N>)
public val <N> Vector3<N>.lengthSquared: N get() = with(euclideanKategory) { this@lengthSquared.lengthSquared }

context(euclideanKategory: EuclideanKategory3<N>)
public infix fun <N> Vector3<N>.dot(other: Vector3<N>): N = with(euclideanKategory) { this@dot dot other }

internal class EuclideanKategory3WithNumberRingAndVectorKategory<N>(
    internal val numberRing: Ring<N>,
    private val vectorKategory: VectorKategory<N> = numberRing.vectorKategory(),
): EuclideanKategory3<N> {
    override val pointEquality: Equality<Point<N>> = pointEquality(numberRing)
    override val vectorEquality: Equality<Vector<N>> = vectorEquality(numberRing)
    
    override fun Vector3<N>.unaryMinus(): Vector3<N> = Vector3(context(vectorKategory) { -coordinates })
    override fun Vector3<N>.plus(other: Vector3<N>): Vector3<N> = Vector3(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Vector3<N>.minus(other: Vector3<N>): Vector3<N> = Vector3(context(vectorKategory) { this.coordinates - other.coordinates })
    
    override fun Vector3<N>.times(other: N): Vector3<N> = Vector3(context(vectorKategory) { coordinates * other })
    override fun N.times(other: Vector3<N>): Vector3<N> = Vector3(context(vectorKategory) { this * other.coordinates })
    
    override fun Point3<N>.plus(other: Vector3<N>): Point3<N> = Point3(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Point3<N>.minus(other: Vector3<N>): Point3<N> = Point3(context(vectorKategory) { this.coordinates - other.coordinates })
    override fun Vector3<N>.plus(other: Point3<N>): Point3<N> = Point3(context(vectorKategory) { this.coordinates + other.coordinates })
    override fun Point3<N>.minus(other: Point3<N>): Vector3<N> = Vector3(context(vectorKategory) { this.coordinates - other.coordinates })
    
    override val Vector3<N>.lengthSquared: N get() = context(numberRing) { this.x * this.x + this.y * this.y + this.z * this.z }
    
    override fun Vector3<N>.dot(other: Vector3<N>): N = context(numberRing) { this.x * other.x + this.y * other.y + this.z * other.z }
}