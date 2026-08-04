/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.array

import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterator.getAndMoveNext


// region General arrays

/**
 * Returns a [KoneMutableArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <reified Element> KoneMutableArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneMutableArray<Element> =
    KoneMutableArray(Array(size.toInt()) { initializer(it.toUInt()) })

public inline fun <reified Element> KoneMutableArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneMutableArray<Element> =
    if (indices.first > indices.last) KoneMutableArray(emptyArray())
    else KoneMutableArray(Array((indices.last - indices.first + 1u).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <reified Element> KoneArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneArray<Element> =
    KoneArray(Array(size.toInt()) { initializer(it.toUInt()) })

public inline fun <reified Element> KoneArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneArray<Element> =
    if (indices.first > indices.last) KoneArray(emptyArray())
    else KoneArray(Array((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun <reified Element> KoneMutableArray.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneMutableArray<Element> =
    if (size == 0u) KoneMutableArray(emptyArray())
    else {
        var current = initialElement
        KoneMutableArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun <reified Element> KoneMutableArray.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneMutableArray<Element> =
    if (indices.first > indices.last) KoneMutableArray(emptyArray())
    else {
        var current = initialElement
        KoneMutableArray.generate(indices.last - indices.first + 1u) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun <reified Element> KoneArray.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArray<Element> =
    if (size == 0u) KoneArray(emptyArray())
    else {
        var current = initialElement
        KoneArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun <reified Element> KoneArray.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneArray<Element> =
    if (indices.first > indices.last) KoneArray(emptyArray())
    else {
        var current = initialElement
        KoneArray.generate(indices.last - indices.first + 1u) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun <reified Element> KoneMutableArray.Companion.fill(size: UInt, element: Element): KoneMutableArray<Element> =
    KoneMutableArray.generate(size) { element }

public inline fun <reified Element> KoneMutableArray.Companion.fill(indices: UIntRange, element: Element): KoneMutableArray<Element> =
    KoneMutableArray.generate(indices) { element }

public inline fun <reified Element> KoneArray.Companion.fill(size: UInt, element: Element): KoneArray<Element> =
    KoneArray.generate(size) { element }

public inline fun <reified Element> KoneArray.Companion.fill(indices: UIntRange, element: Element): KoneArray<Element> =
    KoneArray.generate(indices) { element }

public inline fun <reified Element> KoneMutableArray.Companion.empty(): KoneMutableArray<Element> = KoneMutableArray(emptyArray())

public inline fun <reified Element> KoneArray.Companion.empty(): KoneArray<Element> = KoneArray(emptyArray())

public inline fun <reified Element> KoneMutableArray.Companion.of(vararg elements: Element): KoneMutableArray<Element> =
    KoneMutableArray(elements as Array<Element>)

public inline fun <reified Element> KoneArray.Companion.of(vararg elements: Element): KoneArray<Element> =
    KoneArray(elements as Array<Element>)

public inline fun <reified Element> KoneIterable<Element>.toKoneMutableArray(): KoneMutableArray<Element> {
    val iterator = iterator()
    return KoneMutableArray.generate(size) { iterator.getAndMoveNext() }
}

public inline fun <reified Element> KoneIterable<Element>.toKoneArray(): KoneArray<Element> {
    val iterator = iterator()
    return KoneArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region Boolean

/**
 * Returns a [KoneMutableBooleanArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableBooleanArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Boolean): KoneMutableBooleanArray =
    KoneMutableBooleanArray(BooleanArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableBooleanArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Boolean): KoneMutableBooleanArray =
    if (indices.first > indices.last) KoneMutableBooleanArray(BooleanArray(0))
    else KoneMutableBooleanArray(BooleanArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneBooleanArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneBooleanArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Boolean): KoneBooleanArray =
    KoneBooleanArray(BooleanArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneBooleanArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Boolean): KoneBooleanArray =
    if (indices.first > indices.last) KoneBooleanArray(BooleanArray(0))
    else KoneBooleanArray(BooleanArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableBooleanArray.Companion.induce(size: UInt, initialElement: Boolean, inducer: (index: UInt, previous: Boolean) -> Boolean): KoneMutableBooleanArray =
    if (size == 0u) KoneMutableBooleanArray(BooleanArray(0))
    else {
        var current = initialElement
        KoneMutableBooleanArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableBooleanArray.Companion.induce(indices: UIntRange, initialElement: Boolean, inducer: (index: UInt, previous: Boolean) -> Boolean): KoneMutableBooleanArray =
    if (indices.isEmpty()) KoneMutableBooleanArray(BooleanArray(0))
    else {
        var current = initialElement
        KoneMutableBooleanArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneBooleanArray.Companion.induce(size: UInt, initialElement: Boolean, inducer: (index: UInt, previous: Boolean) -> Boolean): KoneBooleanArray =
    if (size == 0u) KoneBooleanArray(BooleanArray(0))
    else {
        var current = initialElement
        KoneBooleanArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneBooleanArray.Companion.induce(indices: UIntRange, initialElement: Boolean, inducer: (index: UInt, previous: Boolean) -> Boolean): KoneBooleanArray =
    if (indices.isEmpty()) KoneBooleanArray(BooleanArray(0))
    else {
        var current = initialElement
        KoneBooleanArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneBooleanArray] of provided [size] of `false`s.
 */
public fun KoneBooleanArray.Companion.fill(size: UInt, element: Boolean = false): KoneBooleanArray =
    KoneBooleanArray(BooleanArray(size.toInt()) { element })

public fun KoneBooleanArray.Companion.fill(indices: UIntRange, element: Boolean = false): KoneBooleanArray =
    KoneBooleanArray(BooleanArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneMutableBooleanArray] of provided [size] of `false`s.
 */
public fun KoneMutableBooleanArray.Companion.fill(size: UInt, element: Boolean = false): KoneMutableBooleanArray =
    KoneMutableBooleanArray(BooleanArray(size.toInt()) { element })

public fun KoneMutableBooleanArray.Companion.fill(indices: UIntRange, element: Boolean = false): KoneMutableBooleanArray =
    KoneMutableBooleanArray(BooleanArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableBooleanArray.Companion.empty(): KoneMutableBooleanArray = KoneMutableBooleanArray(BooleanArray(0))

public fun KoneBooleanArray.Companion.empty(): KoneBooleanArray = KoneBooleanArray(BooleanArray(0))

public fun KoneMutableBooleanArray.Companion.of(vararg elements: Boolean): KoneMutableBooleanArray =
    KoneMutableBooleanArray(elements)

public fun KoneBooleanArray.Companion.of(vararg elements: Boolean): KoneBooleanArray =
    KoneBooleanArray(elements)

public fun KoneIterable<Boolean>.toKoneMutableBooleanArray(): KoneMutableBooleanArray {
    val iterator = iterator()
    return KoneMutableBooleanArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Boolean>.toKoneBooleanArray(): KoneBooleanArray {
    val iterator = iterator()
    return KoneBooleanArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region Char

/**
 * Returns a [KoneMutableCharArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableCharArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Char): KoneMutableCharArray =
    KoneMutableCharArray(CharArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableCharArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Char): KoneMutableCharArray =
    if (indices.first > indices.last) KoneMutableCharArray(CharArray(0))
    else KoneMutableCharArray(CharArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneCharArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneCharArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Char): KoneCharArray =
    KoneCharArray(CharArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneCharArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Char): KoneCharArray =
    if (indices.first > indices.last) KoneCharArray(CharArray(0))
    else KoneCharArray(CharArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableCharArray.Companion.induce(size: UInt, initialElement: Char, inducer: (index: UInt, previous: Char) -> Char): KoneMutableCharArray =
    if (size == 0u) KoneMutableCharArray(CharArray(0))
    else {
        var current = initialElement
        KoneMutableCharArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableCharArray.Companion.induce(indices: UIntRange, initialElement: Char, inducer: (index: UInt, previous: Char) -> Char): KoneMutableCharArray =
    if (indices.isEmpty()) KoneMutableCharArray(CharArray(0))
    else {
        var current = initialElement
        KoneMutableCharArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneCharArray.Companion.induce(size: UInt, initialElement: Char, inducer: (index: UInt, previous: Char) -> Char): KoneCharArray =
    if (size == 0u) KoneCharArray(CharArray(0))
    else {
        var current = initialElement
        KoneCharArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneCharArray.Companion.induce(indices: UIntRange, initialElement: Char, inducer: (index: UInt, previous: Char) -> Char): KoneCharArray =
    if (indices.isEmpty()) KoneCharArray(CharArray(0))
    else {
        var current = initialElement
        KoneCharArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableCharArray] of provided [size] of `false`s.
 */
public fun KoneMutableCharArray.Companion.fill(size: UInt, element: Char = '\u0000'): KoneMutableCharArray =
    KoneMutableCharArray(CharArray(size.toInt()) { element })

public fun KoneMutableCharArray.Companion.fill(indices: UIntRange, element: Char = '\u0000'): KoneMutableCharArray =
    KoneMutableCharArray(CharArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneCharArray] of provided [size] of `false`s.
 */
public fun KoneCharArray.Companion.fill(size: UInt, element: Char = '\u0000'): KoneCharArray =
    KoneCharArray(CharArray(size.toInt()) { element })

public fun KoneCharArray.Companion.fill(indices: UIntRange, element: Char = '\u0000'): KoneCharArray =
    KoneCharArray(CharArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableCharArray.Companion.empty(): KoneMutableCharArray = KoneMutableCharArray(CharArray(0))

public fun KoneCharArray.Companion.empty(): KoneCharArray = KoneCharArray(CharArray(0))

public fun KoneMutableCharArray.Companion.of(vararg elements: Char): KoneMutableCharArray =
    KoneMutableCharArray(elements)

public fun KoneCharArray.Companion.of(vararg elements: Char): KoneCharArray =
    KoneCharArray(elements)

public fun KoneIterable<Char>.toKoneMutableCharArray(): KoneMutableCharArray {
    val iterator = iterator()
    return KoneMutableCharArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Char>.toKoneCharArray(): KoneCharArray {
    val iterator = iterator()
    return KoneCharArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region Byte

/**
 * Returns a [KoneMutableByteArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableByteArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Byte): KoneMutableByteArray =
    KoneMutableByteArray(ByteArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableByteArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Byte): KoneMutableByteArray =
    if (indices.first > indices.last) KoneMutableByteArray(ByteArray(0))
    else KoneMutableByteArray(ByteArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneByteArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneByteArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Byte): KoneByteArray =
    KoneByteArray(ByteArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneByteArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Byte): KoneByteArray =
    if (indices.first > indices.last) KoneByteArray(ByteArray(0))
    else KoneByteArray(ByteArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableByteArray.Companion.induce(size: UInt, initialElement: Byte, inducer: (index: UInt, previous: Byte) -> Byte): KoneMutableByteArray =
    if (size == 0u) KoneMutableByteArray(ByteArray(0))
    else {
        var current = initialElement
        KoneMutableByteArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableByteArray.Companion.induce(indices: UIntRange, initialElement: Byte, inducer: (index: UInt, previous: Byte) -> Byte): KoneMutableByteArray =
    if (indices.isEmpty()) KoneMutableByteArray(ByteArray(0))
    else {
        var current = initialElement
        KoneMutableByteArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneByteArray.Companion.induce(size: UInt, initialElement: Byte, inducer: (index: UInt, previous: Byte) -> Byte): KoneByteArray =
    if (size == 0u) KoneByteArray(ByteArray(0))
    else {
        var current = initialElement
        KoneByteArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneByteArray.Companion.induce(indices: UIntRange, initialElement: Byte, inducer: (index: UInt, previous: Byte) -> Byte): KoneByteArray =
    if (indices.isEmpty()) KoneByteArray(ByteArray(0))
    else {
        var current = initialElement
        KoneByteArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableByteArray] of provided [size] of zeros.
 */
public fun KoneMutableByteArray.Companion.fill(size: UInt, element: Byte = 0): KoneMutableByteArray =
    KoneMutableByteArray(ByteArray(size.toInt()) { element })

public fun KoneMutableByteArray.Companion.fill(indices: UIntRange, element: Byte = 0): KoneMutableByteArray =
    KoneMutableByteArray(ByteArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneByteArray] of provided [size] of zeros.
 */
public fun KoneByteArray.Companion.fill(size: UInt, element: Byte = 0): KoneByteArray =
    KoneByteArray(ByteArray(size.toInt()) { element })

public fun KoneByteArray.Companion.fill(indices: UIntRange, element: Byte = 0): KoneByteArray =
    KoneByteArray(ByteArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableByteArray.Companion.empty(): KoneMutableByteArray = KoneMutableByteArray(ByteArray(0))

public fun KoneByteArray.Companion.empty(): KoneByteArray = KoneByteArray(ByteArray(0))

public fun KoneMutableByteArray.Companion.of(vararg elements: Byte): KoneMutableByteArray =
    KoneMutableByteArray(elements)

public fun KoneByteArray.Companion.of(vararg elements: Byte): KoneByteArray =
    KoneByteArray(elements)

public fun KoneIterable<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val iterator = iterator()
    return KoneMutableByteArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Byte>.toKoneByteArray(): KoneByteArray {
    val iterator = iterator()
    return KoneByteArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region Short

/**
 * Returns a [KoneMutableShortArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableShortArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Short): KoneMutableShortArray =
    KoneMutableShortArray(ShortArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableShortArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Short): KoneMutableShortArray =
    if (indices.first > indices.last) KoneMutableShortArray(ShortArray(0))
    else KoneMutableShortArray(ShortArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneShortArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneShortArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Short): KoneShortArray =
    KoneShortArray(ShortArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneShortArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Short): KoneShortArray =
    if (indices.first > indices.last) KoneShortArray(ShortArray(0))
    else KoneShortArray(ShortArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableShortArray.Companion.induce(size: UInt, initialElement: Short, inducer: (index: UInt, previous: Short) -> Short): KoneMutableShortArray =
    if (size == 0u) KoneMutableShortArray(ShortArray(0))
    else {
        var current = initialElement
        KoneMutableShortArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableShortArray.Companion.induce(indices: UIntRange, initialElement: Short, inducer: (index: UInt, previous: Short) -> Short): KoneMutableShortArray =
    if (indices.isEmpty()) KoneMutableShortArray(ShortArray(0))
    else {
        var current = initialElement
        KoneMutableShortArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneShortArray.Companion.induce(size: UInt, initialElement: Short, inducer: (index: UInt, previous: Short) -> Short): KoneShortArray =
    if (size == 0u) KoneShortArray(ShortArray(0))
    else {
        var current = initialElement
        KoneShortArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneShortArray.Companion.induce(indices: UIntRange, initialElement: Short, inducer: (index: UInt, previous: Short) -> Short): KoneShortArray =
    if (indices.isEmpty()) KoneShortArray(ShortArray(0))
    else {
        var current = initialElement
        KoneShortArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableShortArray] of provided [size] of zeros.
 */
public fun KoneMutableShortArray.Companion.fill(size: UInt, element: Short = 0): KoneMutableShortArray =
    KoneMutableShortArray(ShortArray(size.toInt()) { element })

public fun KoneMutableShortArray.Companion.fill(indices: UIntRange, element: Short = 0): KoneMutableShortArray =
    KoneMutableShortArray(ShortArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneShortArray] of provided [size] of zeros.
 */
public fun KoneShortArray.Companion.fill(size: UInt, element: Short = 0): KoneShortArray =
    KoneShortArray(ShortArray(size.toInt()) { element })

public fun KoneShortArray.Companion.fill(indices: UIntRange, element: Short = 0): KoneShortArray =
    KoneShortArray(ShortArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableShortArray.Companion.empty(): KoneMutableShortArray = KoneMutableShortArray(ShortArray(0))

public fun KoneShortArray.Companion.empty(): KoneShortArray = KoneShortArray(ShortArray(0))

public fun KoneMutableShortArray.Companion.of(vararg elements: Short): KoneMutableShortArray =
    KoneMutableShortArray(elements)

public fun KoneShortArray.Companion.of(vararg elements: Short): KoneShortArray =
    KoneShortArray(elements)

public fun KoneIterable<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val iterator = iterator()
    return KoneMutableShortArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Short>.toKoneShortArray(): KoneShortArray {
    val iterator = iterator()
    return KoneShortArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region Int

/**
 * Returns a [KoneMutableIntArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableIntArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Int): KoneMutableIntArray =
    KoneMutableIntArray(IntArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableIntArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Int): KoneMutableIntArray =
    if (indices.first > indices.last) KoneMutableIntArray(IntArray(0))
    else KoneMutableIntArray(IntArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneIntArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneIntArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Int): KoneIntArray =
    KoneIntArray(IntArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneIntArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Int): KoneIntArray =
    if (indices.first > indices.last) KoneIntArray(IntArray(0))
    else KoneIntArray(IntArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableIntArray.Companion.induce(size: UInt, initialElement: Int, inducer: (index: UInt, previous: Int) -> Int): KoneMutableIntArray =
    if (size == 0u) KoneMutableIntArray(IntArray(0))
    else {
        var current = initialElement
        KoneMutableIntArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableIntArray.Companion.induce(indices: UIntRange, initialElement: Int, inducer: (index: UInt, previous: Int) -> Int): KoneMutableIntArray =
    if (indices.isEmpty()) KoneMutableIntArray(IntArray(0))
    else {
        var current = initialElement
        KoneMutableIntArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneIntArray.Companion.induce(size: UInt, initialElement: Int, inducer: (index: UInt, previous: Int) -> Int): KoneIntArray =
    if (size == 0u) KoneIntArray(IntArray(0))
    else {
        var current = initialElement
        KoneIntArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneIntArray.Companion.induce(indices: UIntRange, initialElement: Int, inducer: (index: UInt, previous: Int) -> Int): KoneIntArray =
    if (indices.isEmpty()) KoneIntArray(IntArray(0))
    else {
        var current = initialElement
        KoneIntArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableIntArray] of provided [size] of zeros.
 */
public fun KoneMutableIntArray.Companion.fill(size: UInt, element: Int = 0): KoneMutableIntArray =
    KoneMutableIntArray(IntArray(size.toInt()) { element })

public fun KoneMutableIntArray.Companion.fill(indices: UIntRange, element: Int = 0): KoneMutableIntArray =
    KoneMutableIntArray(IntArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneIntArray] of provided [size] of zeros.
 */
public fun KoneIntArray.Companion.fill(size: UInt, element: Int = 0): KoneIntArray =
    KoneIntArray(IntArray(size.toInt()) { element })

public fun KoneIntArray.Companion.fill(indices: UIntRange, element: Int = 0): KoneIntArray =
    KoneIntArray(IntArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableIntArray.Companion.empty(): KoneMutableIntArray = KoneMutableIntArray(IntArray(0))

public fun KoneIntArray.Companion.empty(): KoneIntArray = KoneIntArray(IntArray(0))

public fun KoneMutableIntArray.Companion.of(vararg elements: Int): KoneMutableIntArray =
    KoneMutableIntArray(elements)

public fun KoneIntArray.Companion.of(vararg elements: Int): KoneIntArray =
    KoneIntArray(elements)

public fun KoneIterable<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val iterator = iterator()
    return KoneMutableIntArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Int>.toKoneIntArray(): KoneIntArray {
    val iterator = iterator()
    return KoneIntArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region Long

/**
 * Returns a [KoneMutableLongArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableLongArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Long): KoneMutableLongArray =
    KoneMutableLongArray(LongArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableLongArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Long): KoneMutableLongArray =
    if (indices.first > indices.last) KoneMutableLongArray(LongArray(0))
    else KoneMutableLongArray(LongArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneLongArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneLongArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Long): KoneLongArray =
    KoneLongArray(LongArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneLongArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Long): KoneLongArray =
    if (indices.first > indices.last) KoneLongArray(LongArray(0))
    else KoneLongArray(LongArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableLongArray.Companion.induce(size: UInt, initialElement: Long, inducer: (index: UInt, previous: Long) -> Long): KoneMutableLongArray =
    if (size == 0u) KoneMutableLongArray(LongArray(0))
    else {
        var current = initialElement
        KoneMutableLongArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableLongArray.Companion.induce(indices: UIntRange, initialElement: Long, inducer: (index: UInt, previous: Long) -> Long): KoneMutableLongArray =
    if (indices.isEmpty()) KoneMutableLongArray(LongArray(0))
    else {
        var current = initialElement
        KoneMutableLongArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneLongArray.Companion.induce(size: UInt, initialElement: Long, inducer: (index: UInt, previous: Long) -> Long): KoneLongArray =
    if (size == 0u) KoneLongArray(LongArray(0))
    else {
        var current = initialElement
        KoneLongArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneLongArray.Companion.induce(indices: UIntRange, initialElement: Long, inducer: (index: UInt, previous: Long) -> Long): KoneLongArray =
    if (indices.isEmpty()) KoneLongArray(LongArray(0))
    else {
        var current = initialElement
        KoneLongArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableLongArray] of provided [size] of zeros.
 */
public fun KoneMutableLongArray.Companion.fill(size: UInt, element: Long = 0): KoneMutableLongArray =
    KoneMutableLongArray(LongArray(size.toInt()) { element })

public fun KoneMutableLongArray.Companion.fill(indices: UIntRange, element: Long = 0): KoneMutableLongArray =
    KoneMutableLongArray(LongArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneLongArray] of provided [size] of zeros.
 */
public fun KoneLongArray.Companion.fill(size: UInt, element: Long = 0): KoneLongArray =
    KoneLongArray(LongArray(size.toInt()) { element })

public fun KoneLongArray.Companion.fill(indices: UIntRange, element: Long = 0): KoneLongArray =
    KoneLongArray(LongArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableLongArray.Companion.empty(): KoneMutableLongArray = KoneMutableLongArray(LongArray(0))

public fun KoneLongArray.Companion.empty(): KoneLongArray = KoneLongArray(LongArray(0))

public fun KoneMutableLongArray.Companion.of(vararg elements: Long): KoneMutableLongArray =
    KoneMutableLongArray(elements)

public fun KoneLongArray.Companion.of(vararg elements: Long): KoneLongArray =
    KoneLongArray(elements)

public fun KoneIterable<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val iterator = iterator()
    return KoneMutableLongArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Long>.toKoneLongArray(): KoneLongArray {
    val iterator = iterator()
    return KoneLongArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region Float

/**
 * Returns a [KoneMutableFloatArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableFloatArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Float): KoneMutableFloatArray =
    KoneMutableFloatArray(FloatArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableFloatArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Float): KoneMutableFloatArray =
    if (indices.first > indices.last) KoneMutableFloatArray(FloatArray(0))
    else KoneMutableFloatArray(FloatArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneFloatArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneFloatArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Float): KoneFloatArray =
    KoneFloatArray(FloatArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneFloatArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Float): KoneFloatArray =
    if (indices.first > indices.last) KoneFloatArray(FloatArray(0))
    else KoneFloatArray(FloatArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableFloatArray.Companion.induce(size: UInt, initialElement: Float, inducer: (index: UInt, previous: Float) -> Float): KoneMutableFloatArray =
    if (size == 0u) KoneMutableFloatArray(FloatArray(0))
    else {
        var current = initialElement
        KoneMutableFloatArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableFloatArray.Companion.induce(indices: UIntRange, initialElement: Float, inducer: (index: UInt, previous: Float) -> Float): KoneMutableFloatArray =
    if (indices.isEmpty()) KoneMutableFloatArray(FloatArray(0))
    else {
        var current = initialElement
        KoneMutableFloatArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneFloatArray.Companion.induce(size: UInt, initialElement: Float, inducer: (index: UInt, previous: Float) -> Float): KoneFloatArray =
    if (size == 0u) KoneFloatArray(FloatArray(0))
    else {
        var current = initialElement
        KoneFloatArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneFloatArray.Companion.induce(indices: UIntRange, initialElement: Float, inducer: (index: UInt, previous: Float) -> Float): KoneFloatArray =
    if (indices.isEmpty()) KoneFloatArray(FloatArray(0))
    else {
        var current = initialElement
        KoneFloatArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableFloatArray] of provided [size] of zeros.
 */
public fun KoneMutableFloatArray.Companion.fill(size: UInt, element: Float = 0.0f): KoneMutableFloatArray =
    KoneMutableFloatArray(FloatArray(size.toInt()) { element })

public fun KoneMutableFloatArray.Companion.fill(indices: UIntRange, element: Float = 0.0f): KoneMutableFloatArray =
    KoneMutableFloatArray(FloatArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneFloatArray] of provided [size] of zeros.
 */
public fun KoneFloatArray.Companion.fill(size: UInt, element: Float = 0.0f): KoneFloatArray =
    KoneFloatArray(FloatArray(size.toInt()) { element })

public fun KoneFloatArray.Companion.fill(indices: UIntRange, element: Float = 0.0f): KoneFloatArray =
    KoneFloatArray(FloatArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableFloatArray.Companion.empty(): KoneMutableFloatArray = KoneMutableFloatArray(FloatArray(0))

public fun KoneFloatArray.Companion.empty(): KoneFloatArray = KoneFloatArray(FloatArray(0))

public fun KoneMutableFloatArray.Companion.of(vararg elements: Float): KoneMutableFloatArray =
    KoneMutableFloatArray(elements)

public fun KoneFloatArray.Companion.of(vararg elements: Float): KoneFloatArray =
    KoneFloatArray(elements)

public fun KoneIterable<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val iterator = iterator()
    return KoneMutableFloatArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Float>.toKoneFloatArray(): KoneFloatArray {
    val iterator = iterator()
    return KoneFloatArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region Double

/**
 * Returns a [KoneMutableDoubleArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableDoubleArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Double): KoneMutableDoubleArray =
    KoneMutableDoubleArray(DoubleArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableDoubleArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Double): KoneMutableDoubleArray =
    if (indices.first > indices.last) KoneMutableDoubleArray(DoubleArray(0))
    else KoneMutableDoubleArray(DoubleArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneDoubleArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneDoubleArray.Companion.generate(size: UInt, initializer: (index: UInt) -> Double): KoneDoubleArray =
    KoneDoubleArray(DoubleArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneDoubleArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Double): KoneDoubleArray =
    if (indices.first > indices.last) KoneDoubleArray(DoubleArray(0))
    else KoneDoubleArray(DoubleArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableDoubleArray.Companion.induce(size: UInt, initialElement: Double, inducer: (index: UInt, previous: Double) -> Double): KoneMutableDoubleArray =
    if (size == 0u) KoneMutableDoubleArray(DoubleArray(0))
    else {
        var current = initialElement
        KoneMutableDoubleArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableDoubleArray.Companion.induce(indices: UIntRange, initialElement: Double, inducer: (index: UInt, previous: Double) -> Double): KoneMutableDoubleArray =
    if (indices.isEmpty()) KoneMutableDoubleArray(DoubleArray(0))
    else {
        var current = initialElement
        KoneMutableDoubleArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneDoubleArray.Companion.induce(size: UInt, initialElement: Double, inducer: (index: UInt, previous: Double) -> Double): KoneDoubleArray =
    if (size == 0u) KoneDoubleArray(DoubleArray(0))
    else {
        var current = initialElement
        KoneDoubleArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneDoubleArray.Companion.induce(indices: UIntRange, initialElement: Double, inducer: (index: UInt, previous: Double) -> Double): KoneDoubleArray =
    if (indices.isEmpty()) KoneDoubleArray(DoubleArray(0))
    else {
        var current = initialElement
        KoneDoubleArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableDoubleArray] of provided [size] of zeros.
 */
public fun KoneMutableDoubleArray.Companion.fill(size: UInt, element: Double = 0.0): KoneMutableDoubleArray =
    KoneMutableDoubleArray(DoubleArray(size.toInt()) { element })

public fun KoneMutableDoubleArray.Companion.fill(indices: UIntRange, element: Double = 0.0): KoneMutableDoubleArray =
    KoneMutableDoubleArray(DoubleArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneDoubleArray] of provided [size] of zeros.
 */
public fun KoneDoubleArray.Companion.fill(size: UInt, element: Double = 0.0): KoneDoubleArray =
    KoneDoubleArray(DoubleArray(size.toInt()) { element })

public fun KoneDoubleArray.Companion.fill(indices: UIntRange, element: Double = 0.0): KoneDoubleArray =
    KoneDoubleArray(DoubleArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableDoubleArray.Companion.empty(): KoneMutableDoubleArray = KoneMutableDoubleArray(DoubleArray(0))

public fun KoneDoubleArray.Companion.empty(): KoneDoubleArray = KoneDoubleArray(DoubleArray(0))

public fun KoneMutableDoubleArray.Companion.of(vararg elements: Double): KoneMutableDoubleArray =
    KoneMutableDoubleArray(elements)

public fun KoneDoubleArray.Companion.of(vararg elements: Double): KoneDoubleArray =
    KoneDoubleArray(elements)

public fun KoneIterable<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val iterator = iterator()
    return KoneMutableDoubleArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<Double>.toKoneDoubleArray(): KoneDoubleArray {
    val iterator = iterator()
    return KoneDoubleArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region UByte

/**
 * Returns a [KoneMutableUByteArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableUByteArray.Companion.generate(size: UInt, initializer: (index: UInt) -> UByte): KoneMutableUByteArray =
    KoneMutableUByteArray(UByteArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableUByteArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> UByte): KoneMutableUByteArray =
    if (indices.first > indices.last) KoneMutableUByteArray(UByteArray(0))
    else KoneMutableUByteArray(UByteArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneUByteArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneUByteArray.Companion.generate(size: UInt, initializer: (index: UInt) -> UByte): KoneUByteArray =
    KoneUByteArray(UByteArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneUByteArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> UByte): KoneUByteArray =
    if (indices.first > indices.last) KoneUByteArray(UByteArray(0))
    else KoneUByteArray(UByteArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableUByteArray.Companion.induce(size: UInt, initialElement: UByte, inducer: (index: UInt, previous: UByte) -> UByte): KoneMutableUByteArray =
    if (size == 0u) KoneMutableUByteArray(UByteArray(0))
    else {
        var current = initialElement
        KoneMutableUByteArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableUByteArray.Companion.induce(indices: UIntRange, initialElement: UByte, inducer: (index: UInt, previous: UByte) -> UByte): KoneMutableUByteArray =
    if (indices.isEmpty()) KoneMutableUByteArray(UByteArray(0))
    else {
        var current = initialElement
        KoneMutableUByteArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneUByteArray.Companion.induce(size: UInt, initialElement: UByte, inducer: (index: UInt, previous: UByte) -> UByte): KoneUByteArray =
    if (size == 0u) KoneUByteArray(UByteArray(0))
    else {
        var current = initialElement
        KoneUByteArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneUByteArray.Companion.induce(indices: UIntRange, initialElement: UByte, inducer: (index: UInt, previous: UByte) -> UByte): KoneUByteArray =
    if (indices.isEmpty()) KoneUByteArray(UByteArray(0))
    else {
        var current = initialElement
        KoneUByteArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableUByteArray] of provided [size] of zeros.
 */
public fun KoneMutableUByteArray.Companion.fill(size: UInt, element: UByte = 0u): KoneMutableUByteArray =
    KoneMutableUByteArray(UByteArray(size.toInt()) { element })

public fun KoneMutableUByteArray.Companion.fill(indices: UIntRange, element: UByte = 0u): KoneMutableUByteArray =
    KoneMutableUByteArray(UByteArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneUByteArray] of provided [size] of zeros.
 */
public fun KoneUByteArray.Companion.fill(size: UInt, element: UByte = 0u): KoneUByteArray =
    KoneUByteArray(UByteArray(size.toInt()) { element })

public fun KoneUByteArray.Companion.fill(indices: UIntRange, element: UByte = 0u): KoneUByteArray =
    KoneUByteArray(UByteArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableUByteArray.Companion.empty(): KoneMutableUByteArray = KoneMutableUByteArray(UByteArray(0))

public fun KoneUByteArray.Companion.empty(): KoneUByteArray = KoneUByteArray(UByteArray(0))

public fun KoneMutableUByteArray.Companion.of(vararg elements: UByte): KoneMutableUByteArray =
    KoneMutableUByteArray(elements)

public fun KoneUByteArray.Companion.of(vararg elements: UByte): KoneUByteArray =
    KoneUByteArray(elements)

public fun KoneIterable<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val iterator = iterator()
    return KoneMutableUByteArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<UByte>.toKoneUByteArray(): KoneUByteArray {
    val iterator = iterator()
    return KoneUByteArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region UShort

/**
 * Returns a [KoneMutableUShortArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableUShortArray.Companion.generate(size: UInt, initializer: (index: UInt) -> UShort): KoneMutableUShortArray =
    KoneMutableUShortArray(UShortArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableUShortArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> UShort): KoneMutableUShortArray =
    if (indices.first > indices.last) KoneMutableUShortArray(UShortArray(0))
    else KoneMutableUShortArray(UShortArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneUShortArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneUShortArray.Companion.generate(size: UInt, initializer: (index: UInt) -> UShort): KoneUShortArray =
    KoneUShortArray(UShortArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneUShortArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> UShort): KoneUShortArray =
    if (indices.first > indices.last) KoneUShortArray(UShortArray(0))
    else KoneUShortArray(UShortArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableUShortArray.Companion.induce(size: UInt, initialElement: UShort, inducer: (index: UInt, previous: UShort) -> UShort): KoneMutableUShortArray =
    if (size == 0u) KoneMutableUShortArray(UShortArray(0))
    else {
        var current = initialElement
        KoneMutableUShortArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableUShortArray.Companion.induce(indices: UIntRange, initialElement: UShort, inducer: (index: UInt, previous: UShort) -> UShort): KoneMutableUShortArray =
    if (indices.isEmpty()) KoneMutableUShortArray(UShortArray(0))
    else {
        var current = initialElement
        KoneMutableUShortArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneUShortArray.Companion.induce(size: UInt, initialElement: UShort, inducer: (index: UInt, previous: UShort) -> UShort): KoneUShortArray =
    if (size == 0u) KoneUShortArray(UShortArray(0))
    else {
        var current = initialElement
        KoneUShortArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneUShortArray.Companion.induce(indices: UIntRange, initialElement: UShort, inducer: (index: UInt, previous: UShort) -> UShort): KoneUShortArray =
    if (indices.isEmpty()) KoneUShortArray(UShortArray(0))
    else {
        var current = initialElement
        KoneUShortArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableUShortArray] of provided [size] of zeros.
 */
public fun KoneMutableUShortArray.Companion.fill(size: UInt, element: UShort = 0u): KoneMutableUShortArray =
    KoneMutableUShortArray(UShortArray(size.toInt()) { element })

public fun KoneMutableUShortArray.Companion.fill(indices: UIntRange, element: UShort = 0u): KoneMutableUShortArray =
    KoneMutableUShortArray(UShortArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneUShortArray] of provided [size] of zeros.
 */
public fun KoneUShortArray.Companion.fill(size: UInt, element: UShort = 0u): KoneUShortArray =
    KoneUShortArray(UShortArray(size.toInt()) { element })

public fun KoneUShortArray.Companion.fill(indices: UIntRange, element: UShort = 0u): KoneUShortArray =
    KoneUShortArray(UShortArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableUShortArray.Companion.empty(): KoneMutableUShortArray = KoneMutableUShortArray(UShortArray(0))

public fun KoneUShortArray.Companion.empty(): KoneUShortArray = KoneUShortArray(UShortArray(0))

public fun KoneMutableUShortArray.Companion.of(vararg elements: UShort): KoneMutableUShortArray =
    KoneMutableUShortArray(elements)

public fun KoneUShortArray.Companion.of(vararg elements: UShort): KoneUShortArray =
    KoneUShortArray(elements)

public fun KoneIterable<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val iterator = iterator()
    return KoneMutableUShortArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<UShort>.toKoneUShortArray(): KoneUShortArray {
    val iterator = iterator()
    return KoneUShortArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region UInt

/**
 * Returns a [KoneMutableUIntArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableUIntArray.Companion.generate(size: UInt, initializer: (index: UInt) -> UInt): KoneMutableUIntArray =
    KoneMutableUIntArray(UIntArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableUIntArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> UInt): KoneMutableUIntArray =
    if (indices.first > indices.last) KoneMutableUIntArray(UIntArray(0))
    else KoneMutableUIntArray(UIntArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneUIntArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneUIntArray.Companion.generate(size: UInt, initializer: (index: UInt) -> UInt): KoneUIntArray =
    KoneUIntArray(UIntArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneUIntArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> UInt): KoneUIntArray =
    if (indices.first > indices.last) KoneUIntArray(UIntArray(0))
    else KoneUIntArray(UIntArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableUIntArray.Companion.induce(size: UInt, initialElement: UInt, inducer: (index: UInt, previous: UInt) -> UInt): KoneMutableUIntArray =
    if (size == 0u) KoneMutableUIntArray(UIntArray(0))
    else {
        var current = initialElement
        KoneMutableUIntArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableUIntArray.Companion.induce(indices: UIntRange, initialElement: UInt, inducer: (index: UInt, previous: UInt) -> UInt): KoneMutableUIntArray =
    if (indices.isEmpty()) KoneMutableUIntArray(UIntArray(0))
    else {
        var current = initialElement
        KoneMutableUIntArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneUIntArray.Companion.induce(size: UInt, initialElement: UInt, inducer: (index: UInt, previous: UInt) -> UInt): KoneUIntArray =
    if (size == 0u) KoneUIntArray(UIntArray(0))
    else {
        var current = initialElement
        KoneUIntArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneUIntArray.Companion.induce(indices: UIntRange, initialElement: UInt, inducer: (index: UInt, previous: UInt) -> UInt): KoneUIntArray =
    if (indices.isEmpty()) KoneUIntArray(UIntArray(0))
    else {
        var current = initialElement
        KoneUIntArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableUIntArray] of provided [size] of zeros.
 */
public fun KoneMutableUIntArray.Companion.fill(size: UInt, element: UInt = 0u): KoneMutableUIntArray =
    KoneMutableUIntArray(UIntArray(size.toInt()) { element })

public fun KoneMutableUIntArray.Companion.fill(indices: UIntRange, element: UInt = 0u): KoneMutableUIntArray =
    KoneMutableUIntArray(UIntArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneUIntArray] of provided [size] of zeros.
 */
public fun KoneUIntArray.Companion.fill(size: UInt, element: UInt = 0u): KoneUIntArray =
    KoneUIntArray(UIntArray(size.toInt()) { element })

public fun KoneUIntArray.Companion.fill(indices: UIntRange, element: UInt = 0u): KoneUIntArray =
    KoneUIntArray(UIntArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableUIntArray.Companion.empty(): KoneMutableUIntArray = KoneMutableUIntArray(UIntArray(0))

public fun KoneUIntArray.Companion.empty(): KoneUIntArray = KoneUIntArray(UIntArray(0))

public fun KoneMutableUIntArray.Companion.of(vararg elements: UInt): KoneMutableUIntArray =
    KoneMutableUIntArray(elements)

public fun KoneUIntArray.Companion.of(vararg elements: UInt): KoneUIntArray =
    KoneUIntArray(elements)

public fun KoneIterable<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val iterator = iterator()
    return KoneMutableUIntArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<UInt>.toKoneUIntArray(): KoneUIntArray {
    val iterator = iterator()
    return KoneUIntArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion

// region ULong

/**
 * Returns a [KoneMutableULongArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneMutableULongArray.Companion.generate(size: UInt, initializer: (index: UInt) -> ULong): KoneMutableULongArray =
    KoneMutableULongArray(ULongArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneMutableULongArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> ULong): KoneMutableULongArray =
    if (indices.first > indices.last) KoneMutableULongArray(ULongArray(0))
    else KoneMutableULongArray(ULongArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

/**
 * Returns a [KoneULongArray] of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun KoneULongArray.Companion.generate(size: UInt, initializer: (index: UInt) -> ULong): KoneULongArray =
    KoneULongArray(ULongArray(size.toInt()) { initializer(it.toUInt()) })

public inline fun KoneULongArray.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> ULong): KoneULongArray =
    if (indices.first > indices.last) KoneULongArray(ULongArray(0))
    else KoneULongArray(ULongArray((indices.last + 1u - indices.first).toInt()) { initializer(it.toUInt() + indices.first) })

public inline fun KoneMutableULongArray.Companion.induce(size: UInt, initialElement: ULong, inducer: (index: UInt, previous: ULong) -> ULong): KoneMutableULongArray =
    if (size == 0u) KoneMutableULongArray(ULongArray(0))
    else {
        var current = initialElement
        KoneMutableULongArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneMutableULongArray.Companion.induce(indices: UIntRange, initialElement: ULong, inducer: (index: UInt, previous: ULong) -> ULong): KoneMutableULongArray =
    if (indices.isEmpty()) KoneMutableULongArray(ULongArray(0))
    else {
        var current = initialElement
        KoneMutableULongArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

public inline fun KoneULongArray.Companion.induce(size: UInt, initialElement: ULong, inducer: (index: UInt, previous: ULong) -> ULong): KoneULongArray =
    if (size == 0u) KoneULongArray(ULongArray(0))
    else {
        var current = initialElement
        KoneULongArray.generate(size) { if (it == 0u) current else inducer(it, current).also { current = it } }
    }

public inline fun KoneULongArray.Companion.induce(indices: UIntRange, initialElement: ULong, inducer: (index: UInt, previous: ULong) -> ULong): KoneULongArray =
    if (indices.isEmpty()) KoneULongArray(ULongArray(0))
    else {
        var current = initialElement
        KoneULongArray.generate(indices.last + 1u - indices.first) { if (it == 0u) current else inducer(it + indices.first, current).also { current = it } }
    }

/**
 * Returns a [KoneMutableULongArray] of provided [size] of zeros.
 */
public fun KoneMutableULongArray.Companion.fill(size: UInt, element: ULong = 0u): KoneMutableULongArray =
    KoneMutableULongArray(ULongArray(size.toInt()) { element })

public fun KoneMutableULongArray.Companion.fill(indices: UIntRange, element: ULong = 0u): KoneMutableULongArray =
    KoneMutableULongArray(ULongArray((indices.last + 1u - indices.first).toInt()) { element })

/**
 * Returns a [KoneULongArray] of provided [size] of zeros.
 */
public fun KoneULongArray.Companion.fill(size: UInt, element: ULong = 0u): KoneULongArray =
    KoneULongArray(ULongArray(size.toInt()) { element })

public fun KoneULongArray.Companion.fill(indices: UIntRange, element: ULong = 0u): KoneULongArray =
    KoneULongArray(ULongArray((indices.last + 1u - indices.first).toInt()) { element })

public fun KoneMutableULongArray.Companion.empty(): KoneMutableULongArray = KoneMutableULongArray(ULongArray(0))

public fun KoneULongArray.Companion.empty(): KoneULongArray = KoneULongArray(ULongArray(0))

public fun KoneMutableULongArray.Companion.of(vararg elements: ULong): KoneMutableULongArray =
    KoneMutableULongArray(elements)

public fun KoneULongArray.Companion.of(vararg elements: ULong): KoneULongArray =
    KoneULongArray(elements)

public fun KoneIterable<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val iterator = iterator()
    return KoneMutableULongArray.generate(size) { iterator.getAndMoveNext() }
}

public fun KoneIterable<ULong>.toKoneULongArray(): KoneULongArray {
    val iterator = iterator()
    return KoneULongArray.generate(size) { iterator.getAndMoveNext() }
}

// endregion