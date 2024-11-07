/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.producers

import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.KoneMutableNoddedList
import dev.lounres.kone.collections.KoneNoddedList
import dev.lounres.kone.collections.KoneSettableList
import dev.lounres.kone.collections.KoneSettableNoddedList


public interface KoneListProducer {
    public fun <E> produce(): KoneList<E>
    public fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneList<E>
}

public interface KoneSettableListProducer : KoneListProducer {
    override fun <E> produce(): KoneSettableList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneSettableList<E>
}

public interface KoneMutableListProducer : KoneSettableListProducer {
    override fun <E> produce(): KoneMutableList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneMutableList<E>
}

public interface KoneNoddedListProducer : KoneListProducer {
    override fun <E> produce(): KoneNoddedList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneNoddedList<E>
}

public interface KoneSettableNoddedListProducer : KoneNoddedListProducer, KoneSettableListProducer {
    override fun <E> produce(): KoneSettableNoddedList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneSettableNoddedList<E>
}

public interface KoneMutableNoddedListProducer : KoneSettableNoddedListProducer, KoneMutableListProducer {
    override fun <E> produce(): KoneMutableNoddedList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneMutableNoddedList<E>
}