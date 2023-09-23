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


public fun <C> Line(x: C, y: C, z: C): Line<C> = Line(x.asLabeledPolynomial(), y.asLabeledPolynomial(), z.asLabeledPolynomial())
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
