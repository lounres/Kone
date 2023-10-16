/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.polynomial.LabeledPolynomial


@JvmInline
public value class Transformation<E>(
    public val matrix: SquareMatrix<LabeledPolynomial<E>>
) {
    init {
        require(matrix.countOfRows == 3) { "Transformation should be defined by 3⨉3 matrix. No other sizes are compatible." }
    }
}

context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Transformation<E>.equalsTo(other: Transformation<E>): Boolean = this.matrix === other.matrix || calculate {
    for (i1 in matrix.rowIndices) {
        for (i2 in 0 until i1) for (j1 in matrix.columnIndices) for (j2 in matrix.columnIndices)
            if (matrix[i1, j1] * other.matrix[i2, j2] neq matrix[i2, j2] * other.matrix[i1, j1]) return@calculate false
        val i2 = i1
        for (j1 in matrix.columnIndices) for (j2 in 0 until j1)
            if (matrix[i1, j1] * other.matrix[i2, j2] neq matrix[i2, j2] * other.matrix[i1, j1]) return@calculate false
    }
    true
}
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Transformation<E>.notEqualsTo(other: Transformation<E>): Boolean = !(this equalsTo other)
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Transformation<E>.eq(other: Transformation<E>): Boolean = this equalsTo other
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E>Transformation<E>.neq(other: Transformation<E>): Boolean = !(this equalsTo other)

context(PlanimetricsCalculationContext<E, *>)
public operator fun <E> Transformation<E>.invoke(P: Point<E>): Point<E> = calculate { Point(matrix * P.columnVector) }
context(PlanimetricsCalculationContext<E, *>)
public operator fun <E> Transformation<E>.invoke(l: Line<E>): Line<E> = calculate { Line(l.rowVector * matrix.adjugate()) }
context(PlanimetricsCalculationContext<E, *>)
public operator fun <E> Transformation<E>.invoke(q: Quadric<E>): Quadric<E> = calculate { (matrix.adjugate()).let { Quadric(it.transposed() * q.matrix * it) } }