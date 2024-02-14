/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public fun indexException(index: UInt, size: UInt): Nothing =
    throw IndexOutOfBoundsException("Index $index out of bounds for length $size")

public fun noElementException(index: UInt, size: UInt): Nothing =
    throw NoSuchElementException("Index $index out of bounds for length $size")

public class CapacityOverflowException(message: String = "An operation is not implemented.") : RuntimeException(message)

public fun capacityOverflowException(capacity: UInt): Nothing =
    throw CapacityOverflowException("Overflow of collection with fixed capacity of $capacity")