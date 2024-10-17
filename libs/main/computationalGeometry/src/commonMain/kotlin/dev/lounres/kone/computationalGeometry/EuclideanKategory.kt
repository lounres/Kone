/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.computationalGeometry.utils.fold
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.experiment1.VectorKategory
import dev.lounres.kone.linearAlgebra.experiment1.requireShapeEquality


public interface EuclideanKategory<N> : KoneContext {
    public operator fun Vector<N>.unaryPlus(): Vector<N> = this
    public operator fun Vector<N>.unaryMinus(): Vector<N>
    public operator fun Vector<N>.plus(other: Vector<N>): Vector<N>
    public operator fun Vector<N>.minus(other: Vector<N>): Vector<N>

    public operator fun Vector<N>.times(other: N): Vector<N>
    public operator fun N.times(other: Vector<N>): Vector<N>

    public operator fun Point<N>.plus(other: Vector<N>): Point<N>
    public operator fun Vector<N>.plus(other: Point<N>): Point<N>
    public operator fun Point<N>.minus(other: Point<N>): Vector<N>

    public val Vector<N>.lengthSquared: N

    public infix fun Vector<N>.dot(other: Vector<N>): N
}

@PublishedApi
internal class EuclideanKategoryWithNumberRingAndVectorKategory<N>(
    internal val numberRing: Ring<N>,
    private val vectorKategory: VectorKategory<N>,
): EuclideanKategory<N> {

    override fun Vector<N>.unaryMinus(): Vector<N> = Vector(vectorKategory { -coordinates })
    override fun Vector<N>.plus(other: Vector<N>): Vector<N> = Vector(vectorKategory { this.coordinates + other.coordinates })
    override fun Vector<N>.minus(other: Vector<N>): Vector<N> = Vector(vectorKategory { this.coordinates - other.coordinates })

    override fun Vector<N>.times(other: N): Vector<N> = Vector(vectorKategory { coordinates * other })
    override fun N.times(other: Vector<N>): Vector<N> = Vector(vectorKategory { this * other.coordinates })

    override fun Point<N>.plus(other: Vector<N>): Point<N> = Point(vectorKategory { this.coordinates + other.coordinates })
    override fun Vector<N>.plus(other: Point<N>): Point<N> = Point(vectorKategory { this.coordinates + other.coordinates })
    override fun Point<N>.minus(other: Point<N>): Vector<N> = Vector(vectorKategory { this.coordinates - other.coordinates })

    override val Vector<N>.lengthSquared: N get() = fold(numberRing.zero) { acc, n -> numberRing { acc + n * n } }

    override fun Vector<N>.dot(other: Vector<N>): N {
        requireShapeEquality(this.coordinates, other.coordinates)
        val size = this.coordinates.size
        // FIXME: KT-67840
//        return (0u..<size).fold(numberRing.zero) { acc, index -> numberRing { acc + this.coordinates[index] * other.coordinates[index] } }
        var result = numberRing.zero
        var index = 0u
        while (index < size) {
            result = numberRing { result + this.coordinates[index] * other.coordinates[index] }
            index++
        }
        return result
    }
}