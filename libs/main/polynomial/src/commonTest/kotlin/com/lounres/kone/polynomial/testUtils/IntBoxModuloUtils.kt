/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.testUtils

import com.lounres.kone.polynomial.ListPolynomial
import com.lounres.kone.polynomial.ListPolynomialSpace


fun ListPolynomialSpace<IntBox, IntBoxModuloRing>.ListPolynomial(vararg coefs: Int): ListPolynomial<IntBox> =
    ListPolynomial(coefs.map { IntBox(it) })
fun IntBoxModuloRing.ListPolynomial(vararg coefs: Int): ListPolynomial<IntBox> =
    ListPolynomial(coefs.map { IntBox(it) })

operator fun Int.not() = IntBox(this)