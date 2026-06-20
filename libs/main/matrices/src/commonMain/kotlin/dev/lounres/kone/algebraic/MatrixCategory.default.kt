/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class MatrixCategoryOverRingViaDefault<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val ring: CommutativeRing<Number>,
) : MatrixCategoryOverRing<Number, Matrix> {
    // region Matrix-Int operations
    override fun Matrix.times(other: Int): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region Matrix-UInt operations
    override fun Matrix.times(other: UInt): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region Matrix-Long operations
    override fun Matrix.times(other: Long): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region Matrix-ULong operations
    override fun Matrix.times(other: ULong): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region Int-Matrix operations
    override fun Int.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region UInt-Matrix operations
    override fun UInt.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region Long-Matrix operations
    override fun Long.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region ULong-Matrix operations
    override fun ULong.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region Matrix-Number operations
    override fun Matrix.times(other: Number): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region Number-Matrix operations
    override fun Number.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region Matrix-Matrix operations
    override fun Matrix.unaryMinus(): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { -this[row, column] } }
    override fun Matrix.plus(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] + other[row, column] } }
    override fun Matrix.minus(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] - other[row, column] } }
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
    override fun Matrix.times(other: Int): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun Matrix.div(other: Int): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region Matrix-UInt operations
    override fun Matrix.times(other: UInt): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun Matrix.div(other: UInt): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region Matrix-Long operations
    override fun Matrix.times(other: Long): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun Matrix.div(other: Long): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region Matrix-ULong operations
    override fun Matrix.times(other: ULong): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun Matrix.div(other: ULong): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region Int-Matrix operations
    override fun Int.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region UInt-Matrix operations
    override fun UInt.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region Long-Matrix operations
    override fun Long.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region ULong-Matrix operations
    override fun ULong.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region Matrix-Number operations
    override fun Matrix.times(other: Number): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun Matrix.div(other: Number): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region Number-Matrix operations
    override fun Number.times(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region Matrix-Matrix operations
    override fun Matrix.unaryMinus(): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { -this[row, column] } }
    override fun Matrix.plus(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] + other[row, column] } }
    override fun Matrix.minus(other: Matrix): Matrix =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] - other[row, column] } }
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