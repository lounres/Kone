/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.linearAlgebra.ColumnVector
import com.lounres.kone.linearAlgebra.RowVector
import com.lounres.kone.polynomial.LabeledPolynomial
import kotlin.reflect.KProperty


public data class Point<C>(
    val x: LabeledPolynomial<C>,
    val y: LabeledPolynomial<C>,
    val z: LabeledPolynomial<C>
) {
    public val rowVector: RowVector<LabeledPolynomial<C>> get() = RowVector(x, y, z)
    public val columnVector: ColumnVector<LabeledPolynomial<C>> get() = ColumnVector(x, y, z)

    public companion object {
//        context(A)
//        public operator fun <C, A: Ring<C>> getValue(thisRef: Any?, property: KProperty<*>) : Point<C> = Point(property.name)
//        context(PlanimetricsCalculationContext<C, *>)
//        public operator fun <C> getValue(thisRef: Any?, property: KProperty<*>) : Point<C> = Point(property.name)
    }
}