/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(DelicatePolynomialAPI::class)

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.polynomial.*
import space.kscience.kmath.expressions.Symbol


context(Ring<C>)
internal fun <C> String.convert(): LabeledPolynomial<C> =
    LabeledPolynomialWithoutCheck(mapOf(Symbol(this) to 1U) to one)
context(MultivariatePolynomialSpace<C, Symbol, LabeledPolynomial<C>, *>)
internal fun <C> String.convert(): LabeledPolynomial<C> = Symbol(this).polynomialValue