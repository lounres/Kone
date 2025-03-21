/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.KoneContextRegistryBuilder
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.vectorKategory
import dev.lounres.kone.polynomial.LabeledPolynomial
import dev.lounres.kone.polynomial.LabeledVariable
import dev.lounres.kone.polynomial.MultivariatePolynomialSpace
import dev.lounres.kone.polynomial.labeledPolynomialSpace
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KVariance


internal val labeledVariableType =
    SuppliedType.Regular<LabeledVariable>(
        kClass = LabeledVariable::class,
        typeArguments = listOf(),
        isNullable = false,
    )

public fun <N> KoneContextRegistryBuilder.installPlanimetricsCalculationSpaceFor(numberType: SuppliedType<N>) {
    val polynomialType = SuppliedType.Regular<LabeledPolynomial<N>>(
        kClass = LabeledPolynomial::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType
            ),
        ),
        isNullable = false,
    )
    val pointType = SuppliedType.Regular<Point<N>>(
        kClass = Point::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    val lineType = SuppliedType.Regular<Line<N>>(
        kClass = Line::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    val quadricType = SuppliedType.Regular<Quadric<N>>(
        kClass = Quadric::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    val numberRing = contextsBuilder[Ring.Key(numberType)]
    val polynomialSpace = contextsBuilder.getOrNull(MultivariatePolynomialSpace.Key(numberType, labeledVariableType, polynomialType)) ?: numberRing.labeledPolynomialSpace
    val polynomialVectorKategory = contextsBuilder.getOrNull(VectorKategory.Key(polynomialType)) ?: polynomialSpace.vectorKategory()
    val planimetricsCalculationSpace = PlanimetricsCalculationSpace(numberRing, polynomialSpace, polynomialVectorKategory)
    val pointEquality = pointEquality(polynomialSpace)
    val lineEquality = lineEquality(polynomialSpace)
    val quadricEquality = quadricEquality(polynomialSpace)
    contextsBuilder[PlanimetricsCalculationSpace.Key(numberType)] = planimetricsCalculationSpace
    contextsBuilder[Equality.Key(pointType)] = pointEquality
    contextsBuilder[Equality.Key(lineType)] = lineEquality
    contextsBuilder[Equality.Key(quadricType)] = quadricEquality
}

public fun <N, R> KoneContextRegistry.inPlanimetricsCalculationSpaceFor(numberType: SuppliedType<N>, block: context(PlanimetricsCalculationSpace<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[PlanimetricsCalculationSpace.Key(numberType)])
}

public fun <N, R> KoneContextRegistry.inPlanimetricsCalculationSpaceScopeFor(numberType: SuppliedType<N>, block: context(PlanimetricsCalculationSpace<N>, Equality<Point<N>>, Equality<Line<N>>, Equality<Quadric<N>>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val pointType = SuppliedType.Regular<Point<N>>(
        kClass = Point::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    val lineType = SuppliedType.Regular<Line<N>>(
        kClass = Line::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    val quadricType = SuppliedType.Regular<Quadric<N>>(
        kClass = Quadric::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    return block(
        this.contexts[PlanimetricsCalculationSpace.Key(numberType)],
        this.contexts[Equality.Key(pointType)],
        this.contexts[Equality.Key(lineType)],
        this.contexts[Equality.Key(quadricType)],
    )
}