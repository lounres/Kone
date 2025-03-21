/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.list.koneListOf
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.polynomial.LabeledPolynomial


public data class Quadric<E>(
    val xx: LabeledPolynomial<E>,
    val yy: LabeledPolynomial<E>,
    val zz: LabeledPolynomial<E>,
    val xy: LabeledPolynomial<E>,
    val xz: LabeledPolynomial<E>,
    val yz: LabeledPolynomial<E>
) {
    public companion object {
//        context(_: PlanimetricsCalculationScope<E, *>)
//        public operator fun <E> provideDelegate(thisRef: Any?, property: KProperty<*>) : Quadric<E> = Quadric(property.name)
    }
}

context(_: PlanimetricsCalculationSpace<E>)
public val <E> Quadric<E>.matrix: Matrix<LabeledPolynomial<E>>
    get() = calculate {
        Matrix(
            koneListOf(2 * xx, xy, xz),
            koneListOf(xy, 2 * yy, yz),
            koneListOf(xz, yz, 2 * zz),
        )
    }