package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public interface MatrixFactory<Number, Matrix: MDList2<Number>> : KoneContext {
    // TODO: Think about adding:
//    public fun convertMatrix(matrix: MDList2<Number>): Matrix
    public fun generateMatrix(rowNumber: UInt, columnNumber: UInt, generator: (row: UInt, column: UInt) -> Number): Matrix
    public fun fillMatrix(rowNumber: UInt, columnNumber: UInt, number: Number): Matrix = generateMatrix(rowNumber, columnNumber) { _, _ -> number }
    public fun mapMatrix(rowNumber: UInt, columnNumber: UInt, numbers: KoneMap<MDIndex, Number>): Matrix
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<MatrixFactory<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.MatrixFactory.Key<?, $matrixType>"
    }
}