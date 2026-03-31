/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class PositiveSquareRootKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<Maybe<Number>> {
    override fun equals(other: Any?): Boolean = other is SquareRootsKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.PositiveSquareRootKey<$numberType>"
}

public interface PositiveSquareRootComputer<Number> : KoneContext {
    public fun Number.positiveSquareRoot(): Number
    public fun Number.positiveSquareRootOrNull(): Number?
    public fun Number.positiveSquareRootMaybe(): Maybe<Number>
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<PositiveSquareRootComputer<Number>> {
        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
        override fun hashCode(): Int = numberType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer.Key<$numberType>"
    }
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