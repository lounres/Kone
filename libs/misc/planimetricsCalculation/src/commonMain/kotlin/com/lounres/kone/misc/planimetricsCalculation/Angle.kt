/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.polynomial.LabeledPolynomial


// TODO: Docs
/**
 * Represents the \(\pi\)-periodic oriented angle \(\angle\) as \((k \sin(\alpha), k \cos(\alpha))\).
 */
public data class Angle<V>(
    val sin: LabeledPolynomial<V>,
    val cos: LabeledPolynomial<V>,
)