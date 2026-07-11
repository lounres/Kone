/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextHolderContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


/**
 * Describes a context that represents [mathematical field](https://en.wikipedia.org/wiki/Field_(mathematics)).
 * It means that it is an extension of [Ring] interface that also provides division and exponentiation to the negative
 * integer power. See docs of [Ring] for a full description and docs of the [Field] interface's operations.
 */
public interface Field<Number> : CommutativeRing<Number> {
    @KoneContextHolderContext
    public val numberDivideNumber: Divide<Number, Number, Number>
    @KoneContextHolderContext
    public val numberReciprocal: Reciprocal<Number, Number> get() = Reciprocal { numberDivideNumber { one / this } }
    @KoneContextHolderContext
    public val numberDivideInt: Divide<Number, Int, Number> get() = Divide { other -> numberDivideNumber { this / valueOf(other) } }
    @KoneContextHolderContext
    public val numberDivideUInt: Divide<Number, UInt, Number> get() = Divide { other -> numberDivideNumber { this / valueOf(other) } }
    @KoneContextHolderContext
    public val numberDivideLong: Divide<Number, Long, Number> get() = Divide { other -> numberDivideNumber { this / valueOf(other) } }
    @KoneContextHolderContext
    public val numberDivideULong: Divide<Number, ULong, Number> get() = Divide { other -> numberDivideNumber { this / valueOf(other) } }
    @KoneContextHolderContext
    public val intDivideNumber: Divide<Int, Number, Number> get() = Divide { other -> numberDivideNumber { valueOf(this) / other } }
    @KoneContextHolderContext
    public val uIntDivideNumber: Divide<UInt, Number, Number> get() = Divide { other -> numberDivideNumber { valueOf(this) / other } }
    @KoneContextHolderContext
    public val longDivideNumber: Divide<Long, Number, Number> get() = Divide { other -> numberDivideNumber { valueOf(this) / other } }
    @KoneContextHolderContext
    public val uLongDivideNumber: Divide<ULong, Number, Number> get() = Divide { other -> numberDivideNumber { valueOf(this) / other } }
    @KoneContextHolderContext
    public val powerNumberInt: Power<Number, Int, Number>
        get() = Power { base, exponent ->
            context(powerNumberUInt, numberReciprocal) {
                if (exponent >= 0) power(base, exponent.toUInt())
                else power(base, (-exponent).toUInt()).reciprocal()
            }
        }
    @KoneContextHolderContext
    public val powerNumberLong: Power<Number, Long, Number>
        get() = Power { base, exponent ->
            context(powerNumberULong, numberReciprocal) {
                if (exponent >= 0) power(base, exponent.toULong())
                else power(base, (-exponent).toULong()).reciprocal()
            }
        }
    
    public companion object;
    
    /**
     * Registry key for [Field] interface in [KoneContextRegistry].
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