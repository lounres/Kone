/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices


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

public object QuadroSquareLattice: Lattice<Pair<Int, Int>, QuadroSquareKind, Pair<Int, Int>> {

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
}