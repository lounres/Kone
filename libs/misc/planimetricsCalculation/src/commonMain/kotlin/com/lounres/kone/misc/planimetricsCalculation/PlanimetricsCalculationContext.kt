/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.context.KoneContext
import com.lounres.kone.context.invoke
import com.lounres.kone.linearAlgebra.MatrixSpace
import com.lounres.kone.linearAlgebra.SquareMatrix
import com.lounres.kone.polynomial.LabeledPolynomial
import com.lounres.kone.polynomial.LabeledPolynomialSpace
import com.lounres.kone.polynomial.labeledPolynomialSpace
import kotlin.contracts.InvocationKind.*
import kotlin.contracts.contract
import kotlin.reflect.KProperty


public class PlanimetricsCalculationContext<E, out A : Ring<E>>(
    public val ring: A,
) : KoneContext {
    public val polynomialSpace: LabeledPolynomialSpace<E, A> by lazy { ring.labeledPolynomialSpace }
    public val matrixSpace: MatrixSpace<LabeledPolynomial<E>, LabeledPolynomialSpace<E, A>> by lazy { MatrixSpace(polynomialSpace) }

    public inline fun <C, A: Ring<C>, PC: PlanimetricsCalculationContext<C, A>, R> PC.calculate(block: context(A, LabeledPolynomialSpace<C, A>, MatrixSpace<LabeledPolynomial<C>, LabeledPolynomialSpace<C, A>>, PC) () -> R): R {
        contract { callsInPlace(block, EXACTLY_ONCE) }
        return block(ring, polynomialSpace, matrixSpace, this)
    }

    public val origin: Point<E> = calculate { Point(zero, zero, one) }
    public val infinity: Line<E> = calculate { Line(zero, zero, one) }
    public val xBasis: Point<E> = calculate { Point(one, zero, one) }
    public val yBasis: Point<E> = calculate { Point(zero, one, one) }
    public val xAxis: Line<E> = calculate { Line(zero, one, zero) }
    public val yAxis: Line<E> = calculate { Line(one, zero, zero) }

}

public inline val <C, A: Ring<C>> A.planimetricsCalculationContext: PlanimetricsCalculationContext<C, A> get() = PlanimetricsCalculationContext(this)