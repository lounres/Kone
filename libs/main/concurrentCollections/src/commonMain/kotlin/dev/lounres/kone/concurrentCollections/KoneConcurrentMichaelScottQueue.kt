/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import kotlin.concurrent.atomics.AtomicReference


public class KoneConcurrentMichaelScottQueue<Element> {
    private val head: AtomicReference<Node<Element>> = AtomicReference(Node())
    private val tail: AtomicReference<Node<Element>> = AtomicReference(head.load())

    public fun addLast(element: Element) {
        val newNode = Node(value = element)
        var tail: Node<Element>
        while (true) {
            tail = this.tail.load()
            val next = tail.next?.load() ?: continue
            if (tail == this.tail.load()) {
                if (next === Stub) {
                    if (tail.next?.compareAndSet(next, newNode) == true) break
                } else {
                    @Suppress("RETURN_VALUE_NOT_USED", "UNCHECKED_CAST")
                    this.tail.compareAndSet(tail, next as Node<Element>)
                }
            }
        }
        @Suppress("RETURN_VALUE_NOT_USED")
        this.tail.compareAndSet(tail, newNode)
    }

    public fun removeFirstIfPresent() {
        while (true) {
            val head = this.head.load()
            val tail = this.tail.load()
            val next = head.next?.load() ?: continue
            if (head == this.head.load()) {
                if (head === tail) {
                    if (next === Stub) return
                    @Suppress("RETURN_VALUE_NOT_USED", "UNCHECKED_CAST")
                    this.tail.compareAndSet(tail, next as Node<Element>)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    next as Node<Element>
                    if (this.head.compareAndSet(head, next)) {
                        next.empty()
                        head.dispose()
                        return
                    }
                }
            }
        }
    }

    public fun popFirstMaybe(): Maybe<Element> {
        var result: Element
        while (true) {
            val head = this.head.load()
            val tail = this.tail.load()
            val next = head.next?.load() ?: continue
            if (head == this.head.load()) {
                if (head === tail) {
                    if (next == Stub) return None
                    @Suppress("RETURN_VALUE_NOT_USED", "UNCHECKED_CAST")
                    this.tail.compareAndSet(tail, next as Node<Element>)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    next as Node<Element>
                    result = next.value
                    if (this.head.compareAndSet(head, next)) {
                        next.empty()
                        head.dispose()
                        return Some(result)
                    }
                }
            }
        }
    }

    private class Node<Element>(
        value: Element? = null,
    ) {
        private var _value: Element? = value
        @Suppress("UNCHECKED_CAST")
        val value: Element get() = _value as Element

        var next: AtomicReference<Any?>? = AtomicReference(Stub)
            private set

        fun empty() {
            _value = null
        }

        fun dispose() {
            _value = null
            next?.store(null)
            next = null
        }
    }

    private object Stub
}