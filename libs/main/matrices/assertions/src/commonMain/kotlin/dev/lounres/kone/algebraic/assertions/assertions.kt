/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:JvmName("MatricesAlgebraicAssertionsKt")

package dev.lounres.kone.algebraic.assertions

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.assertions.AssertionScope
import dev.lounres.kone.assertions.Expect
import dev.lounres.kone.assertions.fail
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.unwrap
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.contentEquals
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.multidimensionalCollections.rowIndices
import dev.lounres.kone.multidimensionalCollections.utils.any
import dev.lounres.kone.multidimensionalCollections.utils.anyIndexed
import dev.lounres.kone.multidimensionalCollections.utils.forEachIndexed
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.lt
import dev.lounres.kone.repeat
import kotlin.jvm.JvmName


context(_: AssertionScope, ring: CommutativeRing<Number>, _: Order<Number>)
public fun <Number> Expect<MDList2<Number>>.toBeEqualToWithTolerance(other: MDList2<Number>, tolerance: Number) {
    val value = this.exposeValue()
    
    KoneContext.unwrap(ring)
    
    if (!(value.size contentEquals other.size)) {
        fail(
            message = buildString {
                appendLine("Incompatible sizes.")
                appendLine("Expected: ${other.size}")
                appendLine("Actual: ${value.size}")
            }
        )
        return
    }
    
    val isEqualWithTolerance = MDList2.generate(rowNumber = value.rowNumber, columnNumber = value.columnNumber) { row, column ->
        val expected = other[row, column]
        val actual = value[row, column]
        (actual - expected).absoluteValue() lt tolerance
    }
    
    if (isEqualWithTolerance.any { !it }) {
        fail(
            message = buildString {
                appendLine("Inequal elements in 2-dimensional lists.")
                appendLine("Inequal elements diagram (■ means inequality, □ means equality):")
                append('⎛')
                repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎞')
                for (row in isEqualWithTolerance.rowIndices) {
                    if (row != 0u) {
                        append('⎜')
                        repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                        appendLine('⎟')
                    }
                    append('⎜')
                    repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { column ->
                        append(
                            when {
                                column % 2u == 0u -> ' '
                                isEqualWithTolerance[row, column / 2u] -> '□'
                                else -> '■'
                            }
                        )
                    }
                    appendLine('⎟')
                }
                append('⎝')
                repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎠')
                appendLine("Inequal elements list:")
                isEqualWithTolerance.forEachIndexed { row, column, flag ->
                    if (!flag) {
                        appendLine(
                            """
                                [$row, $column]:
                                  expected: ${other[row, column]}
                                  actual: ${value[row, column]}
                            """.trimIndent()
                        )
                    }
                }
            }
        )
    }
}

context(_: AssertionScope, ring: CommutativeRing<Number>, _: Order<Number>)
public fun <Number> Expect<MDList2<Number>>.toBeEqualToWithLinearTolerance(other: MDList2<Number>, relativeTolerance: Number, absoluteTolerance: Number = ring.zero) {
    val value = this.exposeValue()
    
    KoneContext.unwrap(ring)
    
    if (!(value.size contentEquals other.size)) {
        fail(
            message = buildString {
                appendLine("Incompatible sizes.")
                appendLine("Expected: ${other.size}")
                appendLine("Actual: ${value.size}")
            }
        )
        return
    }
    
    val isEqualWithTolerance = MDList2.generate(rowNumber = value.rowNumber, columnNumber = value.columnNumber) { row, column ->
        val expected = other[row, column]
        val actual = value[row, column]
        (actual - expected).absoluteValue() lt relativeTolerance * expected.absoluteValue() + absoluteTolerance
    }
    
    if (isEqualWithTolerance.any { !it }) {
        fail(
            message = buildString {
                appendLine("Inequal elements in 2-dimensional lists.")
                appendLine("Inequal elements diagram (■ means inequality, □ means equality):")
                append('⎛')
                repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎞')
                for (row in isEqualWithTolerance.rowIndices) {
                    if (row != 0u) {
                        append('⎜')
                        repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                        appendLine('⎟')
                    }
                    append('⎜')
                    repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { column ->
                        append(
                            when {
                                column % 2u == 0u -> ' '
                                isEqualWithTolerance[row, column / 2u] -> '□'
                                else -> '■'
                            }
                        )
                    }
                    appendLine('⎟')
                }
                append('⎝')
                repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎠')
                appendLine("Inequal elements list:")
                isEqualWithTolerance.forEachIndexed { row, column, flag ->
                    if (!flag) {
                        appendLine(
                            """
                                [$row, $column]:
                                  expected: ${other[row, column]}
                                  actual: ${value[row, column]}
                            """.trimIndent()
                        )
                    }
                }
            }
        )
    }
}

context(_: AssertionScope, ring: CommutativeRing<Number>, _: PositiveSquareRootComputer<Number>, _: Order<Number>, complexNumbersRing: CommutativeRing<ComplexNumber<Number>>)
public fun <Number> Expect<MDList2<ComplexNumber<Number>>>.toBeEqualToWithTolerance(other: MDList2<ComplexNumber<Number>>, tolerance: Number) {
    val value = this.exposeValue()
    
    KoneContext.unwrap(ring, complexNumbersRing)
    
    if (!(value.size contentEquals other.size)) {
        fail(
            message = buildString {
                appendLine("Incompatible sizes.")
                appendLine("Expected: ${other.size}")
                appendLine("Actual: ${value.size}")
            }
        )
        return
    }
    
    val isEqualWithTolerance = MDList2.generate(rowNumber = value.rowNumber, columnNumber = value.columnNumber) { row, column ->
        val expected = other[row, column]
        val actual = value[row, column]
        (actual - expected).absoluteValue() lt tolerance
    }
    
    if (isEqualWithTolerance.any { !it }) {
        fail(
            message = buildString {
                appendLine("Inequal elements in 2-dimensional lists.")
                appendLine()
                appendLine("Inequal elements diagram (■ means inequality, □ means equality):")
                append('⎛')
                repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎞')
                for (row in isEqualWithTolerance.rowIndices) {
                    if (row != 0u) {
                        append('⎜')
                        repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                        appendLine('⎟')
                    }
                    append('⎜')
                    repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { column ->
                        append(
                            when {
                                column % 2u == 0u -> ' '
                                isEqualWithTolerance[row, column / 2u] -> '□'
                                else -> '■'
                            }
                        )
                    }
                    appendLine('⎟')
                }
                append('⎝')
                repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎠')
                appendLine()
                appendLine("Inequal elements list:")
                isEqualWithTolerance.forEachIndexed { row, column, flag ->
                    if (!flag) {
                        appendLine(
                            """
                                [$row, $column]:
                                  expected: ${other[row, column]}
                                  actual: ${value[row, column]}
                            """.trimIndent()
                        )
                    }
                }
            }
        )
    }
}

context(_: AssertionScope, ring: CommutativeRing<Number>, _: PositiveSquareRootComputer<Number>, _: Order<Number>, complexNumbersRing: CommutativeRing<ComplexNumber<Number>>)
public fun <Number> Expect<MDList2<ComplexNumber<Number>>>.toBeEqualToWithLinearTolerance(other: MDList2<ComplexNumber<Number>>, relativeTolerance: Number, absoluteTolerance: Number = ring.zero) {
    val value = this.exposeValue()
    
    KoneContext.unwrap(ring, complexNumbersRing)
    
    if (!(value.size contentEquals other.size)) {
        fail(
            message = buildString {
                appendLine("Incompatible sizes.")
                appendLine("Expected: ${other.size}")
                appendLine("Actual: ${value.size}")
            }
        )
        return
    }
    
    val isEqualWithTolerance = MDList2.generate(rowNumber = value.rowNumber, columnNumber = value.columnNumber) { row, column ->
        val expected = other[row, column]
        val actual = value[row, column]
        (actual - expected).absoluteValue() lt relativeTolerance * expected.absoluteValue() + absoluteTolerance
    }
    
    if (isEqualWithTolerance.any { !it }) {
        fail(
            message = buildString {
                appendLine("Inequal elements in 2-dimensional lists.")
                appendLine()
                appendLine("Inequal elements diagram (■ means inequality, □ means equality):")
                append('⎛')
                repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎞')
                for (row in isEqualWithTolerance.rowIndices) {
                    if (row != 0u) {
                        append('⎜')
                        repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                        appendLine('⎟')
                    }
                    append('⎜')
                    repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { column ->
                        append(
                            when {
                                column % 2u == 0u -> ' '
                                isEqualWithTolerance[row, column / 2u] -> '□'
                                else -> '■'
                            }
                        )
                    }
                    appendLine('⎟')
                }
                append('⎝')
                repeat(isEqualWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎠')
                appendLine()
                appendLine("Inequal elements list:")
                isEqualWithTolerance.forEachIndexed { row, column, flag ->
                    if (!flag) {
                        appendLine(
                            """
                                [$row, $column]:
                                  expected: ${other[row, column]}
                                  actual: ${value[row, column]}
                            """.trimIndent()
                        )
                    }
                }
            }
        )
    }
}

context(_: AssertionScope, ring: CommutativeRing<Number>, _: Order<Number>)
public fun <Number> Expect<MDList2<Number>>.toBeUnitMatrixWithTolerance(tolerance: Number) {
    val value = this.exposeValue()
    
    KoneContext.unwrap(ring)
    
    if (value.rowNumber != value.columnNumber) {
        fail(
            message = "Non-square matrix ${value.rowNumber}✖${value.columnNumber} tried to be checked on unitality"
        )
        return
    }
    
    val isCorrectWithTolerance = MDList2.generate(rowNumber = value.rowNumber, columnNumber = value.columnNumber) { row, column ->
        val expected = if (row == column) ring.one else ring.zero
        val actual = value[row, column]
        (actual - expected).absoluteValue() lt tolerance
    }
    
    if (isCorrectWithTolerance.any { !it }) {
        fail(
            message = buildString {
                appendLine("Non-unit matrix.")
                appendLine()
                appendLine("Inequal elements diagram (■ means inequality, □ means equality):")
                append('⎛')
                repeat(isCorrectWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎞')
                for (row in isCorrectWithTolerance.rowIndices) {
                    if (row != 0u) {
                        append('⎜')
                        repeat(isCorrectWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                        appendLine('⎟')
                    }
                    append('⎜')
                    repeat(isCorrectWithTolerance.columnNumber * 2u + 1u) { column ->
                        append(
                            when {
                                column % 2u == 0u -> ' '
                                isCorrectWithTolerance[row, column / 2u] -> '□'
                                else -> '■'
                            }
                        )
                    }
                    appendLine('⎟')
                }
                append('⎝')
                repeat(isCorrectWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎠')
                appendLine()
                appendLine("Inequal elements list:")
                isCorrectWithTolerance.forEachIndexed { row, column, flag ->
                    if (!flag) {
                        appendLine(
                            """
                                [$row, $column]:
                                  expected: ${if (row == column) ring.one else ring.zero}
                                  actual: ${value[row, column]}
                            """.trimIndent()
                        )
                    }
                }
            }
        )
    }
}

context(_: AssertionScope, ring: CommutativeRing<Number>, _: PositiveSquareRootComputer<Number>, _: Order<Number>, complexNumbersRing: CommutativeRing<ComplexNumber<Number>>)
public fun <Number> Expect<MDList2<ComplexNumber<Number>>>.toBeUnitMatrixWithTolerance(tolerance: Number) {
    val value = this.exposeValue()
    
    KoneContext.unwrap(ring, complexNumbersRing)
    
    if (value.rowNumber != value.columnNumber) {
        fail(
            message = "Non-square matrix ${value.rowNumber}✖${value.columnNumber} tried to be checked on unitality"
        )
        return
    }
    
    val isCorrectWithTolerance = MDList2.generate(rowNumber = value.rowNumber, columnNumber = value.columnNumber) { row, column ->
        val expected = if (row == column) complexNumbersRing.one else complexNumbersRing.zero
        val actual = value[row, column]
        (actual - expected).absoluteValue() lt tolerance
    }
    
    if (isCorrectWithTolerance.any { !it }) {
        fail(
            message = buildString {
                appendLine("Non-unit matrix.")
                appendLine()
                appendLine("Inequal elements diagram (■ means inequality, □ means equality):")
                append('⎛')
                repeat(isCorrectWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎞')
                for (row in isCorrectWithTolerance.rowIndices) {
                    if (row != 0u) {
                        append('⎜')
                        repeat(isCorrectWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                        appendLine('⎟')
                    }
                    append('⎜')
                    repeat(isCorrectWithTolerance.columnNumber * 2u + 1u) { column ->
                        append(
                            when {
                                column % 2u == 0u -> ' '
                                isCorrectWithTolerance[row, column / 2u] -> '□'
                                else -> '■'
                            }
                        )
                    }
                    appendLine('⎟')
                }
                append('⎝')
                repeat(isCorrectWithTolerance.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎠')
                appendLine()
                appendLine("Inequal elements list:")
                isCorrectWithTolerance.forEachIndexed { row, column, flag ->
                    if (!flag) {
                        appendLine(
                            """
                                [$row, $column]:
                                  expected: ${if (row == column) complexNumbersRing.one else complexNumbersRing.zero}
                                  actual: ${value[row, column]}
                            """.trimIndent()
                        )
                    }
                }
            }
        )
    }
}

context(_: AssertionScope, ring: CommutativeRing<Number>)
public fun <Number> Expect<MDList2<Number>>.toBeUpperTriangularMatrix() {
    val value = this.exposeValue()
    
    KoneContext.unwrap(ring)
    
    if (value.rowNumber != value.columnNumber) {
        fail(
            message = "Non-square matrix ${value.rowNumber}✖${value.columnNumber} tried to be checked on unitality"
        )
        return
    }
    
    val isCorrect = MDList2.generate(rowNumber = value.rowNumber, columnNumber = value.columnNumber) { row, column ->
        if (row <= column) return@generate true
        val actual = value[row, column]
        actual.isZero()
    }
    
    if (isCorrect.any { !it }) {
        fail(
            message = buildString {
                appendLine("Non-upper-triangular matrix.")
                appendLine()
                appendLine("Inequal elements diagram (■ means non-zero element under diagonal, □ means zero element under diagonal, ✖ means element on diagonal and above):")
                append('⎛')
                repeat(isCorrect.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎞')
                for (row in isCorrect.rowIndices) {
                    if (row != 0u) {
                        append('⎜')
                        repeat(isCorrect.columnNumber * 2u + 1u) { append(' ') }
                        appendLine('⎟')
                    }
                    append('⎜')
                    repeat(isCorrect.columnNumber * 2u + 1u) { column ->
                        append(
                            when {
                                column % 2u == 0u -> ' '
                                row <= column / 2u -> '✖'
                                isCorrect[row, column / 2u] -> '□'
                                else -> '■'
                            }
                        )
                    }
                    appendLine('⎟')
                }
                append('⎝')
                repeat(isCorrect.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎠')
                appendLine()
                appendLine("Inequal elements list:")
                isCorrect.forEachIndexed { row, column, flag ->
                    if (!flag) {
                        appendLine(
                            """
                                [$row, $column]:
                                  expected: ${ring.zero}
                                  actual: ${value[row, column]}
                            """.trimIndent()
                        )
                    }
                }
            }
        )
    }
}

context(_: AssertionScope, ring: CommutativeRing<Number>)
public fun <Number> Expect<MDList2<Number>>.toBeUpperHessenbergMatrix() {
    val value = this.exposeValue()
    
    KoneContext.unwrap(ring)
    
    if (value.rowNumber != value.columnNumber) {
        fail(
            message = "Non-square matrix ${value.rowNumber}✖${value.columnNumber} tried to be checked on unitality"
        )
        return
    }
    
    val isCorrect = MDList2.generate(rowNumber = value.rowNumber, columnNumber = value.columnNumber) { row, column ->
        if (row <= column + 1u) return@generate true
        val actual = value[row, column]
        actual.isZero()
    }
    
    if (isCorrect.any { !it }) {
        fail(
            message = buildString {
                appendLine("Non-upper-hessenberg matrix.")
                appendLine()
                appendLine("Inequal elements diagram (■ means non-zero element under diagonal, □ means zero element under diagonal, ✖ means element on lower subdiagonal and above):")
                append('⎛')
                repeat(isCorrect.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎞')
                for (row in isCorrect.rowIndices) {
                    if (row != 0u) {
                        append('⎜')
                        repeat(isCorrect.columnNumber * 2u + 1u) { append(' ') }
                        appendLine('⎟')
                    }
                    append('⎜')
                    repeat(isCorrect.columnNumber * 2u + 1u) { column ->
                        append(
                            when {
                                column % 2u == 0u -> ' '
                                row <= column / 2u + 1u -> '✖'
                                isCorrect[row, column / 2u] -> '□'
                                else -> '■'
                            }
                        )
                    }
                    appendLine('⎟')
                }
                append('⎝')
                repeat(isCorrect.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎠')
                appendLine()
                appendLine("Inequal elements list:")
                isCorrect.forEachIndexed { row, column, flag ->
                    if (!flag) {
                        appendLine(
                            """
                                [$row, $column]:
                                  expected: ${ring.zero}
                                  actual: ${value[row, column]}
                            """.trimIndent()
                        )
                    }
                }
            }
        )
    }
}

context(_: AssertionScope, ring: CommutativeRing<Number>)
public fun <Number> Expect<MDList2<Number>>.toBeQuasiUpperTriangularMatrix() {
    val value = this.exposeValue()
    
    KoneContext.unwrap(ring)
    
    if (value.rowNumber != value.columnNumber) {
        fail(
            message = "Non-square matrix ${value.rowNumber}✖${value.columnNumber} tried to be checked on unitality"
        )
        return
    }
    
    val isCorrect = MDList2.generate(rowNumber = value.rowNumber, columnNumber = value.columnNumber) { row, column ->
        if (row <= column + 1u) return@generate true
        val actual = value[row, column]
        actual.isZero()
    }
    
    if (
        isCorrect.anyIndexed { rowIndex, columnIndex, flag -> rowIndex >= columnIndex + 1u && !flag } ||
        (2u ..< value.rowNumber).any { !isCorrect[it, it - 1u] && !isCorrect[it - 1u, it - 2u] }
    ) {
        fail(
            message = buildString {
                appendLine("Non-quasi-upper-triangular matrix.")
                appendLine()
                appendLine("Inequal elements diagram (■ means non-zero element under diagonal, □ means zero element under diagonal, ✖ means element on diagonal and above):")
                append('⎛')
                repeat(isCorrect.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎞')
                for (row in isCorrect.rowIndices) {
                    if (row != 0u) {
                        append('⎜')
                        repeat(isCorrect.columnNumber * 2u + 1u) {
                            when {
                                row == 1u -> append(' ')
                                it % 2u == 1u -> append(' ')
                                it == 0u || it == isCorrect.columnNumber * 2u -> append(' ')
                                it / 2u + 1u != row -> append(' ')
                                !isCorrect[row - 1u, it / 2u - 1u] && !isCorrect[row, it / 2u] -> append('⤡')
                                else -> append(' ')
                            }
                        }
                        appendLine('⎟')
                    }
                    append('⎜')
                    repeat(isCorrect.columnNumber * 2u + 1u) { column ->
                        append(
                            when {
                                column % 2u == 0u -> ' '
                                row <= column / 2u -> '✖'
                                isCorrect[row, column / 2u] -> '□'
                                else -> '■'
                            }
                        )
                    }
                    appendLine('⎟')
                }
                append('⎝')
                repeat(isCorrect.columnNumber * 2u + 1u) { append(' ') }
                appendLine('⎠')
                appendLine()
                appendLine("Inequal elements list:")
                isCorrect.forEachIndexed { row, column, flag ->
                    if (!flag) {
                        appendLine(
                            """
                                [$row, $column]:
                                  expected: ${ring.zero}
                                  actual: ${value[row, column]}
                            """.trimIndent()
                        )
                    }
                }
            }
        )
    }
}