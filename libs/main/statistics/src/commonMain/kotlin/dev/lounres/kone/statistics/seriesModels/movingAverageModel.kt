/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(DelicateCollectionsInheritanceAPI::class)

package dev.lounres.kone.statistics.seriesModels

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.statistics.KoneSeriesModelDescriber
import dev.lounres.kone.statistics.KoneSeriesModelGenerator
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

@GenerateKoneContextKey
public fun interface MovingAverageModelGenerator<Number> : KoneSeriesModelGenerator<Number, MovingAverageModelDescription<Number>> {
    public companion object;
}

@GenerateKoneContextKey
public interface MovingAverageModelDescriber<Number> : KoneSeriesModelDescriber<Number, MovingAverageModelDescription<Number>> {
    public val order: UInt
    
    public companion object;
}