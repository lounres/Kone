/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.polynomial.LabeledPolynomial


public data class Length<V>(
    val measure: LabeledPolynomial<V>,
    val normalizer: LabeledPolynomial<V>
)
