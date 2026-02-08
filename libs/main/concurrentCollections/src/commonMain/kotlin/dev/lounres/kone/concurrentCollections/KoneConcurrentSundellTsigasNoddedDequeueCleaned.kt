/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.update


// TODO: WIP
public class KoneConcurrentSundellTsigasNoddedDequeueCleaned<Element> {
    internal val head = Node<Element>(null)
    internal val tail = Node<Element>(null)
    
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
        
        // SetMark for `prev` `Link`
        private fun <Element> Node<Element>.markPrevLink() {
            while (true) {
                val link = prev.load() ?: error("Marking prev link of node without prev link")
                if (link.isBeingDeleted || prev.compareAndSet(link, Node.Link(link.node, true))) break
            }
        }
        
        @IgnorableReturnValue
        private fun <Element> correctPrev(prev: Node<Element>, node: Node<Element>): Node<Element> {
            var prev = prev
            prev.reference()
            var lastLink: Node<Element>? = null
            while (true) {
                val link = node.prev.load()!!
                link.node.reference()
                if (link.isBeingDeleted) {
                    link.node.dereference()
                    break
                }
                val prev2 = prev.next.load()!!
                prev2.node.reference()
                if (prev2.isBeingDeleted) {
                    if (lastLink != null) {
                        prev.markPrevLink()
                        if (lastLink.next.checkEqualityAndSet(prev, false, Node.Link(prev2.node, false))) {
                            prev2.node.reference()
                            prev.dereference()
                        }
                        prev.dereference()
                        prev = lastLink
                        lastLink = null
                        link.node.dereference()
                        prev2.node.dereference()
                        continue
                    }
                    prev = prev.prev.load()!!.node.also { prev.dereference() }
                    prev.reference()
                    link.node.dereference()
                    prev2.node.dereference()
                    continue
                }
                if (prev2.node !== node) {
                    lastLink?.dereference()
                    lastLink = prev
                    prev = prev2.node
                    link.node.dereference()
                    prev2.node.dereference()
                    continue
                }
                prev2.node.dereference()
                if (node.prev.compareAndSet(link, Node.Link(prev, false))) {
                    link.node.dereference()
                    prev.reference()
                    link.node.dereference()
                    if (prev.prev.load()?.isBeingDeleted == true) {
                        continue
                    }
                    break
                }
                link.node.dereference()
                // BACK-OFF
            }
            prev.dereference()
            lastLink?.dereference()
            return prev
        }
        
        private fun <Element> Node<Element>.pushEnd(next: Node<Element>) {
            while (true) {
                val link = next.prev.load()!!
                link.node.reference()
                if (link.isBeingDeleted || this.next.load()!!.let { it.node !== next || it.isBeingDeleted }) {
                    link.node.dereference()
                    break
                }
                if (next.prev.compareAndSet(link, Node.Link(this, false))) {
                    link.node.dereference()
                    this.reference()
                    link.node.dereference()
                    if (this.prev.load()!!.isBeingDeleted) correctPrev(this, next)
                    break
                }
                link.node.dereference()
            }
        }
    }
    
    @IgnorableReturnValue
    public fun addFirst(element: Element): Node<Element> {
        val newNode = Node(element)
        val prev = head
        while (true) {
            val next = prev.next.load()!!.node
            next.reference()
            newNode.prev.exchange(Node.Link(prev, false))?.node?.dereference()
            prev.reference()
            newNode.next.exchange(Node.Link(next, false))?.node?.dereference()
            next.reference()
            if (prev.next.checkEqualityAndSet(next, false, Node.Link(newNode, false))) {
                next.dereference()
                next.dereference()
                newNode.reference()
                newNode.pushEnd(next)
                newNode.dereference()
                return newNode
            }
            next.dereference()
            // BACK-OFF
        }
    }
    
    @IgnorableReturnValue
    public fun addLast(element: Element): Node<Element> {
        val newNode = Node(element)
        val next = tail
        var prev = next.prev.load()!!.node
        prev.reference()
        while (true) {
            newNode.prev.exchange(Node.Link(prev, false))?.node?.dereference()
            prev.reference()
            newNode.next.exchange(Node.Link(next, false))?.node?.dereference()
            next.reference()
            if (prev.next.checkEqualityAndSet(next, false, Node.Link(newNode, false))) {
                next.dereference()
                newNode.reference()
                break
            }
            prev = correctPrev(prev, next).also { prev.dereference() }
            prev.reference()
            // BACK-OFF
        }
        prev.dereference()
        newNode.pushEnd(next)
        newNode.dereference()
        return newNode
    }
    
    @IgnorableReturnValue
    public fun popFirstMaybe(): Node<Element>? {
        val prev = head
        while (true) {
            val node = prev.next.load()!!.node
            node.reference()
            if (node === tail) {
                node.dereference()
                return null
            }
            val next = node.next.load()!!
            next.node.reference()
            if (next.isBeingDeleted) {
                node.markPrevLink()
                if (prev.next.checkNodeEqualityAndSet(node, Node.Link(next.node, false))) {
                    next.node.reference()
                    node.dereference()
                }
                node.dereference()
                next.node.dereference()
                continue
            }
            if (node.next.compareAndSet(next, Node.Link(next.node, true))) {
                correctPrev(prev, next.node)
                prev.dereference()
                node.dereference()
                next.node.dereference()
                return node
            }
            node.dereference()
            next.node.dereference()
            // BACK-OFF
        }
    }
    
    @IgnorableReturnValue
    public fun popLastMaybe(): Node<Element>? {
        val next = tail
        var node = next.prev.load()!!.node
        node.reference()
        while (true) {
            if (node.next.load()!!.let { it.node !== next || it.isBeingDeleted }) {
                node = correctPrev(node, next).also { node.dereference() }
                node.reference()
                continue
            }
            if (node === head) {
                node.dereference()
                return null
            }
            if (node.next.checkEqualityAndSet(next, false, Node.Link(next, true))) {
                val prev = node.prev.load()!!.node
                prev.reference()
                correctPrev(prev, next)
                prev.dereference()
                node.dereference()
                return node
            }
            // BACK-OFF
        }
    }
    
    public class Node<Element> internal constructor(
        value: Element?,
    ) {
        private val _value: Element? = value
        @Suppress("UNCHECKED_CAST")
        public val value: Element get() = _value as Element
        
        /*value*/ internal data class Link<Element>(
            val node: Node<Element>,
            val isBeingDeleted: Boolean = false,
        )
        
        private var _prev: AtomicReference<Link<Element>?>? = AtomicReference(null)
        internal val prev: AtomicReference<Link<Element>?> get() = _prev ?: error("Accessing disposed previous atomic reference")
        private var _next: AtomicReference<Link<Element>?>? = AtomicReference(null)
        internal val next: AtomicReference<Link<Element>?> get() = _next ?: error("Accessing disposed next atomic reference")
        
        private var referenceCounter: AtomicInt? = AtomicInt(1)
        
        private fun dispose() {
            next.exchange(null)?.node?.dereference()
            _next = null
            prev.exchange(null)?.node?.dereference()
            _prev = null
            referenceCounter = null
        }
        
        internal fun reference() {
            referenceCounter!!.update { it + 1 }
        }
        
        internal fun dereference() {
            if (referenceCounter!!.decrementAndFetch() == 0) dispose()
        }
        
        public fun remove() {
            while (true) {
                val next = this.next.load()!!
                if (next.isBeingDeleted) return
                if (this.next.compareAndSet(next, Link(next.node, true))) {
                    while (true) {
                        val prev = this.prev.load()!!
                        if (prev.isBeingDeleted || this.prev.compareAndSet(prev, Link(prev.node, true))) {
                            correctPrev(prev.node, next.node)
                            return
                        }
                    }
                }
            }
        }
    }
}