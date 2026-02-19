package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixKey
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


private class IsDiagonalMatrixCheckerViaProperties<Number, Matrix : MDList2<Number>>(
    private val fallbackIsDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) : IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.isDiagonal(): Boolean =
        properties.getOrElse(IsDiagonalMatrixKey) {
            with(fallbackIsDiagonalMatrixChecker) { this@isDiagonal.isDiagonal() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.viaProperties(
    fallbackIsDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsDiagonalMatrixCheckerViaProperties(
    fallbackIsDiagonalMatrixChecker = fallbackIsDiagonalMatrixChecker,
)

public fun <Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.viaProperties(
    block: IsDiagonalMatrixChecker.Companion.() -> IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackIsDiagonalMatrixChecker = block(),
)

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackIsDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
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
    IsDiagonalMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsDiagonalMatrixChecker = fallbackIsDiagonalMatrixChecker,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: IsDiagonalMatrixChecker.Companion.() -> IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
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
    IsDiagonalMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsDiagonalMatrixChecker = block(),
        )
    }
}