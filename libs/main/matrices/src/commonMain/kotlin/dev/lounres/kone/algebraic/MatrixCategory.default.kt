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
    override val matrixTimesInt: Times<Matrix, Int, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> ring.numberTimesInt { left[row, column] * right } }
    }
    // endregion
    
    // region Matrix-UInt operations
    override val matrixTimesUInt: Times<Matrix, UInt, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> ring.numberTimesUInt { left[row, column] * right } }
    }
    // endregion
    
    // region Matrix-Long operations
    override val matrixTimesLong: Times<Matrix, Long, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> ring.numberTimesLong { left[row, column] * right } }
    }
    // endregion
    
    // region Matrix-ULong operations
    override val matrixTimesULong: Times<Matrix, ULong, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> ring.numberTimesULong { left[row, column] * right } }
    }
    // endregion
    
    // region Matrix-Number operations
    override val matrixTimesNumber: Times<Matrix, Number, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> ring.numberTimesNumber { left[row, column] * right } }
    }
    // endregion
    
    // region Int-Matrix operations
    override val intTimesMatrix: Times<Int, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> ring.intTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region UInt-Matrix operations
    override val uIntTimesMatrix: Times<UInt, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> ring.uIntTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region Long-Matrix operations
    override val longTimesMatrix: Times<Long, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> ring.longTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region ULong-Matrix operations
    override val uLongTimesMatrix: Times<ULong, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> ring.uLongTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region Number-Matrix operations
    override val numberTimesMatrix: Times<Number, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> ring.numberTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region Matrix-Matrix operations
    override val matrixUnaryMinus: UnaryMinus<Matrix, Matrix> = UnaryMinus {
        matrixFactory.generateMatrix(rowNumber = it.rowNumber, columnNumber = it.columnNumber) { row, column -> ring.numberUnaryMinus { -it[row, column] } }
    }
    override val matrixPlusMatrix: Plus<Matrix, Matrix, Matrix> = Plus { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> ring.numberPlusNumber { left[row, column] + right[row, column] } }
    }
    override val matrixMinusMatrix: Minus<Matrix, Matrix, Matrix> = Minus { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> ring.numberMinusNumber { left[row, column] - right[row, column] } }
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
    override val matrixTimesInt: Times<Matrix, Int, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberTimesInt { left[row, column] * right } }
    }
    override val matrixDivideInt: Divide<Matrix, Int, Matrix> = Divide { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberDivideInt { left[row, column] / right } }
    }
    // endregion
    
    // region Matrix-UInt operations
    override val matrixTimesUInt: Times<Matrix, UInt, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberTimesUInt { left[row, column] * right } }
    }
    override val matrixDivideUInt: Divide<Matrix, UInt, Matrix> = Divide { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberDivideUInt { left[row, column] / right } }
    }
    // endregion
    
    // region Matrix-Long operations
    override val matrixTimesLong: Times<Matrix, Long, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberTimesLong { left[row, column] * right } }
    }
    override val matrixDivideLong: Divide<Matrix, Long, Matrix> = Divide { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberDivideLong { left[row, column] / right } }
    }
    // endregion
    
    // region Matrix-ULong operations
    override val matrixTimesULong: Times<Matrix, ULong, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberTimesULong { left[row, column] * right } }
    }
    override val matrixDivideULong: Divide<Matrix, ULong, Matrix> = Divide { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberDivideULong { left[row, column] / right } }
    }
    // endregion
    
    // region Matrix-Number operations
    override val matrixTimesNumber: Times<Matrix, Number, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberTimesNumber { left[row, column] * right } }
    }
    override val matrixDivideNumber: Divide<Matrix, Number, Matrix> = Divide { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberDivideNumber { left[row, column] / right } }
    }
    // endregion
    
    // region Int-Matrix operations
    override val intTimesMatrix: Times<Int, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> field.intTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region UInt-Matrix operations
    override val uIntTimesMatrix: Times<UInt, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> field.uIntTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region Long-Matrix operations
    override val longTimesMatrix: Times<Long, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> field.longTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region ULong-Matrix operations
    override val uLongTimesMatrix: Times<ULong, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> field.uLongTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region Number-Matrix operations
    override val numberTimesMatrix: Times<Number, Matrix, Matrix> = Times { left, right ->
        matrixFactory.generateMatrix(rowNumber = right.rowNumber, columnNumber = right.columnNumber) { row, column -> field.numberTimesNumber { left * right[row, column] } }
    }
    // endregion
    
    // region Matrix-Matrix operations
    override val matrixUnaryMinus: UnaryMinus<Matrix, Matrix> = UnaryMinus {
        matrixFactory.generateMatrix(rowNumber = it.rowNumber, columnNumber = it.columnNumber) { row, column -> field.numberUnaryMinus { -it[row, column] } }
    }
    override val matrixPlusMatrix: Plus<Matrix, Matrix, Matrix> = Plus { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberPlusNumber { left[row, column] + right[row, column] } }
    }
    override val matrixMinusMatrix: Minus<Matrix, Matrix, Matrix> = Minus { left, right ->
        matrixFactory.generateMatrix(rowNumber = left.rowNumber, columnNumber = left.columnNumber) { row, column -> field.numberMinusNumber { left[row, column] - right[row, column] } }
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