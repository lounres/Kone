/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.producers

import dev.lounres.kone.collections.KoneGrowableMutableList
import dev.lounres.kone.collections.KoneGrowableMutableNoddedList
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.KoneMutableNoddedList
import dev.lounres.kone.collections.KoneNoddedList
import dev.lounres.kone.collections.KoneSettableList
import dev.lounres.kone.collections.KoneSettableNoddedList


public interface KoneListProducer {
    public fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneList<E>
}

public interface KoneSettableListProducer : KoneListProducer {
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneSettableList<E>
}

public interface KoneResizableMutableListProducer : KoneSettableListProducer {
    public fun <E> produce(): KoneMutableList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneMutableList<E>
}

public interface KoneGrowableMutableListProducer : KoneSettableListProducer {
    public fun <E> produce(): KoneGrowableMutableList<E> = produce(0u)
    public fun <E> produce(initialCapacity: UInt): KoneGrowableMutableList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneGrowableMutableList<E> = produceBy(number, number, builder)
    public fun <E> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> E): KoneGrowableMutableList<E>
}

public interface KoneFixedCapacityMutableListProducer : KoneSettableListProducer {
    public fun <E> produce(capacity: UInt): KoneMutableList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneMutableList<E> = produceBy(number, number, builder)
    public fun <E> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> E): KoneMutableList<E>
}

public interface KoneNoddedListProducer : KoneListProducer {
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneNoddedList<E>
}

public interface KoneSettableNoddedListProducer : KoneNoddedListProducer, KoneSettableListProducer {
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneSettableNoddedList<E>
}

public interface KoneResizableMutableNoddedListProducer : KoneSettableNoddedListProducer, KoneResizableMutableListProducer {
    override fun <E> produce(): KoneMutableNoddedList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneMutableNoddedList<E>
}

public interface KoneGrowableMutableNoddedListProducer : KoneSettableListProducer {
    public fun <E> produce(): KoneGrowableMutableNoddedList<E> = produce(0u)
    public fun <E> produce(initialCapacity: UInt): KoneGrowableMutableNoddedList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneGrowableMutableNoddedList<E> = produceBy(number, number, builder)
    public fun <E> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> E): KoneGrowableMutableNoddedList<E>
}

public interface KoneFixedCapacityMutableNoddedListProducer : KoneSettableListProducer {
    public fun <E> produce(capacity: UInt): KoneMutableNoddedList<E>
    override fun <E> produceBy(number: UInt, builder: (UInt) -> E): KoneMutableNoddedList<E> = produceBy(number, number, builder)
    public fun <E> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> E): KoneMutableNoddedList<E>
}