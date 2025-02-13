/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */


package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.zero
import dev.lounres.kone.context
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.vectorKategory
import dev.lounres.kone.polynomial.LabeledPolynomial
import dev.lounres.kone.polynomial.LabeledVariable
import dev.lounres.kone.polynomial.MultivariatePolynomialSpace
import dev.lounres.kone.polynomial.labeledPolynomialSpace
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.reflect.KVariance


// TODO: Think about using other polynomial types
public class PlanimetricsCalculationSpace<Number>(
    @PublishedApi internal val numberRing: Ring<Number>,
    @PublishedApi internal val polynomialSpace: MultivariatePolynomialSpace<Number, LabeledVariable, LabeledPolynomial<Number>> = numberRing.labeledPolynomialSpace,
    @PublishedApi internal val polynomialVectorKategory: VectorKategory<LabeledPolynomial<Number>> = polynomialSpace.vectorKategory(),
) {
    public val origin: Point<Number> = context(polynomialSpace) { Point(zero, zero, one) }
    public val xBasis: Point<Number> = context(polynomialSpace) { Point(one, zero, one) }
    public val yBasis: Point<Number> = context(polynomialSpace) { Point(zero, one, one) }
    public val xAxis: Line<Number> = context(polynomialSpace) { Line(zero, one, zero) }
    public val yAxis: Line<Number> = context(polynomialSpace) { Line(one, zero, zero) }
    public val lineAtInfinity: Line<Number> = context(polynomialSpace) { Line(zero, zero, one) }
    
    // TODO: Think about point and line equalities
//    public val pointContext: Equality<Point<Number>> =
//        object : Equality<Point<Number>> {
//            override fun Point<Number>.equalsTo(other: Point<Number>): Boolean {
//                if (this === other) return true
//
//                return context(polynomialSpace) {
//                    x * other.y eq y * other.x && y * other.z eq z * other.y && z * other.x eq x * other.z
//                }
//            }
//        }
//
//    public val lineContext: Equality<Line<Number>> =
//        object : Equality<Line<Number>> {
//            override fun Line<Number>.equalsTo(other: Line<Number>): Boolean {
//                if (this === other) return true
//
//                return context(polynomialSpace) {
//                    x * other.y eq y * other.x && y * other.z eq z * other.y && z * other.x eq x * other.z
//                }
//            }
//        }
    
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<PlanimetricsCalculationSpace<Number>> {
        override val typeKey: SuppliedType.Regular<PlanimetricsCalculationSpace<Number>> =
            SuppliedType.Regular(
                kClass = PlanimetricsCalculationSpace::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
    }
}

context(planimetricsCalculationSpace: PlanimetricsCalculationSpace<Number>)
public inline fun <Number, R> calculate(
    block: context(Ring<Number>, MultivariatePolynomialSpace<Number, LabeledVariable, LabeledPolynomial<Number>>, VectorKategory<LabeledPolynomial<Number>>) () -> R
): R {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    return block(planimetricsCalculationSpace.numberRing, planimetricsCalculationSpace.polynomialSpace, planimetricsCalculationSpace.polynomialVectorKategory)
}

public fun <Number> Ring<Number>.planimetricsCalculationSpace(
    polynomialSpace: MultivariatePolynomialSpace<Number, LabeledVariable, LabeledPolynomial<Number>> = labeledPolynomialSpace,
    polynomialVectorKategory: VectorKategory<LabeledPolynomial<Number>> = polynomialSpace.vectorKategory(),
): PlanimetricsCalculationSpace<Number> =
    PlanimetricsCalculationSpace(
        numberRing = this,
        polynomialSpace = polynomialSpace,
        polynomialVectorKategory = polynomialVectorKategory,
    )