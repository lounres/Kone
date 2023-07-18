/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.manipulation

import com.lounres.kone.polynomial.manipulation.MultivariatePolynomialManipulationSpace.*
import kotlin.math.max
import kotlin.math.min


context(MultivariatePolynomialManipulationSpace<*, V, MS, *, *, *, *>)
public operator fun <V, MS> MS.plus(other: MS): MS =
    monomialSignatureOf((this.variables + other.variables).map { VariablePower(it, this.getOrDefault(it) + other.getOrDefault(it)) })

context(MultivariatePolynomialManipulationSpace<*, V, MS, *, *, *, *>)
public operator fun <V, MS> MS.minus(other: MS): MS =
    monomialSignatureOf(
        (this.variables + other.variables).map {
            val thisPower = this.getOrDefault(it)
            val otherPower = other.getOrDefault(it)
            require(thisPower >= otherPower)
            VariablePower(it, thisPower - otherPower)
        }
    )

context(MultivariatePolynomialManipulationSpace<*, V, MS, *, *, *, *>)
public fun <V, MS> gcd(that: MS, other: MS): MS =
    monomialSignatureOf((that.variables + other.variables).map { VariablePower(it, min(that.getOrDefault(it), other.getOrDefault(it))) })

context(MultivariatePolynomialManipulationSpace<*, V, MS, *, *, *, *>)
public fun <V, MS> lcm(that: MS, other: MS): MS =
    monomialSignatureOf((that.variables + other.variables).map { VariablePower(it, max(that.getOrDefault(it), other.getOrDefault(it))) })