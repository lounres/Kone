/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface IsZero<in Input> : KoneContext {
    public fun Input.isZero(): Boolean
    
    @Suppliable
    public class Key<@Supply Input> : SuppliedTypeRegistryKey<IsZero<Input>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.IsZero.Key<${suppliedTypeOf<Input>()}>"
    }
}

public inline fun <Input> IsZero(crossinline block: (Input) -> Boolean): IsZero<Input> = object : IsZero<Input> {
    override fun Input.isZero(): Boolean = block(this)
}

context(isZero: IsZero<Input>)
public fun <Input> Input.isZero(): Boolean = with(isZero) { this@isZero.isZero() }

// FIXME: KT-5351
context(isZero: IsZero<Input>)
public fun <Input> Input.isNotZero(): Boolean = with(isZero) { !this@isNotZero.isZero() }

public interface IsOne<in Input> : KoneContext {
    public fun Input.isOne(): Boolean
    
    @Suppliable
    public class Key<@Supply Input> : SuppliedTypeRegistryKey<IsOne<Input>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.IsOne.Key<${suppliedTypeOf<Input>()}>"
    }
}

public inline fun <Input> IsOne(crossinline block: (Input) -> Boolean): IsOne<Input> = object : IsOne<Input> {
    override fun Input.isOne(): Boolean = block(this)
}

context(isOne: IsOne<Input>)
public fun <Input> Input.isOne(): Boolean = with(isOne) { this@isOne.isOne() }

// FIXME: KT-5351
context(isOne: IsOne<Input>)
public fun <Input> Input.isNotOne(): Boolean = with(isOne) { !this@isNotOne.isOne() }

public interface UnaryPlus<in Input, out Result> : KoneContext {
    public operator fun Input.unaryPlus(): Result
    
    @Suppliable
    public class Key<@Supply Input, @Supply Result> : SuppliedTypeRegistryKey<UnaryPlus<Input, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.UnaryPlus.Key<${suppliedTypeOf<Input>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Input, Result> UnaryPlus(crossinline block: (Input) -> Result): UnaryPlus<Input, Result> = object : UnaryPlus<Input, Result> {
    override fun Input.unaryPlus(): Result = block(this)
}

context(unaryPlus: UnaryPlus<Input, Result>)
public operator fun <Input, Result> Input.unaryPlus(): Result = with(unaryPlus) { +this@unaryPlus }

public interface UnaryMinus<in Input, out Result> : KoneContext {
    public operator fun Input.unaryMinus(): Result
    
    @Suppliable
    public class Key<@Supply Input, @Supply Result> : SuppliedTypeRegistryKey<UnaryMinus<Input, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.UnaryMinus.Key<${suppliedTypeOf<Input>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Input, Result> UnaryMinus(crossinline block: (Input) -> Result): UnaryMinus<Input, Result> = object : UnaryMinus<Input, Result> {
    override fun Input.unaryMinus(): Result = block(this)
}

context(unaryMinus: UnaryMinus<Input, Result>)
public operator fun <Input, Result> Input.unaryMinus(): Result = with(unaryMinus) { -this@unaryMinus }

public interface Plus<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.plus(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Plus<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Plus.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Left, Right, Result> Plus(crossinline block: (left: Left, right: Right) -> Result): Plus<Left, Right, Result> = object : Plus<Left, Right, Result> {
    override fun Left.plus(other: Right): Result = block(this, other)
}

context(plus: Plus<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.plus(other: Right): Result = with(plus) { this@plus + other }

public interface Minus<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.minus(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Minus<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Minus.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Left, Right, Result> Minus(crossinline block: (left: Left, right: Right) -> Result): Minus<Left, Right, Result> = object : Minus<Left, Right, Result> {
    override fun Left.minus(other: Right): Result = block(this, other)
}

context(minus: Minus<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.minus(other: Right): Result = with(minus) { this@minus - other }

public interface Times<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.times(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Times<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Times.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Left, Right, Result> Times(crossinline block: (left: Left, right: Right) -> Result): Times<Left, Right, Result> = object : Times<Left, Right, Result> {
    override fun Left.times(other: Right): Result = block(this, other)
}

context(times: Times<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.times(other: Right): Result = with(times) { this@times * other }

public interface Divide<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.div(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Divide<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Divide.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Left, Right, Result> Divide(crossinline block: (left: Left, right: Right) -> Result): Divide<Left, Right, Result> = object : Divide<Left, Right, Result> {
    override fun Left.div(other: Right): Result = block(this, other)
}

context(divide: Divide<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.div(other: Right): Result = with(divide) { this@div / other }

public interface Reciprocal<in Input, out Result> : KoneContext {
    public fun Input.reciprocal(): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Divide<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Divide.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Input, Result> Reciprocal(crossinline block: (Input) -> Result): Reciprocal<Input, Result> = object : Reciprocal<Input, Result> {
    override fun Input.reciprocal(): Result = block(this)
}

context(divide: Reciprocal<Input, Result>)
public fun <Input, Result> Input.reciprocal(): Result = with(divide) { this@reciprocal.reciprocal() }

public interface Remainder<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.rem(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Remainder<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Remainder.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Left, Right, Result> Remainder(crossinline block: (left: Left, right: Right) -> Result): Remainder<Left, Right, Result> = object : Remainder<Left, Right, Result> {
    override fun Left.rem(other: Right): Result = block(this, other)
}

context(remainder: Remainder<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.rem(other: Right): Result = with(remainder) { this@rem.rem(other) }

public interface DivideRemainder<in Left, in Right, out Result> : KoneContext {
    public infix fun Left.divrem(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<DivideRemainder<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.DivideRemainder.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Left, Right, Result> DivideRemainder(crossinline block: (left: Left, right: Right) -> Result): DivideRemainder<Left, Right, Result> = object : DivideRemainder<Left, Right, Result> {
    override fun Left.divrem(other: Right): Result = block(this, other)
}

context(divideRemainder: DivideRemainder<Left, Right, Result>)
public infix fun <Left, Right, Result> Left.divrem(other: Right): Result = with(divideRemainder) { this@divrem.divrem(other) }

public interface Power<in Base, in Exponent, out Result> : KoneContext {
    public fun power(base: Base, exponent: Exponent): Result
    
    @Suppliable
    public class Key<@Supply Base, @Supply Exponent, @Supply Result> : SuppliedTypeRegistryKey<Power<Base, Exponent, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Power.Key<${suppliedTypeOf<Base>()}, ${suppliedTypeOf<Exponent>()}, ${suppliedTypeOf<Result>()}>"
    }
}

public inline fun <Base, Exponent, Result> Power(crossinline block: (base: Base, exponent: Exponent) -> Result): Power<Base, Exponent, Result> = object : Power<Base, Exponent, Result> {
    override fun power(base: Base, exponent: Exponent): Result = block(base, exponent)
}

context(power: Power<Base, Exponent, Result>)
public fun <Base, Exponent, Result> power(base: Base, exponent: Exponent): Result = with(power) { power(base, exponent) }

context(power: Power<Base, Exponent, Result>)
public infix fun <Base, Exponent, Result> Base.pow(exponent: Exponent): Result = with(power) { power(this@pow, exponent) }