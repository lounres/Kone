/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.operations

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


public interface AntisymmetricityComputer<Number, in Content2: MDList2<Number>> : KoneContext {
    public fun Matrix<Number, Content2>.isAntisymmetric(): Boolean
    
    public companion object;
    
    public class Key<Number, Content2: MDList2<Number>>(
        elementType: SuppliedType,
        content2Type: SuppliedType,
    ) : RegistryKey<AntisymmetricityComputer<Number, Content2>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.operations.AntisymmetricityComputer",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    ),
                    SuppliedProjection.Regular(
                        variance = IN,
                        type = content2Type
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(antisymmetricityComputer: AntisymmetricityComputer<Number, Content2>)
public fun <Number, Content2: MDList2<Number>> Matrix<Number, Content2>.isAntisymmetric(): Boolean =
    with(antisymmetricityComputer) { this@isAntisymmetric.isAntisymmetric() }

private class DefaultAntisymmetricityComputer<Number>(
    private val ring: Ring<Number>,
) : AntisymmetricityComputer<Number, MDList2<Number>> {
    override fun Matrix<Number, MDList2<Number>>.isAntisymmetric(): Boolean {
        if (rowNumber != columnNumber) return false
        
        for (row in 0u ..< rowNumber) {
            if (ring { this[row, row].isNotZero() }) return false
            for (column in row + 1u..<columnNumber) if (ring { (this[row, column] + this[column, row]).isNotZero() }) return false
        }
        
        return true
    }
}

public fun <Number, Content2: MDList2<Number>> AntisymmetricityComputer.Companion.default(ring: Ring<Number>): AntisymmetricityComputer<Number, Content2> =
    DefaultAntisymmetricityComputer(ring)

public fun <Number, Content2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setDefaultAntisymmetricityComputerFor(
    numberType: SuppliedType,
    content2Type: SuppliedType,
) {
    AntisymmetricityComputer.Key<Number, Content2>(numberType, content2Type) correspondsTo
            AntisymmetricityComputer.default(this[Ring.Key<Number>(numberType)])
}