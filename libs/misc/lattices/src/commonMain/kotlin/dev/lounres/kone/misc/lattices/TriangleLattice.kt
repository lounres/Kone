/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices

import dev.lounres.kone.collections.KoneIterableCollection
import dev.lounres.kone.collections.koneMutableIterableSetOf
import dev.lounres.kone.collections.utils.first


public enum class TriangleKind {
    Up, Down
}

internal operator fun TriangleKind.not(): TriangleKind =
    when(this) {
        TriangleKind.Up -> TriangleKind.Down
        TriangleKind.Down -> TriangleKind.Up
    }
internal fun Position<Pair<Int, Int>, TriangleKind>.rotate60(): Position<Pair<Int, Int>, TriangleKind> =
    when(kind) {
        TriangleKind.Up -> Position(Pair(-coordinates.second - 1, coordinates.first + coordinates.second), TriangleKind.Down)
        TriangleKind.Down -> Position(Pair(-coordinates.second - 1, coordinates.first + coordinates.second + 1), TriangleKind.Up)
    }
internal fun Position<Pair<Int, Int>, TriangleKind>.xySymmetry(): Position<Pair<Int, Int>, TriangleKind> =
    Position(Pair(coordinates.second, coordinates.first), kind)
internal fun Position<Pair<Int, Int>, TriangleKind>.xyPerpSymmetry(): Position<Pair<Int, Int>, TriangleKind> =
    Position(Pair(-coordinates.first, -coordinates.second), !kind)

public object TriangleLattice: LatticeWithConnectivity<Pair<Int, Int>, TriangleKind, Pair<Int, Int>> {

    override fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
        Pair(this.first + other.first, this.second + other.second)

    override fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> =
        Pair(this.first - other.first, this.second - other.second)

    override val rotations: List<(Position<Pair<Int, Int>, TriangleKind>) -> Position<Pair<Int, Int>, TriangleKind>> =
        // TODO: Replace with manually optimised formulas
        listOf(
            { it },
            { it.rotate60() },
            { it.rotate60().rotate60() },
            { it.rotate60().rotate60().rotate60() },
            { it.rotate60().rotate60().rotate60().rotate60() },
            { it.rotate60().rotate60().rotate60().rotate60().rotate60() },
            { it.xySymmetry() },
            { it.xySymmetry().rotate60() },
            { it.xySymmetry().rotate60().rotate60() },
            { it.xySymmetry().rotate60().rotate60().rotate60() },
            { it.xySymmetry().rotate60().rotate60().rotate60().rotate60() },
            { it.xySymmetry().rotate60().rotate60().rotate60().rotate60().rotate60() },
        )

    override fun KoneIterableCollection<Position<Pair<Int, Int>, TriangleKind>>.isConnected(): Boolean {
        val startPosition = this.first()
        val positionsToTest = ArrayDeque<Position<Pair<Int, Int>, TriangleKind>>()
        positionsToTest.add(startPosition)
        val testedPositions = koneMutableIterableSetOf<Position<Pair<Int, Int>, TriangleKind>>()
        while (positionsToTest.isNotEmpty()) {
            val nextPosition = positionsToTest.removeFirst()
            testedPositions.add(nextPosition)
            val adjacentPositions = when(nextPosition.kind) {
                TriangleKind.Up -> listOf(
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second-1), TriangleKind.Down),
                    Position(Pair(nextPosition.coordinates.first-1, nextPosition.coordinates.second), TriangleKind.Down),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), TriangleKind.Down),
                )
                TriangleKind.Down -> listOf(
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second+1), TriangleKind.Up),
                    Position(Pair(nextPosition.coordinates.first+1, nextPosition.coordinates.second), TriangleKind.Up),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), TriangleKind.Up),
                )
            }

            for (position in adjacentPositions) if (position !in testedPositions && position in this) positionsToTest.add(position)
        }
        return testedPositions.size == this.size
    }
}