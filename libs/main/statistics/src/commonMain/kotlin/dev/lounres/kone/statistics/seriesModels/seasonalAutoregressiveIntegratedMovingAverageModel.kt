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


public data class SeasonalAutoregressiveMovingIntegratedAverageModelDescription<Number>(
    val autoregressiveParameters: AutoregressiveModelParameters<Number>,
    val integrationOrder: UInt,
    val movingAverageParameters: MovingAverageModelParameters<Number>,
    val seasonalPeriod: UInt,
    val seasonalAutoregressiveParameters: AutoregressiveModelParameters<Number>,
    val seasonalIntegrationOrder: UInt,
    val seasonalMovingAverageParameters: MovingAverageModelParameters<Number>,
    val initialValues: KoneList<Number>,
    val initialErrors: KoneList<Number>,
) {
    init {
        require(seasonalPeriod >= 1u) { TODO() }
        require(initialValues.size >= autoregressiveParameters.size + integrationOrder + (seasonalAutoregressiveParameters.size + seasonalIntegrationOrder) * seasonalPeriod) { TODO() }
        require(initialErrors.size >= movingAverageParameters.size + seasonalMovingAverageParameters.size * seasonalPeriod) { TODO() }
    }
}

public fun interface SeasonalAutoregressiveMovingIntegratedAverageModelGenerator<Number> : KoneSeriesModelGenerator<Number, SeasonalAutoregressiveMovingIntegratedAverageModelDescription<Number>> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<SeasonalAutoregressiveMovingIntegratedAverageModelGenerator<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<SeasonalAutoregressiveMovingIntegratedAverageModelGenerator<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelGenerator.Key<Number, SeasonalAutoregressiveMovingIntegratedAverageModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.SeasonalAutoregressiveMovingIntegratedAverageModelGenerator.Key<${suppliedTypeOf<Number>()}>"
    }
}

public interface SeasonalAutoregressiveMovingIntegratedAverageModelDescriber<Number> : KoneSeriesModelDescriber<Number, SeasonalAutoregressiveMovingIntegratedAverageModelDescription<Number>> {
    public val autoregressiveOrder: UInt
    public val integrationOrder: UInt
    public val movingAverageOrder: UInt
    public val seasonalPeriod: UInt
    public val seasonalAutoregressiveOrder: UInt
    public val seasonalIntegrationOrder: UInt
    public val seasonalMovingAverageOrder: UInt
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<SeasonalAutoregressiveMovingIntegratedAverageModelDescriber<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<SeasonalAutoregressiveMovingIntegratedAverageModelDescriber<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelDescriber.Key<Number, SeasonalAutoregressiveMovingIntegratedAverageModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.SeasonalAutoregressiveMovingIntegratedAverageModelDescriber.Key<${suppliedTypeOf<Number>()}>"
    }
}