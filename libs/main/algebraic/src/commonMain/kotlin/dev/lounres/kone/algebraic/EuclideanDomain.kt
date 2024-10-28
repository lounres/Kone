/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.ExperimentalKoneAPI
import kotlin.jvm.JvmInline


/**
 * Wrapper class for result of Euclidean division (a.k.a. division with remainder). See [EuclideanRing] for more.
 */
@ExperimentalKoneAPI
@JvmInline
public value class EuclideanDivisionResult<N>(public val quotient: N, public val remainder: N) {
    public operator fun component1(): N = quotient
    public operator fun component2(): N = remainder
}

/**
 * Describes a context that represents [Euclidean ring](https://en.wikipedia.org/wiki/Euclidean_domain).
 * It means that it extends [Ring] interface and besides ring's operations also provides Euclidean division
 * (a.k.a. division with remainder) that takes dividend and divisor and returns quotient and remainder,
 * where either remainder is zero or has less Euclidean norm.
 * See definition of [Euclidean ring on Wikipedia](https://en.wikipedia.org/wiki/Euclidean_domain)
 * for a more thorough description of the Euclidean division and the Euclidean norm.
 */
@ExperimentalKoneAPI
public interface EuclideanRing<N> : Ring<N> {
    /**
     * Returns result of Euclidean division (a.k.a. division with remainder), both quotient and remainder.
     */
    public infix fun N.divrem(other: N): EuclideanDivisionResult<N>
    /**
     * Returns quotient of Euclidean division (a.k.a. division with remainder).
     */
    public operator fun N.div(other: N): N = (this divrem other).quotient
    /**
     * Returns remainder of Euclidean division (a.k.a. division with remainder).
     */
    public operator fun N.rem(other: N): N = (this divrem other).remainder
}