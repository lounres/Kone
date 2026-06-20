/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.algorithms.MatrixProductComputer
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
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


private class MatrixProductComputerViaDefault<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val ring: CommutativeRing<Number>,
) : MatrixProductComputer<Number, Matrix> {
    override fun Matrix.times(other: Matrix): Matrix {
        require(this.columnNumber == other.rowNumber) { "Cannot multiply two matrices with incompatible sizes: ${this.rowNumber}✖${this.columnNumber} and ${other.rowNumber}✖${other.columnNumber}" }
        
        return matrixFactory.generateMatrix(rowNumber = this.rowNumber, columnNumber = other.columnNumber) { row, column ->
            ring {
                var sum = ring.zero
                
                for (i in 0u ..< this.columnNumber) sum += this[row, i] * other[i, column]
                
                sum
            }
        }
    }
}

public fun <Number, Matrix : MDList2<Number>> MatrixProductComputer.Companion.viaDefault(
    matrixFactory: MatrixFactory<Number, Matrix>,
    ring: CommutativeRing<Number>,
): MatrixProductComputer<Number, Matrix> = MatrixProductComputerViaDefault(
    matrixFactory = matrixFactory,
    ring = ring,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> MatrixProductComputer.Companion.viaDefault(): MatrixProductComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
            "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        ring = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
            "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> MatrixProductComputer.Companion.setViaDefault(
    matrixFactory: MatrixFactory<Number, Matrix>,
    ring: CommutativeRing<Number>,
) {
    MatrixProductComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault(
            matrixFactory = matrixFactory,
            ring = ring,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> MatrixProductComputer.Companion.setViaDefault() {
    MatrixProductComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault()
    }
}