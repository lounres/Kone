/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.contentSize
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.multidimensionalCollections.utils.all
import dev.lounres.kone.multidimensionalCollections.utils.map
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class MDList1Module<Number>(
    private val ring: CommutativeRing<Number>,
    private val dimension: UInt,
) : Module<Number, MDList1<Number>> {
    // region Constants
    override val zero: MDList1<Number> = MDList1.generate(dimension) { ring.zero }
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<MDList1<Number>> = IsZero {
        require(this.contentSize == dimension) { TODO() }
        this.all { ring.numberIsZero { it.isZero() } }
    }
    // endregion
    
    // region Vector-Int operations
    override val numberTimesInt: Times<MDList1<Number>, Int, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { ring.numberTimesInt { it * other } }
    }
    // endregion
    
    // region Vector-UInt operations
    override val numberTimesUInt: Times<MDList1<Number>, UInt, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { ring.numberTimesUInt { it * other } }
    }
    // endregion
    
    // region Vector-Long operations
    override val numberTimesLong: Times<MDList1<Number>, Long, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { ring.numberTimesLong { it * other } }
    }
    // endregion
    
    // region Vector-ULong operations
    override val numberTimesULong: Times<MDList1<Number>, ULong, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { ring.numberTimesULong { it * other } }
    }
    // endregion
    
    // region Vector-Number operations
    override val vectorTimesNumber: Times<MDList1<Number>, Number, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { ring.numberTimesNumber { it * other } }
    }
    // endregion
    
    // region Int-Vector operations
    override val intTimesNumber: Times<Int, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { ring.intTimesNumber { this * it } }
    }
    // endregion
    
    // region UInt-Vector operations
    override val uIntTimesNumber: Times<UInt, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { ring.uIntTimesNumber { this * it } }
    }
    // endregion
    
    // region Long-Vector operations
    override val longTimesNumber: Times<Long, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { ring.longTimesNumber { this * it } }
    }
    // endregion
    
    // region ULong-Vector operations
    override val uLongTimesNumber: Times<ULong, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { ring.uLongTimesNumber { this * it } }
    }
    // endregion
    
    // region Number-Vector operations
    override val numberTimesVector: Times<Number, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { ring.numberTimesNumber { this * it } }
    }
    // endregion
    
    // region Vector-Vector operations
    override val numberUnaryMinus: UnaryMinus<MDList1<Number>, MDList1<Number>> = UnaryMinus {
        require(this.contentSize == dimension) { TODO() }
        this.map { ring.numberUnaryMinus { -it } }
    }
    override val numberPlusNumber: Plus<MDList1<Number>, MDList1<Number>, MDList1<Number>> = Plus { other ->
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { ring.numberPlusNumber { this[it] + other[it] } }
    }
    override val numberMinusNumber: Minus<MDList1<Number>, MDList1<Number>, MDList1<Number>> = Minus { other ->
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { ring.numberMinusNumber { this[it] - other[it] } }
    }
    // endregion
}

public fun <Number> Module.Companion.mdList1(ring: CommutativeRing<Number>, dimension: UInt): Module<Number, MDList1<Number>> =
    MDList1Module(ring = ring, dimension = dimension)

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> Module.Companion.setMDList1For(dimension: UInt) {
    Module.Key<Number, MDList1<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        mdList1(koneContextRegistry[CommutativeRing.Key<Number>()], dimension)
    }
}

private class MDList1VectorSpace<Number>(
    private val field: Field<Number>,
    override val dimension: UInt,
) : VectorSpace.FiniteDimensional<Number, MDList1<Number>> {
    // region Constants
    override val zero: MDList1<Number> = MDList1.generate(dimension) { field.zero }
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<MDList1<Number>> = IsZero {
        require(this.contentSize == dimension) { TODO() }
        this.all { field.numberIsZero { it.isZero() } }
    }
    // endregion
    
    // region Vector-Int operations
    override val numberTimesInt: Times<MDList1<Number>, Int, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberTimesInt { it * other } }
    }
    override val vectorDivideInt: Divide<MDList1<Number>, Int, MDList1<Number>> = Divide { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberDivideInt { it / other } }
    }
    // endregion
    
    // region Vector-UInt operations
    override val numberTimesUInt: Times<MDList1<Number>, UInt, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberTimesUInt { it * other } }
    }
    override val vectorDivideUInt: Divide<MDList1<Number>, UInt, MDList1<Number>> = Divide { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberDivideUInt { it / other } }
    }
    // endregion
    
    // region Vector-Long operations
    override val numberTimesLong: Times<MDList1<Number>, Long, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberTimesLong { it * other } }
    }
    override val vectorDivideLong: Divide<MDList1<Number>, Long, MDList1<Number>> = Divide { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberDivideLong { it / other } }
    }
    // endregion
    
    // region Vector-ULong operations
    override val numberTimesULong: Times<MDList1<Number>, ULong, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberTimesULong { it * other } }
    }
    override val vectorDivideULong: Divide<MDList1<Number>, ULong, MDList1<Number>> = Divide { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberDivideULong { it / other } }
    }
    // endregion
    
    // region Vector-Number operations
    override val vectorTimesNumber: Times<MDList1<Number>, Number, MDList1<Number>> = Times { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberTimesNumber { it * other } }
    }
    override val vectorDivideNumber: Divide<MDList1<Number>, Number, MDList1<Number>> = Divide { other ->
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberDivideNumber { it / other } }
    }
    // endregion
    
    // region Int-Vector operations
    override val intTimesNumber: Times<Int, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { field.intTimesNumber { this * it } }
    }
    // endregion
    
    // region UInt-Vector operations
    override val uIntTimesNumber: Times<UInt, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { field.uIntTimesNumber { this * it } }
    }
    // endregion
    
    // region Long-Vector operations
    override val longTimesNumber: Times<Long, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { field.longTimesNumber { this * it } }
    }
    // endregion
    
    // region ULong-Vector operations
    override val uLongTimesNumber: Times<ULong, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { field.uLongTimesNumber { this * it } }
    }
    // endregion
    
    // region Number-Vector operations
    override val numberTimesVector: Times<Number, MDList1<Number>, MDList1<Number>> = Times { other ->
        require(other.contentSize == dimension) { TODO() }
        other.map { field.numberTimesNumber { this * it } }
    }
    // endregion
    
    // region Vector-Vector operations
    override val numberUnaryMinus: UnaryMinus<MDList1<Number>, MDList1<Number>> = UnaryMinus {
        require(this.contentSize == dimension) { TODO() }
        this.map { field.numberUnaryMinus { -it } }
    }
    override val numberPlusNumber: Plus<MDList1<Number>, MDList1<Number>, MDList1<Number>> = Plus { other ->
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { field.numberPlusNumber { this[it] + other[it] } }
    }
    override val numberMinusNumber: Minus<MDList1<Number>, MDList1<Number>, MDList1<Number>> = Minus { other ->
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { field.numberMinusNumber { this[it] - other[it] } }
    }
    // endregion
}

public fun <Number> VectorSpace.Companion.mdList1(field: Field<Number>, dimension: UInt): VectorSpace<Number, MDList1<Number>> =
    MDList1VectorSpace(field = field, dimension = dimension)

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> VectorSpace.Companion.setMDList1For(dimension: UInt) {
    VectorSpace.Key<Number, MDList1<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        mdList1(koneContextRegistry[Field.Key<Number>()], dimension)
    }
}