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
public value class AutoregressiveModelParameters<Number>(public val parameters: KoneList<Number>): KoneList<Number> by parameters

public data class AutoregressiveModelDescription<Number>(
    val parameters: AutoregressiveModelParameters<Number>,
    val initialValues: KoneList<Number>,
) {
    init {
        require(initialValues.size >= parameters.size) { TODO() }
    }
}

@GenerateKoneContextKey
public fun interface AutoregressiveModelGenerator<Number> : KoneSeriesModelGenerator<Number, AutoregressiveModelDescription<Number>> {
    public companion object;
}

@GenerateKoneContextKey
public interface AutoregressiveModelDescriber<Number> : KoneSeriesModelDescriber<Number, AutoregressiveModelDescription<Number>> {
    public val order: UInt
    
    public companion object;
}