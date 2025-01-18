/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneUIntArray


public class ShapeMismatchException(message: String = "Shapes mismatch") : RuntimeException(message)

public fun shapeMismatchException(left: MDShape, right: MDShape): Nothing =
    throw ShapeMismatchException("Shapes $left and $right mismatch.")

public class IndexOutOfShapeException(message: String = "Index is out of shape") : RuntimeException(message)

public fun indexOutOfShapeException(shape: MDShape, index: KoneUIntArray): Nothing =
    throw IndexOutOfShapeException("Index $index is out of shape $shape")