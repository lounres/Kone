/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.RowVector
import dev.lounres.kone.polynomial.LabeledPolynomial
import kotlin.reflect.KProperty


public data class Line<E>(
    val x: LabeledPolynomial<E>,
    val y: LabeledPolynomial<E>,
    val z: LabeledPolynomial<E>
) {
    public val rowVector: RowVector<LabeledPolynomial<E>> get() = RowVector(x, y, z)
    public val columnVector: ColumnVector<LabeledPolynomial<E>> get() = ColumnVector(x, y, z)
    
    public operator fun getValue(thisRef: Any?, property: KProperty<*>) : Line<E> = this

    public companion object {
//        context(_: PlanimetricsCalculationSpace<E>)
//        public operator fun <E> provideDelegate(thisRef: Any?, property: KProperty<*>): Line<E> = Line(property.name)
    }
}