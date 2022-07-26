/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName")

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.algebraic.invoke
import com.lounres.kone.algebraic.Ring
import com.lounres.kone.linearAlgebra.ColumnVector
import com.lounres.kone.linearAlgebra.RowVector
import com.lounres.kone.polynomial.LabeledPolynomial
import space.kscience.kmath.expressions.Symbol


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
