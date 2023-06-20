/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.order.Order
import com.lounres.kone.util.mapOperations.mergeBy
import kotlin.math.max
import kotlin.math.min


public inline operator fun <
        C,
        P,
        A: Ring<C>,
        PS: PolynomialSpace<C, P, A>,
        R
        > PS.invoke(block: context(A, PS) () -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(ring, this)
}

public inline operator fun <
        C,
        P,
        RF: RationalFunction<C, P>,
        A: Ring<C>,
        PS: PolynomialSpace<C, P, A>,
        RFS: RationalFunctionSpace<C, P, RF, A, PS>,
        R
        > RFS.invoke(block: context(A, PS, RFS) () -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(ring, polynomialSpace, this)
}


context(MultivariatePolynomialManipulationContext<C, *, MS, P, *>, Order<MS>)
public val <C, MS, M, P> P.leadingTerm: M
    get() = asIterable().maxWithOrNull { e1, e2 -> e1.signature.compareTo(e2.signature) }
        .let { if (it != null) monomialOf(it.signature, it.coefficient) else monomialOf(signatureOf(), constantZero) }

context(MultivariatePolynomialManipulationContext<*, *, MS, M, P, *>, Order<MS>)
public val <MS, M, P> P.leadingSignature: MS
    get() = asIterable().maxWithOrNull { e1, e2 -> e1.signature.compareTo(e2.signature) }
        .let { if (it != null) it.signature else signatureOf() }

context(MultivariatePolynomialManipulationContext<C, *, MS, M, P, *>, Order<MS>)
public val <C, MS, M, P> P.leadingCoefficient: C
    get() = asIterable().maxWithOrNull { e1, e2 -> e1.signature.compareTo(e2.signature) }
        .let { if (it != null) it.coefficient else constantZero }

context(MultivariatePolynomialManipulationContext<C, V, MS, *, *, *>)
public fun <C, V, MS> gcd(signature1: MS, signature2: MS): MS =
    if (signature1.powersCount <= signature2.powersCount) gcdInternalLogic(signature1, signature2)
    else gcdInternalLogic(signature2, signature1)

context(MultivariatePolynomialManipulationContext<C, V, MS, *, *, *>)
public fun <C, V, MS> gcdInternalLogic(signature1: MS, signature2: MS): MS =
    signatureOf(signature1.asIterable().map { VariablePower(it.variable, min(it.degree, signature2[it.variable])) })

public data class GcdWithDivisors<MS>(
    val gcd: MS,
    val divisor1: MS,
    val divisor2: MS,
)

context(MultivariatePolynomialManipulationContext<C, V, MS, M, P, *>)
public fun <C, V, MS, M, P> gcdWithDivisors(signature1: MS, signature2: MS): GcdWithDivisors<MS> {
    val gcd = gcd(signature1, signature2)

    return GcdWithDivisors(
        gcd = gcd,
        divisor1 = signatureOf(signature1.asIterable().map { VariablePower(it.variable, it.degree - gcd[it.variable]) }),
        divisor2 = signatureOf(signature2.asIterable().map { VariablePower(it.variable, it.degree - gcd[it.variable]) }),
    )
}


context(MultivariatePolynomialManipulationContext<C, V, MS, *, *, *>)
public fun <C, V, MS> lcm(signature1: MS, signature2: MS): MS =
    signatureOf(mergeBy(signature1.powers, signature2.powers) { _, value1, value2 -> max(value1, value2) })

public data class LcmWithMultipliers<MS>(
    val lcm: MS,
    val multiplier1: MS,
    val multiplier2: MS,
)

context(MultivariatePolynomialManipulationContext <C, V, MS, *, *, *>)
public fun <C, V, MS> lcmWithMultipliers(signature1: MS, signature2: MS): LcmWithMultipliers<MS> {
    val lcm = lcm(signature1, signature2)

    return LcmWithMultipliers(
        lcm = lcm,
        multiplier1 = signatureOf(lcm.asIterable().map { VariablePower(it.variable, it.degree - signature1[it.variable]) }),
        multiplier2 = signatureOf(lcm.asIterable().map { VariablePower(it.variable, it.degree - signature2[it.variable]) }),
    )
}