/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.util

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.computationalGeometry.Point
import kotlinx.serialization.Serializable


@Serializable
data class PointSetDescription<N>(
    val dim: UInt,
    val coords: List<List<N>>
) {
    val points: KoneIterableList<Point<N>> get() = KoneIterableList(coords.size.toUInt()) { pointIndex ->
        val coord = coords[pointIndex.toInt()]
        Point(coord.size.toUInt()) { coord[it.toInt()] }
    }
}
