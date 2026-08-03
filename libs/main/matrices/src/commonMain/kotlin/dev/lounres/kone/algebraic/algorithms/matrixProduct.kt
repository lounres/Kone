/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.multidimensionalCollections.MDList2


@GenerateKoneContextKey
public fun interface MatrixProductComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public operator fun Matrix.times(other: Matrix): Matrix
    
    public companion object;
}

context(matrixProductComputer: MatrixProductComputer<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.times(other: Matrix): Matrix =
    with(matrixProductComputer) { this@times * other }