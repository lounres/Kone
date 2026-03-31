/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.utils

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.multidimensionalCollections.rowIndices
import dev.lounres.kone.multidimensionalCollections.utils.map
import dev.lounres.kone.repeat
import kotlin.jvm.JvmName


fun MDList2<*>.toMatrixString(): String {
    val representations = this.map { it.toString() }
    val lengths = representations.map { it.length.toUInt() }
    val maxLengths = MDList1.generate(lengths.columnNumber) { column ->
        lengths.rowIndices.maxOf { row -> lengths[row, column] }
    }
    return buildString {
        append('⎛')
        repeat(columnNumber * 2u + 1u) {
            if (it % 2u == 0u) append("   ")
            else repeat(maxLengths[it / 2u]) { append(' ') }
        }
        appendLine('⎞')
        for (row in rowIndices) {
            if (row != 0u) {
                append('⎜')
                repeat(columnNumber * 2u + 1u) {
                    if (it % 2u == 0u) append("   ")
                    else repeat(maxLengths[it / 2u]) { append(' ') }
                }
                appendLine('⎟')
            }
            append('⎜')
            repeat(columnNumber * 2u + 1u) { column ->
                append(
                    if (column % 2u == 0u) "   "
                    else representations[row, column / 2u].padStart(maxLengths[column / 2u].toInt(), ' ')
                )
            }
            appendLine('⎟')
        }
        append('⎝')
        repeat(columnNumber * 2u + 1u) {
            if (it % 2u == 0u) append("   ")
            else repeat(maxLengths[it / 2u]) { append(' ') }
        }
        appendLine('⎠')
    }
}

@JvmName("toComplexMatrixString")
fun MDList2<ComplexNumber<*>>.toMatrixString(): String {
    val representations = this.map { Pair(it.realPart.toString(), it.imaginaryPart.toString()) }
    val lengths = representations.map { Pair(it.first.length.toUInt(), it.second.length.toUInt()) }
    val maxLengths = MDList1.generate(lengths.columnNumber) { column ->
        Pair(
            lengths.rowIndices.maxOf { row -> lengths[row, column].first },
            lengths.rowIndices.maxOf { row -> lengths[row, column].second },
        )
    }
    return buildString {
        append('⎛')
        repeat(columnNumber * 2u + 1u) {
            if (it % 2u == 0u) append("   ")
            else repeat(maxLengths[it / 2u].let { it.first + it.second + 5u }) { append(' ') }
        }
        appendLine('⎞')
        for (row in rowIndices) {
            if (row != 0u) {
                append('⎜')
                repeat(columnNumber * 2u + 1u) {
                    if (it % 2u == 0u) append("   ")
                    else repeat(maxLengths[it / 2u].let { it.first + it.second + 5u }) { append(' ') }
                }
                appendLine('⎟')
            }
            append('⎜')
            repeat(columnNumber * 2u + 1u) { column ->
                append(
                    if (column % 2u == 0u) "   "
                    else buildString {
                        val representation = representations[row, column / 2u]
                        append(representation.first.padStart(maxLengths[column / 2u].first.toInt(), ' '))
                        append(" + ")
                        append(representation.second.padStart(maxLengths[column / 2u].second.toInt(), ' '))
                        append(" i")
                    }
                )
            }
            appendLine('⎟')
        }
        append('⎝')
        repeat(columnNumber * 2u + 1u) {
            if (it % 2u == 0u) append("   ")
            else repeat(maxLengths[it / 2u].let { it.first + it.second + 5u }) { append(' ') }
        }
        appendLine('⎠')
    }
}