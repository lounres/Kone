/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class DeterminantKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<Number> {
    override fun equals(other: Any?): Boolean = other is DeterminantKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.DeterminantKey<$numberType>"
}

public fun interface DeterminantComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.determinant(): Number
    
    public companion object;
}

context(determinantComputer: DeterminantComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.determinant(): Number =
    with(determinantComputer) { this@determinant.determinant() }