/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.coroutinesMutexes.KoneMultiMutex
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.atomics.AtomicReference
import kotlin.coroutines.CoroutineContext


public class KoneSundellTsigasOverConcurrentHashMapMultiMutex<in Key: Any> : KoneMultiMutex<Key> {
    private val head = ConcurrentHashMap<Key, Node.ForwardLink>()
    private val tail = ConcurrentHashMap<Key, Node.BackwardLink>()
    
    public companion object {
        @IgnorableReturnValue
        private fun AtomicReference<Node.ForwardLink>.checkNodeAndIsBeingDeletedEqualityAndSet(
            expectedNode: Node?,
            expectedIsBeingDeleted: Boolean,
            newValue: Node.ForwardLink,
        ): Boolean {
            while (true) {
                val link = load()
                if (link.node !== expectedNode || link.isBeingDeleted != expectedIsBeingDeleted) return false
                if (compareAndSet(link, newValue)) return true
            }
        }
        
        private fun <Key: Any, Value: Any> ConcurrentHashMap<Key, Value>.getCompareAndSet(key: Key, oldValue: Value?, newValue: Value?): Boolean =
            when {
                oldValue == null && newValue == null -> this[key] == null
                oldValue == null -> this.putIfAbsent(key, newValue!!) == null
                newValue == null -> this.remove(key, oldValue)
                else -> this.replace(key, oldValue, newValue)
            }
        
        private fun CancellableContinuation<Unit>.justResume(
            onCancellation: ((cause: Throwable, value: Unit, context: CoroutineContext) -> Unit)? = null,
        ) {
            resume(Unit, onCancellation)
        }
    }
    
    // SetMark for `prev` `Link`
    private fun Node?.markPrevLink(key: Key) {
        if (this != null)
            while (true) {
                val link = prev.load()
                if (link.isBeingDeleted || prev.compareAndSet(link, Node.BackwardLink(link.node, true))) break
            }
        else
            while (true) {
                val link = tail[key]
                if (link?.isBeingDeleted == true || tail.getCompareAndSet(key, link, Node.BackwardLink(link?.node, true))) break
            }
    }
    
    @IgnorableReturnValue
    private fun correctPrev(key: Key, prev: Node?, node: Node?): Node? {
        var prev = prev
        var lastLink: Maybe<Node?> = None
        while (true) {
            val nodePrevLink = node?.prev?.load() ?: tail[key]
            if (nodePrevLink?.isBeingDeleted == true) break
            val prev2 = prev?.next?.load() ?: head[key] ?: Node.ForwardLink(null)
            if (prev2.isBeingDeleted) {
                if (lastLink != None) {
                    lastLink as Some
                    prev.markPrevLink(key)
                    while (true) {
                        val link = lastLink.value.let { it?.next?.load() ?: head[key] }
                        if (link?.node !== prev || link?.isBeingDeleted == true) break
                        if (prev2.node == null) {
                            if (
                                if (lastLink.value != null) lastLink.value!!.next.compareAndSet(link!!, Node.ForwardLink(prev2.node, null, false))
                                else head.getCompareAndSet(key, link, null)
                            ) {
                                // TODO: Maybe the line after the next one is better than the next line?..
                                link?.continuation?.justResume()
//                                link?.continuation?.justResume { _, _, _ -> unlockFor(key) }
                                break
                            }
                        } else
                            if (
                                if (lastLink.value != null) lastLink.value!!.next.compareAndSet(link!!, Node.ForwardLink(prev2.node, link.continuation, false))
                                else head.getCompareAndSet(key, link, null)
                            )
                                break
                    }
                    prev = lastLink.value
                    lastLink = None
                    continue
                }
                prev = prev!!.prev.load().node
                continue
            }
            if (prev2.node !== node) {
                lastLink = Some(prev)
                prev = prev2.node
                continue
            }
            if (
                if (node != null) node.prev.compareAndSet(nodePrevLink!!, Node.BackwardLink(prev, false))
                else tail.getCompareAndSet(key, nodePrevLink, prev?.let { Node.BackwardLink(it, false) })
            ) {
                if (prev?.prev?.load()?.isBeingDeleted == true) continue
                break
            }
        }
        return prev
    }
    
    private fun Node.pushEnd(key: Key, next: Node?) {
        while (true) {
            val link = next?.prev?.load() ?: tail[key]
            if (link?.isBeingDeleted == true || this.next.load().let { it.node !== next || it.isBeingDeleted }) break
            if (
                if (next != null) next.prev.compareAndSet(link!!, Node.BackwardLink(this, false))
                else tail.getCompareAndSet(key, link, Node.BackwardLink(this, false))
            ) {
                if (this.prev.load().isBeingDeleted) correctPrev(key, this, next)
                break
            }
        }
    }
    
    override fun tryLockingFor(key: Key): Boolean {
        val newNode = Node()
        newNode.prev.store(Node.BackwardLink(null, false))
        val nextLinkToNewNode = Node.ForwardLink(newNode, null, false)
        while (true) {
            val next = head[key]
            if (next?.node !== null) return false
            if (next?.isBeingDeleted == true) continue
            newNode.next.store(next ?: Node.ForwardLink(null))
            if (head.getCompareAndSet(key, next, nextLinkToNewNode)) {
                newNode.pushEnd(key, null)
                return true
            }
        }
    }
    
    private fun Node.remove(key: Key) {
        while (true) {
            val next = this.next.load()
            if (next.isBeingDeleted) return
            if (this.next.compareAndSet(next, Node.ForwardLink(next.node, null, true))) {
                while (true) {
                    val prev = this.prev.load()
                    if (prev.isBeingDeleted || this.prev.compareAndSet(prev, Node.BackwardLink(prev.node, true))) {
                        correctPrev(key, prev.node, next.node)
                        return
                    }
                }
            }
        }
    }
    
    override suspend fun awaitLockFor(key: Key) {
        if (!tryLockingFor(key)) suspendCancellableCoroutine {
            val newNode = Node()
            newNode.prev.store(Node.BackwardLink(null, false))
            val nextLinkToNewNode = Node.ForwardLink(newNode, null, false)
            while (true) {
                val next = head[key]
                if (next?.node === null) {
                    newNode.next.store(Node.ForwardLink(null, null, false))
                    if (head.getCompareAndSet(key, next, nextLinkToNewNode)) {
                        newNode.pushEnd(key, null)
                        it.justResume()
                        break
                    }
                } else {
                    newNode.next.store(Node.ForwardLink(next.node, it, false))
                    if (head.getCompareAndSet(key, next, nextLinkToNewNode)) {
                        newNode.pushEnd(key, next.node)
                        it.invokeOnCancellation { newNode.remove(key) }
                        break
                    }
                }
            }
        }
    }
    
    override fun unlockFor(key: Key) {
        var node = tail[key]?.node
        while (true) {
            if (node?.next?.load().let { it?.node !== null || it?.isBeingDeleted == true }) {
                node = correctPrev(key, node, null)
                continue
            }
            if (node === null) error("Mutex is not locked")
            if (node.next.checkNodeAndIsBeingDeletedEqualityAndSet(null, false, Node.ForwardLink(null, null, true))) {
                val prev = node.prev.load().node
                correctPrev(key, prev, null)
                return
            }
        }
    }
    
    private class Node {
        /*value*/ data class BackwardLink(
            val node: Node?,
            val isBeingDeleted: Boolean = false,
        )
        
        val prev: AtomicReference<BackwardLink> = AtomicReference(STUB_BACKWARD_LINK)
        
        /*value*/ data class ForwardLink(
            val node: Node?,
            val continuation: CancellableContinuation<Unit>? = null,
            val isBeingDeleted: Boolean = false,
        )
        
        val next: AtomicReference<ForwardLink> = AtomicReference(STUB_FORWARD_LINK)
        
        companion object {
            private val STUB_BACKWARD_LINK = BackwardLink(null)
            private val STUB_FORWARD_LINK = ForwardLink(null)
        }
    }
}