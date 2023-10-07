/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.RowVector
import dev.lounres.kone.polynomial.LabeledPolynomial
import dev.lounres.kone.polynomial.asLabeledPolynomial
import space.kscience.kmath.expressions.Symbol


public fun <C> Point(x: C, y: C, z: C): Point<C> = Point(x.asLabeledPolynomial(), y.asLabeledPolynomial(), z.asLabeledPolynomial())
context(A)
public fun <C, A: Ring<C>> Point(name: String): Point<C> = Point<C>((name + "_x").convert(), (name + "_y").convert(), (name + "_z").convert())
context(PlanimetricsCalculationContext<C, *>)
public fun <C> Point(name: String): Point<C> = polynomialSpace {
    Point<C>(
        Symbol(name + "_x").polynomialValue,
        Symbol(name + "_y").polynomialValue,
        Symbol(name + "_z").polynomialValue
    )
}
public fun <C> Point(rowVector: RowVector<LabeledPolynomial<C>>): Point<C> = Point(
    rowVector[0],
    rowVector[1],
    rowVector[2]
)
public fun <C> Point(columnVector: ColumnVector<LabeledPolynomial<C>>): Point<C> = Point(
    columnVector[0],
    columnVector[1],
    columnVector[2]
)
