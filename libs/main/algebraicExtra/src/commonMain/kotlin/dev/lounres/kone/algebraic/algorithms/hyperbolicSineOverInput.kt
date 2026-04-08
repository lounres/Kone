/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class HyperbolicSineOverInputKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<Number> {
    override fun equals(other: Any?): Boolean = other is HyperbolicSineOverInputKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicSineOverInputKey<$numberType>"
}

public fun interface HyperbolicSineOverInputComputer<Number> : KoneContext {
    public fun Number.sinhOverThis(): Number
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<HyperbolicSineOverInputComputer<Number>> {
        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
        override fun hashCode(): Int = numberType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicSineOverInputComputer.Key<$numberType>"
    }
}

context(hyperbolicSineOverInputComputer: HyperbolicSineOverInputComputer<Number>)
public fun <Number> Number.sinhOverThis(): Number =
    with(hyperbolicSineOverInputComputer) { this@sinhOverThis.sinhOverThis() }