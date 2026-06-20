/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


@Suppliable
public class HyperbolicSineOverInputKey<@Supply Number> : SuppliedTypeRegistryKey<Number>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicSineOverInputKey<${suppliedTypeOf<Number>()}>"
}

public fun interface HyperbolicSineOverInputComputer<Number> : KoneContext {
    public fun Number.sinhOverThis(): Number
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<HyperbolicSineOverInputComputer<Number>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicSineOverInputComputer.Key<${suppliedTypeOf<Number>()}>"
    }
}

context(hyperbolicSineOverInputComputer: HyperbolicSineOverInputComputer<Number>)
public fun <Number> Number.sinhOverThis(): Number =
    with(hyperbolicSineOverInputComputer) { this@sinhOverThis.sinhOverThis() }