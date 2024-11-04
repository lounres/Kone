/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.producers

import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableList


public interface KoneListProducer {
    public fun <E> produce(): KoneList<E>
    public fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneList<E>
}

public interface KoneMutableListProducer : KoneListProducer {
    override fun <E> produce(): KoneMutableList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneMutableList<E>
}