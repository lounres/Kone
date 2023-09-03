/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.context.invoke
import com.lounres.kone.order.Order
import com.lounres.kone.order.comparator
import kotlin.math.max
import kotlin.math.min


public fun <V, VP, MS> MultivariatePolynomialManipulationSpace<*, V, VP, MS, *, *, *, *, *>.divides(that: MS, other: MS): Boolean {
    for ((v, power) in that) if (other[v] < power) return false
    return true
}

public inline fun <V, MS> MultivariatePolynomialManipulationSpace<*, V, *, MS, *, *, *, *, *>.isDivisibleBy(that: MS, other: MS): Boolean = divides(other, that)

@JvmName("timesSignatureSignature")
public fun <V, VP, MS> MultivariatePolynomialManipulationSpace<*, V, VP, MS, *, *, *, *, *>.times(that: MS, other: MS): MS =
    signatureOf((that.variables + other.variables).map { variablePower(it, that[it] + other[it]) })

@JvmName("timesSignatureVariable")
public fun <V, VP, MS> MultivariatePolynomialManipulationSpace<*, V, VP, MS, *, *, *, *, *>.times(that: MS, other: V): MS =
    signatureOf((that.variables + other).map { if (it == other) variablePower(it, that[it] + 1u) else variablePower(it, that[it]) })

public fun <V, VP, MS> MultivariatePolynomialManipulationSpace<*, V, VP, MS, *, *, *, *, *>.div(that: MS, other: MS): MS =
    signatureOf(
        (that.variables + other.variables).map {
            val thisPower = that[it]
            val otherPower = other[it]
            require(thisPower >= otherPower)
            variablePower(it, thisPower - otherPower)
        }
    )

public fun <V, MS> MultivariatePolynomialManipulationSpace<*, V, *, MS, *, *, *, *, *>.gcd(signature1: MS, signature2: MS): MS =
    if (signature1.size <= signature2.size) gcdInternalLogic(signature1, signature2)
    else gcdInternalLogic(signature2, signature1)

public fun <V, VP, MS, MutMS: MS> MultivariatePolynomialManipulationSpace<*, V, VP, MS, MutMS, *, *, *, *>.gcdInternalLogic(signature1: MS, signature2: MS): MS {
    val gcd = mutableSignatureOf()
    for ((v, power) in signature1) {
        if (signature2 containsVariable v) gcd[v] = min(power, signature2[v])
    }
    return gcd
}

public data class GcdWithDivisors<out MS>(
    val gcd: MS,
    val divisor1: MS,
    val divisor2: MS,
)

public fun <V, VP, MS, MutMS: MS> MultivariatePolynomialManipulationSpace<*, V, VP, MS, MutMS, *, *, *, *>.gcdWithDivisors(signature1: MS, signature2: MS): GcdWithDivisors<MS> {
    val gcd = gcd(signature1, signature2)

    val divisor1 = mutableSignatureOf()
    for ((v, power) in signature1) {
        val gcdPower = gcd[v]
        if (power > gcdPower) divisor1[v] = power - gcdPower
    }

    val divisor2 = mutableSignatureOf()
    for ((v, power) in signature2) {
        val gcdPower = gcd[v]
        if (power > gcdPower) divisor2[v] = power - gcdPower
    }

    return GcdWithDivisors(
        gcd = gcd,
        divisor1 = divisor1,
        divisor2 = divisor2,
    )
}

public fun <V, MS, MutMS: MS> MultivariatePolynomialManipulationSpace<*, V, *, MS, MutMS, *, *, *, *>.lcm(signature1: MS, signature2: MS): MS {
    val lcm = mutableSignatureOf()
    for (v in signature1.variables union signature2.variables) {
        lcm[v] = max(signature1[v], signature2[v])
    }
    return lcm
}

public data class LcmWithMultipliers<out MS>(
    val lcm: MS,
    val multiplier1: MS,
    val multiplier2: MS,
)

public fun <V, VP, MS, MutMS: MS> MultivariatePolynomialManipulationSpace<*, V, VP, MS, MutMS, *, *, *, *>.lcmWithMultipliers(signature1: MS, signature2: MS): LcmWithMultipliers<MS> {
    val lcm = lcm(signature1, signature2)

    val multiplier1 = mutableSignatureOf()
    for ((v, power) in signature1) {
        val lcmPower = lcm[v]
        if (lcmPower > power) multiplier1[v] = lcmPower - power
    }

    val multiplier2 = mutableSignatureOf()
    for ((v, power) in signature2) {
        val lcmPower = lcm[v]
        if (lcmPower > power) multiplier2[v] = lcmPower - power
    }

    return LcmWithMultipliers(
        lcm = lcm,
        multiplier1 = multiplier1,
        multiplier2 = multiplier2,
    )
}

public fun <MS, P> MultivariatePolynomialManipulationSpace<*, *, *, MS, *, *, P, *, *>.leadingSignature(order: Order<MS>, that: P): MS = that.signatures.maxWith(order.comparator)

public fun <C, MS, M, P> MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, *, *>.leadingTerm(order: Order<MS>, that: P): M = leadingSignature(order, that).let { monomial(it, that[it]) }

public fun <C, MS, P> MultivariatePolynomialManipulationSpace<C, *, *, MS, *, *, P, *, *>.leadingCoefficient(order: Order<MS>, that: P): C = leadingSignature(order, that).let { that[it] }

public fun <C, MS, M, P, MutP: P, A: Ring<C>> MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, MutP, A>.plusAssign(that: MutP, other: P) {
    for ((ms, c) in other) that[ms] = constantRing { that[ms] + c }
}

public fun <C, MS, M, P, MutP: P, A: Ring<C>> MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, MutP, A>.minusAssign(that: MutP, other: P) {
    for ((ms, c) in other) that[ms] = constantRing { that[ms] - c }
}

public fun <C, MS, M, P, MutP: P, A: Ring<C>> MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, MutP, A>.plusAssignProduct(that: MutP, multiplier1: P, multiplier2: P) {
    for ((ms1, c1) in multiplier1) for ((ms2, c2) in multiplier2) {
        val ms = times(ms1, ms2)
        that[ms] = constantRing { that[ms] + c1 * c2 }
    }
}

public fun <C, MS, M, P, MutP: P, A: Ring<C>> MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, MutP, A>.minusAssignProduct(that: MutP, multiplier1: P, multiplier2: P) {
    for ((ms1, c1) in multiplier1) for ((ms2, c2) in multiplier2) {
        val ms = times(ms1, ms2)
        that[ms] = with(constantRing) { that[ms] + c1 * c2 }
    }
}