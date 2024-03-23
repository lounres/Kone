/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public fun indexException(index: UInt, size: UInt): Nothing =
    throw IndexOutOfBoundsException("Index $index out of bounds for length $size")

public fun noElementException(index: UInt, size: UInt): Nothing =
    throw NoSuchElementException("Index $index out of bounds for length $size")

public class CapacityOverflowException(message: String = "Overflow of collection with fixed capacity") : RuntimeException(message)

public fun capacityOverflowException(capacity: UInt): Nothing =
    throw CapacityOverflowException("Overflow of collection with fixed capacity of $capacity")

public class NoMatchingKeyException(message: String = "There is no value for requested key"): NoSuchElementException(message)

public fun noMatchingKeyException(key: Any?): Nothing =
    throw NoMatchingKeyException("There is no value for key $key")