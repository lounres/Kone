/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixComputer
import dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixKey
import dev.lounres.kone.algebraic.algorithms.conjugateTranspose
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class ConjugateTransposeMatrixComputerViaDefault<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val numberCommutativeRing: CommutativeRing<Number>,
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
) : ConjugateTransposeMatrixComputer<Number, Matrix> {
    override fun Matrix.conjugateTranspose(): Matrix =
        matrixFactory.generateMatrix(rowNumber = columnNumber, columnNumber = rowNumber) { row, column ->
            numberCommutativeRing {
                this[column, row].let { ComplexNumber(realPart = it.realPart, imaginaryPart = -it.imaginaryPart) }
            }
        }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.viaDefault(
    numberCommutativeRing: CommutativeRing<Number>,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
): ConjugateTransposeMatrixComputer<Number, Matrix> = ConjugateTransposeMatrixComputerViaDefault(
    numberCommutativeRing = numberCommutativeRing,
    matrixFactory = matrixFactory,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.viaDefault(
    numberType: SuppliedType,
    matrixType: SuppliedType,
): ConjugateTransposeMatrixComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        numberCommutativeRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>(numberType = numberType)) {
            "ConjugateTransposeMatrixComputer.viaDefault<$numberType, $matrixType>"
        },
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ConjugateTransposeMatrixComputer.viaDefault<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.setViaDefault(
    matrixType: SuppliedType,
    numberCommutativeRing: CommutativeRing<Number>,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
) {
    ConjugateTransposeMatrixComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberCommutativeRing = numberCommutativeRing,
            matrixFactory = matrixFactory,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.setViaDefault(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    ConjugateTransposeMatrixComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberType = numberType,
            matrixType = matrixType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.useViaDefault(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    numberCommutativeRing: CommutativeRing<Number>,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
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
    ConjugateTransposeMatrixKey<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val conjugateTransposeMatrixComputer = viaDefault(
            numberCommutativeRing = numberCommutativeRing,
            matrixFactory = matrixFactory,
        )
        conjugateTransposeMatrixComputer {
            matrix.get().conjugateTranspose()
        }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.useViaDefault(
    numberType: SuppliedType,
    matrixType: SuppliedType,
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
    ConjugateTransposeMatrixKey<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val conjugateTransposeMatrixComputer = viaDefault<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(
            numberType = numberType,
            matrixType = matrixWithPropertiesType,
        )
        conjugateTransposeMatrixComputer {
            matrix.get().conjugateTranspose()
        }
    }
}