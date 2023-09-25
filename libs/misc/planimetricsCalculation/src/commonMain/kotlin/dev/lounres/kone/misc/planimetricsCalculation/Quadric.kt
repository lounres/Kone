/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.polynomial.LabeledPolynomial
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
        // Waiting for context receivers :( FIXME: Uncomment when context receivers will be available
//        context(A)
//        public operator fun <C, A: Ring<C>> getValue(thisRef: Any?, property: KProperty<*>) : Quadric<C> = Quadric(property.name)
//        context(PlanimetricsCalculationContext<C, *>)
//        public operator fun <C> getValue(thisRef: Any?, property: KProperty<*>) : Quadric<C> = Quadric(property.name)
    }
}