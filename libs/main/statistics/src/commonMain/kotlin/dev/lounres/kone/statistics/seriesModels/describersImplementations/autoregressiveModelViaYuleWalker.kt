/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics.seriesModels.describersImplementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.algorithms.LDLDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.ldlDecomposition
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.sum
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.registry.*
import dev.lounres.kone.scope
import dev.lounres.kone.statistics.seriesModels.AutoregressiveModelDescriber
import dev.lounres.kone.statistics.seriesModels.AutoregressiveModelDescription
import dev.lounres.kone.statistics.seriesModels.AutoregressiveModelParameters
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class AutoregressiveModelDescriberViaYuleWalker<Number>(
    override val order: UInt,
    private val field: Field<Number>,
    private val ldlDecompositionComputer: LDLDecompositionComputer<Number, MDList2<Number>>,
) : AutoregressiveModelDescriber<Number> {
    override fun KoneList<Number>.describe(): AutoregressiveModelDescription<Number> {
        val size = this.size
        require(size >= order + 1u) { TODO() }
        
        KoneContext.localUnwrap(field, ldlDecompositionComputer)
        
        val mean = this.sum() / size
        val seriesMinusMean = this.map { it - mean }
        val sampleCovariances = KoneList.generate(order + 1u) { shift ->
            (shift ..< size).asKoneSequence().sumOf { seriesMinusMean[it] * seriesMinusMean[it - shift] } / size
        }
        val covarianceMatrix = MDList2.generate(order, order) { row, column ->
            if (row >= column) sampleCovariances[row - column] else sampleCovariances[column - row]
        }
        val covarianceMatrixLDLDecomposition = covarianceMatrix.ldlDecomposition()
        val covarianceVector = MDList1.generate(order) { sampleCovariances[it + 1u] }
        val autoregressiveModelParameters = scope { // TODO: Move the linear system solving somewhere else...
            val firstSolution = SettableMDList1.generate(order) { field.zero }
            for (i in 0u ..< order) {
                firstSolution[i] = covarianceVector[i] - (0u ..< i).asKoneSequence().sumOf {
                    firstSolution[it] * covarianceMatrixLDLDecomposition.leftLowerTriangular[i, it]
                }
            }
            for (i in 0u ..< order) {
                firstSolution[i] /= covarianceMatrixLDLDecomposition.middleDiagonal[i, i]
            }
            val secondSolution = SettableMDList1.generate(order) { field.zero }
            for (i in order - 1u downTo 0u) {
                secondSolution[i] = firstSolution[i] - (i + 1u ..< order).asKoneSequence().sumOf {
                    secondSolution[it] * covarianceMatrixLDLDecomposition.rightUpperTriangular[i, it]
                }
            }
            AutoregressiveModelParameters(KoneList.generate(order) { secondSolution[it] })
        }
        return AutoregressiveModelDescription(
            parameters = autoregressiveModelParameters,
            initialValues = this,
        )
    }
}

public fun <Number> AutoregressiveModelDescriber.Companion.viaYuleWalker(
    order: UInt,
    field: Field<Number>,
    ldlDecompositionComputer: LDLDecompositionComputer<Number, MDList2<Number>>,
): AutoregressiveModelDescriber<Number> = AutoregressiveModelDescriberViaYuleWalker(
    order = order,
    field = field,
    ldlDecompositionComputer = ldlDecompositionComputer,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object AutoregressiveModelDescriberYuleWalkerSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> AutoregressiveModelDescriber.Companion.viaYuleWalker(order: UInt): AutoregressiveModelDescriber<Number> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaYuleWalker(
            order = order,
            field = koneContextRegistry[Field.Key<Number>()], // TODO: Replace with 'requestFor'
            ldlDecompositionComputer = koneContextRegistry[LDLDecompositionComputer.Key<Number, MDList2<Number>>()], // TODO: Replace with 'requestFor'
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number> AutoregressiveModelDescriber.Companion.setViaYuleWalker(
        order: UInt,
        field: Field<Number>,
        ldlDecompositionComputer: LDLDecompositionComputer<Number, MDList2<Number>>,
    ) {
        AutoregressiveModelDescriber.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaYuleWalker<Number>(
                order = order,
                field = field,
                ldlDecompositionComputer = ldlDecompositionComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number> AutoregressiveModelDescriber.Companion.setViaYuleWalker(order: UInt) {
        AutoregressiveModelDescriber.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaYuleWalker<Number>(order = order)
        }
    }
}