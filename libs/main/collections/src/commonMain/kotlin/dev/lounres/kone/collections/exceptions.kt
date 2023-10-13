/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public inline fun indexException(index: UInt, size: UInt): Nothing =
    throw IndexOutOfBoundsException("Index $index out of bounds for length $size")

public inline fun noElementException(index: UInt, size: UInt): Nothing =
    throw NoSuchElementException("Index $index out of bounds for length $size")