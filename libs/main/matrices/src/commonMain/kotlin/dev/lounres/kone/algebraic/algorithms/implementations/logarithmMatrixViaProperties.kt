package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.LogarithmMatrixComputer
import dev.lounres.kone.algebraic.algorithms.LogarithmMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class LogarithmMatrixComputerViaProperties<Number, Matrix : MDList2<Number>>(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    private val fallbackLogarithmMatrixComputer: LogarithmMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : LogarithmMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> {
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
    override fun MatrixWithProperties<Number, Matrix>.logarithm(): MatrixWithProperties<Number, Matrix>? =
        properties.getOrElse(LogarithmMatrixKey<Number, MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType)) {
            with(fallbackLogarithmMatrixComputer) {
                this@logarithm.logarithm()
            }
        }
}

public fun <Number, Matrix : MDList2<Number>> LogarithmMatrixComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackLogarithmMatrixComputer: LogarithmMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
): LogarithmMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = LogarithmMatrixComputerViaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackLogarithmMatrixComputer = fallbackLogarithmMatrixComputer,
)

public fun <Number, Matrix : MDList2<Number>> LogarithmMatrixComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: LogarithmMatrixComputer.Companion.() -> LogarithmMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
): LogarithmMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackLogarithmMatrixComputer = block(),
)

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> LogarithmMatrixComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackLogarithmMatrixComputer: LogarithmMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    LogarithmMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackLogarithmMatrixComputer = fallbackLogarithmMatrixComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> LogarithmMatrixComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: LogarithmMatrixComputer.Companion.() -> LogarithmMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    LogarithmMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackLogarithmMatrixComputer = block(),
        )
    }
}