/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics.seriesModels

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.statistics.KoneSeriesModelDescriber
import dev.lounres.kone.statistics.KoneSeriesModelGenerator
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public data class AutoregressiveMovingAverageModelDescription<Number>(
    val autoregressiveParameters: AutoregressiveModelParameters<Number>,
    val movingAverageParameters: MovingAverageModelParameters<Number>,
    val initialValues: KoneList<Number>,
    val initialErrors: KoneList<Number>,
) {
    init {
        require(initialValues.size >= autoregressiveParameters.size) { TODO() }
        require(initialErrors.size >= movingAverageParameters.size) { TODO() }
    }
}

public fun interface AutoregressiveMovingAverageModelGenerator<Number> : KoneSeriesModelGenerator<Number, AutoregressiveMovingAverageModelDescription<Number>> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<AutoregressiveMovingAverageModelGenerator<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<AutoregressiveMovingAverageModelGenerator<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelGenerator.Key<Number, AutoregressiveMovingAverageModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.AutoregressiveMovingAverageModelGenerator.Key<${suppliedTypeOf<Number>()}>"
    }
}

public interface AutoregressiveMovingAverageModelDescriber<Number> : KoneSeriesModelDescriber<Number, AutoregressiveMovingAverageModelDescription<Number>> {
    public val autoregressiveOrder: UInt
    public val movingAverageOrder: UInt
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<AutoregressiveMovingAverageModelDescriber<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<AutoregressiveMovingAverageModelDescriber<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelDescriber.Key<Number, AutoregressiveMovingAverageModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.AutoregressiveMovingAverageModelDescriber.Key<${suppliedTypeOf<Number>()}>"
    }
}