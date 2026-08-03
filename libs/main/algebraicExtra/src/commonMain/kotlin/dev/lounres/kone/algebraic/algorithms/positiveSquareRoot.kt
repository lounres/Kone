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
public class PositiveSquareRootKey<@Supply Number> : SuppliedTypeRegistryKey<Maybe<Number>>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.PositiveSquareRootKey<${suppliedTypeOf<Number>()}>"
}

@GenerateKoneContextKey
public interface PositiveSquareRootComputer<Number> : KoneContext {
    public fun Number.positiveSquareRoot(): Number
    public fun Number.positiveSquareRootOrNull(): Number?
    public fun Number.positiveSquareRootMaybe(): Maybe<Number>
    
    public companion object;
}

context(positiveSquareRootComputer: PositiveSquareRootComputer<Number>)
public fun <Number> Number.positiveSquareRoot(): Number =
    with(positiveSquareRootComputer) { this@positiveSquareRoot.positiveSquareRoot() }

context(positiveSquareRootComputer: PositiveSquareRootComputer<Number>)
public fun <Number> Number.positiveSquareRootOrNull(): Number? =
    with(positiveSquareRootComputer) { this@positiveSquareRootOrNull.positiveSquareRootOrNull() }

context(positiveSquareRootComputer: PositiveSquareRootComputer<Number>)
public fun <Number> Number.positiveSquareRootMaybe(): Maybe<Number> =
    with(positiveSquareRootComputer) { this@positiveSquareRootMaybe.positiveSquareRootMaybe() }