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
import dev.lounres.kone.collections.deque.KoneFixedCapacityDequeProducer
import dev.lounres.kone.collections.deque.KoneGrowableDequeProducer
import dev.lounres.kone.collections.deque.KoneResizableDequeProducer
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.contexts.KoneFixedCapacityMutableListProducer
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableListProducer


class KoneListBackedDequeDescription(
    private val listImplementationDescription: ListImplementationDescription
) : DequeImplementationDescription {
    override val name: String = "KoneListBackedDeque over ${listImplementationDescription.name}"
    
    override val dequeProducer: KoneDequeProducer = when (val listProducer = listImplementationDescription.listProducer) {
        is KoneResizableMutableListProducer -> object : KoneResizableDequeProducer {
            override fun <Element> produce(): KoneDeque<Element> = KoneListBackedDeque(listProducer.produce())
        }
        is KoneGrowableMutableListProducer -> object : KoneGrowableDequeProducer {
            override fun <Element> produce(): KoneDeque<Element> = KoneListBackedDeque(listProducer.produce())
            override fun <Element> produce(initialCapacity: UInt): KoneDeque<Element> = KoneListBackedDeque(listProducer.produce(initialCapacity))
        }
        is KoneFixedCapacityMutableListProducer -> object : KoneFixedCapacityDequeProducer {
            override fun <Element> produce(capacity: UInt): KoneDeque<Element> = KoneListBackedDeque(listProducer.produce(capacity))
        }
        else -> error("Unsupported list producer for KoneListBackedDeque")
    }
    
    override val dequeValidator: KoneDequeValidator = object : KoneDequeValidator {
        context(_: AssertionScope)
        override fun <Element : Any> validate(deque: KoneDeque<Element>) {
            if (deque !is KoneListBackedDeque) {
                fail("Deque is invalid")
                return
            }
            listImplementationDescription.listValidator.validate(deque.data)
        }
    }
}