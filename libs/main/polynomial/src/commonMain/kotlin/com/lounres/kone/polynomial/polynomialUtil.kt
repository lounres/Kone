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

// TODO: Перенести в контекст
context(MultivariatePolynomialSpace<C, *, *, MS, M, P, *>, Order<MS>)
public val <C, MS, M, P> zeroMonomial: M
    get() = monomial(signature(), constantZero)

context(MultivariatePolynomialSpace<C, *, *, MS, M, P, *>, Order<MS>)
public val <C, MS, M, P> P.leadingTerm: M
    get() = asIterable().maxWithOrNull { e1, e2 -> e1.signature.compareTo(e2.signature) }
        .let { if (it != null) monomial(it.signature, it.coefficient) else monomial(signature(), constantZero) }

context(MultivariatePolynomialSpace<*, *, *, MS, M, P, *>, Order<MS>)
public val <MS, M, P> P.leadingSignature: MS
    get() = asIterable().maxWithOrNull { e1, e2 -> e1.signature.compareTo(e2.signature) }
        .let { if (it != null) it.signature else signature() }

context(MultivariatePolynomialSpace<C, *, *, MS, M, P, *>, Order<MS>)
public val <C, MS, M, P> P.leadingCoefficient: C
    get() = asIterable().maxWithOrNull { e1, e2 -> e1.signature.compareTo(e2.signature) }
        .let { if (it != null) it.coefficient else constantZero }

context(MultivariatePolynomialSpace<*, *, *, MS, *, *, *>)
public fun <MS> gcd(signature1: MS, signature2: MS): MS =
    if (signature1.powersCount <= signature2.powersCount) gcdInternalLogic(signature1, signature2)
    else gcdInternalLogic(signature2, signature1)

context(MultivariatePolynomialSpace<*, V, VP, MS, *, *, *>)
public fun <V, VP, MS> gcdInternalLogic(signature1: MS, signature2: MS): MS =
    signature(signature1.asIterable().map { variablePower(it.variable, min(it.power, signature2[it.variable])) })

public data class GcdWithDivisors<MS>(
    val gcd: MS,
    val divisor1: MS,
    val divisor2: MS,
)

context(MultivariatePolynomialSpace<*, V, VP, MS, *, *, *>)
public fun <VP, V, MS> gcdWithDivisors(signature1: MS, signature2: MS): GcdWithDivisors<MS> {
    val gcd = gcd(signature1, signature2)

    return GcdWithDivisors(
        gcd = gcd,
        divisor1 = signature(signature1.asIterable().map { variablePower(it.variable, it.power - gcd[it.variable]) }),
        divisor2 = signature(signature2.asIterable().map { variablePower(it.variable, it.power - gcd[it.variable]) }),
    )
}


context(MultivariatePolynomialSpace<*, V, *, MS, *, *, *>)
public fun <V, MS> lcm(signature1: MS, signature2: MS): MS =
    signature(mergeBy(signature1.powersMap, signature2.powersMap) { _, value1, value2 -> max(value1, value2) })

public data class LcmWithMultipliers<MS>(
    val lcm: MS,
    val multiplier1: MS,
    val multiplier2: MS,
)

context(MultivariatePolynomialSpace<*, V, VP, MS, *, *, *>)
public fun <VP, V, MS> lcmWithMultipliers(signature1: MS, signature2: MS): LcmWithMultipliers<MS> {
    val lcm = lcm(signature1, signature2)

    return LcmWithMultipliers(
        lcm = lcm,
        multiplier1 = signature(lcm.asIterable().map { variablePower(it.variable, it.power - signature1[it.variable]) }),
        multiplier2 = signature(lcm.asIterable().map { variablePower(it.variable, it.power - signature2[it.variable]) }),
    )
}