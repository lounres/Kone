/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.coroutinesMutexes.KoneMutex
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.atomics.AtomicReference
import kotlin.coroutines.CoroutineContext


public class KoneSundellTsigasMutex : KoneMutex {
    internal val head = Node.head()
    internal val tail = Node.tail()
    
    init {
        head.next.store(Node.Link(tail))
        tail.prev.store(Node.Link(head))
    }
    
    public companion object {
        @IgnorableReturnValue
        private fun AtomicReference<Node.Link?>.checkEqualityAndSet(
            expectedNode: Node,
            expectedIsBeingDeleted: Boolean,
            newValue: Node.Link,
        ): Boolean {
            while (true) {
                val link = load()!!
                if (link.node !== expectedNode || link.isBeingDeleted != expectedIsBeingDeleted) return false
                if (compareAndSet(link, newValue)) return true
            }
        }
        
        @IgnorableReturnValue
        private fun AtomicReference<Node.Link?>.checkNodeEqualityAndSet(
            expectedNode: Node,
            newValue: Node.Link,
        ): Boolean {
            while (true) {
                val link = load()!!
                if (link.node !== expectedNode) return false
                if (compareAndSet(link, newValue)) return true
            }
        }
        
        // SetMark for `prev` `Link`
        private fun Node.markPrevLink() {
            val nextReference = prev
            while (true) {
                val link = nextReference.load() ?: error("Marking prev link of node without prev link")
                if (link.isBeingDeleted || nextReference.compareAndSet(link, Node.Link(link.node, true))) break
            }
        }
        
        @IgnorableReturnValue
        private fun correctPrev(prev: Node, node: Node): Node {
            var prev = prev
            var lastLink: Node? = null
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
            }
            return prev
        }
        
        private fun Node.pushEnd(next: Node) {
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
    
    override suspend fun lock() {
        suspendCancellableCoroutine {
            val newNode = Node(it)
            val next = tail
            var prev = next.prev.load()!!.node
            while (true) {
                newNode.prev.store(Node.Link(prev, false))
                newNode.next.store(Node.Link(next, false))
                if (prev.next.checkEqualityAndSet(next, false, Node.Link(newNode, false))) break
                prev = correctPrev(prev, next)
            }
            newNode.pushEnd(next)
            
            it.invokeOnCancellation {
                if (!newNode.remove()) unlock()
            }
        }
    }
    
    override fun unlock() {
        val prev = head
        while (true) {
            val node = prev.next.load()!!.node
            if (node === tail) return
            val next = node.next.load()!!
            if (next.isBeingDeleted) {
                node.markPrevLink()
                prev.next.checkNodeEqualityAndSet(node, Node.Link(next.node, false))
                continue
            }
            if (node.next.compareAndSet(next, Node.Link(next.node, true))) {
                correctPrev(prev, next.node)
                node.value.resume(Unit, null as ((cause: Throwable, value: Unit, context: CoroutineContext) -> Unit)?)
            }
        }
    }
    
    internal class Node private constructor(
        value: CancellableContinuation<Unit>?,
    ) {
        companion object {
            internal fun head(): Node = Node(null)
            internal fun tail(): Node = Node(null)
        }
        
        internal constructor(value: CancellableContinuation<Unit>) : this(value as CancellableContinuation<Unit>?)
        
        private var _value: CancellableContinuation<Unit>? = value
        val value: CancellableContinuation<Unit> get() = _value!!
        
        /*value*/ internal data class Link(
            val node: Node,
            val isBeingDeleted: Boolean = false,
        )
        
        internal val prev: AtomicReference<Link?> = AtomicReference(null)
        internal val next: AtomicReference<Link?> = AtomicReference(null)
        
        fun remove(): Boolean {
            while (true) {
                val next = this.next.load()!!
                if (next.isBeingDeleted) return false
                if (this.next.compareAndSet(next, Link(next.node, true))) {
                    var prev: Link
                    while (true) {
                        prev = this.prev.load()!!
                        if (prev.isBeingDeleted || this.prev.compareAndSet(prev, Link(prev.node, true))) break
                    }
                    correctPrev(prev.node, next.node)
                    return true
                }
            }
        }
    }
}