/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.ideals

import com.lounres.kone.annotations.ExperimentalKoneAPI
import kotlin.jvm.JvmInline


public data class VariablePower<out V>(val variable: V, val exponent: UInt)

@ExperimentalKoneAPI
public interface MonomialSignature<V>: Iterable<VariablePower<V>> {
    public operator fun get(monomialDescriptor: V): UInt
    public override operator fun iterator(): Iterator<VariablePower<V>>
}

@ExperimentalKoneAPI
public data class Monomial<out C, out MS>(val monomialSignature: MS, val coefficient: C)

@ExperimentalKoneAPI
public interface Polynomial<out C, MS>: Iterable<Monomial<C, MS>> {
    public operator fun get(monomialDescriptor: MS): C
    public override operator fun iterator(): Iterator<Monomial<C, MS>>
}

@ExperimentalKoneAPI
@JvmInline
public value class Ideal<P>(private val basis: Array<P>) {
    public operator fun get(index: Int): P = basis[index]
    public val size: Int get() = basis.size
    public operator fun iterator(): Iterator<P> = basis.iterator()
}


@ExperimentalKoneAPI
public fun <C, MS, P: Polynomial<C, MS>> Ideal<P>.grobnerBasisByBuchbergersAlgorithm(monomialOrder: Comparator<MS>): List<P> {
    TODO()
}