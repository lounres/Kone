/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics.seriesModels.describersImplementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.localContexts
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.registry.*
import dev.lounres.kone.statistics.seriesModels.AutoregressiveModelDescriber
import dev.lounres.kone.statistics.seriesModels.AutoregressiveModelDescription
import dev.lounres.kone.statistics.seriesModels.AutoregressiveModelParameters
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class AutoregressiveModelDescriberViaBurg<Number>(
    override val order: UInt,
    private val field: Field<Number>,
) : AutoregressiveModelDescriber<Number> {
    override fun KoneList<Number>.describe(): AutoregressiveModelDescription<Number> {
        val size = this.size
        require(size >= order + 1u) { TODO() }
        
        KoneContext.localUnwrap(field)
        localContexts(field.numberTimesInt)
        
        val mean = this.sum() / size
        val seriesMinusMean = this.map { it - mean }
        var u = seriesMinusMean.reversed()
        var v = u
        var d = field.zero
        val phis = KoneArrayFixedCapacityList<Number>(order)
        for (i in 0u ..< order) {
            if (i == 0u) {
                val oldU = u
                val oldV = v
                d = (1u ..< size).asKoneSequence().sumOf { t -> oldU[t - 1u].let { it * it } + oldV[t].let { it * it } }
                val phi = (1u ..< size).asKoneSequence().sumOf { t -> oldU[t - 1u] * oldV[t] } * 2 / d
                phis += phi
                u = KoneList.generate(1u ..< size) { oldU[it - 1u] - phi * oldV[it] }
                v = KoneList.generate(1u ..< size) { oldV[it] - phi * oldU[it - 1u] }
            } else {
                val oldU = u
                val oldV = v
                d = (1 - phis.last().let { it * it }) * d - v.first().let { it * it } - u.last().let { it * it }
                val phi = (1u ..< size - i).asKoneSequence().sumOf { t -> u[t - 1u] * v[t] } * 2 / d
                phis += phi
                u = KoneList.generate(1u ..< (size - i)) { oldU[it - 1u] - phi * oldV[it] }
                v = KoneList.generate(1u ..< (size - i)) { oldV[it] - phi * oldU[it - 1u] }
            }
        }
        return AutoregressiveModelDescription(
            parameters = AutoregressiveModelParameters(phis),
            initialValues = this,
        )
    }
}

public fun <Number> AutoregressiveModelDescriber.Companion.viaBurg(
    order: UInt,
    field: Field<Number>,
): AutoregressiveModelDescriber<Number> = AutoregressiveModelDescriberViaBurg(
    order = order,
    field = field,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object AutoregressiveModelDescriberBurgSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> AutoregressiveModelDescriber.Companion.viaBurg(order: UInt): AutoregressiveModelDescriber<Number> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaBurg(
            order = order,
            field = koneContextRegistry[Field.Key<Number>()], // TODO: Replace with 'requestFor'
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number> AutoregressiveModelDescriber.Companion.setViaBurg(
        order: UInt,
        field: Field<Number>,
    ) {
        AutoregressiveModelDescriber.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaBurg<Number>(
                order = order,
                field = field,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number> AutoregressiveModelDescriber.Companion.setViaBurg(order: UInt) {
        AutoregressiveModelDescriber.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaBurg<Number>(order = order)
        }
    }
}