/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.algebraic.Algebra
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isOne
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.utils.all
import dev.lounres.kone.multidimensionalCollections.utils.allIndexed
import dev.lounres.kone.multidimensionalCollections.utils.map
import dev.lounres.kone.multidimensionalCollections.utils.mapIndexed
import dev.lounres.kone.relations.neq


private class MDList2Algebra<Number>(
    private val ring: Ring<Number>,
    private val dimension: UInt,
) : Algebra<Number, MDList2<Number>> {
    // region Constants
    override val zero: MDList2<Number> = MDList2(dimension, dimension) { _, _ -> ring.zero }
    override val one: MDList2<Number> = MDList2(dimension, dimension) { row, column -> if (row == column) ring.one else ring.zero }
    // endregion
    
    // region Equality
    override fun MDList2<Number>.equalsTo(other: MDList2<Number>): Boolean {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        for (row in 0u ..< dimension) for (column in 0u ..< dimension)
            if (ring { this[row, column] neq other[row, column] }) return false
        return true
    }
    override fun MDList2<Number>.isZero(): Boolean {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        return all { ring { it.isZero() } }
    }
    override fun MDList2<Number>.isOne(): Boolean {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        return allIndexed { rowIndex, columnIndex, value -> ring { if (rowIndex == columnIndex) value.isOne() else value.isZero() } }
    }
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): MDList2<Number> {
        val scalar = ring.valueOf(arg)
        return MDList2(dimension, dimension) { row, column -> if (row == column) scalar else ring.zero }
    }
    override fun valueOf(arg: ULong): MDList2<Number> {
        val scalar = ring.valueOf(arg)
        return MDList2(dimension, dimension) { row, column -> if (row == column) scalar else ring.zero }
    }
    // endregion
    
    // region Number conversion
    override fun valueOf(arg: Number): MDList2<Number> = MDList2(dimension, dimension) { row, column -> if (row == column) arg else ring.zero }
    // endregion
    
    // region Matrix-UInt operations
    override fun MDList2<Number>.plus(other: UInt): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        val summand = ring.valueOf(other)
        return mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) number + summand else number } }
    }
    override fun MDList2<Number>.minus(other: UInt): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        val summand = ring.valueOf(other)
        return mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) number - summand else number } }
    }
    override fun MDList2<Number>.times(other: UInt): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        return map { ring { it * other } }
    }
    // endregion
    
    // region Matrix-ULong operations
    override fun MDList2<Number>.plus(other: ULong): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        val summand = ring.valueOf(other)
        return mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) number + summand else number } }
    }
    override fun MDList2<Number>.minus(other: ULong): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        val summand = ring.valueOf(other)
        return mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) number - summand else number } }
    }
    override fun MDList2<Number>.times(other: ULong): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        return map { ring { it * other } }
    }
    // endregion
    
    // region Matrix-Number operations
    override fun MDList2<Number>.plus(other: Number): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        return mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) number + other else number } }
    }
    override fun MDList2<Number>.minus(other: Number): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        return mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) number - other else number } }
    }
    override fun MDList2<Number>.times(other: Number): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        return map { ring { it * other } }
    }
    // endregion
    
    // region UInt-Matrix operations
    override fun UInt.plus(other: MDList2<Number>): MDList2<Number> {
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        val summand = ring.valueOf(this)
        return other.mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) summand + number else number } }
    }
    override fun UInt.minus(other: MDList2<Number>): MDList2<Number> {
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        val summand = ring.valueOf(this)
        return other.mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) summand - number else -number } }
    }
    override fun UInt.times(other: MDList2<Number>): MDList2<Number> {
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        return other.map { ring { it * this } }
    }
    // endregion
    
    // region ULong-Matrix operations
    override fun ULong.plus(other: MDList2<Number>): MDList2<Number> {
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        val summand = ring.valueOf(this)
        return other.mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) summand + number else number } }
    }
    override fun ULong.minus(other: MDList2<Number>): MDList2<Number> {
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        val summand = ring.valueOf(this)
        return other.mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) summand - number else -number } }
    }
    override fun ULong.times(other: MDList2<Number>): MDList2<Number> {
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        return other.map { ring { it * this } }
    }
    // endregion
    
    // region Number-Matrix operations
    override fun Number.plus(other: MDList2<Number>): MDList2<Number> {
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        return other.mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) this + number else number } }
    }
    override fun Number.minus(other: MDList2<Number>): MDList2<Number> {
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        return other.mapIndexed { rowIndex, columnIndex, number -> ring { if (rowIndex == columnIndex) this - number else -number } }
    }
    override fun Number.times(other: MDList2<Number>): MDList2<Number> {
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        return other.map { ring { this * it } }
    }
    // endregion
    
    // region Matrix-Matrix operations
    override fun MDList2<Number>.unaryMinus(): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        return map { ring { -it } }
    }
    override fun MDList2<Number>.plus(other: MDList2<Number>): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        return MDList2(dimension, dimension) { row, column -> ring { this[row, column] + other[row, column] } }
    }
    override fun MDList2<Number>.minus(other: MDList2<Number>): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        return MDList2(dimension, dimension) { row, column -> ring { this[row, column] - other[row, column] } }
    }
    override fun MDList2<Number>.times(other: MDList2<Number>): MDList2<Number> {
        require(this.rowNumber == dimension && this.columnNumber == dimension) { TODO() }
        require(other.rowNumber == dimension && other.columnNumber == dimension) { TODO() }
        return MDList2(dimension, dimension) { row, column ->
            ring { (0u ..< dimension).toKoneList().sumOf { this[row, it] * other[it, column] } }
        }
    }
    override fun power(base: MDList2<Number>, exponent: UInt): MDList2<Number> = base squaringPower exponent
    override fun power(base: MDList2<Number>, exponent: ULong): MDList2<Number> = base squaringPower exponent
    // endregion
}