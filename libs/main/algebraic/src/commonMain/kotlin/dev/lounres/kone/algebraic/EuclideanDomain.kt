/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import kotlin.jvm.JvmInline


/**
 * Wrapper class for result of Euclidean division (a.k.a. division with remainder). See [EuclideanRing] for more.
 */
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
 * See definition of [Euclidean domain on Wikipedia](https://en.wikipedia.org/wiki/Euclidean_domain)
 * for a more thorough description of the Euclidean division and the Euclidean norm.
 */
public interface EuclideanRing<N> : Ring<N> {
    // TODO: Docs
    public infix fun N.divrem(other: N): EuclideanDivisionResult<N>
    public operator fun N.div(other: N): N = (this divrem other).quotient
    public operator fun N.rem(other: N): N = (this divrem other).remainder
}