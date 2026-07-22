/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.contentSize
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.multidimensionalCollections.utils.all
import dev.lounres.kone.multidimensionalCollections.utils.map
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class MDList1EuclideanSpaceOverRing<Number>(
    private val ring: Ring<Number>,
    private val dimension: UInt,
) : EuclideanSpaceOverRing<Number, MDList1<Number>, PointWrapper<MDList1<Number>>> {
    // region Constants
    override val zero: MDList1<Number> = MDList1.generate(dimension) { ring.zero }
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<MDList1<Number>> = IsZero {
        require(it.contentSize == dimension) { TODO() }
        it.all { ring.numberIsZero { it.isZero() } }
    }
    // endregion
    
    // region Vector-Int operations
    override val numberTimesInt: Times<MDList1<Number>, Int, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { ring.numberTimesInt { it * right } }
    }
    // endregion
    
    // region Vector-UInt operations
    override val numberTimesUInt: Times<MDList1<Number>, UInt, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { ring.numberTimesUInt { it * right } }
    }
    // endregion
    
    // region Vector-Long operations
    override val numberTimesLong: Times<MDList1<Number>, Long, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { ring.numberTimesLong { it * right } }
    }
    // endregion
    
    // region Vector-ULong operations
    override val numberTimesULong: Times<MDList1<Number>, ULong, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { ring.numberTimesULong { it * right } }
    }
    // endregion
    
    // region Vector-Number operations
    override val vectorTimesNumber: Times<MDList1<Number>, Number, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { ring.numberTimesNumber { it * right } }
    }
    // endregion
    
    // region Int-Vector operations
    override val intTimesNumber: Times<Int, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { ring.intTimesNumber { left * it } }
    }
    // endregion
    
    // region UInt-Vector operations
    override val uIntTimesNumber: Times<UInt, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { ring.uIntTimesNumber { left * it } }
    }
    // endregion
    
    // region Long-Vector operations
    override val longTimesNumber: Times<Long, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { ring.longTimesNumber { left * it } }
    }
    // endregion
    
    // region ULong-Vector operations
    override val uLongTimesNumber: Times<ULong, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { ring.uLongTimesNumber { left * it } }
    }
    // endregion
    
    // region Number-Vector operations
    override val numberTimesVector: Times<Number, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { ring.numberTimesNumber { left * it } }
    }
    // endregion
    
    // region Vector-Vector operations
    override val numberUnaryMinus: UnaryMinus<MDList1<Number>, MDList1<Number>> = UnaryMinus {
        require(it.contentSize == dimension) { TODO() }
        it.map { ring.numberUnaryMinus { -it } }
    }
    override val numberPlusNumber: Plus<MDList1<Number>, MDList1<Number>, MDList1<Number>> = Plus { left, right ->
        require(left.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { ring.numberPlusNumber { left[it] + right[it] } }
    }
    override val numberMinusNumber: Minus<MDList1<Number>, MDList1<Number>, MDList1<Number>> = Minus { left, right ->
        require(left.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { ring.numberMinusNumber { left[it] - right[it] } }
    }
    // endregion
    
    override val pointPlusVector: Plus<PointWrapper<MDList1<Number>>, MDList1<Number>, PointWrapper<MDList1<Number>>> = Plus { left, right ->
        require(left.vector.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        PointWrapper(MDList1.generate(dimension) { ring.numberPlusNumber { left.vector[it] + right[it] } })
    }
    override val vectorPlusPoint: Plus<MDList1<Number>, PointWrapper<MDList1<Number>>, PointWrapper<MDList1<Number>>> = Plus { left, right ->
        require(left.contentSize == dimension) { TODO() }
        require(right.vector.contentSize == dimension) { TODO() }
        PointWrapper(MDList1.generate(dimension) { ring.numberPlusNumber { left[it] + right.vector[it] } })
    }
    override val pointMinusVector: Minus<PointWrapper<MDList1<Number>>, MDList1<Number>, PointWrapper<MDList1<Number>>> = Minus { left, right ->
        require(left.vector.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        PointWrapper(MDList1.generate(dimension) { ring.numberMinusNumber { left.vector[it] - right[it] } })
    }
    override val pointMinusPoint: Minus<PointWrapper<MDList1<Number>>, PointWrapper<MDList1<Number>>, MDList1<Number>> = Minus { left, right ->
        require(left.vector.contentSize == dimension) { TODO() }
        require(right.vector.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { ring.numberMinusNumber { left.vector[it] - right.vector[it] } }
    }
    
    override val vectorDotVector: Dot<MDList1<Number>, MDList1<Number>, Number> = Dot { left, right ->
        require(left.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        KoneContext.localUnwrap(ring)
        (0u ..< dimension).toKoneList().sumOf { left[it] * right[it] }
    }
}

public fun <Number> EuclideanSpaceOverRing.Companion.mdList1(ring: Ring<Number>, dimension: UInt): EuclideanSpaceOverRing<Number, MDList1<Number>, PointWrapper<MDList1<Number>>> =
    MDList1EuclideanSpaceOverRing(ring, dimension)

// TODO: Remove the checker when KT-73135 will be fixed
public object EuclideanSpaceOverRingMDList1ForSuppliableTopLevelFunctions {
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> EuclideanSpaceOverRing.Companion.setMDList1For(dimension: UInt) {
        EuclideanSpaceOverRing.Key<Number, MDList1<Number>, PointWrapper<MDList1<Number>>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            val koneContextRegistry = koneContextRegistry.get()
            mdList1(koneContextRegistry[Ring.Key<Number>()], dimension)
        }
    }
}

private class MDList1EuclideanSpaceOverField<Number>(
    private val field: Field<Number>,
    private val dimension: UInt,
) : EuclideanSpaceOverField<Number, MDList1<Number>, PointWrapper<MDList1<Number>>> {
    // region Constants
    override val zero: MDList1<Number> = MDList1.generate(dimension) { field.zero }
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<MDList1<Number>> = IsZero {
        require(it.contentSize == dimension) { TODO() }
        it.all { field.numberIsZero { it.isZero() } }
    }
    // endregion
    
    // region Vector-Int operations
    override val numberTimesInt: Times<MDList1<Number>, Int, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberTimesInt { it * right } }
    }
    override val vectorDivideInt: Divide<MDList1<Number>, Int, MDList1<Number>> = Divide { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberDivideInt { it / right } }
    }
    // endregion
    
    // region Vector-UInt operations
    override val numberTimesUInt: Times<MDList1<Number>, UInt, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberTimesUInt { it * right } }
    }
    override val vectorDivideUInt: Divide<MDList1<Number>, UInt, MDList1<Number>> = Divide { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberDivideUInt { it / right } }
    }
    // endregion
    
    // region Vector-Long operations
    override val numberTimesLong: Times<MDList1<Number>, Long, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberTimesLong { it * right } }
    }
    override val vectorDivideLong: Divide<MDList1<Number>, Long, MDList1<Number>> = Divide { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberDivideLong { it / right } }
    }
    // endregion
    
    // region Vector-ULong operations
    override val numberTimesULong: Times<MDList1<Number>, ULong, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberTimesULong { it * right } }
    }
    override val vectorDivideULong: Divide<MDList1<Number>, ULong, MDList1<Number>> = Divide { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberDivideULong { it / right } }
    }
    // endregion
    
    // region Vector-Number operations
    override val vectorTimesNumber: Times<MDList1<Number>, Number, MDList1<Number>> = Times { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberTimesNumber { it * right } }
    }
    override val vectorDivideNumber: Divide<MDList1<Number>, Number, MDList1<Number>> = Divide { left, right ->
        require(left.contentSize == dimension) { TODO() }
        left.map { field.numberDivideNumber { it / right } }
    }
    // endregion
    
    // region Int-Vector operations
    override val intTimesNumber: Times<Int, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { field.intTimesNumber { left * it } }
    }
    // endregion
    
    // region UInt-Vector operations
    override val uIntTimesNumber: Times<UInt, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { field.uIntTimesNumber { left * it } }
    }
    // endregion
    
    // region Long-Vector operations
    override val longTimesNumber: Times<Long, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { field.longTimesNumber { left * it } }
    }
    // endregion
    
    // region ULong-Vector operations
    override val uLongTimesNumber: Times<ULong, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { field.uLongTimesNumber { left * it } }
    }
    // endregion
    
    // region Number-Vector operations
    override val numberTimesVector: Times<Number, MDList1<Number>, MDList1<Number>> = Times { left, right ->
        require(right.contentSize == dimension) { TODO() }
        right.map { field.numberTimesNumber { left * it } }
    }
    // endregion
    
    // region Vector-Vector operations
    override val numberUnaryMinus: UnaryMinus<MDList1<Number>, MDList1<Number>> = UnaryMinus {
        require(it.contentSize == dimension) { TODO() }
        it.map { field.numberUnaryMinus { -it } }
    }
    override val numberPlusNumber: Plus<MDList1<Number>, MDList1<Number>, MDList1<Number>> = Plus { left, right ->
        require(left.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { field.numberPlusNumber { left[it] + right[it] } }
    }
    override val numberMinusNumber: Minus<MDList1<Number>, MDList1<Number>, MDList1<Number>> = Minus { left, right ->
        require(left.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { field.numberMinusNumber { left[it] - right[it] } }
    }
    // endregion
    
    override val pointPlusVector: Plus<PointWrapper<MDList1<Number>>, MDList1<Number>, PointWrapper<MDList1<Number>>> = Plus { left, right ->
        require(left.vector.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        PointWrapper(MDList1.generate(dimension) { field.numberPlusNumber { left.vector[it] + right[it] } })
    }
    override val vectorPlusPoint: Plus<MDList1<Number>, PointWrapper<MDList1<Number>>, PointWrapper<MDList1<Number>>> = Plus { left, right ->
        require(left.contentSize == dimension) { TODO() }
        require(right.vector.contentSize == dimension) { TODO() }
        PointWrapper(MDList1.generate(dimension) { field.numberPlusNumber { left[it] + right.vector[it] } })
    }
    override val pointMinusVector: Minus<PointWrapper<MDList1<Number>>, MDList1<Number>, PointWrapper<MDList1<Number>>> = Minus { left, right ->
        require(left.vector.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        PointWrapper(MDList1.generate(dimension) { field.numberMinusNumber { left.vector[it] - right[it] } })
    }
    override val pointMinusPoint: Minus<PointWrapper<MDList1<Number>>, PointWrapper<MDList1<Number>>, MDList1<Number>> = Minus { left, right ->
        require(left.vector.contentSize == dimension) { TODO() }
        require(right.vector.contentSize == dimension) { TODO() }
        MDList1.generate(dimension) { field.numberMinusNumber { left.vector[it] - right.vector[it] } }
    }
    
    override val vectorDotVector: Dot<MDList1<Number>, MDList1<Number>, Number> = Dot { left, right ->
        require(left.contentSize == dimension) { TODO() }
        require(right.contentSize == dimension) { TODO() }
        KoneContext.localUnwrap(field)
        (0u ..< dimension).toKoneList().sumOf { left[it] * right[it] }
    }
}

public fun <Number> EuclideanSpaceOverField.Companion.mdList1(field: Field<Number>, dimension: UInt): EuclideanSpaceOverField<Number, MDList1<Number>, PointWrapper<MDList1<Number>>> =
    MDList1EuclideanSpaceOverField(field, dimension)

// TODO: Remove the checker when KT-73135 will be fixed
public object EuclideanSpaceOverFieldMDList1ForSuppliableTopLevelFunctions {
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> EuclideanSpaceOverField.Companion.setMDList1For(dimension: UInt) {
        EuclideanSpaceOverField.Key<Number, MDList1<Number>, PointWrapper<MDList1<Number>>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            val koneContextRegistry = koneContextRegistry.get()
            mdList1(koneContextRegistry[Field.Key<Number>()], dimension)
        }
    }
}