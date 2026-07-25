/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(DelicateCollectionsInheritanceAPI::class)

package dev.lounres.kone.statistics.seriesModels

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.statistics.KoneSeriesModelDescriber
import dev.lounres.kone.statistics.KoneSeriesModelGenerator
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf
import kotlin.jvm.JvmInline


@JvmInline
public value class MovingAverageModelParameters<Number>(public val parameters: KoneList<Number>): KoneList<Number> by parameters

public data class MovingAverageModelDescription<Number>(
    val parameters: MovingAverageModelParameters<Number>,
    val initialErrors: KoneList<Number>,
) {
    init {
        require(initialErrors.size >= parameters.size) { TODO() }
    }
}

public fun interface MovingAverageModelGenerator<Number> : KoneSeriesModelGenerator<Number, MovingAverageModelDescription<Number>> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<MovingAverageModelGenerator<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<MovingAverageModelGenerator<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelGenerator.Key<Number, MovingAverageModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.MovingAverageModelGenerator.Key<${suppliedTypeOf<Number>()}>"
    }
}

public interface MovingAverageModelDescriber<Number> : KoneSeriesModelDescriber<Number, MovingAverageModelDescription<Number>> {
    public val order: UInt
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<MovingAverageModelDescriber<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<MovingAverageModelDescriber<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelDescriber.Key<Number, MovingAverageModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.MovingAverageModelDescriber.Key<${suppliedTypeOf<Number>()}>"
    }
}