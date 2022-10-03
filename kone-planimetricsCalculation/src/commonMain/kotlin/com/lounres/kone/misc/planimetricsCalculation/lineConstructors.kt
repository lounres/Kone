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


public fun <C, A: Ring<C>> A.Line(name: String): Line<C> = Line<C>(convert(name + "_x"), convert(name + "_y"), convert(name + "_z"))
public fun <C> PlanimetricsCalculationContext<C, *>.Line(name: String): Line<C> = polynomialSpace {
    Line<C>(
        Symbol(name + "_x").value,
        Symbol(name + "_y").value,
        Symbol(name + "_z").value
    )
}
public fun <C> Line(rowVector: RowVector<LabeledPolynomial<C>>): Line<C> = Line(
    rowVector[0],
    rowVector[1],
    rowVector[2]
)
public fun <C> Line(columnVector: ColumnVector<LabeledPolynomial<C>>): Line<C> = Line(
    columnVector[0],
    columnVector[1],
    columnVector[2]
)
