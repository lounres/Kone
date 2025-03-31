/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(DelicatePolynomialAPI::class)

package dev.lounres.kone.misc.planimetricsCalculus

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.polynomial.*


context(_: Ring<C>)
@OptIn(DelicatePolynomialAPI::class)
internal fun <C> String.convert(): LabeledPolynomial<C> = LabeledVariable(this).asLabeledPolynomial()