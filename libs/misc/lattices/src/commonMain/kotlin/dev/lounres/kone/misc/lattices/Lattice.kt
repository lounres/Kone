/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices

import dev.lounres.kone.combinatorics.enumerative.combinations
import dev.lounres.kone.context.KoneContext
import kotlin.jvm.JvmName


public data class Position<C, K>(val coordinates: C, val kind: K)
public data class Cell<C, K, A>(val position: Position<C, K>, val attributes: Set<A> = emptySet())
public fun <C, K, A> Cell(coordinates: C, kind: K, attributes: Set<A> = emptySet()): Cell<C, K, A> =
    Cell(Position(coordinates, kind), attributes)

@Suppress("INAPPLICABLE_JVM_NAME")
public interface Lattice<C, K, V>: KoneContext {
    @JvmName("plus-V-V")
    public operator fun V.plus(other: V): V
    @JvmName("minus-V-V")
    public operator fun V.minus(other: V): V

    @JvmName("plus-C-V")
    public operator fun C.plus(other: V): C
    @JvmName("minus-C-V")
    public operator fun C.minus(other: V): C
    @JvmName("minus-C-C")
    public operator fun C.minus(other: C): V

    public val rotations: List<(Position<C, K>) -> Position<C, K>>
}


context(Lattice<C, K, V>)
public operator fun <C, K, A, V> Cell<C, K, A>.plus(other: V): Cell<C, K, A> = Cell(Position(position.coordinates + other, position.kind), attributes)
context(Lattice<C, K, V>)
public operator fun <C, K, A, V> Cell<C, K, A>.minus(other: V): Cell<C, K, A> = Cell(Position(position.coordinates - other, position.kind), attributes)
context(Lattice<C, K, V>)
public operator fun <C, K, A, V> Cell<C, K, A>.minus(other: Cell<C, K, A>): V {
    require(this.position.kind == other.position.kind)
    return this.position.coordinates - other.position.coordinates
}

context(Lattice<C, K, *>)
public inline operator fun <C, K, A> ((Position<C, K>) -> Position<C, K>).invoke(cell: Cell<C, K, A>): Cell<C, K, A> =
    Cell(this(cell.position), cell.attributes)

context(Lattice<C, K, *>)
public inline operator fun <C, K, A> ((Position<C, K>) -> Position<C, K>).invoke(cells: Set<Cell<C, K, A>>): Set<Cell<C, K, A>> =
    cells.mapTo(HashSet(cells.size)) { this(it) }



internal data class Form<C, K, A>(val startCell: Cell<C, K ,A>, val cells: Set<Cell<C, K, A>>)

context(Lattice<C, K, V>)
public fun <C, K, A, V> Set<Cell<C, K, A>>.divideInParts(numberOfParts: Int, takeFormIf: (Set<Position<C, K>>) -> Boolean = { true }): List<List<Set<Cell<C, K, A>>>> {
    // TODO: В идеале здесь нужна проверка на то, что никакие две клетки не равны одновременно в координатах и в типе
    if (this.groupingBy { it.attributes }.eachCount().values.any { it % numberOfParts != 0 }) return listOf()
    if (isEmpty()) return listOf(listOf())
    val cellsPerPart = size / numberOfParts
    val allCells = this

    val results = mutableListOf<List<Set<Cell<C, K, A>>>>()

    val firstCell = allCells.first()
    for (otherCellsOfFirstPart in (allCells - firstCell).toList().combinations(cellsPerPart - 1)) {
        val firstPart = otherCellsOfFirstPart.toSet() + firstCell
        if (!takeFormIf(firstPart.mapTo(HashSet(firstPart.size)) { it.position })) continue
        val restCells = allCells - firstPart
        val forms = rotations.map { Form(it(firstCell), it(firstPart)) }

        val allPossibleParts = buildSet {
            for (form in forms) for (otherFirstCell in restCells) {
                if (otherFirstCell.position.kind != form.startCell.position.kind) continue
                val shift = otherFirstCell - form.startCell
                val part = form.cells.mapTo(HashSet(cellsPerPart)) { it + shift }
                if (part.all { it in allCells } && part.none { it in firstPart }) add(part)
            }
        }.toList()

        for (parts in allPossibleParts.combinations(numberOfParts - 1)) {
            if (parts.combinations(2).any { (part1, part2) -> part1.any { it in part2 } }) continue
            results.add(buildList { addAll(parts); add(firstPart) })
        }
    }

    return results
}