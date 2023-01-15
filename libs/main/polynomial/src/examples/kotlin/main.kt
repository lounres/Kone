/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.examples

import com.lounres.kone.algebraic.field
import com.lounres.kone.polynomial.labeledPolynomialSpace
import space.kscience.kmath.expressions.Symbol


fun main() = Double.field.labeledPolynomialSpace {
    val x = Symbol.x
    println(x * (x - 1) * (x - 2))
}