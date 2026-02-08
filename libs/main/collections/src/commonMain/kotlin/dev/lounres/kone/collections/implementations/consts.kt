/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.utils.firstIndexThat
import dev.lounres.kone.collections.utils.firstThat
import dev.lounres.kone.collections.utils.lastIndexThat
import dev.lounres.kone.collections.utils.lastThat
import kotlin.math.ceil
import kotlin.math.floor


internal const val MAX_CAPACITY = 0b10000000000000000000000000000000u

@PublishedApi
internal /*const*/ val POWERS_OF_2: KoneUIntArray = KoneUIntArray.generate(33u) { if (it == 0u) 0u else 1u shl (it.toInt() - 1) }

@PublishedApi
internal fun powerOf2GreaterOrEqualTo(number: UInt): UInt = POWERS_OF_2.firstThat { it >= number }

@PublishedApi
internal fun powerOf2IndexGreaterOrEqualTo(number: UInt): UInt = POWERS_OF_2.firstIndexThat { _, power -> power >= number }

@PublishedApi
internal fun powerOf2ArraySizeGreaterOrEqualTo(size: UInt): UInt =
    if (size > MAX_CAPACITY) throw IllegalArgumentException("Kone collection implementations can not allocate array of size more than 2^31")
    else powerOf2GreaterOrEqualTo(size)

@PublishedApi
internal /*const*/ val FIBONACCI_NUMBERS: KoneUIntArray = KoneUIntArray.of(
    0u, 1u, 1u, 2u, 3u, 5u, 8u, 13u, 21u, 34u, 55u, 89u, 144u, 233u, 377u, 610u, 987u, 1597u, 2584u, 4181u, 6765u,
    10946u, 17711u, 28657u, 46368u, 75025u, 121393u, 196418u, 317811u, 514229u, 832040u, 1346269u, 2178309u, 3524578u,
    5702887u, 9227465u, 14930352u, 24157817u, 39088169u, 63245986u, 102334155u, 165580141u, 267914296u, 433494437u,
    701408733u, 1134903170u, 1836311903u, 2971215073u,
)

@PublishedApi
internal fun fibonacciNumberLessOrEqualTo(number: UInt): UInt = FIBONACCI_NUMBERS.lastThat { it <= number }

@PublishedApi
internal fun fibonacciNumberIndexLessOrEqualTo(number: UInt): UInt = FIBONACCI_NUMBERS.lastIndexThat { _, fib -> fib <= number }

public const val DEFAULT_HASH_TABLE_LOAD_FACTOR: Float = 0.75f

internal fun calculateHashTableCapacity(size: UInt, loadFactor: Float): UInt = ceil(size.toFloat() / loadFactor).toUInt()
internal fun calculateHashTableSize(capacity: UInt, loadFactor: Float): UInt = floor(capacity.toFloat() * loadFactor).toUInt()