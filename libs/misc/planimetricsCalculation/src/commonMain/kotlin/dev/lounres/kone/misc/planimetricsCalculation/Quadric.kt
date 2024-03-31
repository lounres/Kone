/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.utils.koneIterableListOf
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.linearAlgebra.experiment1.Matrix
import dev.lounres.kone.polynomial.LabeledPolynomial
import kotlin.reflect.KProperty


public data class Quadric<E>(
    val xx: LabeledPolynomial<E>,
    val yy: LabeledPolynomial<E>,
    val zz: LabeledPolynomial<E>,
    val xy: LabeledPolynomial<E>,
    val xz: LabeledPolynomial<E>,
    val yz: LabeledPolynomial<E>
) {
    public companion object {
        context(A)
        public operator fun <E, A: Ring<E>> getValue(thisRef: Any?, property: KProperty<*>) : Quadric<E> = Quadric(property.name)
        context(PlanimetricsCalculationContext<E, *>)
        public operator fun <E> getValue(thisRef: Any?, property: KProperty<*>) : Quadric<E> = Quadric(property.name)
    }
}

context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Quadric<E>.equalsTo(other: Quadric<E>): Boolean = this === other || calculate {
    xx * other.yy eq yy * other.xx &&
            xx * other.zz eq zz * other.xx &&
            xx * other.xy eq xy * other.xx &&
            xx * other.xz eq xz * other.xx &&
            xx * other.yz eq yz * other.xx &&
            yy * other.zz eq zz * other.yy &&
            yy * other.xy eq xy * other.yy &&
            yy * other.xz eq xz * other.yy &&
            yy * other.yz eq yz * other.yy &&
            zz * other.xy eq xy * other.zz &&
            zz * other.xz eq xz * other.zz &&
            zz * other.yz eq yz * other.zz &&
            xy * other.xz eq xz * other.xy &&
            xy * other.yz eq yz * other.xy &&
            xz * other.yz eq yz * other.xz
}
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Quadric<E>.notEqualsTo(other: Quadric<E>): Boolean = !(this equalsTo other)
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Quadric<E>.eq(other: Quadric<E>): Boolean = this equalsTo other
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Quadric<E>.neq(other: Quadric<E>): Boolean = !(this equalsTo other)

context(PlanimetricsCalculationContext<E, *>)
public val <E> Quadric<E>.matrix: Matrix<LabeledPolynomial<E>>
    get() = calculate {
        Matrix(
            koneIterableListOf(2 * xx, xy, xz),
            koneIterableListOf(xy, 2 * yy, yz),
            koneIterableListOf(xz, yz, 2 * zz),
            context = defaultEquality() // TODO: Maybe, the contexts should be replaced
        )
    }