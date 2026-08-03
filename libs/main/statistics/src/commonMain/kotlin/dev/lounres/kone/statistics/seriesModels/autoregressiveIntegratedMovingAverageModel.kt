/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics.seriesModels

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.statistics.KoneSeriesModelDescriber
import dev.lounres.kone.statistics.KoneSeriesModelGenerator


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

@GenerateKoneContextKey
public fun interface AutoregressiveMovingIntegratedAverageModelGenerator<Number> : KoneSeriesModelGenerator<Number, AutoregressiveMovingIntegratedAverageModelDescription<Number>> {
    public companion object;
}

@GenerateKoneContextKey
public interface AutoregressiveMovingIntegratedAverageModelDescriber<Number> : KoneSeriesModelDescriber<Number, AutoregressiveMovingIntegratedAverageModelDescription<Number>> {
    public val autoregressiveOrder: UInt
    public val integrationOrder: UInt
    public val movingAverageOrder: UInt
    
    public companion object;
}