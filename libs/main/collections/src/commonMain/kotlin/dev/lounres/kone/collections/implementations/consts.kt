/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.contextual.KoneContextualUIntArray
import dev.lounres.kone.collections.contextual.utils.first


internal const val MAX_CAPACITY = 0b10000000000000000000000000000000u

internal /*const*/ val POWERS_OF_2: KoneContextualUIntArray = KoneContextualUIntArray(33u) { if (it == 0u) 0u else 1u shl (it.toInt() - 1) }

internal fun powerOf2GreaterOrEqualTo(size: UInt): UInt =
    if (size > MAX_CAPACITY) throw IllegalArgumentException("Kone collection implementations can not allocate array of size more than 2^31")
    else POWERS_OF_2.first { it >= size }

internal fun powerOf2IndexGreaterOrEqualTo(size: UInt): UInt =
    if (size > MAX_CAPACITY) throw IllegalArgumentException("Kone collection implementations can not allocate array of size more than 2^31")
    else POWERS_OF_2.indexThat { _, power -> power >= size }