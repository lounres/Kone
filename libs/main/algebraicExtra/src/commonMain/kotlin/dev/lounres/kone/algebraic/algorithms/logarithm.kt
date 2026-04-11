/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class LogarithmKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<Maybe<Number>> {
    override fun equals(other: Any?): Boolean = other is LogarithmKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.LogarithmKey<?, $numberType>"
}

public fun interface LogarithmComputer<Number> : KoneContext {
    public fun Number.logarithm(): Number
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<LogarithmComputer<Number>> {
        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
        override fun hashCode(): Int = numberType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.LogarithmComputer.Key<?, $numberType>"
    }
}

public interface LogarithmSoftComputer<Number> : LogarithmComputer<Number> {
    public fun Number.logarithmOrNull(): Number?
    public fun Number.logarithmMaybe(): Maybe<Number>
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<LogarithmSoftComputer<Number>> {
        override val impliedKeys: ImpliedKeysRegistry<LogarithmSoftComputer<Number>> = ImpliedKeysRegistry {
            LogarithmComputer.Key<Number>(numberType).impliesSame()
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
        override fun hashCode(): Int = numberType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.LogarithmSoftComputer.Key<?, $numberType>"
    }
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