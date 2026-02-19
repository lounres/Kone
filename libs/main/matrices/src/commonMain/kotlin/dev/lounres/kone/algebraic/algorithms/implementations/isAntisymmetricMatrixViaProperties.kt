package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixKey
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


private class IsAntisymmetricMatrixCheckerViaProperties<Number, Matrix : MDList2<Number>>(
    private val fallbackIsAntisymmetricMatrixChecker: IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) : IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.isAntisymmetric(): Boolean =
        properties.getOrElse(IsAntisymmetricMatrixKey) {
            with(fallbackIsAntisymmetricMatrixChecker) { this@isAntisymmetric.isAntisymmetric() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.viaProperties(
    fallbackIsAntisymmetricMatrixChecker: IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsAntisymmetricMatrixCheckerViaProperties(
    fallbackIsAntisymmetricMatrixChecker = fallbackIsAntisymmetricMatrixChecker,
)

public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.viaProperties(
    block: IsAntisymmetricMatrixChecker.Companion.() -> IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackIsAntisymmetricMatrixChecker = block(),
)

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackIsAntisymmetricMatrixChecker: IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
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
    IsAntisymmetricMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsAntisymmetricMatrixChecker = fallbackIsAntisymmetricMatrixChecker,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: IsAntisymmetricMatrixChecker.Companion.() -> IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
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
    IsAntisymmetricMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsAntisymmetricMatrixChecker = block(),
        )
    }
}