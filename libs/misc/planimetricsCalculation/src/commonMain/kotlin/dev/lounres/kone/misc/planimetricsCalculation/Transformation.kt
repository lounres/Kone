/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.linearAlgebra.SquareMatrix
import dev.lounres.kone.polynomial.LabeledPolynomial


public class Transformation<C>(
    public val matrix: SquareMatrix<LabeledPolynomial<C>>
) {
    init {
        require(matrix.countOfRows == 3) { "Transformation should be defined by 3⨉3 matrix. No other sizes are compatible." }
    }
}