/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.multidimensionalCollections.MDList1
import kotlinx.serialization.Serializable


// FIXME: KT-42977
@Serializable(with = PointSerializer::class)
//@JvmInline
public open /*value*/ class Point<out N>(public val coordinates: ColumnVector<N>) {
    override fun toString(): String = "Point${coordinates.coefficients}"
}

@Serializable(with = Point2Serializer::class)
//@JvmInline
public /*value*/ class Point2<out N>(coordinates: ColumnVector<N>): Point<N>(coordinates) {
    init {
        require(coordinates.size == 2u) { "Cannot create a euclidean point of dimension 2 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    
    override fun toString(): String = "Point2${coordinates.coefficients}"
}

@Serializable(with = Point3Serializer::class)
//@JvmInline
public /*value*/ class Point3<out N>(coordinates: ColumnVector<N>): Point<N>(coordinates) {
    init {
        require(coordinates.size == 3u) { "Cannot create a euclidean point of dimension 3 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    public val z: N get() = coordinates[2u]
    
    override fun toString(): String = "Point3${coordinates.coefficients}"
}

@Serializable(with = Point4Serializer::class)
//@JvmInline
public /*value*/ class Point4<out N>(coordinates: ColumnVector<N>): Point<N>(coordinates) {
    init {
        require(coordinates.size == 4u) { "Cannot create a euclidean point of dimension 4 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    public val z: N get() = coordinates[2u]
    public val t: N get() = coordinates[3u]
    
    override fun toString(): String = "Point4${coordinates.coefficients}"
}

public fun <N> Point(coordinates: MDList1<N>): Point<N> = Point(ColumnVector(coordinates))
public fun <N> Point(vararg coordinates: N): Point<N> = Point(ColumnVector(*coordinates))
public fun <N> Point(size: UInt, initializer: (coordinate: UInt) -> N): Point<N> = Point(ColumnVector(size, initializer))

public fun <N> Point2(coordinates: MDList1<N>): Point2<N> = Point2(ColumnVector(coordinates))
public fun <N> Point2(x: N, y: N): Point2<N> = Point2(ColumnVector(x, y))

public fun <N> Point3(coordinates: MDList1<N>): Point3<N> = Point3(ColumnVector(coordinates))
public fun <N> Point3(x: N, y: N, z: N): Point3<N> = Point3(ColumnVector(x, y, z))

public fun <N> Point4(coordinates: MDList1<N>): Point4<N> = Point4(ColumnVector(coordinates))
public fun <N> Point4(x: N, y: N, z: N, t: N): Point4<N> = Point4(ColumnVector(x, y, z, t))