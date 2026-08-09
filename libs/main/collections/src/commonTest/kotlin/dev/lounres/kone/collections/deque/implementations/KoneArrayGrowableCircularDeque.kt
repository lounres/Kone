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
import dev.lounres.kone.collections.deque.KoneGrowableDequeProducer
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.iterable.contains
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.repeat
import dev.lounres.kone.scope


object KoneArrayGrowableCircularDequeDescription : DequeImplementationDescription {
    override val name: String get() = "KoneArrayGrowableCircularDeque"
    
    override val dequeProducer: KoneDequeProducer = object : KoneGrowableDequeProducer {
        override fun <Element> produce(): KoneDeque<Element> = KoneArrayGrowableCircularDeque()
        override fun <Element> produce(initialCapacity: UInt): KoneDeque<Element> = KoneArrayGrowableCircularDeque(initialCapacity)
    }
    
    override val dequeValidator: KoneDequeValidator = object : KoneDequeValidator {
        context(_: AssertionScope)
        override fun <Element : Any> validate(deque: KoneDeque<Element>) {
            if (deque !is KoneArrayGrowableCircularDeque) {
                fail("The list is invalid")
                return
            }
            if (deque.isDisposed) fail("The list is invalid")
            
            val sizeUpperBound = deque.sizeUpperBound
            val size = deque.size
            val start = deque.start
            val end = deque.end
            val data = deque.data
            
            if ((Equality.defaultFor<UInt>()) { sizeUpperBound !in POWERS_OF_2 }) fail("The list is invalid")
            if (size > sizeUpperBound) fail("The list is invalid")
            if (data.size != sizeUpperBound) fail("The list is invalid")
            
            scope {
                var currentIndex = start
                repeat(sizeUpperBound) { iteration ->
                    if ((iteration == (size + sizeUpperBound - 1u) % sizeUpperBound) != (currentIndex == end)) fail("The list is invalid")
                    if ((iteration < size) != (data[currentIndex] != null))fail("The list is invalid")
                    currentIndex = (currentIndex + 1u) % sizeUpperBound
                }
            }
        }
    }
}