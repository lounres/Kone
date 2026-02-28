package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.HessenbergDecomposition
import dev.lounres.kone.algebraic.algorithms.HessenbergDecompositionComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


private class HessenbergDecompositionComputerViaProperties<Number, Matrix : MDList2<Number>>(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    private val fallbackHessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>> {
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
    override fun MatrixWithProperties<Number, Matrix>.hessenbergDecomposition(): HessenbergDecomposition<Number, MatrixWithProperties<Number, Matrix>> =
        properties.getOrElse(HessenbergDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType)) {
            with(fallbackHessenbergDecompositionComputer) {
                this@hessenbergDecomposition.hessenbergDecomposition()
            }
        }
}

public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackHessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
): HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>> = HessenbergDecompositionComputerViaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackHessenbergDecompositionComputer = fallbackHessenbergDecompositionComputer,
)

public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: HessenbergDecompositionComputer.Companion.() -> HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
): HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>> = HessenbergDecompositionComputerViaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackHessenbergDecompositionComputer = block(),
)

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackHessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    HessenbergDecompositionComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackHessenbergDecompositionComputer = fallbackHessenbergDecompositionComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: HessenbergDecompositionComputer.Companion.() -> HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    HessenbergDecompositionComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackHessenbergDecompositionComputer = block(),
        )
    }
}