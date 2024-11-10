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
    public fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneList<Element>
}

public interface KoneSettableListProducer : KoneListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneSettableList<Element>
}

public interface KoneResizableMutableListProducer : KoneSettableListProducer {
    public fun <Element> produce(): KoneMutableList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableList<Element>
}

public interface KoneGrowableMutableListProducer : KoneSettableListProducer {
    public fun <Element> produce(): KoneGrowableMutableList<Element> = produce(0u)
    public fun <Element> produce(initialCapacity: UInt): KoneGrowableMutableList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneGrowableMutableList<Element> = produceBy(number, number, builder)
    public fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneGrowableMutableList<Element>
}

public interface KoneFixedCapacityMutableListProducer : KoneSettableListProducer {
    public fun <Element> produce(capacity: UInt): KoneMutableList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableList<Element> = produceBy(number, number, builder)
    public fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneMutableList<Element>
}

public interface KoneNoddedListProducer : KoneListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneNoddedList<Element>
}

public interface KoneSettableNoddedListProducer : KoneNoddedListProducer, KoneSettableListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneSettableNoddedList<Element>
}

public interface KoneResizableMutableNoddedListProducer : KoneSettableNoddedListProducer, KoneResizableMutableListProducer {
    override fun <Element> produce(): KoneMutableNoddedList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element>
}

public interface KoneGrowableMutableNoddedListProducer : KoneSettableListProducer {
    public fun <Element> produce(): KoneGrowableMutableNoddedList<Element> = produce(0u)
    public fun <Element> produce(initialCapacity: UInt): KoneGrowableMutableNoddedList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneGrowableMutableNoddedList<Element> = produceBy(number, number, builder)
    public fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneGrowableMutableNoddedList<Element>
}

public interface KoneFixedCapacityMutableNoddedListProducer : KoneSettableListProducer {
    public fun <Element> produce(capacity: UInt): KoneMutableNoddedList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element> = produceBy(number, number, builder)
    public fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element>
}