/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics.seriesModels.generatorsImplementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.getOrElse
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.registry.*
import dev.lounres.kone.statistics.KoneSeries
import dev.lounres.kone.statistics.seriesModels.MovingAverageModelDescription
import dev.lounres.kone.statistics.seriesModels.MovingAverageModelGenerator
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class MovingAverageModelGeneratorViaDefault<Number>(
    private val ring: CommutativeRing<Number>,
) : MovingAverageModelGenerator<Number> {
    override fun MovingAverageModelDescription<Number>.generate(): KoneSeries<Number> {
        KoneContext.localUnwrap(ring)
        val (parameters, initialErrors) = this
        return object : KoneSeries<Number> {
            val results = KoneList.generate(initialErrors.size + parameters.size) { resultIndex ->
                initialErrors.getOrElse(resultIndex) { ring.zero } +
                        (resultIndex - minOf(resultIndex, parameters.size) ..< minOf(resultIndex, initialErrors.size)).asKoneSequence().sumOf {
                            initialErrors[it] * parameters[resultIndex - it - 1u]
                        }
            }
            
            override fun iterator(): KoneIterator<Number> = object : KoneIterator<Number> {
                var currentIndex = 0u
                override fun hasNext(): Boolean = true
                override fun getNext(): Number = if (currentIndex < results.size) results[currentIndex] else ring.zero
                override fun moveNext() {
                    currentIndex++
                }
                
                override fun equals(other: Any?): Boolean = this === other
                override fun hashCode(): Int = super.hashCode()
                override fun toString(): String = "${super.toString()}[current index = $currentIndex]"
            }
            override fun get(index: UInt): Number = if (index < results.size) results[index] else ring.zero
        }
    }
}

public fun <Number> MovingAverageModelGenerator.Companion.viaDefault(
    ring: CommutativeRing<Number>,
): MovingAverageModelGenerator<Number> = MovingAverageModelGeneratorViaDefault(
    ring = ring,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object MovingAverageModelGeneratorDefaultSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> MovingAverageModelGenerator.Companion.viaDefault(): MovingAverageModelGenerator<Number> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaDefault(
            ring = koneContextRegistry[CommutativeRing.Key<Number>()], // TODO: Replace with 'requestFor'
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number> MovingAverageModelGenerator.Companion.setViaDefault(
        ring: CommutativeRing<Number>,
    ) {
        MovingAverageModelGenerator.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number>(
                ring = ring,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number> MovingAverageModelGenerator.Companion.setViaDefault() {
        MovingAverageModelGenerator.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number>()
        }
    }
}