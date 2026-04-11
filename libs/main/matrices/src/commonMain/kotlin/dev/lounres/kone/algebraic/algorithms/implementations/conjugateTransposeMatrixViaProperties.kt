/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixComputer
import dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class ConjugateTransposeMatrixComputerViaProperties<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    private val fallbackConjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
) : ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>> {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    private val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = SuppliedType.Regular(
                    fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
                    typeArguments = listOf(
                        SuppliedProjection.Regular(
                            variance = OUT,
                            type = numberType,
                        )
                    ),
                    isNullable = false,
                ),
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    override fun MatrixWithProperties<ComplexNumber<Number>, Matrix>.conjugateTranspose(): MatrixWithProperties<ComplexNumber<Number>, Matrix> =
        properties.getOrElse(ConjugateTransposeMatrixKey<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType)) {
            with(fallbackConjugateTransposeMatrixComputer) { this@conjugateTranspose.conjugateTranspose() }
        }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackConjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
): ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>> = ConjugateTransposeMatrixComputerViaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackConjugateTransposeMatrixComputer = fallbackConjugateTransposeMatrixComputer,
)

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: ConjugateTransposeMatrixComputer.Companion.() -> ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
): ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>> = viaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackConjugateTransposeMatrixComputer = block()
)

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackConjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = SuppliedType.Regular(
                    fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
                    typeArguments = listOf(
                        SuppliedProjection.Regular(
                            variance = OUT,
                            type = numberType,
                        )
                    ),
                    isNullable = false,
                ),
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    ConjugateTransposeMatrixComputer.Key<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackConjugateTransposeMatrixComputer = fallbackConjugateTransposeMatrixComputer,
        )
    }
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: ConjugateTransposeMatrixComputer.Companion.() -> ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = SuppliedType.Regular(
                    fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
                    typeArguments = listOf(
                        SuppliedProjection.Regular(
                            variance = OUT,
                            type = numberType,
                        )
                    ),
                    isNullable = false,
                ),
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    ConjugateTransposeMatrixComputer.Key<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackConjugateTransposeMatrixComputer = block(),
        )
    }
}