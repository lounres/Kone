/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(DelicatePolynomialAPI::class)

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.polynomial.*
import space.kscience.kmath.expressions.Symbol


context(Ring<C>)
@OptIn(DelicatePolynomialAPI::class)
internal fun <C> String.convert(): LabeledPolynomial<C> =
    LabeledPolynomialWithoutCheck(mapOf(Symbol(this) to 1U) to one)
context(MultivariatePolynomialSpace<C, Symbol, LabeledPolynomial<C>, *>)
internal fun <C> String.convert(): LabeledPolynomial<C> = Symbol(this).polynomialValue