/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.operations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.algebraic.zero
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.collections.utils.foldIndexed
import dev.lounres.kone.collections.utils.hasDuplicates
import dev.lounres.kone.combinatorics.enumerative.permutations
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.defaultHashing
import dev.lounres.kone.relations.defaultOrder
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


public interface MinorComputer<Number, in Content2: MDList2<Number>> : KoneContext {
    public fun Matrix<Number, Content2>.minor(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number
    public fun Matrix<Number, Content2>.firstMinor(rowIndex: UInt, columnIndex: UInt): Number {
        require(this.rowNumber == this.columnNumber) { TODO("Error message is not specified") }
        
        return minor(
            KoneUIntArray(this.rowNumber - 1u) { if (it < rowIndex) it else it + 1u },
            KoneUIntArray(this.columnNumber - 1u) { if (it < columnIndex) it else it + 1u },
        )
    }
    public fun Matrix<Number, Content2>.cofactor(rowIndex: UInt, columnIndex: UInt): Number
    
    public companion object;
    
    public class Key<Number, Content2: MDList2<Number>>(
        elementType: SuppliedType,
        content2Type: SuppliedType,
    ) : RegistryKey<MinorComputer<Number, Content2>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.operations.MinorComputer",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    ),
                    SuppliedProjection.Regular(
                        variance = IN,
                        type = content2Type
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(minorComputer: MinorComputer<Number, Content1>)
public fun <Number, Content1: MDList2<Number>> Matrix<Number, Content1>.minor(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number =
    with(minorComputer) { this@minor.minor(rowIndices, columnIndices) }

context(minorComputer: MinorComputer<Number, Content1>)
public fun <Number, Content1: MDList2<Number>> Matrix<Number, Content1>.firstMinor(rowIndex: UInt, columnIndex: UInt): Number =
    with(minorComputer) { this@firstMinor.firstMinor(rowIndex, columnIndex) }

context(minorComputer: MinorComputer<Number, Content1>)
public fun <Number, Content1: MDList2<Number>> Matrix<Number, Content1>.cofactor(rowIndex: UInt, columnIndex: UInt): Number =
    with(minorComputer) { this@cofactor.cofactor(rowIndex, columnIndex) }

private class MinorViaLeibnizFormulaComputer<Number>(
    val ring: Ring<Number>,
) : MinorComputer<Number, MDList2<Number>> {
    override fun Matrix<Number, MDList2<Number>>.minor(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number = ring {
        require(rowIndices.size == columnIndices.size) { TODO("Error message is not specified") }
        val minorSize = rowIndices.size
        if (rowIndices.hasDuplicates() || columnIndices.hasDuplicates()) return zero
        
        (0u ..< minorSize).toKoneList().permutations().fold(zero) { result, permutation ->
            val permutationIsEven = permutation.isEvenPermutation()
            
            result + permutation.foldIndexed(one) { row, product, column -> product * this[rowIndices[row], columnIndices[column]] }.let { if (permutationIsEven) it else -it }
        }
    }
    override fun Matrix<Number, MDList2<Number>>.cofactor(rowIndex: UInt, columnIndex: UInt): Number = ring {
        firstMinor(rowIndex, columnIndex).let { if ((rowIndex + columnIndex) % 2u == 0u) it else -it }
    }
}

public fun <Number, Content2: MDList2<Number>> MinorComputer.Companion.viaLeibnizFormula(
    ring: Ring<Number>,
): MinorComputer<Number, Content2> = MinorViaLeibnizFormulaComputer(ring)

public fun <Number, Content2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setMinorViaLeibnizFormulaComputer(
    numberType: SuppliedType,
    content2Type: SuppliedType,
) {
    this[MinorComputer.Key<Number, Content2>(numberType, content2Type)] =
        MinorComputer.viaLeibnizFormula(this[Ring.Key<Number>(numberType)])
}

private class MinorViaGaussianEliminationComputer<Number>(
    val field: Field<Number>,
) : MinorComputer<Number, MDList2<Number>> {
    override fun Matrix<Number, MDList2<Number>>.minor(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number = field {
        require(rowIndices.size == columnIndices.size) { TODO("Error message is not specified") }
        val minorSize = rowIndices.size
        if (rowIndices.hasDuplicates(elementEquality = defaultEquality(), elementHashing = defaultHashing(), elementOrder = defaultOrder())) return zero
        if (columnIndices.hasDuplicates(elementEquality = defaultEquality(), elementHashing = defaultHashing(), elementOrder = defaultOrder())) return zero
        
        val matrix = ArrayMDList2(minorSize, minorSize) { rowIndex, columnIndex -> this.coefficients[rowIndices[rowIndex], columnIndices[columnIndex]] }
        
        var applyMinus = false
        var fromRow = 0u
        while (fromRow < matrix.rowNumber) {
            var coolRow = fromRow
            while (coolRow < matrix.rowNumber) {
                if (matrix[coolRow, fromRow].isNotZero()) break
                coolRow++
            }
            if (coolRow == matrix.rowNumber) {
                return zero
            }
            
            if (coolRow != fromRow) {
                applyMinus = !applyMinus
                for (column in fromRow ..< matrix.columnNumber) {
                    matrix[coolRow, column] = matrix[fromRow, column].also { matrix[fromRow, column] = matrix[coolRow, column] }
                }
            }
            
            val coolCoef = matrix[fromRow, fromRow]
            
            for (row in fromRow + 1u ..< matrix.rowNumber) {
                val rowCoef = matrix[row, fromRow]
                for (column in fromRow + 1u ..< matrix.columnNumber)
                    matrix[row, column] = matrix[row, column] - matrix[fromRow, column] / coolCoef * rowCoef
            }
            
            fromRow++
        }
        
        (0u ..< matrix.rowNumber).fold(if (applyMinus) -one else one) { acc, index -> acc * matrix[index, index] }
    }
    override fun Matrix<Number, MDList2<Number>>.cofactor(rowIndex: UInt, columnIndex: UInt): Number = field {
        firstMinor(rowIndex, columnIndex).let { if ((rowIndex + columnIndex) % 2u == 0u) it else -it }
    }
}

public fun <Number, Content2: MDList2<Number>> MinorComputer.Companion.minorViaGaussianEliminationComputer(
    field: Field<Number>,
): MinorComputer<Number, Content2> = MinorViaGaussianEliminationComputer(field)

public fun <Number, Content2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setMinorViaGaussianEliminationComputer(
    numberType: SuppliedType,
    content2Type: SuppliedType,
) {
    this[MinorComputer.Key<Number, Content2>(numberType, content2Type)] =
        MinorComputer.minorViaGaussianEliminationComputer(this[Field.Key<Number>(numberType)])
}