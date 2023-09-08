/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

import com.lounres.kone.algebraic.ring
import com.lounres.kone.context.invoke
import com.lounres.kone.polynomial.LabeledPolynomialSpace
import space.kscience.kmath.expressions.symbol


fun main(): Unit = LabeledPolynomialSpace(Int.ring).invoke {
    val x by symbol
    val xPoly = +x
}