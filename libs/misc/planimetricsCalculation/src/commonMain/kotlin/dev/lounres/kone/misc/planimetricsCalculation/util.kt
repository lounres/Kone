/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(DelicatePolynomialAPI::class)

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.polynomial.*
import space.kscience.kmath.expressions.Symbol


internal fun <C, A: Ring<C>> A.convert(name: String): LabeledPolynomial<C> =
    LabeledPolynomialWithoutCheck(mapOf(Symbol(name) to 1U) to one)