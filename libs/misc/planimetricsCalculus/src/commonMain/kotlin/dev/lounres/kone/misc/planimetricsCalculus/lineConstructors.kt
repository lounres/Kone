/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName")

package dev.lounres.kone.misc.planimetricsCalculus

import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.RowVector
import dev.lounres.kone.polynomial.LabeledPolynomial
import dev.lounres.kone.polynomial.LabeledVariable
import dev.lounres.kone.polynomial.asLabeledPolynomial


public fun <C> Line(x: C, y: C, z: C): Line<C> = Line(x.asLabeledPolynomial(), y.asLabeledPolynomial(), z.asLabeledPolynomial())
context(_: PlanimetricsCalculationSpace<C>)
public fun <C> Line(name: String): Line<C> = calculate {
    Line(
        LabeledVariable(name + "_x").asLabeledPolynomial<C>(),
        LabeledVariable(name + "_y").asLabeledPolynomial<C>(),
        LabeledVariable(name + "_z").asLabeledPolynomial<C>(),
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
