/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.QRDecomposition
import dev.lounres.kone.algebraic.algorithms.QRDecompositionComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


@Suppliable
private class QRDecompositionComputerViaProperties<@Supply Number, @Supply Matrix : MDList2<Number>>(
    private val fallbackQRDecompositionComputer: QRDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : QRDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.qrDecomposition(): QRDecomposition<Number, MatrixWithProperties<Number, Matrix>> =
        properties.getOrElse(QRDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>()) {
            with(fallbackQRDecompositionComputer) {
                this@qrDecomposition.qrDecomposition()
            }
        }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object QRDecompositionComputerPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.viaProperties(
        fallbackQRDecompositionComputer: QRDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ): QRDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>> = QRDecompositionComputerViaProperties(
        fallbackQRDecompositionComputer = fallbackQRDecompositionComputer,
    )
    
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.viaProperties(
        block: QRDecompositionComputer.Companion.() -> QRDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ): QRDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>> = QRDecompositionComputerViaProperties(
        fallbackQRDecompositionComputer = block(),
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.setViaProperties(
        fallbackQRDecompositionComputer: QRDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        QRDecompositionComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackQRDecompositionComputer = fallbackQRDecompositionComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.setViaProperties(
        block: QRDecompositionComputer.Companion.() -> QRDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        QRDecompositionComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackQRDecompositionComputer = block(),
            )
        }
    }
}