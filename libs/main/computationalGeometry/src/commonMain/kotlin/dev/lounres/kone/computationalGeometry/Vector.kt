/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.multidimensionalCollections.MDList1
import kotlinx.serialization.Serializable


// FIXME: KT-42977
@Serializable(with = VectorSerializer::class)
//@JvmInline
public open /*value*/ class Vector<out N>(public val coordinates: ColumnVector<N>) {
    override fun toString(): String = "Vector${coordinates.coefficients}"
}

@Serializable(with = Vector2Serializer::class)
//@JvmInline
public /*value*/ class Vector2<out N>(coordinates: ColumnVector<N>): Vector<N>(coordinates) {
    init {
        require(coordinates.size == 2u) { "Cannot create a euclidean vector of dimension 2 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
    
    override fun toString(): String = "Vector2${coordinates.coefficients}"
}

public fun <N> Vector(coordinates: MDList1<N>): Vector<N> = Vector(ColumnVector(coordinates))
public fun <N> Vector(vararg coordinates: N): Vector<N> = Vector(ColumnVector(*coordinates))
public fun <N> Vector(size: UInt, initializer: (coordinate: UInt) -> N): Vector<N> = Vector(ColumnVector(size, initializer))

public fun <N> Vector2(coordinates: MDList1<N>): Vector2<N> = Vector2(ColumnVector(coordinates))
public fun <N> Vector2(x: N, y: N): Vector2<N> = Vector2(ColumnVector(x, y))