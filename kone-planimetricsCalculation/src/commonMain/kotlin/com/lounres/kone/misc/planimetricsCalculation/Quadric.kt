/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.polynomial.LabeledPolynomial
import kotlin.reflect.KProperty


public data class Quadric<C>(
    val xx: LabeledPolynomial<C>,
    val yy: LabeledPolynomial<C>,
    val zz: LabeledPolynomial<C>,
    val xy: LabeledPolynomial<C>,
    val xz: LabeledPolynomial<C>,
    val yz: LabeledPolynomial<C>
) {
    public companion object {
        public operator fun <C, A: Ring<C>> A.getValue(thisRef: Any?, property: KProperty<*>) : Quadric<C> = Quadric(property.name)
        public operator fun <C> PlanimetricsCalculationContext<C, *>.getValue(thisRef: Any?, property: KProperty<*>) : Quadric<C> = Quadric(property.name)
    }
}