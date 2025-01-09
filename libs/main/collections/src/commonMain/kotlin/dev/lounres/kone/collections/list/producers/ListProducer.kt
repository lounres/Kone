/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.producers

import dev.lounres.kone.collections.list.KoneGrowableMutableList
import dev.lounres.kone.collections.list.KoneGrowableMutableNoddedList
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.KoneNoddedList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.KoneSettableNoddedList


/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of [KoneList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneListProducer {
    /**
     * Produces this type list of provided [number] size and initialises the elements with the provided [builder].
     *
     * `i`th element of the list is set to `builder(i)`.
     * All [builder] invocations are computed consecutively on values from `0` to [number] exclusive
     * in their order starting with `0`.
     */
    public fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneList<Element>
}

/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of [KoneSettableList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneSettableListProducer : KoneListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneSettableList<Element>
}

/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of resizable [KoneMutableList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneResizableMutableListProducer : KoneSettableListProducer {
    /**
     * Produces empty list.
     */
    public fun <Element> produce(): KoneMutableList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableList<Element>
}

/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of growable [KoneMutableList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneGrowableMutableListProducer : KoneSettableListProducer {
    /**
     * Produces empty list.
     */
    public fun <Element> produce(): KoneGrowableMutableList<Element> = produce(0u)
    /**
     * Produces empty list with initial capacity not less than [initialCapacity].
     */
    public fun <Element> produce(initialCapacity: UInt): KoneGrowableMutableList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneGrowableMutableList<Element> = produceBy(number, number, builder)
    /**
     * Produces this type list of provided [initialCapacity] capacity and [number] size
     * and initialises the elements with the provided [builder].
     *
     * [number] must be not greater than [initialCapacity]. Otherwise [IllegalArgumentException] is thrown.
     * `i`th element of the list is set to `builder(i)`.
     * All [builder] invocations are computed consecutively on values from `0` to [number] exclusive
     * in their order starting with `0`.
     *
     * @throws IllegalArgumentException when [number] is greater than [initialCapacity]
     */
    public fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneGrowableMutableList<Element>
}

/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of fixed capacity [KoneMutableList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneFixedCapacityMutableListProducer : KoneSettableListProducer {
    /**
     * Produces empty list of the fixed provided [capacity].
     */
    public fun <Element> produce(capacity: UInt): KoneMutableList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableList<Element> = produceBy(number, number, builder)
    /**
     * Produces this type list of provided [capacity] and [number] size
     * and initialises the elements with the provided [builder].
     *
     * [number] must be not greater than [capacity]. Otherwise [IllegalArgumentException] is thrown.
     * `i`th element of the list is set to `builder(i)`.
     * All [builder] invocations are computed consecutively on values from `0` to [number] exclusive
     * in their order starting with `0`.
     *
     * @throws IllegalArgumentException when [number] is greater than [capacity]
     */
    public fun <Element> produceBy(capacity: UInt, number: UInt, builder: (UInt) -> Element): KoneMutableList<Element>
}

/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of [KoneNoddedList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneNoddedListProducer : KoneListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneNoddedList<Element>
}

/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of [KoneSettableNoddedList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneSettableNoddedListProducer : KoneNoddedListProducer, KoneSettableListProducer {
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneSettableNoddedList<Element>
}

/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of resizable [KoneMutableNoddedList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneResizableMutableNoddedListProducer : KoneSettableNoddedListProducer, KoneResizableMutableListProducer {
    override fun <Element> produce(): KoneMutableNoddedList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element>
}

/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of growable [KoneMutableNoddedList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneGrowableMutableNoddedListProducer : KoneSettableListProducer, KoneGrowableMutableListProducer {
    override fun <Element> produce(): KoneGrowableMutableNoddedList<Element> = produce(0u)
    override fun <Element> produce(initialCapacity: UInt): KoneGrowableMutableNoddedList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneGrowableMutableNoddedList<Element> = produceBy(number, number, builder)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneGrowableMutableNoddedList<Element>
}

/**
 * Represents a companion object (not in the Kotlin meaning but in common one) of fixed capacity [KoneMutableNoddedList] inheritor type
 * that abstract API of this type of lists creation.
 */
public interface KoneFixedCapacityMutableNoddedListProducer : KoneSettableListProducer, KoneFixedCapacityMutableListProducer {
    override fun <Element> produce(capacity: UInt): KoneMutableNoddedList<Element>
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element> = produceBy(number, number, builder)
    override fun <Element> produceBy(initialCapacity: UInt, number: UInt, builder: (UInt) -> Element): KoneMutableNoddedList<Element>
}