/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.order.Order
import com.lounres.kone.order.comparator
import com.lounres.kone.polynomial.Polynomial
import com.lounres.kone.polynomial.manipulation.MultivariatePolynomialManipulationSpace.*
import kotlin.math.max
import kotlin.math.min


context(MultivariatePolynomialManipulationSpace<*, V, MS, *, *, *, *>)
public infix fun <V, MS> MS.divides(other: MS): Boolean {
    for ((v, power) in this) if (other.getOrZero(v) < power) return false
    return true
}

context(MultivariatePolynomialManipulationSpace<*, V, MS, *, *, *, *>)
public inline infix fun <V, MS> MS.isDivisibleBy(other: MS): Boolean = other divides this

context(MultivariatePolynomialManipulationSpace<*, V, MS, *, *, *, *>)
public operator fun <V, MS> MS.plus(other: MS): MS =
    signatureOf((this.variables + other.variables).map { VariablePower(it, this.getOrZero(it) + other.getOrZero(it)) })

context(MultivariatePolynomialManipulationSpace<*, V, MS, *, *, *, *>)
public operator fun <V, MS> MS.minus(other: MS): MS =
    signatureOf(
        (this.variables + other.variables).map {
            val thisPower = this.getOrZero(it)
            val otherPower = other.getOrZero(it)
            require(thisPower >= otherPower)
            VariablePower(it, thisPower - otherPower)
        }
    )

context(MultivariatePolynomialManipulationSpace<*, V, MS, *, *, *, *>)
public fun <V, MS> gcd(signature1: MS, signature2: MS): MS =
    if (signature1.size <= signature2.size) gcdInternalLogic(signature1, signature2)
    else gcdInternalLogic(signature2, signature1)

context(MultivariatePolynomialManipulationSpace<*, V, MS, MutMS, *, *, *>)
public fun <V, MS, MutMS: MS> gcdInternalLogic(signature1: MS, signature2: MS): MS {
    val gcd = mutableSignatureOf()
    for ((v, power) in signature1) {
        if (signature2 containsVariable v) gcd[v] = min(power, signature2.getOrZero(v))
    }
    return gcd
}

public data class GcdWithDivisors<out MS>(
    val gcd: MS,
    val divisor1: MS,
    val divisor2: MS,
)

context(MultivariatePolynomialManipulationSpace<*, V, MS, MutMS, *, *, *>)
public fun <V, MS, MutMS: MS> gcdWithDivisors(signature1: MS, signature2: MS): GcdWithDivisors<MS> {
    val gcd = gcd(signature1, signature2)

    val divisor1 = mutableSignatureOf()
    for ((v, power) in signature1) {
        val gcdPower = gcd.getOrZero(v)
        if (power > gcdPower) divisor1[v] = power - gcdPower
    }

    val divisor2 = mutableSignatureOf()
    for ((v, power) in signature2) {
        val gcdPower = gcd.getOrZero(v)
        if (power > gcdPower) divisor2[v] = power - gcdPower
    }

    return GcdWithDivisors(
        gcd = gcd,
        divisor1 = divisor1,
        divisor2 = divisor2,
    )
}

context(MultivariatePolynomialManipulationSpace<*, V, MS, MutMS, *, *, *>)
public fun <V, MS, MutMS: MS> lcm(signature1: MS, signature2: MS): MS {
    val lcm = mutableSignatureOf()
    for (v in signature1.variables union signature2.variables) {
        lcm[v] = min(signature1.getOrZero(v), signature2.getOrZero(v))
    }
    return lcm
}

public data class LcmWithMultipliers<out MS>(
    val lcm: MS,
    val multiplier1: MS,
    val multiplier2: MS,
)

context(MultivariatePolynomialManipulationSpace<*, V, MS, MutMS, *, *, *>)
public fun <V, MS, MutMS: MS> lcmWithMultipliers(signature1: MS, signature2: MS): LcmWithMultipliers<MS> {
    val lcm = lcm(signature1, signature2)

    val multiplier1 = mutableSignatureOf()
    for ((v, power) in signature1) {
        val lcmPower = lcm.getOrZero(v)
        if (lcmPower > power) multiplier1[v] = lcmPower - power
    }

    val multiplier2 = mutableSignatureOf()
    for ((v, power) in signature2) {
        val lcmPower = lcm.getOrZero(v)
        if (lcmPower > power) multiplier2[v] = lcmPower - power
    }

    return LcmWithMultipliers(
        lcm = lcm,
        multiplier1 = multiplier1,
        multiplier2 = multiplier2,
    )
}

context(MultivariatePolynomialManipulationSpace<*, *, MS, *, P, *, *>, Order<MS>)
public val <MS, P: Polynomial<*>> P.leadingSignature: MS get() = signatures.maxWith(comparator)

context(MultivariatePolynomialManipulationSpace<C, V, MS, *, P, *, *>, Order<MS>)
public val <C, V, MS, P: Polynomial<C>> P.leadingTerm: Monomial<C, MS> get() = leadingSignature.let { Monomial(this.getOrZero(it), it) }

context(MultivariatePolynomialManipulationSpace<C, *, MS, *, P, *, *>, Order<MS>)
public val <C, MS, P: Polynomial<C>> P.leadingCoefficient: C get() = leadingSignature.let { this.getOrZero(it) }

context(A, MultivariatePolynomialManipulationSpace<C, *, MS, *, P, MutP, A>)
public operator fun <C, MS, P: Polynomial<C>, MutP: P, A: Ring<C>> MutP.plusAssign(other: P) {
    for ((c, ms) in other) this[ms] = this.getOrZero(ms) + c
}

context(A, MultivariatePolynomialManipulationSpace<C, *, MS, *, P, MutP, A>)
public operator fun <C, MS, P: Polynomial<C>, MutP: P, A: Ring<C>> MutP.minusAssign(other: P) {
    for ((c, ms) in other) this[ms] = this.getOrZero(ms) - c
}

context(A, MultivariatePolynomialManipulationSpace<C, *, MS, *, P, MutP, A>)
public fun <C, MS, P: Polynomial<C>, MutP: P, A: Ring<C>> MutP.plusAssignProduct(multiplier1: P, multiplier2: P) {
    for ((c1, ms1) in multiplier1) for ((c2, ms2) in multiplier2) {
        val ms = ms1 + ms2
        this[ms] = this.getOrZero(ms) + c1 * c2
    }
}

context(A, MultivariatePolynomialManipulationSpace<C, *, MS, *, P, MutP, A>)
public fun <C, MS, P: Polynomial<C>, MutP: P, A: Ring<C>> MutP.minusAssignProduct(multiplier1: P, multiplier2: P) {
    for ((c1, ms1) in multiplier1) for ((c2, ms2) in multiplier2) {
        val ms = ms1 + ms2
        this[ms] = this.getOrZero(ms) + c1 * c2
    }
}