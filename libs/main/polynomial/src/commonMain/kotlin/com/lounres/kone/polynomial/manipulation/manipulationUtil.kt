/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.order.Order
import com.lounres.kone.order.comparator
import kotlin.math.max
import kotlin.math.min


context(MultivariatePolynomialManipulationSpace<*, V, VP, MS, *, *, *, *, *>)
public infix fun <V, VP, MS> MS.divides(other: MS): Boolean {
    for ((v, power) in this) if (other[v] < power) return false
    return true
}

context(MultivariatePolynomialManipulationSpace<*, V, *, MS, *, *, *, *, *>)
public inline infix fun <V, MS> MS.isDivisibleBy(other: MS): Boolean = other divides this

context(MultivariatePolynomialManipulationSpace<*, V, VP, MS, *, *, *, *, *>)
@JvmName("timesSignatureSignature")
public operator fun <V, VP, MS> MS.times(other: MS): MS =
    signatureOf((this.variables + other.variables).map { variablePower(it, this[it] + other[it]) })

context(MultivariatePolynomialManipulationSpace<*, V, VP, MS, *, *, *, *, *>)
@JvmName("timesSignatureVariable")
public operator fun <V, VP, MS> MS.times(other: V): MS =
    signatureOf((this.variables + other).map { if (it == other) variablePower(it, this[it] + 1u) else variablePower(it, this[it]) })

context(MultivariatePolynomialManipulationSpace<*, V, VP, MS, *, *, *, *, *>)
public operator fun <V, VP, MS> MS.div(other: MS): MS =
    signatureOf(
        (this.variables + other.variables).map {
            val thisPower = this[it]
            val otherPower = other[it]
            require(thisPower >= otherPower)
            variablePower(it, thisPower - otherPower)
        }
    )

context(MultivariatePolynomialManipulationSpace<*, V, *, MS, *, *, *, *, *>)
public fun <V, MS> gcd(signature1: MS, signature2: MS): MS =
    if (signature1.size <= signature2.size) gcdInternalLogic(signature1, signature2)
    else gcdInternalLogic(signature2, signature1)

context(MultivariatePolynomialManipulationSpace<*, V, VP, MS, MutMS, *, *, *, *>)
public fun <V, VP, MS, MutMS: MS> gcdInternalLogic(signature1: MS, signature2: MS): MS {
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

context(MultivariatePolynomialManipulationSpace<*, V, VP, MS, MutMS, *, *, *, *>)
public fun <V, VP, MS, MutMS: MS> gcdWithDivisors(signature1: MS, signature2: MS): GcdWithDivisors<MS> {
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

context(MultivariatePolynomialManipulationSpace<*, V, *, MS, MutMS, *, *, *, *>)
public fun <V, MS, MutMS: MS> lcm(signature1: MS, signature2: MS): MS {
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

context(MultivariatePolynomialManipulationSpace<*, V, VP, MS, MutMS, *, *, *, *>)
public fun <V, VP, MS, MutMS: MS> lcmWithMultipliers(signature1: MS, signature2: MS): LcmWithMultipliers<MS> {
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

context(MultivariatePolynomialManipulationSpace<*, *, *, MS, *, *, P, *, *>, Order<MS>)
public val <MS, P> P.leadingSignature: MS get() = signatures.maxWith(comparator)

context(MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, *, *>, Order<MS>)
public val <C, MS, M, P> P.leadingTerm: M get() = leadingSignature.let { monomial(it, this[it]) }

context(MultivariatePolynomialManipulationSpace<C, *, *, MS, *, *, P, *, *>, Order<MS>)
public val <C, MS, P> P.leadingCoefficient: C get() = leadingSignature.let { this[it] }

context(A, MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, MutP, A>)
public operator fun <C, MS, M, P, MutP: P, A: Ring<C>> MutP.plusAssign(other: P) {
    for ((ms, c) in other) this[ms] = this[ms] + c
}

context(A, MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, MutP, A>)
public operator fun <C, MS, M, P, MutP: P, A: Ring<C>> MutP.minusAssign(other: P) {
    for ((ms, c) in other) this[ms] = this[ms] - c
}

context(A, MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, MutP, A>)
public fun <C, MS, M, P, MutP: P, A: Ring<C>> MutP.plusAssignProduct(multiplier1: P, multiplier2: P) {
    for ((ms1, c1) in multiplier1) for ((ms2, c2) in multiplier2) {
        val ms = ms1.times(ms2)
        this[ms] = this[ms] + c1 * c2
    }
}

context(/*A, */MultivariatePolynomialManipulationSpace<C, *, *, MS, *, M, P, MutP, A>)
public fun <C, MS, M, P, MutP: P, A: Ring<C>> MutP.minusAssignProduct(multiplier1: P, multiplier2: P) {
    for ((ms1, c1) in multiplier1) for ((ms2, c2) in multiplier2) {
        val ms = ms1.times(ms2)
        this[ms] = with(constantRing) { this@MutP[ms] + c1 * c2 }
    }
}