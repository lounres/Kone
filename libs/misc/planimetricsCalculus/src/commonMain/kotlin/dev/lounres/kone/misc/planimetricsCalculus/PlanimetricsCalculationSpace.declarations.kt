/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */


package dev.lounres.kone.misc.planimetricsCalculus

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.zero
import dev.lounres.kone.context
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.vectorKategory
import dev.lounres.kone.polynomial.LabeledPolynomial
import dev.lounres.kone.polynomial.LabeledVariable
import dev.lounres.kone.polynomial.MultivariatePolynomialSpace
import dev.lounres.kone.polynomial.asLabeledPolynomial
import dev.lounres.kone.polynomial.labeledPolynomialSpace
import dev.lounres.kone.polynomial.polynomialOne
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.reflect.KProperty
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
    
    @Suppress("PropertyName")
    public val Point: PointDelegate<Number> = PointDelegate(this)
    
    @Suppress("PropertyName")
    public val Line: LineDelegate<Number> = LineDelegate(this)
    
    @Suppress("PropertyName")
    public val Quadric: QuadricDelegate<Number> = QuadricDelegate(this)
    
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
    
    public class PointDelegate<Number> internal constructor(
        private val space: PlanimetricsCalculationSpace<Number>,
    ) {
        public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Point<Number> = context(space) {
            Point(property.name)
        }
        
        public val finite: FinitePointDelegate<Number> = FinitePointDelegate(space)
        
        public class FinitePointDelegate<Number> internal constructor(
            private val space: PlanimetricsCalculationSpace<Number>,
        ) {
            public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Point<Number> = context(space) {
                calculate {
                    property.name.let {
                        Point(
                            LabeledVariable(it + "_x").asLabeledPolynomial<Number>(),
                            LabeledVariable(it + "_y").asLabeledPolynomial<Number>(),
                            polynomialOne
                        )
                    }
                }
            }
        }
    }
    
    public class LineDelegate<Number> internal constructor(
        private val space: PlanimetricsCalculationSpace<Number>,
    ) {
        public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Line<Number> = context(space) {
            Line(property.name)
        }
    }
    
    public class QuadricDelegate<Number> internal constructor(
        private val space: PlanimetricsCalculationSpace<Number>,
    ) {
        public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Quadric<Number> = context(space) {
            Quadric(property.name)
        }
    }
    
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
public val <Number> origin: Point<Number> get() = planimetricsCalculationSpace.origin
context(planimetricsCalculationSpace: PlanimetricsCalculationSpace<Number>)
public val <Number> xBasis: Point<Number> get() = planimetricsCalculationSpace.xBasis
context(planimetricsCalculationSpace: PlanimetricsCalculationSpace<Number>)
public val <Number> yBasis: Point<Number> get() = planimetricsCalculationSpace.yBasis
context(planimetricsCalculationSpace: PlanimetricsCalculationSpace<Number>)
public val <Number> xAxis: Line<Number> get() = planimetricsCalculationSpace.xAxis
context(planimetricsCalculationSpace: PlanimetricsCalculationSpace<Number>)
public val <Number> yAxis: Line<Number> get() = planimetricsCalculationSpace.yAxis
context(planimetricsCalculationSpace: PlanimetricsCalculationSpace<Number>)
public val <Number> lineAtInfinity: Line<Number> get() = planimetricsCalculationSpace.lineAtInfinity

context(planimetricsCalculationSpace: PlanimetricsCalculationSpace<Number>)
public val <Number> Point: PlanimetricsCalculationSpace.PointDelegate<Number> get() = planimetricsCalculationSpace.Point
context(planimetricsCalculationSpace: PlanimetricsCalculationSpace<Number>)
public val <Number> Line: PlanimetricsCalculationSpace.LineDelegate<Number> get() = planimetricsCalculationSpace.Line
context(planimetricsCalculationSpace: PlanimetricsCalculationSpace<Number>)
public val <Number> Quadric: PlanimetricsCalculationSpace.QuadricDelegate<Number> get() = planimetricsCalculationSpace.Quadric

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