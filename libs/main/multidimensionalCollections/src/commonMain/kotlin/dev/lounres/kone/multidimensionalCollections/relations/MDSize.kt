/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.relations

import dev.lounres.kone.collections.array.contentHashCode
import dev.lounres.kone.multidimensionalCollections.MDSize
import dev.lounres.kone.multidimensionalCollections.contentEquals
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing


private object MDSizeEquality : Equality<MDSize> {
    override fun MDSize.equalsTo(other: MDSize): Boolean = this contentEquals other
}

public fun MDSize.Companion.equality(): Equality<MDSize> = MDSizeEquality

private object MDSizeHashing : Hashing<MDSize> {
    override fun MDSize.hash(): Int = this.sizes.contentHashCode()
}

public fun MDSize.Companion.hashing(): Hashing<MDSize> = MDSizeHashing