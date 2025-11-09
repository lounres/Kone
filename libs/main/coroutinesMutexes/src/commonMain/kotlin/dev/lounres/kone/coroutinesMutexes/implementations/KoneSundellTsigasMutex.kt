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
    internal val head = Node(this)
    internal val tail = Node(this)
    
    init {
        head.next.store(Node.ForwardLink(tail))
        tail.prev.store(Node.BackwardLink(head))
    }
    
    public companion object {
        @IgnorableReturnValue
        private fun AtomicReference<Node.ForwardLink?>.checkNodeAndIsBeingDeletedEqualityAndSet(
            expectedNode: Node,
            expectedIsBeingDeleted: Boolean,
            newValue: Node.ForwardLink,
        ): Boolean {
            while (true) {
                val link = load()!!
                if (link.node !== expectedNode || link.isBeingDeleted != expectedIsBeingDeleted) return false
                if (compareAndSet(link, newValue)) return true
            }
        }
        
        // SetMark for `prev` `Link`
        private fun Node.markPrevLink() {
            while (true) {
                val link = prev.load() ?: error("Marking prev link of node without prev link")
                if (link.isBeingDeleted || prev.compareAndSet(link, Node.BackwardLink(link.node, true))) break
            }
        }
        
        private fun CancellableContinuation<Unit>.justResume(
            onCancellation: ((cause: Throwable, value: Unit, context: CoroutineContext) -> Unit)? = null,
        ) {
            resume(Unit, onCancellation)
        }
    }

//    private val onCancellation: (cause: Throwable, value: Unit, context: CoroutineContext) -> Unit = { _, _, _ -> unlock() }
    
    @IgnorableReturnValue
    private fun correctPrev(prev: Node, node: Node): Node {
        var prev = prev
        var lastLink: Node? = null
        while (true) {
            val link = node.prev.load()!!
            if (link.isBeingDeleted) break
            val prev2 = prev.next.load()!!
            if (prev2.isBeingDeleted) {
                if (lastLink != null) {
                    prev.markPrevLink()
                    while (true) {
                        val link = lastLink.next.load()!!
                        if (link.node !== prev || link.isBeingDeleted) break
                        if (prev2.node === tail)
                            if (lastLink.next.compareAndSet(link, Node.ForwardLink(prev2.node, null, false))) {
                                // TODO: Maybe the line after the next one is better than the next line?..
                                link.continuation?.justResume()
//                                link.continuation?.justResume(onCancellation)
                                break
                            }
                            else
                                if (lastLink.next.compareAndSet(link, Node.ForwardLink(prev2.node, link.continuation, false)))
                                    break
                    }
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
            if (node.prev.compareAndSet(link, Node.BackwardLink(prev, false))) {
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
            if (next.prev.compareAndSet(link, Node.BackwardLink(this, false))) {
                if (this.prev.load()!!.isBeingDeleted) correctPrev(this, next)
                break
            }
        }
    }
    
    override fun tryLocking(): Boolean {
        val newNode = Node(this)
        val prev = head
        newNode.prev.store(Node.BackwardLink(prev, false))
        val nextLinkToNewNode = Node.ForwardLink(newNode, null, false)
        while (true) {
            val next = prev.next.load()!!
            if (next.node !== tail) return false
            if (next.isBeingDeleted) continue
            newNode.next.store(next)
            if (prev.next.compareAndSet(next, nextLinkToNewNode)) {
                newNode.pushEnd(next.node)
                return true
            }
        }
    }
    
    override suspend fun awaitLock() {
        if (!tryLocking()) suspendCancellableCoroutine {
            val newNode = Node(this)
            val prev = head
            newNode.prev.store(Node.BackwardLink(prev, false))
            val nextLinkToNewNode = Node.ForwardLink(newNode, null, false)
            while (true) {
                val next = prev.next.load()!!
                if (next.node === tail) {
                    newNode.next.store(Node.ForwardLink(next.node, null, false))
                    if (prev.next.compareAndSet(next, nextLinkToNewNode)) {
                        newNode.pushEnd(next.node)
                        it.justResume()
                        break
                    }
                } else {
                    newNode.next.store(Node.ForwardLink(next.node, it, false))
                    if (prev.next.compareAndSet(next, nextLinkToNewNode)) {
                        newNode.pushEnd(next.node)
                        it.invokeOnCancellation { newNode.remove() }
                        break
                    }
                }
            }
        }
    }
    
    override fun unlock() {
        val next = tail
        var node = next.prev.load()!!.node
        while (true) {
            if (node.next.load()!!.let { it.node !== next || it.isBeingDeleted }) {
                node = correctPrev(node, next)
                continue
            }
            if (node === head) error("Mutex is not locked")
            if (node.next.checkNodeAndIsBeingDeletedEqualityAndSet(next, false, Node.ForwardLink(next, null, true))) {
                val prev = node.prev.load()!!.node
                correctPrev(prev, next)
                return
            }
        }
    }
    
    internal class Node(
        private val mutex: KoneSundellTsigasMutex,
    ) {
        /*value*/ internal data class BackwardLink(
            val node: Node,
            val isBeingDeleted: Boolean = false,
        )
        
        val prev: AtomicReference<BackwardLink?> = AtomicReference(null)
        
        /*value*/ internal data class ForwardLink(
            val node: Node,
            val continuation: CancellableContinuation<Unit>? = null,
            val isBeingDeleted: Boolean = false,
        )
        
        val next: AtomicReference<ForwardLink?> = AtomicReference(null)
        
        fun remove() {
            while (true) {
                val next = this.next.load()!!
                if (next.isBeingDeleted) return
                if (this.next.compareAndSet(next, ForwardLink(next.node, null, true))) {
                    while (true) {
                        val prev = this.prev.load()!!
                        if (prev.isBeingDeleted || this.prev.compareAndSet(prev, BackwardLink(prev.node, true))) {
                            mutex.correctPrev(prev.node, next.node)
                            return
                        }
                    }
                }
            }
        }
    }
}