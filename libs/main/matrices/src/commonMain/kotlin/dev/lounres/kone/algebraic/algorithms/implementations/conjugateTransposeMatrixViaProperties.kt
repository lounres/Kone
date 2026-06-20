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
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


@Suppliable
private class ConjugateTransposeMatrixComputerViaProperties<@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>>(
    private val fallbackConjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
) : ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>> {
    override fun MatrixWithProperties<ComplexNumber<Number>, Matrix>.conjugateTranspose(): MatrixWithProperties<ComplexNumber<Number>, Matrix> =
        properties.getOrElse(ConjugateTransposeMatrixKey<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>()) {
            with(fallbackConjugateTransposeMatrixComputer) { this@conjugateTranspose.conjugateTranspose() }
        }
}

@Suppliable
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.viaProperties(
    fallbackConjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
): ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>> = ConjugateTransposeMatrixComputerViaProperties(
    fallbackConjugateTransposeMatrixComputer = fallbackConjugateTransposeMatrixComputer,
)

@Suppliable
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.viaProperties(
    block: ConjugateTransposeMatrixComputer.Companion.() -> ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
): ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>> = viaProperties(
    fallbackConjugateTransposeMatrixComputer = block()
)

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.setViaProperties(
    fallbackConjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
) {
    ConjugateTransposeMatrixComputer.Key<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackConjugateTransposeMatrixComputer = fallbackConjugateTransposeMatrixComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> ConjugateTransposeMatrixComputer.Companion.setViaProperties(
    block: ConjugateTransposeMatrixComputer.Companion.() -> ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
) {
    ConjugateTransposeMatrixComputer.Key<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackConjugateTransposeMatrixComputer = block(),
        )
    }
}