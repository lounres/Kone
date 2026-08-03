/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


@Suppliable
public class LogarithmKey<@Supply Number> : SuppliedTypeRegistryKey<Maybe<Number>>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.LogarithmKey<${suppliedTypeOf<Number>()}>"
}

@GenerateKoneContextKey
public fun interface LogarithmComputer<Number> : KoneContext {
    public fun Number.logarithm(): Number
    
    public companion object;
}

@GenerateKoneContextKey
public interface LogarithmSoftComputer<Number> : LogarithmComputer<Number> {
    public fun Number.logarithmOrNull(): Number?
    public fun Number.logarithmMaybe(): Maybe<Number>
    
    public companion object;
}

context(logarithmComputer: LogarithmComputer<Number>)
public fun <Number> Number.logarithm(): Number =
    with(logarithmComputer) { this@logarithm.logarithm() }

context(logarithmComputer: LogarithmSoftComputer<Number>)
public fun <Number> Number.logarithmOrNull(): Number? =
    with(logarithmComputer) { this@logarithmOrNull.logarithmOrNull() }

context(logarithmComputer: LogarithmSoftComputer<Number>)
public fun <Number> Number.logarithmMaybe(): Maybe<Number> =
    with(logarithmComputer) { this@logarithmMaybe.logarithmMaybe() }