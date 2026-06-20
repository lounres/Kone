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
public class HyperbolicCosineKey<@Supply Number> : SuppliedTypeRegistryKey<Number>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicCosineKey<${suppliedTypeOf<Number>()}>"
}

public fun interface HyperbolicCosineComputer<Number> : KoneContext {
    public fun Number.cosh(): Number
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<HyperbolicCosineComputer<Number>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HyperbolicCosineComputer.Key<${suppliedTypeOf<Number>()}>"
    }
}

context(hyperbolicCosineComputer: HyperbolicCosineComputer<Number>)
public fun <Number> Number.cosh(): Number =
    with(hyperbolicCosineComputer) { this@cosh.cosh() }