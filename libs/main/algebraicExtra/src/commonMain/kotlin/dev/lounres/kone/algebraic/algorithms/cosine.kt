/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


@Suppliable
public class CosineKey<@Supply Number> : SuppliedTypeRegistryKey<Number>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.CosineKey<${suppliedTypeOf<Number>()}>"
}

@GenerateKoneContextKey
public fun interface CosineComputer<Number> : KoneContext {
    public fun Number.cos(): Number
    
    public companion object;
}

context(cosineComputer: CosineComputer<Number>)
public fun <Number> Number.cos(): Number =
    with(cosineComputer) { this@cos.cos() }