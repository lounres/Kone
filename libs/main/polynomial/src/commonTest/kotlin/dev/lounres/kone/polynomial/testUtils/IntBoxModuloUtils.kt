/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial.testUtils

import dev.lounres.kone.polynomial.ListPolynomial
import dev.lounres.kone.polynomial.ListPolynomialSpace


fun ListPolynomialSpace<IntBox, IntBoxModuloRing>.ListPolynomial(vararg coefs: Int): ListPolynomial<IntBox> =
    ListPolynomial(coefs.map { IntBox(it) })
fun IntBoxModuloRing.ListPolynomial(vararg coefs: Int): ListPolynomial<IntBox> =
    ListPolynomial(coefs.map { IntBox(it) })

operator fun Int.not() = IntBox(this)