/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics.seriesModels.generatorsImplementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.utils.plusAssign
import dev.lounres.kone.collections.utils.reversed
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.registry.*
import dev.lounres.kone.repeat
import dev.lounres.kone.statistics.KoneSeries
import dev.lounres.kone.statistics.seriesModels.AutoregressiveModelDescription
import dev.lounres.kone.statistics.seriesModels.AutoregressiveModelGenerator
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class AutoregressiveModelGeneratorViaDefault<Number>(
    private val ring: CommutativeRing<Number>,
) : AutoregressiveModelGenerator<Number> {
    override fun AutoregressiveModelDescription<Number>.generate(): KoneSeries<Number> = object : KoneSeries<Number> {
        val parameters = this@generate.parameters.reversed()
        val results = KoneArrayGrowableList<Number>().also { it += this@generate.initialValues }
        
        fun addNewValue() {
            KoneContext.localUnwrap(ring)
            results += (0u ..< parameters.size).asKoneSequence().sumOf { results[results.size + it - parameters.size] * parameters[it] }
        }
        
        override fun iterator(): KoneIterator<Number> = object : KoneIterator<Number> {
            var currentIndex = 0u
            override fun hasNext(): Boolean = true
            override fun getNext(): Number {
                if (currentIndex >= results.size) repeat(currentIndex - results.size + 1u) { addNewValue() }
                return results[currentIndex]
            }
            override fun moveNext() {
                currentIndex++
            }
        }
        override fun get(index: UInt): Number {
            if (index >= results.size) repeat(index - results.size + 1u) { addNewValue() }
            return results[index]
        }
    }
}

public fun <Number> AutoregressiveModelGenerator.Companion.viaDefault(
    ring: CommutativeRing<Number>,
): AutoregressiveModelGenerator<Number> = AutoregressiveModelGeneratorViaDefault(
    ring = ring,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object AutoregressiveModelGeneratorDefaultSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> AutoregressiveModelGenerator.Companion.viaDefault(): AutoregressiveModelGenerator<Number> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaDefault(
            ring = koneContextRegistry[CommutativeRing.Key<Number>()], // TODO: Replace with 'requestFor'
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number> AutoregressiveModelGenerator.Companion.setViaDefault(
        ring: CommutativeRing<Number>,
    ) {
        AutoregressiveModelGenerator.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number>(
                ring = ring,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number> AutoregressiveModelGenerator.Companion.setViaDefault() {
        AutoregressiveModelGenerator.Key<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number>()
        }
    }
}