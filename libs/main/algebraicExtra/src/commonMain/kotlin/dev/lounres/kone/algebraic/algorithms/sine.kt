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
public class SineKey<@Supply Number> : SuppliedTypeRegistryKey<Number>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.SineKey<${suppliedTypeOf<Number>()}>"
}

public fun interface SineComputer<Number> : KoneContext {
    public fun Number.sin(): Number
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<SineComputer<Number>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.SineComputer.Key<${suppliedTypeOf<Number>()}>"
    }
}

context(sineComputer: SineComputer<Number>)
public fun <Number> Number.sin(): Number =
    with(sineComputer) { this@sin.sin() }