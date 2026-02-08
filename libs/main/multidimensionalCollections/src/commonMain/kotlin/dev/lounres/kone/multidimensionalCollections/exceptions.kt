/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections


public class MDSizeMismatchException(message: String = "MD sizes mismatch") : RuntimeException(message)

public fun mdSizeMismatchException(left: MDSize, right: MDSize): Nothing =
    throw MDSizeMismatchException("MD sizes $left and $right mismatch.")

public class MDIndexOutOfMDSizeException(message: String = "MD index is out of MD size") : RuntimeException(message)

public fun mdIndexOutOfSizeException(index: MDIndex, size: MDSize): Nothing =
    throw MDIndexOutOfMDSizeException("MD index $index is out of MD size $size")