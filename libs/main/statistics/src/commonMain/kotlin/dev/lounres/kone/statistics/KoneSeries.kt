/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics

import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.repeat
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public fun interface KoneSeries<Element> : KoneSequence<Element> {
    public operator fun get(index: UInt): Element {
        val iterator = iterator()
        if (index > 0u) repeat(index - 1u) { iterator.moveNext() }
        return iterator.next()
    }
}

public fun interface KoneSeriesModelGenerator<Element, SeriesModelDescription> : KoneContext {
    public fun SeriesModelDescription.generate(): KoneSeries<Element>
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Element, @Supply SeriesModelDescription> : SuppliedTypeRegistryKey<KoneSeriesModelGenerator<Element, SeriesModelDescription>>() {
        override fun toString(): String = "dev.lounres.kone.statistics.KoneSeriesModelGenerator.Key<${suppliedTypeOf<Element>()}, ${suppliedTypeOf<SeriesModelDescription>()}>"
    }
}

context(model: KoneSeriesModelGenerator<Element, SeriesModelDescription>)
public fun <Element, SeriesModelDescription> SeriesModelDescription.generate(): KoneSeries<Element> = with(model) { this@generate.generate() }

public fun interface KoneSeriesModelDescriber<Element, SeriesModelDescription> : KoneContext {
    public fun KoneList<Element>.describe(): SeriesModelDescription
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Element, @Supply SeriesModelDescription> : SuppliedTypeRegistryKey<KoneSeriesModelDescriber<Element, SeriesModelDescription>>() {
        override fun toString(): String = "dev.lounres.kone.statistics.KoneSeriesModelDescriber.Key<${suppliedTypeOf<Element>()}, ${suppliedTypeOf<SeriesModelDescription>()}>"
    }
}

context(modelDescriber: KoneSeriesModelDescriber<Element, SeriesModelDescription>)
public fun <Element, SeriesModelDescription> KoneList<Element>.describe(): SeriesModelDescription = with(modelDescriber) { this@describe.describe() }

context(_: KoneSeriesModelGenerator<Element, SeriesModelDescription>, _: KoneSeriesModelDescriber<Element, SeriesModelDescription>)
public fun <Element, SeriesModelDescription> KoneList<Element>.forecast(): KoneSeries<Element> = this.describe().generate()