/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName")

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.context.invoke
import com.lounres.kone.linearAlgebra.SquareMatrix
import com.lounres.kone.polynomial.LabeledPolynomial
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
public fun <C> PlanimetricsCalculationContext<C, *>.Quadric(matrix: SquareMatrix<LabeledPolynomial<C>>): Quadric<C> = polynomialSpace {
    require(matrix.countOfRows == 3) { "Defining matrix should have sizes 3×3." }
    require(matrixSpace { matrix.isSymmetric }) { "Defining matrix should be symmetric." }
    Quadric(
        xx = matrix[0, 0],
        yy = matrix[1, 1],
        zz = matrix[2, 2],
        xy = 2 * matrix[0, 1],
        xz = 2 * matrix[0, 2],
        yz = 2 * matrix[1, 2]
    )
}