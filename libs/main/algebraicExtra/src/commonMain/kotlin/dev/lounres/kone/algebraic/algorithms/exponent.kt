package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class ExponentKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<Number> {
    override fun equals(other: Any?): Boolean = other is ExponentKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ExponentKey<$numberType>"
}

public fun interface ExponentComputer<Number> : KoneContext {
    public fun Number.exponent(): Number
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<ExponentComputer<Number>> {
        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
        override fun hashCode(): Int = numberType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ExponentComputer.Key<$numberType>"
    }
}

context(exponentComputer: ExponentComputer<Number>)
public fun <Number> Number.exponent(): Number =
    with(exponentComputer) { this@exponent.exponent() }