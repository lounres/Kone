/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.contentSize
import kotlinx.serialization.Serializable


// FIXME: KT-42977
@Serializable(with = VectorSerializer::class)
//@JvmInline
public open /*value*/ class Vector<out N, out Content: MDList1<N>>(public val coordinates: Content) {
    override fun toString(): String = "Vector${coordinates}"
    
    public companion object;
}

@Serializable(with = Vector2Serializer::class)
//@JvmInline
public /*value*/ class Vector2<out N, out Content: MDList1<N>>(coordinates: Content): Vector<N, Content>(coordinates) {
    init {
        require(coordinates.contentSize == 2u) { "Cannot create a euclidean vector of dimension 2 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    
    override fun toString(): String = "Vector2${coordinates}"
}

@Serializable(with = Vector3Serializer::class)
//@JvmInline
public /*value*/ class Vector3<out N, out Content: MDList1<N>>(coordinates: Content): Vector<N, Content>(coordinates) {
    init {
        require(coordinates.contentSize == 3u) { "Cannot create a euclidean vector of dimension 3 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    public val z: N get() = coordinates[2u]
    
    override fun toString(): String = "Vector3${coordinates}"
}

@Serializable(with = Vector4Serializer::class)
//@JvmInline
public /*value*/ class Vector4<out N, out Content: MDList1<N>>(coordinates: Content): Vector<N, Content>(coordinates) {
    init {
        require(coordinates.contentSize == 4u) { "Cannot create a euclidean vector of dimension 4 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    public val z: N get() = coordinates[2u]
    public val t: N get() = coordinates[3u]
    
    override fun toString(): String = "Vector4${coordinates}"
}

public fun <N> Vector(vararg coordinates: N): Vector<N, MDList1<N>> = Vector(MDList1(*coordinates))
public fun <N> Vector(size: UInt, initializer: (coordinate: UInt) -> N): Vector<N, MDList1<N>> = Vector(MDList1(size, initializer))

public fun <N> Vector2(x: N, y: N): Vector2<N, MDList1<N>> = Vector2(MDList1(x, y))

public fun <N> Vector3(x: N, y: N, z: N): Vector3<N, MDList1<N>> = Vector3(MDList1(x, y, z))

public fun <N> Vector4(x: N, y: N, z: N, t: N): Vector4<N, MDList1<N>> = Vector4(MDList1(x, y, z, t))