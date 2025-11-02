/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import kotlin.concurrent.atomics.AtomicReference


public class KoneConcurrentSundellTsigasNoddedDequeue<Element> {
    internal val head = Node<Element>()
    internal val tail = Node<Element>()
    
    init {
        head.next.store(Node.Link(tail))
        tail.prev.store(Node.Link(head))
    }
    
    public companion object {
        @IgnorableReturnValue
        private fun <Element> AtomicReference<Node.Link<Element>?>.checkEqualityAndSet(
            expectedNode: Node<Element>,
            expectedIsBeingDeleted: Boolean,
            newValue: Node.Link<Element>,
        ): Boolean {
            while (true) {
                val link = load()!!
                if (link.node !== expectedNode || link.isBeingDeleted != expectedIsBeingDeleted) return false
                if (compareAndSet(link, newValue)) return true
            }
        }
        
        @IgnorableReturnValue
        private fun <Element> AtomicReference<Node.Link<Element>?>.checkNodeEqualityAndSet(
            expectedNode: Node<Element>,
            newValue: Node.Link<Element>,
        ): Boolean {
            while (true) {
                val link = load()!!
                if (link.node !== expectedNode) return false
                if (compareAndSet(link, newValue)) return true
            }
        }
        
        // SetMark for `next` `Link`
        private fun <Element> Node<Element>.markNextLink() {
            val nextReference = next
            while (true) {
                val link = nextReference.load() ?: error("Marking next link of node without next link")
                if (link.isBeingDeleted || nextReference.compareAndSet(link, Node.Link(link.node, true))) break
            }
        }
        
        // SetMark for `prev` `Link`
        private fun <Element> Node<Element>.markPrevLink() {
            val nextReference = prev
            while (true) {
                val link = nextReference.load() ?: error("Marking prev link of node without prev link")
                if (link.isBeingDeleted || nextReference.compareAndSet(link, Node.Link(link.node, true))) break
            }
        }
        
        @IgnorableReturnValue
        private fun <Element> correctPrev(prev: Node<Element>, node: Node<Element>): Node<Element> {
            var prev = prev
            var lastLink: Node<Element>? = null
            while (true) {
                val link = node.prev.load()!!
                if (link.isBeingDeleted) break
                var prev2 = prev.next.load()!!
                if (prev2.isBeingDeleted) {
                    if (lastLink != null) {
                        prev.markPrevLink()
                        lastLink.next.checkEqualityAndSet(prev, false, Node.Link(prev2.node, false))
                        prev = lastLink
                        lastLink = null
                        continue
                    }
                    prev2 = prev.prev.load()!!
                    prev = prev2.node
                    continue
                }
                if (prev2.node !== node) {
                    lastLink = prev
                    prev = prev2.node
                    continue
                }
                if (node.prev.compareAndSet(link, Node.Link(prev, false))) {
                    if (prev.prev.load()?.isBeingDeleted == true) continue
                    break
                }
                // BACK-OFF
            }
            return prev
        }
        
        private fun <Element> Node<Element>.pushEnd(next: Node<Element>) {
            while (true) {
                val link = next.prev.load()!!
                if (link.isBeingDeleted || this.next.load()!!.let { it.node !== next || it.isBeingDeleted }) break
                if (next.prev.compareAndSet(link, Node.Link(this, false))) {
                    if (this.prev.load()!!.isBeingDeleted)
                        @Suppress("RETURN_VALUE_NOT_USED")
                        correctPrev(this, next)
                    break
                }
            }
        }
    }
    
    public fun addFirst(element: Element) {
        val newNode = Node(element)
        val prev = head
        var next = prev.next.load()!!.node
        while (true) {
            newNode.prev.store(Node.Link(prev, false))
            newNode.next.store(Node.Link(next, false))
            if (prev.next.checkEqualityAndSet(next, false, Node.Link(newNode, false))) break
            next = prev.next.load()!!.node
            // BACK-OFF
        }
        newNode.pushEnd(next)
    }
    
    public fun addLast(element: Element) {
        val newNode = Node(element)
        val next = tail
        var prev = next.prev.load()!!.node
        while (true) {
            newNode.prev.store(Node.Link(prev, false))
            newNode.next.store(Node.Link(next, false))
            if (prev.next.checkEqualityAndSet(next, false, Node.Link(newNode, false))) break
            prev = correctPrev(prev, next)
            // BACK-OFF
        }
        newNode.pushEnd(next)
    }
    
    public fun removeFirstIfPresent() {
        TODO()
    }
    
    public fun popFirstMaybe(): Maybe<Element> {
        val prev = head
        while (true) {
            val node = prev.next.load()!!.node
            if (node === tail) return None
            val next = node.next.load()!!
            if (next.isBeingDeleted) {
                node.markPrevLink()
                prev.next.checkNodeEqualityAndSet(node, Node.Link(next.node, false))
                continue
            }
            if (node.next.compareAndSet(next, Node.Link(next.node, true))) {
                correctPrev(prev, next.node)
                return Some(node.value)
            }
            // BACK-OFF
        }
    }
    
    public fun popLastMaybe(): Maybe<Element> {
        val next = tail
        var node = next.prev.load()!!.node
        while (true) {
            if (node.next.load()!!.let { it.node !== next || it.isBeingDeleted }) {
                node = correctPrev(node, next)
                continue
            }
            if (node === head) return None
            if (node.next.checkEqualityAndSet(next, false, Node.Link(next, true))) {
                val prev = node.prev.load()!!.node
                correctPrev(prev, next)
                return Some(node.value)
            }
            // BACK-OFF
        }
    }
    
    internal class Node<Element>(
        value: Element? = null,
    ) {
        private var _value: Element? = value
        @Suppress("UNCHECKED_CAST")
        val value: Element get() = _value as Element
        
        /*value*/ data class Link<Element>(
            val node: Node<Element>,
            val isBeingDeleted: Boolean = false,
        )
        
        private var _prev: AtomicReference<Link<Element>?>? = AtomicReference(null)
        val prev: AtomicReference<Link<Element>?> get() = _prev ?: error("Accessing disposed previous atomic reference")
        private var _next: AtomicReference<Link<Element>?>? = AtomicReference(null)
        val next: AtomicReference<Link<Element>?> get() = _next ?: error("Accessing disposed next atomic reference")
        
        fun dispose() {
            _value = null
            _next?.store(null)
            _next = null
            _prev?.store(null)
            _prev = null
        }
    }
}