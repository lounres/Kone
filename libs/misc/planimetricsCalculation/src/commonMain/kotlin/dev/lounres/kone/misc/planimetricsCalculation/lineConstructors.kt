/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.experiment1.ColumnVector
import dev.lounres.kone.linearAlgebra.experiment1.RowVector
import dev.lounres.kone.polynomial.LabeledPolynomial
import dev.lounres.kone.polynomial.asLabeledPolynomial
import space.kscience.kmath.expressions.Symbol


public fun <C> Line(x: C, y: C, z: C): Line<C> = Line(x.asLabeledPolynomial(), y.asLabeledPolynomial(), z.asLabeledPolynomial())
public fun <C, A: Ring<C>> A.Line(name: String): Line<C> = Line<C>((name + "_x").convert(), (name + "_y").convert(), (name + "_z").convert())
public fun <C> PlanimetricsCalculationContext<C, *>.Line(name: String): Line<C> = polynomialSpace {
    Line<C>(
        Symbol(name + "_x").polynomialValue,
        Symbol(name + "_y").polynomialValue,
        Symbol(name + "_z").polynomialValue
    )
}
public fun <C> Line(rowVector: RowVector<LabeledPolynomial<C>>): Line<C> = Line(
    rowVector[0u],
    rowVector[1u],
    rowVector[2u]
)
public fun <C> Line(columnVector: ColumnVector<LabeledPolynomial<C>>): Line<C> = Line(
    columnVector[0u],
    columnVector[1u],
    columnVector[2u]
)
