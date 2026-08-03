/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics.seriesModels

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.statistics.KoneSeriesModelDescriber
import dev.lounres.kone.statistics.KoneSeriesModelGenerator


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

@GenerateKoneContextKey
public fun interface AutoregressiveMovingAverageModelGenerator<Number> : KoneSeriesModelGenerator<Number, AutoregressiveMovingAverageModelDescription<Number>> {
    public companion object;
}

@GenerateKoneContextKey
public interface AutoregressiveMovingAverageModelDescriber<Number> : KoneSeriesModelDescriber<Number, AutoregressiveMovingAverageModelDescription<Number>> {
    public val autoregressiveOrder: UInt
    public val movingAverageOrder: UInt
    
    public companion object;
}