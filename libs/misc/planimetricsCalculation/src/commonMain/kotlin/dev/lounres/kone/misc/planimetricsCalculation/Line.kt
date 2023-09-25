/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.RowVector
import dev.lounres.kone.polynomial.LabeledPolynomial
import kotlin.reflect.KProperty


public data class Line<C>(
    val x: LabeledPolynomial<C>,
    val y: LabeledPolynomial<C>,
    val z: LabeledPolynomial<C>
) {
    public val rowVector: RowVector<LabeledPolynomial<C>> get() = RowVector(x, y, z)
    public val columnVector: ColumnVector<LabeledPolynomial<C>> get() = ColumnVector(x, y, z)

    public companion object {
        // Waiting for context receivers :( FIXME: Uncomment when context receivers will be available
//        context(A)
//        public operator fun <C, A: Ring<C>> getValue(thisRef: Any?, property: KProperty<*>) : Line<C> = Line(property.name)
//        context(PlanimetricsCalculationContext<C, *>)
//        public operator fun <C> getValue(thisRef: Any?, property: KProperty<*>) : Line<C> = Line(property.name)
    }
}