/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices


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

public object TriangleLattice: Lattice<Pair<Int, Int>, TriangleKind, Pair<Int, Int>> {

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
}