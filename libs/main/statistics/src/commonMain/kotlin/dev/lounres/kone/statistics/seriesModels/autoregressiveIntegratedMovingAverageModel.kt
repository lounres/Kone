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


public data class AutoregressiveMovingIntegratedAverageModelDescription<Number>(
    val autoregressiveParameters: AutoregressiveModelParameters<Number>,
    val integrationOrder: UInt,
    val movingAverageParameters: MovingAverageModelParameters<Number>,
    val initialValues: KoneList<Number>,
    val initialErrors: KoneList<Number>,
) {
    init {
        require(initialValues.size >= autoregressiveParameters.size + integrationOrder) { TODO() }
        require(initialErrors.size >= movingAverageParameters.size) { TODO() }
    }
}

public fun interface AutoregressiveMovingIntegratedAverageModelGenerator<Number> : KoneSeriesModelGenerator<Number, AutoregressiveMovingIntegratedAverageModelDescription<Number>> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<AutoregressiveMovingIntegratedAverageModelGenerator<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<AutoregressiveMovingIntegratedAverageModelGenerator<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelGenerator.Key<Number, AutoregressiveMovingIntegratedAverageModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.AutoregressiveMovingIntegratedAverageModelGenerator.Key<${suppliedTypeOf<Number>()}>"
    }
}

public interface AutoregressiveMovingIntegratedAverageModelDescriber<Number> : KoneSeriesModelDescriber<Number, AutoregressiveMovingIntegratedAverageModelDescription<Number>> {
    public val autoregressiveOrder: UInt
    public val integrationOrder: UInt
    public val movingAverageOrder: UInt
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<AutoregressiveMovingIntegratedAverageModelDescriber<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<AutoregressiveMovingIntegratedAverageModelDescriber<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelDescriber.Key<Number, AutoregressiveMovingIntegratedAverageModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.AutoregressiveMovingIntegratedAverageModelDescriber.Key<${suppliedTypeOf<Number>()}>"
    }
}