/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class HyperbolicCosineKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<Number> {
    override fun equals(other: Any?): Boolean = other is HyperbolicCosineKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicCosineKey<$numberType>"
}

public fun interface HyperbolicCosineComputer<Number> : KoneContext {
    public fun Number.cosh(): Number
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<HyperbolicCosineComputer<Number>> {
        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
        override fun hashCode(): Int = numberType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicCosineComputer.Key<$numberType>"
    }
}

context(hyperbolicCosineComputer: HyperbolicCosineComputer<Number>)
public fun <Number> Number.cosh(): Number =
    with(hyperbolicCosineComputer) { this@cosh.cosh() }