/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.producers.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.repeat


//public fun <Element> KoneTwoThreeTreeList(): KoneTwoThreeTreeList<Element> =
//    KoneArrayResizableNoddedList(size = 0u)

// TODO: Maybe there is a better tree reconstruction?..
public inline fun <Element> KoneTwoThreeTreeList(size: UInt, initializer: (index: UInt) -> Element): KoneTwoThreeTreeList<Element> =
    KoneTwoThreeTreeList<Element>().apply {
        repeat(size) { add(initializer(it)) }
    }

public object KoneTwoThreeTreeListProducer : KoneResizableMutableNoddedListProducer {
    override fun <Element> produce(): KoneTwoThreeTreeList<Element> = KoneTwoThreeTreeList()
    override fun <Element> produceBy(number: UInt, builder: (UInt) -> Element): KoneTwoThreeTreeList<Element> =
        KoneTwoThreeTreeList(size = number, initializer = builder)
}