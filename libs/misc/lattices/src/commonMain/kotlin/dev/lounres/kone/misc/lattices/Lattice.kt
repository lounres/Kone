/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.implementations.KoneResizableHashSet
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.combinatorics.enumerative.combinations
import dev.lounres.kone.computations.ChannelComputation
import dev.lounres.kone.computations.ComputationScope
import dev.lounres.kone.context.KoneContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
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

// FIXME: Uncomment when KT-5837 will be fixed.
//context(Lattice<C, K, *>)
//public inline operator fun <C, K, A> ((Position<C, K>) -> Position<C, K>).invoke(cell: Cell<C, K, A>): Cell<C, K, A> =
//    Cell(this(cell.position), cell.attributes)

// FIXME: Uncomment when KT-5837 will be fixed.
//context(Lattice<C, K, *>)
//public inline operator fun <C, K, A> ((Position<C, K>) -> Position<C, K>).invoke(cells: KoneIterableSet<Cell<C, K, A>>): KoneIterableSet<Cell<C, K, A>> =
//    cells.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `cells.size` */)) { this(it) }

context(CoroutineScope, Lattice<C, K, V>)
public fun <C, K, A, V> KoneIterableSet<Cell<C, K, A>>.divideInParts(numberOfParts: UInt, takeFormIf: (KoneIterableSet<Position<C, K>>) -> Boolean = { true }): Sequence<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> = sequence {
    // TODO: В идеале здесь нужна проверка на то, что никакие две клетки не равны одновременно в координатах и в типе
    if (this@divideInParts.groupingBy { it.attributes }.eachCount().values.any { it % numberOfParts != 0u }) return@sequence
    if (this@divideInParts.isEmpty()) {
        yield(emptyKoneIterableList())
        return@sequence
    }
    val cellsPerPart = size / numberOfParts
    val allCells = this@divideInParts

    val firstCell = allCells.first()
    for (otherCellsOfFirstPart in buildKoneIterableList { addAllFrom(allCells); remove(firstCell) }.combinations(cellsPerPart - 1u)) {
        if(!isActive) return@sequence
        val firstPart = buildKoneIterableSet(initialCapacity = otherCellsOfFirstPart.size + 1u) {
            addAllFrom(otherCellsOfFirstPart)
            add(firstCell)
        }
        if (!takeFormIf(firstPart.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `firstPart.size` */)) { it.position })) continue
        val restCells = buildKoneIterableSet {
            addAllFrom(allCells)
            removeAllFrom(firstPart)
        }

        data class Form<C, K, A>(val startCell: Cell<C, K ,A>, val cells: KoneIterableSet<Cell<C, K, A>>)
        val forms = rotations.map {
            Form(
                Cell(it(firstCell.position), firstCell.attributes),
                firstPart.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `cells.size` */)) { cell ->
                    Cell(it(cell.position), cell.attributes)
                }
            )
        }

        val allPossibleParts = buildKoneIterableSet {
            for (form in forms) for (otherFirstCell in restCells) {
                if(!isActive) return@sequence
                if (otherFirstCell.position.kind != form.startCell.position.kind) continue
                val shift = otherFirstCell - form.startCell
                val part = form.cells.mapTo(KoneResizableHashSet(/* TODO: Replace with fixed capacity implementation with capacity `cellsPerPart` */)) { it + shift }
                if (part.all { it in allCells } && part.none { it in firstPart }) add(part)
            }
        }.toKoneIterableList()

        for (parts in allPossibleParts.combinations(numberOfParts - 1u)) {
            if(!isActive) return@sequence
            if (parts.combinations(2u).any { (part1, part2) -> part1.any { it in part2 } }) continue
            yield(buildKoneIterableList { addAllFrom(parts); add(firstPart) })
        }
    }
}

// TODO: Architect and implement sets with built-in partition. Maybe.

context(ComputationScope, Lattice<C, K, V>)
public fun <C, K, A, V> KoneIterableSet<Cell<C, K, A>>.divideInParts2(numberOfParts: UInt, takeFormIf: (KoneIterableSet<Position<C, K>>) -> Boolean = { true }): ChannelComputation<KoneIterableList<KoneIterableSet<Cell<C, K, A>>>> =
    TODO("Not yet implemented")