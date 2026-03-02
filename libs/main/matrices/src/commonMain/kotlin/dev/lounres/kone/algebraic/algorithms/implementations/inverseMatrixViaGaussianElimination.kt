package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.InverseMatrixComputer
import dev.lounres.kone.algebraic.algorithms.InverseMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.invert
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.registry.*
import dev.lounres.kone.scope
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class InverseMatrixComputerViaGaussianElimination<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val field: Field<Number>,
) : InverseMatrixComputer<Number, Matrix> {
    override fun Matrix.invert(): Matrix? {
        if (rowNumber != columnNumber) return null
        
        val n = rowNumber
        val source = SettableMDList2.generate(rowNumber = n, columnNumber = n) { row, column -> this[row, column] }
        val result = SettableMDList2.generate(rowNumber = n, columnNumber = n) { row, column ->
            if (row == column) field.one else field.zero
        }
        
        for (currentRow in 0u ..< n) {
            val nonZeroRow = scope {
                var nonZeroRow = currentRow
                
                while (nonZeroRow < n) {
                    if (field { source[currentRow, nonZeroRow].isNotZero() }) break
                    nonZeroRow++
                }
                
                if (nonZeroRow == n) return null
                nonZeroRow
            }
            
            if (nonZeroRow != currentRow) for (column in 0u ..< n) {
                source[nonZeroRow, column] = source[currentRow, column].also { source[currentRow, column] = source[nonZeroRow, column] }
                result[nonZeroRow, column] = result[currentRow, column].also { result[currentRow, column] = result[nonZeroRow, column] }
            }
            
            val nonZeroCoef = source[currentRow, currentRow]
            for (column in 0u ..< n) field {
                source[currentRow, column] /= nonZeroCoef
                result[currentRow, column] /= nonZeroCoef
            }
            
            for (row in currentRow + 1u ..< n) {
                val rowCoef = source[row, currentRow]
                for (column in 0u ..< n) field {
                    source[row, column] -= source[currentRow, column] * rowCoef
                    result[row, column] -= result[currentRow, column] * rowCoef
                }
            }
        }
        
        for (currentRow in 1u ..< n) {
            for (row in 0u ..< currentRow) {
                val rowCoef = source[row, currentRow]
                for (column in 0u ..< n) field {
                    source[row, column] -= source[currentRow, column] * rowCoef
                    result[row, column] -= result[currentRow, column] * rowCoef
                }
            }
        }
        
        return matrixFactory.generateMatrix(rowNumber = n, columnNumber = n) { row, column -> result[row, column] }
    }
}

public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaGaussianElimination(
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
): InverseMatrixComputer<Number, Matrix> = InverseMatrixComputerViaGaussianElimination(
    matrixFactory = matrixFactory,
    field = field,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaGaussianElimination(
    numberType: SuppliedType,
    matrixType: SuppliedType,
): InverseMatrixComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaGaussianElimination(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>(matrixType = matrixType)) {
            "InverseMatrixComputer.viaGaussianElimination<$numberType, $matrixType>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "InverseMatrixComputer.viaGaussianElimination<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaGaussianElimination(
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
) {
    InverseMatrixComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGaussianElimination<Number, Matrix>(
            matrixFactory = matrixFactory,
            field = field,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaGaussianElimination(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    InverseMatrixComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGaussianElimination<Number, Matrix>(
            numberType = numberType,
            matrixType = matrixType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.useViaGaussianElimination(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
    field: Field<Number>,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        (viaGaussianElimination(matrixFactory = matrixFactory, field = field)) {
            matrix.get().invert()
        }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.useViaGaussianElimination(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val inverseMatrixComputer = viaGaussianElimination<Number, MatrixWithProperties<Number, Matrix>>(
            numberType = numberType,
            matrixType = matrixWithPropertiesType,
        )
        inverseMatrixComputer {
            matrix.get().invert()
        }
    }
}