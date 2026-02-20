package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class SquareRootsKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<KoneList<Number>> {
    override fun equals(other: Any?): Boolean = other is SquareRootsKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.SquareRootsKey<$numberType>"
}

public fun interface SquareRootsComputer<Number> : KoneContext {
    public fun Number.squareRoots(): KoneList<Number>
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<SquareRootsComputer<Number>> {
        override fun equals(other: Any?): Boolean = other is Key<*> && numberType == other.numberType
        override fun hashCode(): Int = numberType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.SquareRootsComputer.Key<$numberType>"
    }
}

context(squareRootComputer: SquareRootsComputer<Number>)
public fun <Number> Number.squareRoots(): KoneList<Number> =
    with(squareRootComputer) { this@squareRoots.squareRoots() }