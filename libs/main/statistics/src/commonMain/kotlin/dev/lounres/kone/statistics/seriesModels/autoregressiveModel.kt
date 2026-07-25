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
public value class AutoregressiveModelParameters<Number>(public val parameters: KoneList<Number>): KoneList<Number> by parameters

public data class AutoregressiveModelDescription<Number>(
    val parameters: AutoregressiveModelParameters<Number>,
    val initialValues: KoneList<Number>,
) {
    init {
        require(initialValues.size >= parameters.size) { TODO() }
    }
}

public fun interface AutoregressiveModelGenerator<Number> : KoneSeriesModelGenerator<Number, AutoregressiveModelDescription<Number>> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<AutoregressiveModelGenerator<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<AutoregressiveModelGenerator<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelGenerator.Key<Number, AutoregressiveModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.AutoregressiveModelGenerator.Key<${suppliedTypeOf<Number>()}>"
    }
}

public interface AutoregressiveModelDescriber<Number> : KoneSeriesModelDescriber<Number, AutoregressiveModelDescription<Number>> {
    public val order: UInt
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<AutoregressiveModelDescriber<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<AutoregressiveModelDescriber<Number>> by lazy {
            ImpliedKeysRegistry {
                KoneSeriesModelDescriber.Key<Number, AutoregressiveModelDescription<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.statistics.seriesModels.AutoregressiveModelDescriber.Key<${suppliedTypeOf<Number>()}>"
    }
}