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


public fun interface IsZero<in Input> : KoneContext {
    public fun Input.isZero(): Boolean
    
    @Suppliable
    public class Key<@Supply Input> : SuppliedTypeRegistryKey<IsZero<Input>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.IsZero.Key<${suppliedTypeOf<Input>()}>"
    }
}

context(isZero: IsZero<Input>)
public fun <Input> Input.isZero(): Boolean = with(isZero) { this@isZero.isZero() }

// FIXME: KT-5351
context(isZero: IsZero<Input>)
public fun <Input> Input.isNotZero(): Boolean = with(isZero) { !this@isNotZero.isZero() }

public fun interface IsOne<in Input> : KoneContext {
    public fun Input.isOne(): Boolean
    
    @Suppliable
    public class Key<@Supply Input> : SuppliedTypeRegistryKey<IsOne<Input>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.IsOne.Key<${suppliedTypeOf<Input>()}>"
    }
}

context(isOne: IsOne<Input>)
public fun <Input> Input.isOne(): Boolean = with(isOne) { this@isOne.isOne() }

// FIXME: KT-5351
context(isOne: IsOne<Input>)
public fun <Input> Input.isNotOne(): Boolean = with(isOne) { !this@isNotOne.isOne() }

public fun interface UnaryPlus<in Input, out Result> : KoneContext {
    public operator fun Input.unaryPlus(): Result
    
    @Suppliable
    public class Key<@Supply Input, @Supply Result> : SuppliedTypeRegistryKey<UnaryPlus<Input, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.UnaryPlus.Key<${suppliedTypeOf<Input>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(unaryPlus: UnaryPlus<Input, Result>)
public operator fun <Input, Result> Input.unaryPlus(): Result = with(unaryPlus) { +this@unaryPlus }

public fun interface UnaryMinus<in Input, out Result> : KoneContext {
    public operator fun Input.unaryMinus(): Result
    
    @Suppliable
    public class Key<@Supply Input, @Supply Result> : SuppliedTypeRegistryKey<UnaryMinus<Input, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.UnaryMinus.Key<${suppliedTypeOf<Input>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(unaryMinus: UnaryMinus<Input, Result>)
public operator fun <Input, Result> Input.unaryMinus(): Result = with(unaryMinus) { -this@unaryMinus }

public fun interface Plus<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.plus(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Plus<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Plus.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(plus: Plus<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.plus(other: Right): Result = with(plus) { this@plus + other }

public fun interface Minus<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.minus(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Minus<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Minus.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(minus: Minus<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.minus(other: Right): Result = with(minus) { this@minus - other }

public fun interface Times<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.times(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Times<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Times.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(times: Times<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.times(other: Right): Result = with(times) { this@times * other }

public fun interface Divide<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.div(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Divide<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Divide.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(divide: Divide<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.div(other: Right): Result = with(divide) { this@div / other }

public fun interface Reciprocal<in Input, out Result> : KoneContext {
    public fun Input.reciprocal(): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Divide<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Divide.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(divide: Reciprocal<Input, Result>)
public fun <Input, Result> Input.reciprocal(): Result = with(divide) { this@reciprocal.reciprocal() }

public fun interface Remainder<in Left, in Right, out Result> : KoneContext {
    public operator fun Left.rem(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<Remainder<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Remainder.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(remainder: Remainder<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.rem(other: Right): Result = with(remainder) { this@rem.rem(other) }

public fun interface DivideRemainder<in Left, in Right, out Result> : KoneContext {
    public infix fun Left.divrem(other: Right): Result
    
    @Suppliable
    public class Key<@Supply Left, @Supply Right, @Supply Result> : SuppliedTypeRegistryKey<DivideRemainder<Left, Right, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.DivideRemainder.Key<${suppliedTypeOf<Left>()}, ${suppliedTypeOf<Right>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(divideRemainder: DivideRemainder<Left, Right, Result>)
public infix fun <Left, Right, Result> Left.divrem(other: Right): Result = with(divideRemainder) { this@divrem.divrem(other) }

public fun interface Power<in Base, in Exponent, out Result> : KoneContext {
    public fun power(base: Base, exponent: Exponent): Result
    
    @Suppliable
    public class Key<@Supply Base, @Supply Exponent, @Supply Result> : SuppliedTypeRegistryKey<Power<Base, Exponent, Result>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.Power.Key<${suppliedTypeOf<Base>()}, ${suppliedTypeOf<Exponent>()}, ${suppliedTypeOf<Result>()}>"
    }
}

context(power: Power<Base, Exponent, Result>)
public fun <Base, Exponent, Result> power(base: Base, exponent: Exponent): Result = with(power) { power(base, exponent) }

context(power: Power<Base, Exponent, Result>)
public infix fun <Base, Exponent, Result> Base.pow(exponent: Exponent): Result = with(power) { power(this@pow, exponent) }