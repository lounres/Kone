/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */


package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.experiment1.VectorSpace
import dev.lounres.kone.linearAlgebra.experiment1.vectorSpace
import dev.lounres.kone.polynomial.LabeledPolynomial
import dev.lounres.kone.polynomial.LabeledPolynomialSpace
import dev.lounres.kone.polynomial.labeledPolynomialSpace
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract


context(A)
public class PlanimetricsCalculationContext<E, out A : Ring<E>>: KoneContext {
    public val ring: A = this@A
    public val polynomialSpace: LabeledPolynomialSpace<E, A> by lazy { ring.labeledPolynomialSpace }
    public val matrixSpace: VectorSpace<LabeledPolynomial<E>, LabeledPolynomialSpace<E, A>> by lazy { polynomialSpace.vectorSpace }

    public val origin: Point<E> = Point(polynomialSpace.zero, polynomialSpace.zero, polynomialSpace.one)
    public val xBasis: Point<E> = Point(polynomialSpace.one, polynomialSpace.zero, polynomialSpace.one)
    public val yBasis: Point<E> = Point(polynomialSpace.zero, polynomialSpace.one, polynomialSpace.one)
    public val xAxis: Line<E> = Line(polynomialSpace.zero, polynomialSpace.one, polynomialSpace.zero)
    public val yAxis: Line<E> = Line(polynomialSpace.one, polynomialSpace.zero, polynomialSpace.zero)
}

public inline val <E, A: Ring<E>> A.planimetricsCalculationContext: PlanimetricsCalculationContext<E, A> get() = this { PlanimetricsCalculationContext() }

context(PC)
public inline fun <E, A: Ring<E>, PC: PlanimetricsCalculationContext<E, A>, R> calculate(block: context(A, LabeledPolynomialSpace<E, A>, VectorSpace<LabeledPolynomial<E>, LabeledPolynomialSpace<E, A>>, PC) () -> R): R {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return block(ring, polynomialSpace, matrixSpace, this@PC)
}