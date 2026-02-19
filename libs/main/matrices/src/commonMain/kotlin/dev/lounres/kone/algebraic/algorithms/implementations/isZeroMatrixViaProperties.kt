package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsZeroMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsZeroMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class IsZeroMatrixCheckerViaProperties<Number, Matrix : MDList2<Number>>(
    private val fallbackIsZeroMatrixChecker: IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) : IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.isZero(): Boolean =
        properties.getOrElse(IsZeroMatrixKey) {
            with(fallbackIsZeroMatrixChecker) { this@isZero.isZero() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.viaProperties(
    fallbackIsZeroMatrixChecker: IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsZeroMatrixCheckerViaProperties(
    fallbackIsZeroMatrixChecker = fallbackIsZeroMatrixChecker,
)

public fun <Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.viaProperties(
    block: IsZeroMatrixChecker.Companion.() -> IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackIsZeroMatrixChecker = block(),
)

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackIsZeroMatrixChecker: IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
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
    IsZeroMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsZeroMatrixChecker = fallbackIsZeroMatrixChecker,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: IsZeroMatrixChecker.Companion.() -> IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
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
    IsZeroMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsZeroMatrixChecker = block(),
        )
    }
}