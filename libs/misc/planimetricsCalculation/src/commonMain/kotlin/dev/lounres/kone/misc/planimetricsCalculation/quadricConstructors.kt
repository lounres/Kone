/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.experiment1.Matrix
import dev.lounres.kone.linearAlgebra.experiment1.isSymmetric
import dev.lounres.kone.polynomial.LabeledPolynomial
import space.kscience.kmath.expressions.Symbol


public fun <C, A: Ring<C>> A.Quadric(name: String): Quadric<C> = Quadric<C>(
    (name + "_xx").convert(),
    (name + "_yy").convert(),
    (name + "_zz").convert(),
    (name + "_xy").convert(),
    (name + "_xz").convert(),
    (name + "_yz").convert()
)
public fun <C> PlanimetricsCalculationContext<C, *>.Quadric(name: String): Quadric<C> = polynomialSpace {
    Quadric<C>(
        Symbol(name + "_xx").polynomialValue,
        Symbol(name + "_yy").polynomialValue,
        Symbol(name + "_zz").polynomialValue,
        Symbol(name + "_xy").polynomialValue,
        Symbol(name + "_xz").polynomialValue,
        Symbol(name + "_yz").polynomialValue
    )
}
public fun <C> PlanimetricsCalculationContext<C, *>.Quadric(matrix: Matrix<LabeledPolynomial<C>>): Quadric<C> = polynomialSpace {
    require(matrix.rowNumber == 3u && matrix.columnNumber == 3u) { "Defining matrix should have sizes 3×3." }
    require(matrixSpace { matrix.isSymmetric }) { "Defining matrix should be symmetric." }
    Quadric(
        xx = matrix[0u, 0u],
        yy = matrix[1u, 1u],
        zz = matrix[2u, 2u],
        xy = 2 * matrix[0u, 1u],
        xz = 2 * matrix[0u, 2u],
        yz = 2 * matrix[1u, 2u]
    )
}