/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.contentSize
import kotlinx.serialization.Serializable


// FIXME: KT-42977
@Serializable(with = PointSerializer::class)
//@JvmInline
public open /*value*/ class Point<out N, out Content: MDList1<N>>(public val coordinates: Content) {
    override fun toString(): String = "Point${coordinates}"
    
    public companion object
}

@Serializable(with = Point2Serializer::class)
//@JvmInline
public /*value*/ class Point2<out N, out Content: MDList1<N>>(coordinates: Content): Point<N, Content>(coordinates) {
    init {
        require(coordinates.contentSize == 2u) { "Cannot create a euclidean point of dimension 2 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    
    override fun toString(): String = "Point2${coordinates}"
}

@Serializable(with = Point3Serializer::class)
//@JvmInline
public /*value*/ class Point3<out N, out Content: MDList1<N>>(coordinates: Content): Point<N, Content>(coordinates) {
    init {
        require(coordinates.contentSize == 3u) { "Cannot create a euclidean point of dimension 3 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    public val z: N get() = coordinates[2u]
    
    override fun toString(): String = "Point3${coordinates}"
}

@Serializable(with = Point4Serializer::class)
//@JvmInline
public /*value*/ class Point4<out N, out Content: MDList1<N>>(coordinates: Content): Point<N, Content>(coordinates) {
    init {
        require(coordinates.contentSize == 4u) { "Cannot create a euclidean point of dimension 4 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    public val z: N get() = coordinates[2u]
    public val t: N get() = coordinates[3u]
    
    override fun toString(): String = "Point4${coordinates}"
}

public fun <N> Point(vararg coordinates: N): Point<N, MDList1<N>> = Point(MDList1(*coordinates))
public fun <N> Point(size: UInt, initializer: (coordinate: UInt) -> N): Point<N, MDList1<N>> = Point(MDList1(size, initializer))

public fun <N> Point2(x: N, y: N): Point2<N, MDList1<N>> = Point2(MDList1(x, y))

public fun <N> Point3(x: N, y: N, z: N): Point3<N, MDList1<N>> = Point3(MDList1(x, y, z))

public fun <N> Point4(x: N, y: N, z: N, t: N): Point4<N, MDList1<N>> = Point4(MDList1(x, y, z, t))