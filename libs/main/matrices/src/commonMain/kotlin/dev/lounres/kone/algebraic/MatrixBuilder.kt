package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of


public interface MatrixBuilder<Number, Matrix: MDList2<Number>> : KoneContext {
    public fun generateMatrix(rowNumber: UInt, columnNumber: UInt, generator: (row: UInt, column: UInt) -> Number): Matrix
    public fun fillMatrix(rowNumber: UInt, columnNumber: UInt, number: Number): Matrix = generateMatrix(rowNumber, columnNumber) { _, _ -> number }
    public fun mapMatrix(rowNumber: UInt, columnNumber: UInt, numbers: KoneMap<MDIndex, Number>): Matrix {
        require(numbers.keysView.all { it.size == 2u && it[0u] < rowNumber && it[1u] < columnNumber }) { TODO() }
        return generateMatrix(rowNumber, columnNumber) { row, column -> numbers[MDIndex.of(row, column)] }
    }
}