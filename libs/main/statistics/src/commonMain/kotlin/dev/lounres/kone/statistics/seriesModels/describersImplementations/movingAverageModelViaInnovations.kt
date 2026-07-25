/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics.seriesModels.describersImplementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.localContexts
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.registry.*
import dev.lounres.kone.statistics.seriesModels.MovingAverageModelDescriber
import dev.lounres.kone.statistics.seriesModels.MovingAverageModelDescription
import dev.lounres.kone.statistics.seriesModels.MovingAverageModelParameters
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class MovingAverageModelDescriberViaInnovations<Number>(
    override val order: UInt,
    private val field: Field<Number>,
) : MovingAverageModelDescriber<Number> {
    override fun KoneList<Number>.describe(): MovingAverageModelDescription<Number> {
        val size = this.size
        require(size >= order + 1u) { TODO() }
        
        KoneContext.localUnwrap(field)
        localContexts(field.numberTimesInt)
        
        fun kappa(i: UInt, j: UInt) = this[i] * this[j]
        val thetas = SettableMDList2.generate(order, order) { _, _ -> field.zero }
        val nus = KoneSettableList.generate(order) { field.zero }
        nus[0u] = kappa(0u, 0u)
        for (n in 0u ..< order) {
            nus[n] = kappa(n, n) - (0u ..< n).asKoneSequence().sumOf { j -> thetas[n - 1u, n - 1u - j].let { it * it } * nus[j] }
            for (k in 0u .. n)
                thetas[n, n - k] = (kappa(n + 1u, k) - (0u ..< k).asKoneSequence().sumOf { j -> thetas[k - 1u, k - 1u - j] * thetas[n, n - j] * nus[j] }) / nus[k]
        }
        val parameters = MovingAverageModelParameters(KoneList.generate(order) { thetas[order, it] })
        val initialErrors = KoneSettableList.generate(size) { field.zero }
        for (i in 0u ..< size) {
            initialErrors[i] = this[i] - (0u ..< minOf(i, order)).asKoneSequence().sumOf { j -> this[i - 1u - j] * parameters[j] }
        }
        return MovingAverageModelDescription(
            parameters = parameters,
            initialErrors = initialErrors
        )
    }
}

public fun <Number> MovingAverageModelDescriber.Companion.viaInnovations(
    order: UInt,
    field: Field<Number>,
): MovingAverageModelDescriber<Number> = MovingAverageModelDescriberViaInnovations(
    order = order,
    field = field,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object MovingAverageModelDescriberInnovationsSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> MovingAverageModelDescriber.Companion.viaInnovations(order: UInt): MovingAverageModelDescriber<Number> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaInnovations(
            order = order,
            field = koneContextRegistry[Field.Key<Number>()], // TODO: Replace with 'requestFor'
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number> MovingAverageModelDescriber.Companion.setViaInnovations(
        order: UInt,
        field: Field<Number>,
    ) {
        MovingAverageModelDescriber.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaInnovations<Number>(
                order = order,
                field = field,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number> MovingAverageModelDescriber.Companion.setViaInnovations(order: UInt) {
        MovingAverageModelDescriber.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaInnovations<Number>(order = order)
        }
    }
}