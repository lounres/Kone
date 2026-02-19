package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class TransposeMatrixComputerViaProperties<Number, Matrix : MDList2<Number>>(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    private val fallbackTransposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    private val matrixWithPropertiesType = SuppliedType.Regular(
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
    override fun MatrixWithProperties<Number, Matrix>.transpose(): MatrixWithProperties<Number, Matrix> =
        properties.getOrElse(TransposeMatrixKey<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType)) {
            with(fallbackTransposeMatrixComputer) { this@transpose.transpose() }
        }
}

public fun <Number, Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackTransposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
): TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = TransposeMatrixComputerViaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackTransposeMatrixComputer = fallbackTransposeMatrixComputer,
)

public fun <Number, Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: TransposeMatrixComputer.Companion.() -> TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
): TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackTransposeMatrixComputer = block()
)

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackTransposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    TransposeMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackTransposeMatrixComputer = fallbackTransposeMatrixComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: TransposeMatrixComputer.Companion.() -> TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    TransposeMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackTransposeMatrixComputer = block(),
        )
    }
}