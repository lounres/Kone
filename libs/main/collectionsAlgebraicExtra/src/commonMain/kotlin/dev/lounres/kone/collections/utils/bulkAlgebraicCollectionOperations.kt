/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.algebraic.Monoid
import dev.lounres.kone.algebraic.Semiring
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.KoneSequence


context(monoid: Monoid<E>)
public fun <E> KoneIterator<E>.sum(): E = fold(monoid.zero) { acc, e -> acc + e }

context(monoid: Monoid<E>)
public fun <E> KoneIterable<E>.sum(): E = fold(monoid.zero) { acc, e -> acc + e }

context(monoid: Monoid<E>)
public fun <E> KoneSequence<E>.sum(): E = fold(monoid.zero) { acc, e -> acc + e }

context(monoid: Monoid<N>)
public fun <E, N> KoneIterator<E>.sumOf(selector: (E) -> N): N = fold(monoid.zero) { acc, e -> acc + selector(e) }

context(monoid: Monoid<N>)
public fun <E, N> KoneIterable<E>.sumOf(selector: (E) -> N): N = fold(monoid.zero) { acc, e -> acc + selector(e) }

context(monoid: Monoid<N>)
public fun <E, N> KoneSequence<E>.sumOf(selector: (E) -> N): N = fold(monoid.zero) { acc, e -> acc + selector(e) }

context(monoid: Monoid<N>)
public inline fun <E, N> KoneIterator<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(monoid.zero) { index, acc, e -> acc + selector(index, e) }

context(monoid: Monoid<N>)
public inline fun <E, N> KoneIterable<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(monoid.zero) { index, acc, e -> acc + selector(index, e) }

context(monoid: Monoid<N>)
public inline fun <E, N> KoneSequence<E>.sumOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(monoid.zero) { index, acc, e -> acc + selector(index, e) }

context(ring: Semiring<E>)
public fun <E> KoneIterator<E>.product(): E = fold(ring.one) { acc, e -> acc * e }

context(ring: Semiring<E>)
public fun <E> KoneIterable<E>.product(): E = fold(ring.one) { acc, e -> acc * e }

context(ring: Semiring<E>)
public fun <E> KoneSequence<E>.product(): E = fold(ring.one) { acc, e -> acc * e }

context(ring: Semiring<N>)
public fun <E, N> KoneIterator<E>.productOf(selector: (E) -> N): N = fold(ring.one) { acc, e -> acc * selector(e) }

context(ring: Semiring<N>)
public fun <E, N> KoneIterable<E>.productOf(selector: (E) -> N): N = fold(ring.one) { acc, e -> acc * selector(e) }

context(ring: Semiring<N>)
public fun <E, N> KoneSequence<E>.productOf(selector: (E) -> N): N = fold(ring.one) { acc, e -> acc * selector(e) }

context(ring: Semiring<N>)
public inline fun <E, N> KoneIterator<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(ring.one) { index, acc, e -> acc * selector(index, e) }

context(ring: Semiring<N>)
public inline fun <E, N> KoneIterable<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(ring.one) { index, acc, e -> acc * selector(index, e) }

context(ring: Semiring<N>)
public inline fun <E, N> KoneSequence<E>.productOfIndexed(selector: (index: UInt, E) -> N): N = foldIndexed(ring.one) { index, acc, e -> acc * selector(index, e) }

// TODO: Add summing and multiplying extensions for primitives. Maybe.