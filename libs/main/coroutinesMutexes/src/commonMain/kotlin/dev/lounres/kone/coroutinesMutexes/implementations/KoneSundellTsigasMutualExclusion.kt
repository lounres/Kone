/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.coroutinesMutexes.KoneMutualExclusion
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.atomics.AtomicReference
import kotlin.coroutines.CoroutineContext


public class KoneSundellTsigasMutualExclusion : KoneMutualExclusion {
    private val head = AtomicReference<ForwardLink>(ForwardLink(null))
    private val tail = AtomicReference<BackwardLink>(BackwardLink(null))
    
    public companion object {
        // SetMark for `prev` `Link`
        private fun Node.markPrevLink() {
            while (true) {
                val link = loadPrev()
                if (link.isBeingDeleted || compareAndSetPrev(link, BackwardLink(link.node, true))) break
            }
        }
        
        private fun CancellableContinuation<Unit>.justResume(
            onCancellation: ((cause: Throwable, value: Unit, context: CoroutineContext) -> Unit)? = null,
        ) {
            resume(Unit, onCancellation)
        }
    }

    private val onCancellation: (cause: Throwable, value: Unit, context: CoroutineContext) -> Unit = { _, _, _ -> val _ = tryUnlocking() }
    
    @IgnorableReturnValue
    private fun correctPrev(prev: Node?, node: Node?): Node? {
        var prev = prev
        var lastLink: Maybe<Node?> = None
        while (true) {
            val nodePrevLink = node?.loadPrev() ?: tail.load()
            if (nodePrevLink.isBeingDeleted) break
            val prev2 = prev?.loadNext() ?: head.load()
            if (prev2.isBeingDeleted) {
                if (lastLink != None) {
                    lastLink as Some
                    val lastLinkValue = lastLink.value
                    prev!!.markPrevLink()
                    while (true) {
                        val link = lastLinkValue?.loadNext() ?: head.load()
                        if (link.node !== prev || link.isBeingDeleted) break
                        if (prev2.node === null) {
                            if (
                                if (lastLinkValue != null) lastLinkValue.compareAndSetNext(link, ForwardLink(prev2.node, null, false))
                                else head.compareAndSet(link, ForwardLink(prev2.node, null, false))
                            ) {
                                link.continuation?.justResume(onCancellation)
                                break
                            }
                        } else {
                            if (
                                if (lastLinkValue != null) lastLinkValue.compareAndSetNext(link, ForwardLink(prev2.node, link.continuation, false))
                                else head.compareAndSet(link, ForwardLink(prev2.node, link.continuation, false))
                            )
                                break
                        }
                    }
                    prev = lastLinkValue
                    lastLink = None
                    continue
                }
                prev = (prev?.loadNext() ?: head.load()).node
                continue
            }
            if (prev2.node !== node) {
                lastLink = Some(prev)
                prev = prev2.node
                continue
            }
            if (
                if (node != null) node.compareAndSetPrev(nodePrevLink, BackwardLink(prev, false))
                else tail.compareAndSet(nodePrevLink, BackwardLink(prev, false))
            ) {
                if (prev?.loadPrev()?.isBeingDeleted == true) continue
                break
            }
        }
        return prev
    }
    
    private fun pushEnd(node: Node, next: Node?) {
        while (true) {
            val link = next?.loadPrev() ?: tail.load()
            if (link.isBeingDeleted || node.loadNext().let { it.node !== next || it.isBeingDeleted }) break
            if (
                if (next != null) next.compareAndSetPrev(link, BackwardLink(node, false))
                else tail.compareAndSet(link, BackwardLink(node, false))
            ) {
                if (node.loadPrev().isBeingDeleted) correctPrev(node, next)
                break
            }
        }
    }
    
    override fun tryLocking(): Boolean {
        val newNode = Node()
        newNode.storePrev(BackwardLink(null, false))
        val nextLinkToNewNode = ForwardLink(newNode, null, false)
        while (true) {
            val next = head.load()
            if (next.node !== null) return false
            if (next.isBeingDeleted) continue // TODO: Is this line really needed?
            newNode.storeNext(next)
            if (head.compareAndSet(next, nextLinkToNewNode)) {
                pushEnd(newNode, next.node)
                return true
            }
        }
    }
    
    private fun Node.remove() {
        while (true) {
            val next = this.loadNext()
            if (next.isBeingDeleted || next.continuation === null) return
            if (this.compareAndSetNext(next, ForwardLink(next.node, null, true))) {
                while (true) {
                    val prev = this.loadPrev()
                    if (prev.isBeingDeleted || this.compareAndSetPrev(prev, BackwardLink(prev.node, true))) {
                        correctPrev(prev.node, next.node)
                        return
                    }
                }
            }
        }
    }
    
    override suspend fun awaitLock() {
        suspendCancellableCoroutine { continuation ->
            val newNode = Node()
            newNode.storePrev(BackwardLink(null, false))
            val nextLinkToNewNode = ForwardLink(newNode, null, false)
            while (true) {
                val next = head.load()
                if (next.node === null) {
                    newNode.storeNext(ForwardLink(next.node, null, false))
                    if (head.compareAndSet(next, nextLinkToNewNode)) {
                        pushEnd(newNode, next.node)
                        continuation.justResume()
                        break
                    }
                } else {
                    newNode.storeNext(ForwardLink(next.node, continuation, false))
                    if (head.compareAndSet(next, nextLinkToNewNode)) {
                        pushEnd(newNode, next.node)
                        continuation.invokeOnCancellation { newNode.remove() }
                        break
                    }
                }
            }
        }
    }
    
    override fun tryUnlocking(): Boolean {
        var node = tail.load().node
        while (true) {
            if ((node?.loadNext() ?: head.load()).let { it.node !== null || it.isBeingDeleted }) {
                node = correctPrev(node, null)
                continue
            }
            if (node === null) return false
            while (true) {
                val link = node.loadNext()
                if (link.node !== null || link.isBeingDeleted) break
                if (node.compareAndSetNext(link, ForwardLink(null, null, true))) {
                    val prev = node.loadPrev().node
                    correctPrev(prev, null)
                    return true
                }
            }
        }
    }
    
    private class Node {
        private val prev: AtomicReference<BackwardLink?> = AtomicReference(null)
        fun loadPrev(): BackwardLink = prev.load()!!
        fun storePrev(newValue: BackwardLink) {
            prev.store(newValue)
        }
        fun compareAndSetPrev(expectedValue: BackwardLink, newValue: BackwardLink) =
            prev.compareAndSet(expectedValue, newValue)
        private val next: AtomicReference<ForwardLink?> = AtomicReference(null)
        fun loadNext(): ForwardLink = next.load()!!
        fun storeNext(newValue: ForwardLink) {
            next.store(newValue)
        }
        fun compareAndSetNext(expectedValue: ForwardLink, newValue: ForwardLink) =
            next.compareAndSet(expectedValue, newValue)
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