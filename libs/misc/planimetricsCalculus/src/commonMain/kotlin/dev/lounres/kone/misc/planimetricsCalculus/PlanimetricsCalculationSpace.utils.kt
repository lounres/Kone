/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.planimetricsCalculus

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
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KVariance


internal val labeledVariableType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular<LabeledVariable>(
        fullyQualifiedName = "dev.lounres.kone.polynomial.LabeledVariable",
        typeArguments = listOf(),
        isNullable = false,
    )

public fun <N> KoneContextRegistryBuilder.installPlanimetricsCalculationSpaceFor(numberType: SuppliedType<N>) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val polynomialType = SuppliedType.Regular<LabeledPolynomial<N>>(
        fullyQualifiedName = "dev.lounres.kone.polynomial.LabeledPolynomial",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType
            ),
        ),
        isNullable = false,
    )
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val pointType = SuppliedType.Regular<Point<N>>(
        fullyQualifiedName = "dev.lounres.kone.misc.planimetricsCalculus.Point",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val lineType = SuppliedType.Regular<Line<N>>(
        fullyQualifiedName = "dev.lounres.kone.misc.planimetricsCalculus.Line",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val quadricType = SuppliedType.Regular<Quadric<N>>(
        fullyQualifiedName = "dev.lounres.kone.misc.planimetricsCalculus.Quadric",
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
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val pointType = SuppliedType.Regular<Point<N>>(
        fullyQualifiedName = "dev.lounres.kone.misc.planimetricsCalculus.Point",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val lineType = SuppliedType.Regular<Line<N>>(
        fullyQualifiedName = "dev.lounres.kone.misc.planimetricsCalculus.Line",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                KVariance.INVARIANT,
                numberType,
            )
        ),
        isNullable = false,
    )
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val quadricType = SuppliedType.Regular<Quadric<N>>(
        fullyQualifiedName = "dev.lounres.kone.misc.planimetricsCalculus.Quadric",
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