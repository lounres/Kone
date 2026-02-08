/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.coroutinesMutexes.KoneMutualMultiExclusion
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.atomics.AtomicReference
import kotlin.coroutines.CoroutineContext


public class KoneSundellTsigasOverConcurrentHashMapMutualMultiExclusion<in Key: Any> : KoneMutualMultiExclusion<Key> {
    private val head = ConcurrentHashMap<Key, ForwardLink>()
    private val tail = ConcurrentHashMap<Key, BackwardLink>()
    
    public companion object {
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
                if (link.isBeingDeleted || prev.compareAndSet(link, BackwardLink(link.node, true))) break
            }
        else
            while (true) {
                val link = tail[key]
                if (link?.isBeingDeleted == true || tail.getCompareAndSet(key, link, BackwardLink(link?.node, true))) break
            }
    }
    
    @IgnorableReturnValue
    private fun correctPrev(key: Key, prev: Node?, node: Node?): Node? {
        var prev = prev
        var lastLink: Maybe<Node?> = None
        while (true) {
            val nodePrevLink = node?.prev?.load() ?: tail[key]
            if (nodePrevLink?.isBeingDeleted == true) break
            val prev2 = prev?.next?.load() ?: head[key]
            if (prev2?.isBeingDeleted == true) {
                if (lastLink != None) {
                    lastLink as Some
                    prev.markPrevLink(key)
                    while (true) {
                        val link = lastLink.value?.next?.load() ?: head[key]
                        if (link?.node !== prev || link?.isBeingDeleted == true) break
                        if (prev2.node === null) {
                            if (
                                if (lastLink.value != null) lastLink.value!!.next.compareAndSet(link!!, ForwardLink(prev2.node, null, false))
                                else head.getCompareAndSet(key, link, null)
                            ) {
                                link?.continuation?.justResume { _, _, _ -> val _ = tryUnlockingFor(key) }
                                break
                            }
                        } else
                            if (
                                if (lastLink.value != null) lastLink.value!!.next.compareAndSet(link!!, ForwardLink(prev2.node, link.continuation, false))
                                else head.getCompareAndSet(key, link, ForwardLink(prev2.node, link?.continuation, false))
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
            if (prev2?.node !== node) {
                lastLink = Some(prev)
                prev = prev2?.node
                continue
            }
            if (
                if (node != null) node.prev.compareAndSet(nodePrevLink!!, BackwardLink(prev, false))
                else tail.getCompareAndSet(key, nodePrevLink, prev?.let { BackwardLink(it, false) })
            ) {
                if (prev?.prev?.load()?.isBeingDeleted == true) continue
                break
            }
        }
        return prev
    }
    
    private fun pushEnd(key: Key, node: Node, next: Node?) {
        while (true) {
            val link = next?.prev?.load() ?: tail[key]
            if (link?.isBeingDeleted == true || node.next.load().let { it.node !== next || it.isBeingDeleted }) break
            if (
                if (next != null) next.prev.compareAndSet(link!!, BackwardLink(node, false))
                else tail.getCompareAndSet(key, link, BackwardLink(node, false))
            ) {
                if (node.prev.load().isBeingDeleted) correctPrev(key, node, next)
                break
            }
        }
    }
    
    override fun tryLockingFor(key: Key): Boolean {
        val newNode = Node()
        newNode.prev.store(BackwardLink(null, false))
        val nextLinkToNewNode = ForwardLink(newNode, null, false)
        while (true) {
            val next = head[key]
            if (next?.node !== null) return false
            if (next?.isBeingDeleted == true) continue // TODO: Is this line really needed?
            newNode.next.store(next ?: ForwardLink(null))
            if (head.getCompareAndSet(key, next, nextLinkToNewNode)) {
                pushEnd(key, newNode, null)
                return true
            }
        }
    }
    
    private fun Node.remove(key: Key) {
        while (true) {
            val next = this.next.load()
            if (next.isBeingDeleted || next.continuation === null) return
            if (this.next.compareAndSet(next, ForwardLink(next.node, null, true))) {
                while (true) {
                    val prev = this.prev.load()
                    if (prev.isBeingDeleted || this.prev.compareAndSet(prev, BackwardLink(prev.node, true))) {
                        correctPrev(key, prev.node, next.node)
                        return
                    }
                }
            }
        }
    }
    
    override suspend fun awaitLockFor(key: Key) {
        if (!tryLockingFor(key)) suspendCancellableCoroutine { continuation ->
            val newNode = Node()
            newNode.prev.store(BackwardLink(null, false))
            val nextLinkToNewNode = ForwardLink(newNode, null, false)
            while (true) {
                val next = head[key]
                if (next?.node === null) {
                    newNode.next.store(ForwardLink(null, null, false))
                    if (head.getCompareAndSet(key, next, nextLinkToNewNode)) {
                        pushEnd(key, newNode, null)
                        continuation.justResume()
                        break
                    }
                } else {
                    newNode.next.store(ForwardLink(next.node, continuation, false))
                    if (head.getCompareAndSet(key, next, nextLinkToNewNode)) {
                        pushEnd(key, newNode, next.node)
                        continuation.invokeOnCancellation { newNode.remove(key) }
                        break
                    }
                }
            }
        }
    }
    
    override fun tryUnlockingFor(key: Key): Boolean {
        var node = tail[key]?.node
        val newLink = ForwardLink(null, null, true)
        while (true) {
            val link = node?.next?.load() ?: head[key]
            if (link?.isBeingDeleted == true || link?.node !== null) {
                node = correctPrev(key, node, null)
                continue
            }
            if (node === null) return false
            if (node.next.compareAndSet(link!!, newLink)) {
                val prev = node.prev.load().node
                correctPrev(key, prev, null)
                return true
            }
        }
    }
    
    private class Node {
        val prev: AtomicReference<BackwardLink> = AtomicReference(STUB_BACKWARD_LINK)
        val next: AtomicReference<ForwardLink> = AtomicReference(STUB_FORWARD_LINK)
        
        companion object {
            private val STUB_BACKWARD_LINK = BackwardLink(null)
            private val STUB_FORWARD_LINK = ForwardLink(null)
        }
    }
    
    private /*value*/ data class BackwardLink(
        val node: Node?,
        val isBeingDeleted: Boolean = false,
    )
    
    private /*value*/ data class ForwardLink(
        val node: Node?,
        val continuation: CancellableContinuation<Unit>? = null,
        val isBeingDeleted: Boolean = false,
    )
}