/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.RowVector
import dev.lounres.kone.polynomial.LabeledPolynomial
import dev.lounres.kone.polynomial.LabeledVariable
import dev.lounres.kone.polynomial.asLabeledPolynomial


public fun <C> Point(x: C, y: C, z: C): Point<C> = Point(x.asLabeledPolynomial(), y.asLabeledPolynomial(), z.asLabeledPolynomial())
context(_: PlanimetricsCalculationSpace<C>)
public fun <C> Point(name: String): Point<C> = calculate {
    Point(
        LabeledVariable(name + "_x").asLabeledPolynomial<C>(),
        LabeledVariable(name + "_y").asLabeledPolynomial<C>(),
        LabeledVariable(name + "_z").asLabeledPolynomial<C>(),
    )
}
public fun <C> Point(rowVector: RowVector<LabeledPolynomial<C>>): Point<C> {
    require(rowVector.size == 3u) { "Cannot convert row vector to point: expected size 3, got ${rowVector.size}" }
    return Point(
        rowVector[0u],
        rowVector[1u],
        rowVector[2u]
    )
}
public fun <C> Point(columnVector: ColumnVector<LabeledPolynomial<C>>): Point<C> {
    require(columnVector.size == 3u) { "Cannot convert column vector to point: expected size 3, got ${columnVector.size}" }
    return Point(
        columnVector[0u],
        columnVector[1u],
        columnVector[2u]
    )
}
