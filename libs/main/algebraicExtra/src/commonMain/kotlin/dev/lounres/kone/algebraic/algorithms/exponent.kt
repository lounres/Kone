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
public class ExponentKey<@Supply Number> : SuppliedTypeRegistryKey<Number>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ExponentKey<${suppliedTypeOf<Number>()}>"
}

public fun interface ExponentComputer<Number> : KoneContext {
    public fun Number.exponent(): Number
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<ExponentComputer<Number>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ExponentComputer.Key<${suppliedTypeOf<Number>()}>"
    }
}

context(exponentComputer: ExponentComputer<Number>)
public fun <Number> Number.exponent(): Number =
    with(exponentComputer) { this@exponent.exponent() }