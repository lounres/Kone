/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class MatrixCategoryOverRingViaDefault<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val ring: CommutativeRing<Number>,
) : MatrixCategoryOverRing<Number, Matrix> {
    // region Matrix-Int operations
    override val matrixTimesInt: Times<Matrix, Int, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring.numberTimesInt { this[row, column] * other } }
    }
    // endregion
    
    // region Matrix-UInt operations
    override val matrixTimesUInt: Times<Matrix, UInt, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring.numberTimesUInt { this[row, column] * other } }
    }
    // endregion
    
    // region Matrix-Long operations
    override val matrixTimesLong: Times<Matrix, Long, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring.numberTimesLong { this[row, column] * other } }
    }
    // endregion
    
    // region Matrix-ULong operations
    override val matrixTimesULong: Times<Matrix, ULong, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring.numberTimesULong { this[row, column] * other } }
    }
    // endregion
    
    // region Matrix-Number operations
    override val matrixTimesNumber: Times<Matrix, Number, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring.numberTimesNumber { this[row, column] * other } }
    }
    // endregion
    
    // region Int-Matrix operations
    override val intTimesMatrix: Times<Int, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring.intTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region UInt-Matrix operations
    override val uIntTimesMatrix: Times<UInt, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring.uIntTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region Long-Matrix operations
    override val longTimesMatrix: Times<Long, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring.longTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region ULong-Matrix operations
    override val uLongTimesMatrix: Times<ULong, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring.uLongTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region Number-Matrix operations
    override val numberTimesMatrix: Times<Number, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring.numberTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region Matrix-Matrix operations
    override val matrixUnaryMinus: UnaryMinus<Matrix, Matrix> = UnaryMinus {
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring.numberUnaryMinus { -this[row, column] } }
    }
    override val matrixPlusMatrix: Plus<Matrix, Matrix, Matrix> = Plus { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring.numberPlusNumber { this[row, column] + other[row, column] } }
    }
    override val matrixMinusMatrix: Minus<Matrix, Matrix, Matrix> = Minus { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring.numberMinusNumber { this[row, column] - other[row, column] } }
    }
    // endregion
}

public fun <Number, Matrix : MDList2<Number>> MatrixCategoryOverRing.Companion.viaDefault(
    matrixFactory: MatrixFactory<Number, Matrix>,
    ring: CommutativeRing<Number>,
): MatrixCategoryOverRing<Number, Matrix> = MatrixCategoryOverRingViaDefault(
    matrixFactory = matrixFactory,
    ring = ring,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> MatrixCategoryOverRing.Companion.viaDefault(): MatrixCategoryOverRing<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
            "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        ring = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
            "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> MatrixCategoryOverRing.Companion.setViaDefault() {
    MatrixCategoryOverRing.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>()
    }
}

private class MatrixCategoryOverFieldViaDefault<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val field: Field<Number>,
) : MatrixCategoryOverField<Number, Matrix> {
    // region Matrix-Int operations
    override val matrixTimesInt: Times<Matrix, Int, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberTimesInt { this[row, column] * other } }
    }
    override val matrixDivideInt: Divide<Matrix, Int, Matrix> = Divide { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberDivideInt { this[row, column] / other } }
    }
    // endregion
    
    // region Matrix-UInt operations
    override val matrixTimesUInt: Times<Matrix, UInt, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberTimesUInt { this[row, column] * other } }
    }
    override val matrixDivideUInt: Divide<Matrix, UInt, Matrix> = Divide { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberDivideUInt { this[row, column] / other } }
    }
    // endregion
    
    // region Matrix-Long operations
    override val matrixTimesLong: Times<Matrix, Long, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberTimesLong { this[row, column] * other } }
    }
    override val matrixDivideLong: Divide<Matrix, Long, Matrix> = Divide { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberDivideLong { this[row, column] / other } }
    }
    // endregion
    
    // region Matrix-ULong operations
    override val matrixTimesULong: Times<Matrix, ULong, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberTimesULong { this[row, column] * other } }
    }
    override val matrixDivideULong: Divide<Matrix, ULong, Matrix> = Divide { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberDivideULong { this[row, column] / other } }
    }
    // endregion
    
    // region Matrix-Number operations
    override val matrixTimesNumber: Times<Matrix, Number, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberTimesNumber { this[row, column] * other } }
    }
    override val matrixDivideNumber: Divide<Matrix, Number, Matrix> = Divide { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberDivideNumber { this[row, column] / other } }
    }
    // endregion
    
    // region Int-Matrix operations
    override val intTimesMatrix: Times<Int, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field.intTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region UInt-Matrix operations
    override val uIntTimesMatrix: Times<UInt, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field.uIntTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region Long-Matrix operations
    override val longTimesMatrix: Times<Long, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field.longTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region ULong-Matrix operations
    override val uLongTimesMatrix: Times<ULong, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field.uLongTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region Number-Matrix operations
    override val numberTimesMatrix: Times<Number, Matrix, Matrix> = Times { other ->
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field.numberTimesNumber { this * other[row, column] } }
    }
    // endregion
    
    // region Matrix-Matrix operations
    override val matrixUnaryMinus: UnaryMinus<Matrix, Matrix> = UnaryMinus {
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberUnaryMinus { -this[row, column] } }
    }
    override val matrixPlusMatrix: Plus<Matrix, Matrix, Matrix> = Plus { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberPlusNumber { this[row, column] + other[row, column] } }
    }
    override val matrixMinusMatrix: Minus<Matrix, Matrix, Matrix> = Minus { other ->
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field.numberMinusNumber { this[row, column] - other[row, column] } }
    }
    // endregion
}

public fun <Number, Matrix : MDList2<Number>> MatrixCategoryOverField.Companion.viaDefault(
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
): MatrixCategoryOverField<Number, Matrix> = MatrixCategoryOverFieldViaDefault(
    matrixFactory = matrixFactory,
    field = field,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> MatrixCategoryOverField.Companion.viaDefault(): MatrixCategoryOverField<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
            "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Number>()) {
            "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> MatrixCategoryOverField.Companion.setViaDefault() {
    MatrixCategoryOverField.Key<Number, Matrix>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>()
    }
}