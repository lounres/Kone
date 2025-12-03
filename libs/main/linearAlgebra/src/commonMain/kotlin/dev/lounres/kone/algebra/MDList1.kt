/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebra

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.contentSize
import dev.lounres.kone.multidimensionalCollections.utils.all
import dev.lounres.kone.multidimensionalCollections.utils.map
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withSuperkeys
import dev.lounres.kone.relations.eq
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


private class MDList1Module<Number>(
    private val ring: CommutativeRing<Number>,
    private val dimension: UInt,
) : Module<Number, MDList1<Number>> {
    // region Constants
    override val zero: MDList1<Number> = MDList1(dimension) { ring.zero }
    // endregion
    
    // region Equality
    override fun MDList1<Number>.equalsTo(other: MDList1<Number>): Boolean {
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        return (0u ..< dimension).all { ring { this[it] eq other[it] } }
    }
    override fun MDList1<Number>.isZero(): Boolean {
        require(this.contentSize == dimension) { TODO() }
        return this.all { ring { it.isZero() } }
    }
    // FIXME: KT-5351
    override fun MDList1<Number>.isNotZero(): Boolean = !isZero()
    // endregion
    
    // region Vector-UInt operations
    override operator fun MDList1<Number>.times(other: UInt): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { ring { it * other } }
    }
    // endregion
    
    // region Vector-Int operations
    override operator fun MDList1<Number>.times(other: Int): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { ring { it * other } }
    }
    // endregion
    
    // region Vector-Long operations
    override operator fun MDList1<Number>.times(other: Long): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { ring { it * other } }
    }
    // endregion
    
    // region Vector-ULong operations
    override operator fun MDList1<Number>.times(other: ULong): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { ring { it * other } }
    }
    // endregion
    
    // region Vector-Number operations
    override fun MDList1<Number>.times(other: Number): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { ring { it * other } }
    }
    // endregion
    
    // region Int-Vector operations
    override operator fun Int.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { ring { this * it } }
    }
    // endregion
    
    // region UInt-Vector operations
    override operator fun UInt.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { ring { this * it } }
    }
    // endregion
    
    // region Long-Vector operations
    override operator fun Long.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { ring { this * it } }
    }
    // endregion
    
    // region ULong-Vector operations
    override operator fun ULong.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { ring { this * it } }
    }
    // endregion
    
    // region Number-Vector operations
    override fun Number.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { ring { this * it } }
    }
    // endregion
    
    // region Vector-Vector operations
    override operator fun MDList1<Number>.unaryMinus(): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { ring { -it } }
    }
    override operator fun MDList1<Number>.plus(other: MDList1<Number>): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        return MDList1(dimension) { ring { this[it] + other[it] } }
    }
    override operator fun MDList1<Number>.minus(other: MDList1<Number>): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        return MDList1(dimension) { ring { this[it] + other[it] } }
    }
    // endregion
}

public fun <Number> Module.Companion.mdList1(ring: CommutativeRing<Number>, dimension: UInt): Module<Number, MDList1<Number>> =
    MDList1Module(ring = ring, dimension = dimension)

context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun <Number> Module.Companion.setMDList1For(numberType: SuppliedType, dimension: UInt) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val mdList1Type = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList1",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType
            )
        ),
        isNullable = false,
    )
    
    val module = mdList1(koneContextRegistryBuilder[CommutativeRing.Key<Number>(numberType)], dimension)
    
    Module.Key<Number, MDList1<Number>>(numberType, mdList1Type).withSuperkeys correspondsTo module
}

private class MDList1VectorSpace<Number>(
    private val field: Field<Number>,
    override val dimension: UInt,
) : VectorSpace.FiniteDimensional<Number, MDList1<Number>> {
    // region Constants
    override val zero: MDList1<Number> = MDList1(dimension) { field.zero }
    // endregion
    
    // region Equality
    override fun MDList1<Number>.equalsTo(other: MDList1<Number>): Boolean {
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        return (0u ..< dimension).all { field { this[it] eq other[it] } }
    }
    override fun MDList1<Number>.isZero(): Boolean {
        require(this.contentSize == dimension) { TODO() }
        return this.all { field { it.isZero() } }
    }
    // FIXME: KT-5351
    override fun MDList1<Number>.isNotZero(): Boolean = !isZero()
    // endregion
    
    // region Vector-UInt operations
    override operator fun MDList1<Number>.times(other: UInt): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { field { it * other } }
    }
    // endregion
    
    // region Vector-Int operations
    override operator fun MDList1<Number>.times(other: Int): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { field { it * other } }
    }
    // endregion
    
    // region Vector-Long operations
    override operator fun MDList1<Number>.times(other: Long): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { field { it * other } }
    }
    // endregion
    
    // region Vector-ULong operations
    override operator fun MDList1<Number>.times(other: ULong): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { field { it * other } }
    }
    // endregion
    
    // region Vector-Number operations
    override fun MDList1<Number>.times(other: Number): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { field { it * other } }
    }
    override fun MDList1<Number>.div(other: Number): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { field { it / other } }
    }
    // endregion
    
    // region Int-Vector operations
    override operator fun Int.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { field { this * it } }
    }
    // endregion
    
    // region UInt-Vector operations
    override operator fun UInt.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { field { this * it } }
    }
    // endregion
    
    // region Long-Vector operations
    override operator fun Long.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { field { this * it } }
    }
    // endregion
    
    // region ULong-Vector operations
    override operator fun ULong.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { field { this * it } }
    }
    // endregion
    
    // region Number-Vector operations
    override fun Number.times(other: MDList1<Number>): MDList1<Number> {
        require(other.contentSize == dimension) { TODO() }
        return other.map { field { this * it } }
    }
    // endregion
    
    // region Vector-Vector operations
    override operator fun MDList1<Number>.unaryMinus(): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        return this.map { field { -it } }
    }
    override operator fun MDList1<Number>.plus(other: MDList1<Number>): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        return MDList1(dimension) { field { this[it] + other[it] } }
    }
    override operator fun MDList1<Number>.minus(other: MDList1<Number>): MDList1<Number> {
        require(this.contentSize == dimension) { TODO() }
        require(other.contentSize == dimension) { TODO() }
        return MDList1(dimension) { field { this[it] + other[it] } }
    }
    // endregion
}

public fun <Number> VectorSpace.Companion.mdList1(field: Field<Number>, dimension: UInt): VectorSpace<Number, MDList1<Number>> =
    MDList1VectorSpace(field = field, dimension = dimension)

context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun <Number> VectorSpace.Companion.setMDList1For(numberType: SuppliedType, dimension: UInt) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val mdList1Type = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList1",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType
            )
        ),
        isNullable = false,
    )
    
    val module = mdList1(koneContextRegistryBuilder[Field.Key<Number>(numberType)], dimension)
    
    VectorSpace.Key<Number, MDList1<Number>>(numberType, mdList1Type).withSuperkeys correspondsTo module
}