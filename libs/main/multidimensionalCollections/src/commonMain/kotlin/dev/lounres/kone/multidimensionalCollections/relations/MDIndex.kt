/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.relations

import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.contentEquals
import dev.lounres.kone.multidimensionalCollections.contentHashCode
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing


private object MDIndexEquality : Equality<MDIndex> {
    override fun MDIndex.equalsTo(other: MDIndex): Boolean = this contentEquals other
}

public fun MDIndex.Companion.equality(): Equality<MDIndex> = MDIndexEquality

private object MDIndexHashing : Hashing<MDIndex> {
    override fun MDIndex.hash(): Int = this.contentHashCode()
}

public fun MDIndex.Companion.hashing(): Hashing<MDIndex> = MDIndexHashing