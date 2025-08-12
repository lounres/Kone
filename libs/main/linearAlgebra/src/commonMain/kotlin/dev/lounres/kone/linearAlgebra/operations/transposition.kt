/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.operations

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


public interface TranspositionComputer<Number, in InputContent2: MDList2<Number>, out OutputContent2: MDList2<Number>> : KoneContext {
    public fun Matrix<Number, InputContent2>.transpose(): Matrix<Number, OutputContent2>
    
    public companion object;
    
    public class Key<Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>>(
        elementType: SuppliedType,
        inputContent2Type: SuppliedType,
        outputContent2Type: SuppliedType,
    ) : RegistryKey<TranspositionComputer<Number, InputContent2, OutputContent2>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.operations.TranspositionComputer",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    ),
                    SuppliedProjection.Regular(
                        variance = IN,
                        type = inputContent2Type
                    ),
                    SuppliedProjection.Regular(
                        variance = OUT,
                        type = outputContent2Type
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(transpositionComputer: TranspositionComputer<Number, InputContent2, OutputContent2>)
public fun <Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>> Matrix<Number, InputContent2>.transpose(): Matrix<Number, OutputContent2> =
    with(transpositionComputer) { this@transpose.transpose() }

private class DefaultTranspositionComputer<Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>>(
    private val content2Producer: (rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> Number) -> OutputContent2,
) : TranspositionComputer<Number, InputContent2, OutputContent2> {
    override fun Matrix<Number, InputContent2>.transpose(): Matrix<Number, OutputContent2> =
        Matrix(content2Producer(this.columnNumber, this.rowNumber) { row, column -> this[column, row] })
}

public fun <Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>> TranspositionComputer.Companion.default(
    content2Producer: (rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> Number) -> OutputContent2,
): TranspositionComputer<Number, InputContent2, OutputContent2> =
    DefaultTranspositionComputer(content2Producer)

public fun <Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setDefaultTranspositionComputerFor(
    numberType: SuppliedType,
    inputContent2Type: SuppliedType,
    outputContent2Type: SuppliedType,
    content2Producer: (rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> Number) -> OutputContent2,
) {
    this[TranspositionComputer.Key<Number, InputContent2, OutputContent2>(numberType, inputContent2Type, outputContent2Type)] =
        TranspositionComputer.default<Number, InputContent2, OutputContent2>(content2Producer)
}