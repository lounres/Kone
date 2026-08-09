/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque.implementations

import dev.lounres.kone.assertions.AssertionScope
import dev.lounres.kone.assertions.fail
import dev.lounres.kone.collections.deque.DequeImplementationDescription
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.KoneDequeProducer
import dev.lounres.kone.collections.deque.KoneDequeValidator
import dev.lounres.kone.collections.deque.KoneResizableDequeProducer
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.repeat
import dev.lounres.kone.scope


object KoneArrayResizableCircularDequeDescription : DequeImplementationDescription {
    override val name: String get() = "KoneArrayResizableCircularDeque"
    
    override val dequeProducer: KoneDequeProducer = object : KoneResizableDequeProducer {
        override fun <Element> produce(): KoneDeque<Element> = KoneArrayResizableCircularDeque()
    }
    
    override val dequeValidator: KoneDequeValidator = object : KoneDequeValidator {
        context(_: AssertionScope)
        override fun <Element : Any> validate(deque: KoneDeque<Element>) {
            if (deque !is KoneArrayResizableCircularDeque) {
                fail("The list is invalid")
                return
            }
            
            if (deque.isDisposed) fail("The list is invalid")
            
            val dataSizeNumber = deque.dataSizeNumber
            val sizeLowerBound = deque.sizeLowerBound
            val sizeUpperBound = deque.sizeUpperBound
            val size = deque.size
            val start = deque.start
            val end = deque.end
            val data = deque.data
            
            if (dataSizeNumber !in 1u..31u) fail("The list is invalid")
            if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) fail("The list is invalid")
            if (size > sizeUpperBound) fail("The list is invalid")
            if (data.size != sizeUpperBound) fail("The list is invalid")
            
            scope {
                var currentIndex = start
                repeat(sizeUpperBound) { iteration ->
                    if ((iteration == (size + sizeUpperBound - 1u) % sizeUpperBound) != (currentIndex == end)) fail("The list is invalid")
                    if ((iteration < size) != (data[currentIndex] != null)) fail("The list is invalid")
                    currentIndex = (currentIndex + 1u) % sizeUpperBound
                }
            }
        }
    }
}