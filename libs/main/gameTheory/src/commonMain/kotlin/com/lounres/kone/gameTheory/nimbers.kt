/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.gameTheory

import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName


// https://en.wikipedia.org/wiki/Nimber
@JvmInline
public value class Nimber(public val value: UInt) : Comparable<Nimber> {
    public operator fun unaryPlus(): Nimber = this
    public operator fun unaryMinus(): Nimber = this
    public operator fun plus(other: Nimber): Nimber = Nimber(this.value xor other.value)
    public operator fun minus(other: Nimber): Nimber = Nimber(this.value xor other.value)
    public operator fun times(other: Nimber): Nimber = Nimber(this.value xor other.value)

    override infix fun compareTo(other: Nimber): Int = this.value compareTo other.value
    public operator fun rangeTo(other: Nimber): NimberRange = NimberRange(value, other.value)
    public operator fun rangeUntil(other: Nimber): NimberRange = NimberRange(value, other.value)
}

@Suppress("ConvertTwoComparisonsToRangeCheck")
@OptIn(ExperimentalStdlibApi::class)
public class NimberRange(
    public val first: UInt,
    public val last: UInt
) : Iterable<Nimber>, ClosedRange<Nimber>, OpenEndRange<Nimber> {
    private val backingUIntRange = first .. last

    override fun iterator(): Iterator<Nimber> = object : Iterator<Nimber> {
        val backingUIntRangeIterator = backingUIntRange.iterator()
        override fun hasNext(): Boolean = backingUIntRangeIterator.hasNext()
        override fun next(): Nimber = Nimber(backingUIntRangeIterator.next())
    }

    override val start: Nimber get() = Nimber(first)
    override val endInclusive: Nimber get() = Nimber(last)
    override val endExclusive: Nimber get() = Nimber(last + 1u)

    override fun isEmpty(): Boolean = first <= last
    override fun contains(value: Nimber): Boolean = first <= value.value && value.value <= last
}


@JvmName("mexUInt")
public fun mex(values: Iterable<UInt>) : UInt {
    var currentValue = 0u
    val impossibleValues = mutableSetOf<UInt>()
    for (value in values) when {
        value == currentValue -> {
            currentValue++
            while (currentValue == impossibleValues.firstOrNull()) {
                impossibleValues.remove(currentValue)
                currentValue++
            }
        }
        value > currentValue -> impossibleValues.add(value)
    }
    return currentValue
}

@JvmName("mexNimber")
public fun mex(nimbers: Iterable<Nimber>) : Nimber {
    var currentNimber = 0u
    val impossibleNimbers = mutableSetOf<UInt>()
    for (nimber in nimbers) when {
        nimber.value == currentNimber -> {
            currentNimber++
            while (currentNimber == impossibleNimbers.first()) {
                impossibleNimbers.remove(currentNimber)
                currentNimber++
            }
        }
        nimber.value > currentNimber -> impossibleNimbers.add(nimber.value)
    }
    return Nimber(currentNimber)
}

public fun unionOf(nimbers: Iterable<Nimber>) : Nimber = mex(nimbers)


@OptIn(ExperimentalStdlibApi::class)
internal fun times1(left: UInt, right: UInt): UInt = mex(buildList<UInt> {
    for (left2 in 0u ..< left) for (right2 in 0u ..< right)
        add(times1(left2, right) xor times1(left2, right2) xor times1(left, right2))
})

internal fun times2(left: UInt, right: UInt): UInt = mex((0u until left * right).map { times2(it % left, it / left) xor times2(left, it / left) xor times2(it % left, right) })