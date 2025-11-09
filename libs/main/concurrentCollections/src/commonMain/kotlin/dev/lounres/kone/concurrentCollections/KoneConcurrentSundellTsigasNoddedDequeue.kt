/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import kotlin.concurrent.atomics.AtomicReference


public class KoneConcurrentSundellTsigasNoddedDequeue<Element> {
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
            var lastLink: Node<Element>? = null
            while (true) {
                val link = node.prev.load()!!
                if (link.isBeingDeleted) break
                val prev2 = prev.next.load()!!
                if (prev2.isBeingDeleted) {
                    if (lastLink != null) {
                        prev.markPrevLink()
                        lastLink.next.checkEqualityAndSet(prev, false, Node.Link(prev2.node, false))
                        prev = lastLink
                        lastLink = null
                        continue
                    }
                    prev = prev.prev.load()!!.node
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
                    if (this.prev.load()!!.isBeingDeleted) correctPrev(this, next)
                    break
                }
            }
        }
    }
    
    @IgnorableReturnValue
    public fun addFirst(element: Element): Node<Element> {
        val newNode = Node(element)
        val prev = head
        newNode.prev.store(Node.Link(prev, false))
        val nextLinkToNewNode = Node.Link(newNode, false)
        while (true) {
            val next = prev.next.load()!!
            newNode.next.store(next)
            if (prev.next.compareAndSet(next, nextLinkToNewNode)) {
                newNode.pushEnd(next.node)
                return newNode
            }
            // BACK-OFF
        }
    }
    
    @IgnorableReturnValue
    public fun addLast(element: Element): Node<Element> {
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
        return newNode
    }
    
    @IgnorableReturnValue
    public fun popFirstMaybe(): Node<Element>? {
        val prev = head
        while (true) {
            val here = head.next.load()!!
            if (here.node === tail) return null
            val next = here.node.next.load()!!
            if (next.isBeingDeleted) {
                here.node.markPrevLink()
                val _ = prev.next.compareAndSet(here, Node.Link(next.node, false))
                continue
            }
            if (here.node.next.compareAndSet(next, Node.Link(next.node, true))) {
                correctPrev(prev, next.node)
                return here.node
            }
            // BACK-OFF
        }
    }
    
    @IgnorableReturnValue
    public fun popLastMaybe(): Node<Element>? {
        val next = tail
        var node = next.prev.load()!!.node
        while (true) {
            if (node.next.load()!!.let { it.node !== next || it.isBeingDeleted }) {
                node = correctPrev(node, next)
                continue
            }
            if (node === head) return null
            if (node.next.checkEqualityAndSet(next, false, Node.Link(next, true))) {
                val prev = node.prev.load()!!.node
                correctPrev(prev, next)
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
        
        internal val prev: AtomicReference<Link<Element>?> = AtomicReference(null)
        internal val next: AtomicReference<Link<Element>?> = AtomicReference(null)
        
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