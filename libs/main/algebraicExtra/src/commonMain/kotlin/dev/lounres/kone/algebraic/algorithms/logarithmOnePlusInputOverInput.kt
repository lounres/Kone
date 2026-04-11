/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class LogarithmOnePlusInputOverInputKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<Maybe<Number>> {
    override fun equals(other: Any?): Boolean = other is LogarithmOnePlusInputOverInputKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.LogarithmOnePlusInputOverInputKey<?, $numberType>"
}

public fun interface LogarithmOnePlusInputOverInputComputer<Number> : KoneContext {
    public fun Number.logarithmOnePlusThisOverThis(): Number
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<LogarithmOnePlusInputOverInputComputer<Number>> {
        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
        override fun hashCode(): Int = numberType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.LogarithmOnePlusInputOverInputComputer.Key<?, $numberType>"
    }
}

//public interface LogarithmOnePlusInputOverInputSoftComputer<Number> : LogarithmOnePlusInputOverInputComputer<Number> {
//    public fun Number.logarithmOnePlusThisOverThisOrNull(): Number?
//    public fun Number.logarithmOnePlusThisOverThisMaybe(): Maybe<Number>
//
//    public companion object;
//
//    public class Key<Number>(
//        public val numberType: SuppliedType,
//    ) : RegistryKey<LogarithmOnePlusInputOverInputSoftComputer<Number>> {
//        override val impliedKeys: ImpliedKeysRegistry<LogarithmOnePlusInputOverInputSoftComputer<Number>> = ImpliedKeysRegistry {
//            LogarithmOnePlusInputOverInputComputer.Key<Number>(numberType).impliesSame()
//        }
//        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
//        override fun hashCode(): Int = numberType.hashCode()
//        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.LogarithmOnePlusInputOverInputSoftComputer.Key<?, $numberType>"
//    }
//}

context(logarithmComputer: LogarithmOnePlusInputOverInputComputer<Number>)
public fun <Number> Number.logarithmOnePlusThisOverThis(): Number =
    with(logarithmComputer) { this@logarithmOnePlusThisOverThis.logarithmOnePlusThisOverThis() }

//context(logarithmComputer: LogarithmOnePlusInputOverInputSoftComputer<Number>)
//public fun <Number> Number.logarithmOnePlusThisOverThisOrNull(): Number? =
//    with(logarithmComputer) { this@logarithmOnePlusThisOverThisOrNull.logarithmOnePlusThisOverThisOrNull() }
//
//context(logarithmComputer: LogarithmOnePlusInputOverInputSoftComputer<Number>)
//public fun <Number> Number.logarithmOnePlusThisOverThisMaybe(): Maybe<Number> =
//    with(logarithmComputer) { this@logarithmOnePlusThisOverThisMaybe.logarithmOnePlusThisOverThisMaybe() }