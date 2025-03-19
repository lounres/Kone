/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.set.comparison

import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.toKoneSet
import dev.lounres.kone.comparison.*
import dev.lounres.kone.context


internal open class KoneSetEquality<Element>(val elementEquality: Equality<Element>) : Equality<KoneSet<out Element>> {
    override fun KoneSet<out Element>.equalsTo(other: KoneSet<out Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        
        val thisCopied = this.toKoneSet(elementEquality)
        if (thisCopied.size != this.size) return false
        val otherCopied = other.toKoneSet(elementEquality)
        if (otherCopied.size != other.size) return false
        for (element in thisCopied) if (context(elementEquality) { element !in otherCopied }) return false
        
        return true
    }
}

public fun <Element> koneSetEquality(elementEquality: Equality<Element>): Equality<KoneSet<out Element>> =
    KoneSetEquality(elementEquality)