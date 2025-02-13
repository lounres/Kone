/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.polynomial.LabeledVariable
import dev.lounres.kone.polynomial.asLabeledPolynomial


context(_: PlanimetricsCalculationSpace<C>)
public fun <C> Quadric(name: String): Quadric<C> = calculate {
    Quadric<C>(
        LabeledVariable(name + "_xx").asLabeledPolynomial<C>(),
        LabeledVariable(name + "_yy").asLabeledPolynomial<C>(),
        LabeledVariable(name + "_zz").asLabeledPolynomial<C>(),
        LabeledVariable(name + "_xy").asLabeledPolynomial<C>(),
        LabeledVariable(name + "_xz").asLabeledPolynomial<C>(),
        LabeledVariable(name + "_yz").asLabeledPolynomial<C>(),
    )
}
//public fun <C> PlanimetricsCalculationSpace<C>.Quadric(matrix: Matrix<LabeledPolynomial<C>>): Quadric<C> = context(ring) {
//    require(matrix.rowNumber == 3u && matrix.columnNumber == 3u) { "Defining matrix should have sizes 3×3." }
//    require(matrixSpace { matrix.isSymmetric }) { "Defining matrix should be symmetric." }
//    Quadric(
//        xx = matrix[0u, 0u],
//        yy = matrix[1u, 1u],
//        zz = matrix[2u, 2u],
//        xy = 2 * matrix[0u, 1u],
//        xz = 2 * matrix[0u, 2u],
//        yz = 2 * matrix[1u, 2u]
//    )
//}