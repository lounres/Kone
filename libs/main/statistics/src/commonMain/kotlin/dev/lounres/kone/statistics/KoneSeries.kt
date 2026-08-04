/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.statistics

import dev.lounres.kone.collections.iterator.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.sequence.KoneSequence
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.repeat


public fun interface KoneSeries<Element> : KoneSequence<Element> {
    public operator fun get(index: UInt): Element {
        val iterator = iterator()
        if (index > 0u) repeat(index - 1u) { iterator.moveNext() }
        return iterator.next()
    }
}

@GenerateKoneContextKey
public fun interface KoneSeriesModelGenerator<Element, SeriesModelDescription> : KoneContext {
    public fun SeriesModelDescription.generate(): KoneSeries<Element>
    
    public companion object;
}

context(model: KoneSeriesModelGenerator<Element, SeriesModelDescription>)
public fun <Element, SeriesModelDescription> SeriesModelDescription.generate(): KoneSeries<Element> = with(model) { this@generate.generate() }

@GenerateKoneContextKey
public fun interface KoneSeriesModelDescriber<Element, SeriesModelDescription> : KoneContext {
    public fun KoneList<Element>.describe(): SeriesModelDescription
    
    public companion object;
}

context(modelDescriber: KoneSeriesModelDescriber<Element, SeriesModelDescription>)
public fun <Element, SeriesModelDescription> KoneList<Element>.describe(): SeriesModelDescription = with(modelDescriber) { this@describe.describe() }

context(_: KoneSeriesModelGenerator<Element, SeriesModelDescription>, _: KoneSeriesModelDescriber<Element, SeriesModelDescription>)
public fun <Element, SeriesModelDescription> KoneList<Element>.forecast(): KoneSeries<Element> = this.describe().generate()