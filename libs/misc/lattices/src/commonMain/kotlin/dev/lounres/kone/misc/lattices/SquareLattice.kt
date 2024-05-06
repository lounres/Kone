/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices


public data object SquareKind

public object SquareLattice: LatticeWithConnectivity<Pair<Int, Int>, SquareKind, Pair<Int, Int>> {
    public fun <A> Cell(coordinates: Pair<Int, Int>, attributes: Set<A> = emptySet()): Cell<Pair<Int, Int>, SquareKind, A> = Cell(Position(coordinates, SquareKind), attributes)

    override fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
        Pair(this.first + other.first, this.second + other.second)

    override fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> =
        Pair(this.first - other.first, this.second - other.second)

    override val rotations: List<(Position<Pair<Int, Int>, SquareKind>) -> Position<Pair<Int, Int>, SquareKind>> =
        listOf(
            { it },
            { Position(Pair(-it.coordinates.first, it.coordinates.second), it.kind) },
            { Position(Pair(it.coordinates.first, -it.coordinates.second), it.kind) },
            { Position(Pair(-it.coordinates.first, -it.coordinates.second), it.kind) },
            { Position(Pair(it.coordinates.second, it.coordinates.first), it.kind) },
            { Position(Pair(-it.coordinates.second, it.coordinates.first), it.kind) },
            { Position(Pair(it.coordinates.second, -it.coordinates.first), it.kind) },
            { Position(Pair(-it.coordinates.second, -it.coordinates.first), it.kind) },
        )

    override fun Collection<Position<Pair<Int, Int>, SquareKind>>.isConnected(): Boolean {
        val startPosition = this.first()
        val positionsToTest = ArrayDeque<Position<Pair<Int, Int>, SquareKind>>()
        positionsToTest.add(startPosition)
        val testedPositions = mutableSetOf<Position<Pair<Int, Int>, SquareKind>>()
        while (positionsToTest.isNotEmpty()) {
            val nextPosition = positionsToTest.removeFirst()
            testedPositions.add(nextPosition)
            val adjacentPositions = listOf(
                Position(Pair(nextPosition.coordinates.first+1, nextPosition.coordinates.second), nextPosition.kind),
                Position(Pair(nextPosition.coordinates.first-1, nextPosition.coordinates.second), nextPosition.kind),
                Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second+1), nextPosition.kind),
                Position(Pair(nextPosition.coordinates.first, nextPosition.coordinates.second-1), nextPosition.kind),
            )
            for (position in adjacentPositions) if (position !in testedPositions && position in this) positionsToTest.add(position)
        }
        return testedPositions.size == this.size
    }
}