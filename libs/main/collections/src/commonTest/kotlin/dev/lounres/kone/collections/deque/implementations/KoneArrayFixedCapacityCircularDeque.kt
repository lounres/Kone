/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque.implementations

import dev.lounres.kone.assertions.AssertionScope
import dev.lounres.kone.assertions.Expect
import dev.lounres.kone.assertions.fail
import dev.lounres.kone.assertions.notToBe
import dev.lounres.kone.assertions.of
import dev.lounres.kone.assertions.toBe
import dev.lounres.kone.collections.deque.DequeImplementationDescription
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.KoneDequeProducer
import dev.lounres.kone.collections.deque.KoneDequeValidator
import dev.lounres.kone.collections.deque.KoneFixedCapacityDequeProducer


object KoneArrayFixedCapacityCircularDequeDescription : DequeImplementationDescription {
    override val name: String get() = "KoneArrayFixedCapacityCircularDeque"
    
    override val dequeProducer: KoneDequeProducer = object : KoneFixedCapacityDequeProducer {
        override fun <Element> produce(capacity: UInt): KoneDeque<Element> = KoneArrayFixedCapacityCircularDeque(capacity)
    }
    
    override val dequeValidator: KoneDequeValidator = object : KoneDequeValidator {
        context(_: AssertionScope)
        override fun <Element : Any> validate(deque: KoneDeque<Element>) {
            if (deque !is KoneArrayFixedCapacityCircularDeque) {
                fail("Deque is invalid")
                return
            }
            
            for (shift in 0u ..< deque.capacity) {
                if (shift < deque.size) {
                    Expect of deque.data[(deque.start + shift) %  deque.capacity] notToBe null
                } else {
                    Expect of deque.data[(deque.start + shift) %  deque.capacity] toBe null
                }
            }
            
            Expect of deque.end toBe (deque.start + deque.size + deque.capacity - 1u) % deque.capacity
        }
    }
}