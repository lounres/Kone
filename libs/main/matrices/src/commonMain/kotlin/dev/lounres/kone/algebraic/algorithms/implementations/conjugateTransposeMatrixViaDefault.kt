/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixComputer
import dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixKey
import dev.lounres.kone.algebraic.algorithms.conjugateTranspose
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class ConjugateTransposeMatrixComputerViaDefault<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val numberCommutativeRing: CommutativeRing<Number>,
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
) : ConjugateTransposeMatrixComputer<Number, Matrix> {
    override fun Matrix.conjugateTranspose(): Matrix =
        matrixFactory.generateMatrix(rowNumber = columnNumber, columnNumber = rowNumber) { row, column ->
            numberCommutativeRing.numberUnaryMinus {
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

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.viaDefault(): ConjugateTransposeMatrixComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        numberCommutativeRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
            "ConjugateTransposeMatrixComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>()) {
            "ConjugateTransposeMatrixComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.setViaDefault(
    numberCommutativeRing: CommutativeRing<Number>,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
) {
    ConjugateTransposeMatrixComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberCommutativeRing = numberCommutativeRing,
            matrixFactory = matrixFactory,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.setViaDefault() {
    ConjugateTransposeMatrixComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>()
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.useViaDefault(
    numberCommutativeRing: CommutativeRing<Number>,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
) {
    ConjugateTransposeMatrixKey<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val conjugateTransposeMatrixComputer = viaDefault(
            numberCommutativeRing = numberCommutativeRing,
            matrixFactory = matrixFactory,
        )
        conjugateTransposeMatrixComputer {
            matrix.get().conjugateTranspose()
        }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.useViaDefault() {
    ConjugateTransposeMatrixKey<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val conjugateTransposeMatrixComputer = viaDefault<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>()
        conjugateTransposeMatrixComputer {
            matrix.get().conjugateTranspose()
        }
    }
}