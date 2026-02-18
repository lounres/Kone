package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


private class MDList2MatrixCategoryOverRing<Number>(
    private val matrixFactory: MatrixFactory<Number, MDList2<Number>>,
    private val ring: CommutativeRing<Number>,
) : MatrixCategoryOverRing<Number, MDList2<Number>> {
    // region MDList2-Int operations
    override fun MDList2<Number>.times(other: Int): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region MDList2-UInt operations
    override fun MDList2<Number>.times(other: UInt): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region MDList2-Long operations
    override fun MDList2<Number>.times(other: Long): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region MDList2-ULong operations
    override fun MDList2<Number>.times(other: ULong): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region Int-MDList2 operations
    override fun Int.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region UInt-MDList2 operations
    override fun UInt.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region Long-MDList2 operations
    override fun Long.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region ULong-MDList2 operations
    override fun ULong.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region MDList2-Number operations
    override fun MDList2<Number>.times(other: Number): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] * other } }
    // endregion
    
    // region Number-MDList2 operations
    override fun Number.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> ring { this * other[row, column] } }
    // endregion
    
    // region MDList2-MDList2 operations
    override fun MDList2<Number>.unaryMinus(): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { -this[row, column] } }
    override fun MDList2<Number>.plus(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] + other[row, column] } }
    override fun MDList2<Number>.minus(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> ring { this[row, column] - other[row, column] } }
    // endregion
}

public fun <Number> MatrixCategoryOverRing.Companion.mdList2(
    matrixFactory: MatrixFactory<Number, MDList2<Number>>,
    ring: CommutativeRing<Number>,
): MatrixCategoryOverRing<Number, MDList2<Number>> = MDList2MatrixCategoryOverRing(
    matrixFactory = matrixFactory,
    ring = ring,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> MatrixCategoryOverRing.Companion.mdList2(
    numberType: SuppliedType,
): MatrixCategoryOverRing<Number, MDList2<Number>> {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList2",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            )
        ),
        isNullable = false,
    )
    val koneContextRegistry = koneContextRegistry.get()
    return mdList2(
        matrixFactory = koneContextRegistry[MatrixFactory.Key<Number, MDList2<Number>>(matrixType = matrixType)],
        ring = koneContextRegistry[CommutativeRing.Key<Number>(numberType)],
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> MatrixCategoryOverRing.Companion.setMDList2(
    numberType: SuppliedType,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList2",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            )
        ),
        isNullable = false,
    )
    MatrixCategoryOverRing.Key<Number, MDList2<Number>>(matrixType) correspondsTo RegisteredValueProvider.cached {
        mdList2<Number>(
            numberType = numberType,
        )
    }
}

private class MDList2MatrixCategoryOverField<Number>(
    private val matrixFactory: MatrixFactory<Number, MDList2<Number>>,
    private val field: Field<Number>,
) : MatrixCategoryOverField<Number, MDList2<Number>> {
    // region MDList2-Int operations
    override fun MDList2<Number>.times(other: Int): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun MDList2<Number>.div(other: Int): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region MDList2-UInt operations
    override fun MDList2<Number>.times(other: UInt): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun MDList2<Number>.div(other: UInt): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region MDList2-Long operations
    override fun MDList2<Number>.times(other: Long): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun MDList2<Number>.div(other: Long): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region MDList2-ULong operations
    override fun MDList2<Number>.times(other: ULong): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun MDList2<Number>.div(other: ULong): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region Int-MDList2 operations
    override fun Int.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region UInt-MDList2 operations
    override fun UInt.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region Long-MDList2 operations
    override fun Long.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region ULong-MDList2 operations
    override fun ULong.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region MDList2-Number operations
    override fun MDList2<Number>.times(other: Number): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] * other } }
    override fun MDList2<Number>.div(other: Number): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] / other } }
    // endregion
    
    // region Number-MDList2 operations
    override fun Number.times(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = other.rowNumber, columnNumber = other.columnNumber) { row, column -> field { this * other[row, column] } }
    // endregion
    
    // region MDList2-MDList2 operations
    override fun MDList2<Number>.unaryMinus(): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { -this[row, column] } }
    override fun MDList2<Number>.plus(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] + other[row, column] } }
    override fun MDList2<Number>.minus(other: MDList2<Number>): MDList2<Number> =
        matrixFactory.generateMatrix(rowNumber = rowNumber, columnNumber = columnNumber) { row, column -> field { this[row, column] - other[row, column] } }
    // endregion
}

public fun <Number> MatrixCategoryOverField.Companion.mdList2(
    matrixFactory: MatrixFactory<Number, MDList2<Number>>,
    field: Field<Number>,
): MatrixCategoryOverField<Number, MDList2<Number>> = MDList2MatrixCategoryOverField(
    matrixFactory = matrixFactory,
    field = field,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> MatrixCategoryOverField.Companion.mdList2(
    numberType: SuppliedType,
): MatrixCategoryOverField<Number, MDList2<Number>> {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList2",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            )
        ),
        isNullable = false,
    )
    val koneContextRegistry = koneContextRegistry.get()
    return mdList2(
        matrixFactory = koneContextRegistry[MatrixFactory.Key<Number, MDList2<Number>>(matrixType = matrixType)],
        field = koneContextRegistry[Field.Key<Number>(numberType)],
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> MatrixCategoryOverField.Companion.setMDList2(
    numberType: SuppliedType,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList2",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            )
        ),
        isNullable = false,
    )
    MatrixCategoryOverField.Key<Number, MDList2<Number>>(matrixType).withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        mdList2<Number>(
            numberType = numberType,
        )
    }
}