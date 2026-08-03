/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics.seriesModels

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.statistics.KoneSeriesModelDescriber
import dev.lounres.kone.statistics.KoneSeriesModelGenerator


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

@GenerateKoneContextKey
public fun interface SeasonalAutoregressiveMovingIntegratedAverageModelGenerator<Number> : KoneSeriesModelGenerator<Number, SeasonalAutoregressiveMovingIntegratedAverageModelDescription<Number>> {
    public companion object;
}

@GenerateKoneContextKey
public interface SeasonalAutoregressiveMovingIntegratedAverageModelDescriber<Number> : KoneSeriesModelDescriber<Number, SeasonalAutoregressiveMovingIntegratedAverageModelDescription<Number>> {
    public val autoregressiveOrder: UInt
    public val integrationOrder: UInt
    public val movingAverageOrder: UInt
    public val seasonalPeriod: UInt
    public val seasonalAutoregressiveOrder: UInt
    public val seasonalIntegrationOrder: UInt
    public val seasonalMovingAverageOrder: UInt
    
    public companion object;
}