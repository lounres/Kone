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
public fun <C, A: Ring<C>> A.Point(name: String): Point<C> = Point<C>(convert(name + "_x"), convert(name + "_y"), convert(name + "_z"))
public fun <C> PlanimetricsCalculationContext<C, *>.Point(name: String): Point<C> = polynomialSpace {
    Point<C>(
        Symbol(name + "_x").value,
        Symbol(name + "_y").value,
        Symbol(name + "_z").value
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
