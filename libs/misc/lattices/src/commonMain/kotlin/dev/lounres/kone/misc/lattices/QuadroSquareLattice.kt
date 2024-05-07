/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices

import dev.lounres.kone.collections.KoneIterableCollection
import dev.lounres.kone.collections.koneMutableIterableSetOf
import dev.lounres.kone.collections.utils.first


public enum class QuadroSquareKind {
    Up, Down, Left, Right,
}

internal fun Position<Pair<Int, Int>, QuadroSquareKind>.xSymmetry(): Position<Pair<Int, Int>, QuadroSquareKind> =
    Position(
        coordinates = Pair(coordinates.first, -coordinates.second),
        kind = when(kind) {
            QuadroSquareKind.Up -> QuadroSquareKind.Down
            QuadroSquareKind.Down -> QuadroSquareKind.Up
            QuadroSquareKind.Left -> QuadroSquareKind.Left
            QuadroSquareKind.Right -> QuadroSquareKind.Right
        }
    )
internal fun Position<Pair<Int, Int>, QuadroSquareKind>.rotate90(): Position<Pair<Int, Int>, QuadroSquareKind> =
    Position(
        coordinates = Pair(-coordinates.second, coordinates.first),
        kind = when(kind) {
            QuadroSquareKind.Up -> QuadroSquareKind.Left
            QuadroSquareKind.Down -> QuadroSquareKind.Right
            QuadroSquareKind.Left -> QuadroSquareKind.Down
            QuadroSquareKind.Right -> QuadroSquareKind.Up
        }
    )

public object QuadroSquareLattice: LatticeWithConnectivity<Pair<Int, Int>, QuadroSquareKind, Pair<Int, Int>> {

    override fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
        Pair(this.first + other.first, this.second + other.second)

    override fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> =
        Pair(this.first - other.first, this.second - other.second)

    override val rotations: List<(Position<Pair<Int, Int>, QuadroSquareKind>) -> Position<Pair<Int, Int>, QuadroSquareKind>> =
        // TODO: Replace with manually optimised formulas
        listOf(
            { it },
            { it.rotate90() },
            { it.rotate90().rotate90() },
            { it.rotate90().rotate90().rotate90() },
            { it.xSymmetry() },
            { it.xSymmetry().rotate90() },
            { it.xSymmetry().rotate90().rotate90() },
            { it.xSymmetry().rotate90().rotate90().rotate90() },
        )

    override fun KoneIterableCollection<Position<Pair<Int, Int>, QuadroSquareKind>>.isConnected(): Boolean {
        val startPosition = this.first()
        val positionsToTest = ArrayDeque<Position<Pair<Int, Int>, QuadroSquareKind>>()
        positionsToTest.add(startPosition)
        val testedPositions = koneMutableIterableSetOf<Position<Pair<Int, Int>, QuadroSquareKind>>()
        while (positionsToTest.isNotEmpty()) {
            val nextPosition = positionsToTest.removeFirst()
            testedPositions.add(nextPosition)
            val adjacentPositions = when(nextPosition.kind) {
                QuadroSquareKind.Up -> listOf(
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second+1), QuadroSquareKind.Down),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), QuadroSquareKind.Left),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), QuadroSquareKind.Right),
                )
                QuadroSquareKind.Down -> listOf(
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second-1), QuadroSquareKind.Up),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), QuadroSquareKind.Left),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), QuadroSquareKind.Right),
                )
                QuadroSquareKind.Left -> listOf(
                    Position(Pair(nextPosition.coordinates.first-1, nextPosition.coordinates.second), QuadroSquareKind.Right),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), QuadroSquareKind.Up),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), QuadroSquareKind.Down),
                )
                QuadroSquareKind.Right -> listOf(
                    Position(Pair(nextPosition.coordinates.first+1, nextPosition.coordinates.second), QuadroSquareKind.Left),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), QuadroSquareKind.Up),
                    Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second), QuadroSquareKind.Down),
                )
            }

            for (position in adjacentPositions) if (position !in testedPositions && position in this) positionsToTest.add(position)
        }
        return testedPositions.size == this.size
    }
}