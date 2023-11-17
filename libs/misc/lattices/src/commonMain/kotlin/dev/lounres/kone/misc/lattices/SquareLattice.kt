/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices


public object SquareKind

public object SquareLattice: Lattice<Pair<Int, Int>, SquareKind, Pair<Int, Int>> {
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
}