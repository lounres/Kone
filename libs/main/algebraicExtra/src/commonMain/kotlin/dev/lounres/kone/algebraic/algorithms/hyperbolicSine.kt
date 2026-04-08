/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class HyperbolicSineKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<Number> {
    override fun equals(other: Any?): Boolean = other is HyperbolicSineKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicSineKey<$numberType>"
}

public fun interface HyperbolicSineComputer<Number> : KoneContext {
    public fun Number.sinh(): Number
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<HyperbolicSineComputer<Number>> {
        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
        override fun hashCode(): Int = numberType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicSineComputer.Key<$numberType>"
    }
}

context(hyperbolicSineComputer: HyperbolicSineComputer<Number>)
public fun <Number> Number.sinh(): Number =
    with(hyperbolicSineComputer) { this@sinh.sinh() }