/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.unwrap
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
        require(it.rowNumber == dimension && it.columnNumber == dimension) { TODO() }
        it.all { value -> ring.numberIsZero { value.isZero() } }
    }
    override val numberIsOne: IsOne<MDList2<Number>> = IsOne {
        require(it.rowNumber == dimension && it.columnNumber == dimension) { TODO() }
        it.allIndexed { rowIndex, columnIndex, value -> if (rowIndex == columnIndex) ring.numberIsOne { value.isOne() } else ring.numberIsZero { value.isZero() } }
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
    override val numberPlusInt: Plus<MDList2<Number>, Int, MDList2<Number>> = Plus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusInt { if (rowIndex == columnIndex) number + right else number } }
    }
    override val numberMinusInt: Minus<MDList2<Number>, Int, MDList2<Number>> = Minus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusInt { if (rowIndex == columnIndex) number - right else number } }
    }
    override val numberTimesInt: Times<MDList2<Number>, Int, MDList2<Number>> = Times { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.map { ring.numberTimesInt { it * right } }
    }
    // endregion
    
    // region Matrix-UInt operations
    override val numberPlusUInt: Plus<MDList2<Number>, UInt, MDList2<Number>> = Plus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusUInt { if (rowIndex == columnIndex) number + right else number } }
    }
    override val numberMinusUInt: Minus<MDList2<Number>, UInt, MDList2<Number>> = Minus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusUInt { if (rowIndex == columnIndex) number - right else number } }
    }
    override val numberTimesUInt: Times<MDList2<Number>, UInt, MDList2<Number>> = Times { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.map { ring.numberTimesUInt { it * right } }
    }
    // endregion
    
    // region Matrix-ULong operations
    override val numberPlusLong: Plus<MDList2<Number>, Long, MDList2<Number>> = Plus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusLong { if (rowIndex == columnIndex) number + right else number } }
    }
    override val numberMinusLong: Minus<MDList2<Number>, Long, MDList2<Number>> = Minus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusLong { if (rowIndex == columnIndex) number - right else number } }
    }
    override val numberTimesLong: Times<MDList2<Number>, Long, MDList2<Number>> = Times { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.map { ring.numberTimesLong { it * right } }
    }
    // endregion
    
    // region Matrix-ULong operations
    override val numberPlusULong: Plus<MDList2<Number>, ULong, MDList2<Number>> = Plus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusULong { if (rowIndex == columnIndex) number + right else number } }
    }
    override val numberMinusULong: Minus<MDList2<Number>, ULong, MDList2<Number>> = Minus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusULong { if (rowIndex == columnIndex) number - right else number } }
    }
    override val numberTimesULong: Times<MDList2<Number>, ULong, MDList2<Number>> = Times { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.map { ring.numberTimesULong { it * right } }
    }
    // endregion
    
    // region Matrix-Number operations
    override val vectorPlusNumber: Plus<MDList2<Number>, Number, MDList2<Number>> = Plus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusNumber { if (rowIndex == columnIndex) number + right else number } }
    }
    override val vectorMinusNumber: Minus<MDList2<Number>, Number, MDList2<Number>> = Minus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.mapIndexed { rowIndex, columnIndex, number -> ring.numberMinusNumber { if (rowIndex == columnIndex) number - right else number } }
    }
    override val vectorTimesNumber: Times<MDList2<Number>, Number, MDList2<Number>> = Times { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        left.map { ring.numberTimesNumber { it * right } }
    }
    // endregion
    
    // region Int-Matrix operations
    override val intPlusNumber: Plus<Int, MDList2<Number>, MDList2<Number>> = Plus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        right.mapIndexed { rowIndex, columnIndex, number -> ring.intPlusNumber { if (rowIndex == columnIndex) left + number else number } }
    }
    override val intMinusNumber: Minus<Int, MDList2<Number>, MDList2<Number>> = Minus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        KoneContext.unwrap(ring)
        right.mapIndexed { rowIndex, columnIndex, number -> if (rowIndex == columnIndex) left - number else -number }
    }
    override val intTimesNumber: Times<Int, MDList2<Number>, MDList2<Number>> = Times { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        KoneContext.unwrap(ring)
        right.map { left * it }
    }
    // endregion
    
    // region UInt-Matrix operations
    override val uIntPlusNumber: Plus<UInt, MDList2<Number>, MDList2<Number>> = Plus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        right.mapIndexed { rowIndex, columnIndex, number -> ring.uIntPlusNumber { if (rowIndex == columnIndex) left + number else number } }
    }
    override val uIntMinusNumber: Minus<UInt, MDList2<Number>, MDList2<Number>> = Minus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        right.mapIndexed { rowIndex, columnIndex, number -> context(ring.uIntMinusNumber, ring.numberUnaryMinus) { if (rowIndex == columnIndex) left - number else -number } }
    }
    override val uIntTimesNumber: Times<UInt, MDList2<Number>, MDList2<Number>> = Times { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        right.map { ring.uIntTimesNumber { left * it } }
    }
    // endregion
    
    // region Long-Matrix operations
    override val longPlusNumber: Plus<Long, MDList2<Number>, MDList2<Number>> = Plus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        right.mapIndexed { rowIndex, columnIndex, number -> ring.longPlusNumber { if (rowIndex == columnIndex) left + number else number } }
    }
    override val longMinusNumber: Minus<Long, MDList2<Number>, MDList2<Number>> = Minus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        KoneContext.unwrap(ring)
        right.mapIndexed { rowIndex, columnIndex, number -> if (rowIndex == columnIndex) left - number else -number }
    }
    override val longTimesNumber: Times<Long, MDList2<Number>, MDList2<Number>> = Times { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        KoneContext.unwrap(ring)
        right.map { left * it }
    }
    // endregion
    
    // region ULong-Matrix operations
    override val uLongPlusNumber: Plus<ULong, MDList2<Number>, MDList2<Number>> = Plus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        right.mapIndexed { rowIndex, columnIndex, number -> ring.uLongPlusNumber { if (rowIndex == columnIndex) left + number else number } }
    }
    override val uLongMinusNumber: Minus<ULong, MDList2<Number>, MDList2<Number>> = Minus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        right.mapIndexed { rowIndex, columnIndex, number -> context(ring.uLongMinusNumber, ring.numberUnaryMinus) { if (rowIndex == columnIndex) left - number else -number } }
    }
    override val uLongTimesNumber: Times<ULong, MDList2<Number>, MDList2<Number>> = Times { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        right.map { ring.uLongTimesNumber { left * it } }
    }
    // endregion
    
    // region Number-Matrix operations
    override val numberPlusVector: Plus<Number, MDList2<Number>, MDList2<Number>> = Plus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        right.mapIndexed { rowIndex, columnIndex, number -> ring.numberPlusNumber { if (rowIndex == columnIndex) left + number else number } }
    }
    override val numberMinusVector: Minus<Number, MDList2<Number>, MDList2<Number>> = Minus { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        KoneContext.unwrap(ring)
        right.mapIndexed { rowIndex, columnIndex, number -> if (rowIndex == columnIndex) left - number else -number }
    }
    override val numberTimesVector: Times<Number, MDList2<Number>, MDList2<Number>> = Times { left, right ->
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        KoneContext.unwrap(ring)
        right.map { left * it }
    }
    // endregion
    
    // region Matrix-Matrix operations
    override val numberUnaryMinus: UnaryMinus<MDList2<Number>, MDList2<Number>> = UnaryMinus {
        require(it.rowNumber == dimension && it.columnNumber == dimension) { TODO() }
        it.map { ring.numberUnaryMinus { -it } }
    }
    override val numberPlusNumber: Plus<MDList2<Number>, MDList2<Number>, MDList2<Number>> = Plus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        MDList2.generate(dimension, dimension) { row, column -> ring.numberPlusNumber { left[row, column] + right[row, column] } }
    }
    override val numberMinusNumber: Minus<MDList2<Number>, MDList2<Number>, MDList2<Number>> = Minus { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        MDList2.generate(dimension, dimension) { row, column -> ring.numberMinusNumber { left[row, column] - right[row, column] } }
    }
    override val numberTimesNumber: Times<MDList2<Number>, MDList2<Number>, MDList2<Number>> = Times { left, right ->
        require(left.rowNumber == dimension && left.columnNumber == dimension) { TODO() }
        require(right.rowNumber == dimension && right.columnNumber == dimension) { TODO() }
        MDList2.generate(dimension, dimension) { row, column ->
            context(ring, ring.numberTimesNumber) { (0u ..< dimension).toKoneList().sumOf { left[row, it] * right[it, column] } }
        }
    }
    // endregion
}

public fun <Number> Algebra.Companion.mdList2(ring: CommutativeRing<Number>, dimension: UInt): Algebra<Number, MDList2<Number>> =
    MDList2Algebra(ring = ring, dimension = dimension)