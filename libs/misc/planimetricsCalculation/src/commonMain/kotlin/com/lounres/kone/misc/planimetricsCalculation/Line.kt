/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.linearAlgebra.ColumnVector
import com.lounres.kone.linearAlgebra.RowVector
import com.lounres.kone.polynomial.LabeledPolynomial
import kotlin.reflect.KProperty


public data class Line<E>(
    val x: LabeledPolynomial<E>,
    val y: LabeledPolynomial<E>,
    val z: LabeledPolynomial<E>
) {
    public val rowVector: RowVector<LabeledPolynomial<E>> get() = RowVector(x, y, z)
    public val columnVector: ColumnVector<LabeledPolynomial<E>> get() = ColumnVector(x, y, z)

    public companion object {
        context(A)
        public operator fun <E, A: Ring<E>> getValue(thisRef: Any?, property: KProperty<*>) : Line<E> = Line(property.name)
        context(PlanimetricsCalculationContext<E, *>)
        public operator fun <E> getValue(thisRef: Any?, property: KProperty<*>) : Line<E> = Line(property.name)
    }
}

context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Line<E>.equalsTo(other: Line<E>): Boolean = calculate {
    this === other || (x * other.y eq y * other.x && y * other.z eq z * other.y && z * other.x eq x * other.z)
}
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Line<E>.notEqualsTo(other: Line<E>): Boolean = !(this equalsTo other)
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Line<E>.eq(other: Line<E>): Boolean = this equalsTo other
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Line<E>.neq(other: Line<E>): Boolean = !(this equalsTo other)