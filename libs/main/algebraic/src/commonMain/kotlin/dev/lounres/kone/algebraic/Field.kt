/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localContexts
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


/**
 * Describes a context that represents [mathematical field](https://en.wikipedia.org/wiki/Field_(mathematics)).
 *
 * @param Number The type of elements of the Euclidean semiring.
 */
public interface Field<Number> : CommutativeRing<Number> {
    /**
     * The division operation on elements of type [Number].
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val numberDivideNumber: Divide<Number, Number, Number>
    /**
     * The reciprocal computation operation on elements of type [Number].
     *
     * Default implementation uses division operation on two [Number]s and [one] as a dividend.
     *
     * @return The division context represented as [Reciprocal] instance.
     */
    @KoneContextInclude
    public val numberReciprocal: Reciprocal<Number, Number> get() = Reciprocal { numberDivideNumber { one / it } }
    /**
     * The division operation on a [Number] and an [Int].
     *
     * Default implementation uses division operation on two [Number]s and conversion of the [Int] to a [Number].
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val numberDivideInt: Divide<Number, Int, Number> get() = Divide { left, right -> numberDivideNumber { left / valueOf(right) } }
    /**
     * The division operation on a [Number] and an [UInt].
     *
     * Default implementation uses division operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val numberDivideUInt: Divide<Number, UInt, Number> get() = Divide { left, right -> numberDivideNumber { left / valueOf(right) } }
    /**
     * The division operation on a [Number] and an [Long].
     *
     * Default implementation uses division operation on two [Number]s and conversion of the [Long] to a [Number].
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val numberDivideLong: Divide<Number, Long, Number> get() = Divide { left, right -> numberDivideNumber { left / valueOf(right) } }
    /**
     * The division operation on a [Number] and an [ULong].
     *
     * Default implementation uses division operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val numberDivideULong: Divide<Number, ULong, Number> get() = Divide { left, right -> numberDivideNumber { left / valueOf(right) } }
    /**
     * The division operation on an [Int] and a [Number].
     *
     * Default implementation uses division operation on two [Number]s and conversion of the [Int] to a [Number].
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val intDivideNumber: Divide<Int, Number, Number> get() = Divide { left, right -> numberDivideNumber { valueOf(left) / right } }
    /**
     * The division operation on an [UInt] and a [Number].
     *
     * Default implementation uses division operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val uIntDivideNumber: Divide<UInt, Number, Number> get() = Divide { left, right -> numberDivideNumber { valueOf(left) / right } }
    /**
     * The division operation on an [Long] and a [Number].
     *
     * Default implementation uses division operation on two [Number]s and conversion of the [Long] to a [Number].
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val longDivideNumber: Divide<Long, Number, Number> get() = Divide { left, right -> numberDivideNumber { valueOf(left) / right } }
    /**
     * The division operation on an [ULong] and a [Number].
     *
     * Default implementation uses division operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val uLongDivideNumber: Divide<ULong, Number, Number> get() = Divide { left, right -> numberDivideNumber { valueOf(left) / right } }
    /**
     * The exponentiation operation on a [Number] base and an [Int] exponent.
     *
     * Default implementation uses exponentiation operation on a [Number] base and [UInt] exponent and reciprocal computation of the base in case of negative power.
     *
     * @return The exponentiation context represented as [Power] instance.
     */
    @KoneContextInclude
    public val powerNumberInt: Power<Number, Int, Number>
        get() = Power { base, exponent ->
            localContexts(powerNumberUInt, numberReciprocal)
            if (exponent >= 0) power(base, exponent.toUInt())
            else power(base, (-exponent).toUInt()).reciprocal()
        }
    /**
     * The exponentiation operation on a [Number] base and an [Long] exponent.
     *
     * Default implementation uses exponentiation operation on a [Number] base and [ULong] exponent and reciprocal computation of the base in case of negative power.
     *
     * @return The exponentiation context represented as [Power] instance.
     */
    @KoneContextInclude
    public val powerNumberLong: Power<Number, Long, Number>
        get() = Power { base, exponent ->
            localContexts(powerNumberULong, numberReciprocal)
            if (exponent >= 0) power(base, exponent.toULong())
            else power(base, (-exponent).toULong()).reciprocal()
        }
    
    public companion object;
    
    /**
     * Registry key for [Field] interface in [Registry].
     *
     * @param Number The type of elements of the field.
     */
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<Field<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<Field<Number>> by lazy {
            ImpliedKeysRegistry {
                CommutativeRing.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Field.Key<${suppliedTypeOf<Number>()}>"
    }
}