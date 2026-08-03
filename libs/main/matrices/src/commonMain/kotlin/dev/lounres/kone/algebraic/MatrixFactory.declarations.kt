/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2


@GenerateKoneContextKey
public interface MatrixFactory<Number, Matrix: MDList2<Number>> {
    // TODO: Think about adding:
//    public fun convertMatrix(matrix: MDList2<Number>): Matrix
    public fun generateMatrix(rowNumber: UInt, columnNumber: UInt, generator: (row: UInt, column: UInt) -> Number): Matrix
    public fun fillMatrix(rowNumber: UInt, columnNumber: UInt, number: Number): Matrix = generateMatrix(rowNumber, columnNumber) { _, _ -> number }
    public fun mapMatrix(rowNumber: UInt, columnNumber: UInt, numbers: KoneMap<MDIndex, Number>): Matrix
    
    public companion object;
}