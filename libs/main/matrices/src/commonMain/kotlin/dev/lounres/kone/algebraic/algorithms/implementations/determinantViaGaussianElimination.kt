package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.DeterminantComputer
import dev.lounres.kone.algebraic.algorithms.DeterminantKey
import dev.lounres.kone.algebraic.algorithms.determinant
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.scope
import dev.lounres.kone.suppliedTypes.SuppliedType


private class DeterminantComputerViaGaussianElimination<Number, Matrix : MDList2<Number>>(
    private val field: Field<Number>,
) : DeterminantComputer<Number, Matrix> {
    override fun Matrix.determinant(): Number {
        require(rowNumber == columnNumber) { "Cannot compute determinant of matrix with non-equal numbers of rows and columns." }
        
        val n = rowNumber
        val source = SettableMDList2(rowNumber = n, columnNumber = n) { row, column -> this[row, column] }
        var result = field.one
        
        for (currentRow in 0u ..< n) {
            val nonZeroRow = scope {
                var nonZeroRow = currentRow
                
                while (nonZeroRow < n) {
                    if (field { source[currentRow, nonZeroRow].isNotZero() }) break
                    nonZeroRow++
                }
                
                if (nonZeroRow == n) return field.zero
                nonZeroRow
            }
            
            if (nonZeroRow != currentRow) for (column in 0u ..< n)
                source[nonZeroRow, column] = source[currentRow, column].also { source[currentRow, column] = source[nonZeroRow, column] }
            
            val nonZeroCoef = source[currentRow, currentRow]
            for (column in 0u ..< n) field {
                source[currentRow, column] /= nonZeroCoef
            }
            field { result *= nonZeroCoef }
            
            for (row in currentRow + 1u ..< n) {
                val rowCoef = source[row, currentRow]
                for (column in 0u ..< n) field {
                    source[row, column] -= source[currentRow, column] * rowCoef
                }
            }
        }
        
        return result
    }
}

public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.viaGaussianElimination(
    field: Field<Number>,
): DeterminantComputer<Number, Matrix> = DeterminantComputerViaGaussianElimination(
    field = field,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.viaGaussianElimination(
    numberType: SuppliedType,
): DeterminantComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaGaussianElimination(
        field = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "DeterminantComputer.viaGaussianElimination<$numberType, ?>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.setViaGaussianElimination(
    matrixType: SuppliedType,
    field: Field<Number>,
) {
    DeterminantComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGaussianElimination<Number, Matrix>(
            field = field,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.setViaGaussianElimination(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    DeterminantComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGaussianElimination<Number, Matrix>(
            numberType = numberType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.useViaGaussianElimination(
    numberType: SuppliedType,
    field: Field<Number>,
) {
    DeterminantKey<Number>(numberType = numberType) correspondsTo RegisteredValueProvider.cached {
        val determinantComputer = viaGaussianElimination<Number, MatrixWithProperties<Number, Matrix>>(
            field = field,
        )
        determinantComputer { matrix.get().determinant() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.useViaGaussianElimination(
    numberType: SuppliedType,
) {
    DeterminantKey<Number>(numberType = numberType) correspondsTo RegisteredValueProvider.cached {
        val determinantComputer = viaGaussianElimination<Number, MatrixWithProperties<Number, Matrix>>(
            numberType = numberType,
        )
        determinantComputer { matrix.get().determinant() }
    }
}