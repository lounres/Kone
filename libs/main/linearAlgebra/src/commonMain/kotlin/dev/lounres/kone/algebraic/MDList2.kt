/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.multidimensionalCollections.utils.all
import dev.lounres.kone.multidimensionalCollections.utils.allIndexed
import dev.lounres.kone.multidimensionalCollections.utils.map
import dev.lounres.kone.multidimensionalCollections.utils.mapIndexed


private class MDList2Algebra<Number>(
    private val ring: CommutativeRing<Number>,
    private val dimension: UInt,
) : Algebra<Number, MDList2<Number>> {
    // region Constants
    override val zero: MDList2<Number> = MDList2.generate(dimension, dimension) { _, _ -> ring.zero }
    override val one: MDList2<Number> = MDList2.generate(dimension, dimension) { row, column -> if (row == column) ring.one else ring.zero }
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<MDList2<Number>> = IsZero {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.all { ring.numberIsZero { it.isZero() } }
    }
    override val numberIsOne: IsOne<MDList2<Number>> = IsOne {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.allIndexed { rowIndex, columnIndex, value -> if (rowIndex == columnIndex) ring.numberIsOne { value.isOne() } else ring.numberIsZero { value.isZero() } }
    }
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): MDList2<Number> {
        val scalar = ring.valueOf(arg)
        return MDList2.generate(dimension, dimension) { row, column -> if (row == column) scalar else ring.zero }
    }
    override fun valueOf(arg: ULong): MDList2<Number> {
        val scalar = ring.valueOf(arg)
        return MDList2.generate(dimension, dimension) { row, column -> if (row == column) scalar else ring.zero }
    }
    // endregion
    
    // region Number conversion
    override fun valueOf(arg: Number): MDList2<Number> = MDList2.generate(dimension, dimension) { row, column -> if (row == column) arg else ring.zero }
    // endregion
    
    // region Matrix-Int operations
    override val numberPlusInt: Plus<MDList2<Number>, Int, MDList2<Number>> = Plus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusInt { if (rowIndex == columnIndex) number + other else number } }
    }
    override val numberMinusInt: Minus<MDList2<Number>, Int, MDList2<Number>> = Minus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusInt { if (rowIndex == columnIndex) number - other else number } }
    }
    override val numberTimesInt: Times<MDList2<Number>, Int, MDList2<Number>> = Times { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.map { ring.numberTimesInt { it * other } }
    }
    // endregion
    
    // region Matrix-UInt operations
    override val numberPlusUInt: Plus<MDList2<Number>, UInt, MDList2<Number>> = Plus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusUInt { if (rowIndex == columnIndex) number + other else number } }
    }
    override val numberMinusUInt: Minus<MDList2<Number>, UInt, MDList2<Number>> = Minus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusUInt { if (rowIndex == columnIndex) number - other else number } }
    }
    override val numberTimesUInt: Times<MDList2<Number>, UInt, MDList2<Number>> = Times { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.map { ring.numberTimesUInt { it * other } }
    }
    // endregion
    
    // region Matrix-ULong operations
    override val numberPlusLong: Plus<MDList2<Number>, Long, MDList2<Number>> = Plus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusLong { if (rowIndex == columnIndex) number + other else number } }
    }
    override val numberMinusLong: Minus<MDList2<Number>, Long, MDList2<Number>> = Minus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusLong { if (rowIndex == columnIndex) number - other else number } }
    }
    override val numberTimesLong: Times<MDList2<Number>, Long, MDList2<Number>> = Times { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.map { ring.numberTimesLong { it * other } }
    }
    // endregion
    
    // region Matrix-ULong operations
    override val numberPlusULong: Plus<MDList2<Number>, ULong, MDList2<Number>> = Plus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusULong { if (rowIndex == columnIndex) number + other else number } }
    }
    override val numberMinusULong: Minus<MDList2<Number>, ULong, MDList2<Number>> = Minus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusULong { if (rowIndex == columnIndex) number - other else number } }
    }
    override val numberTimesULong: Times<MDList2<Number>, ULong, MDList2<Number>> = Times { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.map { ring.numberTimesULong { it * other } }
    }
    // endregion
    
    // region Matrix-Number operations
    override val vectorPlusNumber: Plus<MDList2<Number>, Number, MDList2<Number>> = Plus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusNumber { if (rowIndex == columnIndex) number + other else number } }
    }
    override val vectorMinusNumber: Minus<MDList2<Number>, Number, MDList2<Number>> = Minus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusNumber { if (rowIndex == columnIndex) number - other else number } }
    }
    override val vectorTimesNumber: Times<MDList2<Number>, Number, MDList2<Number>> = Times { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.map { ring.numberTimesNumber { it * other } }
    }
    // endregion
    
    // region Int-Matrix operations
    override val intPlusNumber: Plus<Int, MDList2<Number>, MDList2<Number>> = Plus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> ring.intPlusNumber { if (rowIndex == columnIndex) this + number else number } }
    }
    override val intMinusNumber: Minus<Int, MDList2<Number>, MDList2<Number>> = Minus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> context(ring.intMinusNumber, ring.numberUnaryMinus) { if (rowIndex == columnIndex) this - number else -number } }
    }
    override val intTimesNumber: Times<Int, MDList2<Number>, MDList2<Number>> = Times { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.map { ring.intTimesNumber { this * it } }
    }
    // endregion
    
    // region UInt-Matrix operations
    override val uIntPlusNumber: Plus<UInt, MDList2<Number>, MDList2<Number>> = Plus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> ring.uIntPlusNumber { if (rowIndex == columnIndex) this + number else number } }
    }
    override val uIntMinusNumber: Minus<UInt, MDList2<Number>, MDList2<Number>> = Minus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> context(ring.uIntMinusNumber, ring.numberUnaryMinus) { if (rowIndex == columnIndex) this - number else -number } }
    }
    override val uIntTimesNumber: Times<UInt, MDList2<Number>, MDList2<Number>> = Times { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.map { ring.uIntTimesNumber { this * it } }
    }
    // endregion
    
    // region Long-Matrix operations
    override val longPlusNumber: Plus<Long, MDList2<Number>, MDList2<Number>> = Plus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> ring.longPlusNumber { if (rowIndex == columnIndex) this + number else number } }
    }
    override val longMinusNumber: Minus<Long, MDList2<Number>, MDList2<Number>> = Minus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> context(ring.longMinusNumber, ring.numberUnaryMinus) { if (rowIndex == columnIndex) this - number else -number } }
    }
    override val longTimesNumber: Times<Long, MDList2<Number>, MDList2<Number>> = Times { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.map { ring.longTimesNumber { this * it } }
    }
    // endregion
    
    // region ULong-Matrix operations
    override val uLongPlusNumber: Plus<ULong, MDList2<Number>, MDList2<Number>> = Plus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> ring.uLongPlusNumber { if (rowIndex == columnIndex) this + number else number } }
    }
    override val uLongMinusNumber: Minus<ULong, MDList2<Number>, MDList2<Number>> = Minus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> context(ring.uLongMinusNumber, ring.numberUnaryMinus) { if (rowIndex == columnIndex) this - number else -number } }
    }
    override val uLongTimesNumber: Times<ULong, MDList2<Number>, MDList2<Number>> = Times { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.map { ring.uLongTimesNumber { this * it } }
    }
    // endregion
    
    // region Number-Matrix operations
    override val numberPlusVector: Plus<Number, MDList2<Number>, MDList2<Number>> = Plus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusNumber { if (rowIndex == columnIndex) this + number else number } }
    }
    override val numberMinusVector: Minus<Number, MDList2<Number>, MDList2<Number>> = Minus { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.mapIndexed { rowIndex, columnIndex, number -> context(ring.numberMinusNumber, ring.numberUnaryMinus) { if (rowIndex == columnIndex) this - number else -number } }
    }
    override val numberTimesVector: Times<Number, MDList2<Number>, MDList2<Number>> = Times { other ->
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        other.map { ring.numberTimesNumber { this * it } }
    }
    // endregion
    
    // region Matrix-Matrix operations
    override val numberUnaryMinus: UnaryMinus<MDList2<Number>, MDList2<Number>> = UnaryMinus {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        this.map { ring.numberUnaryMinus { -it } }
    }
    override val numberPlusNumber: Plus<MDList2<Number>, MDList2<Number>, MDList2<Number>> = Plus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        MDList2.generate(dimension, dimension) { row, column -> ring.numberPlusNumber { this[row, column] + other[row, column] } }
    }
    override val numberMinusNumber: Minus<MDList2<Number>, MDList2<Number>, MDList2<Number>> = Minus { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        MDList2.generate(dimension, dimension) { row, column -> ring.numberMinusNumber { this[row, column] - other[row, column] } }
    }
    override val numberTimesNumber: Times<MDList2<Number>, MDList2<Number>, MDList2<Number>> = Times { other ->
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        MDList2.generate(dimension, dimension) { row, column ->
            context(ring, ring.numberTimesNumber) { (0u ..< dimension).toKoneList().sumOf { this[row, it] * other[it, column] } }
        }
    }
    // endregion
}

public fun <Number> Algebra.Companion.mdList2(ring: CommutativeRing<Number>, dimension: UInt): Algebra<Number, MDList2<Number>> =
    MDList2Algebra(ring = ring, dimension = dimension)