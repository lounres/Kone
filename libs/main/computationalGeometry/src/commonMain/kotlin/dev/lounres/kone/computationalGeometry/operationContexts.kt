/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public fun interface Dot<in Left, in Right, out Result> : KoneContext {
    public infix fun Left.dot(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Dot<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Dot.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(dot: Dot<Left, Right, Result>)
public infix fun <Left, Right, Result> Left.dot(other: Right): Result = with(dot) { this@dot dot other }

context(dot: Dot<Input, Input, Result>)
public fun <Input, Result> Input.lengthSquared(): Result = this dot this