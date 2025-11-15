/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.castOrNull
import dev.lounres.kone.coroutinesMutexes.KoneSemaphore
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.atomics.AtomicReference
import kotlin.coroutines.CoroutineContext


public class KoneSundellTsigasSemaphore(
    private val permits: UInt,
) : KoneSemaphore {
    init {
        require(permits > 0u) { "KoneSundellTsigasSemaphore does not support 0 permits" }
    }

    private val head = AtomicReference<Any>(HeadPermitsLink(permits))
    private val tail = AtomicReference<BackwardLink>(BackwardLink(null))

    private fun loadHeadForwardLinkOrNull() = head.load().castOrNull<HeadForwardLink>()

    public companion object {
        private val ForwardLink.isBeingDeleted get() = deletionStatus != NotYetDeleted

        private fun CancellableContinuation<Unit>.justResume(
            onCancellation: ((cause: Throwable, value: Unit, context: CoroutineContext) -> Unit)? = null,
        ) {
            resume(Unit, onCancellation)
        }
    }

    // SetMark for `prev` `Link`
    private fun Node.markPrevLink() {
        while (true) {
            val link = loadPrev()
            if (link.isBeingDeleted || compareAndSetPrev(link, BackwardLink(link.node, true))) break
        }
    }

    private val onCancellation: (cause: Throwable, value: Unit, context: CoroutineContext) -> Unit = { _, _, _ -> val _ = tryReleasing() }

    @IgnorableReturnValue
    private fun correctPrev(prev: Node?, node: Node?): Node? {
        var prev = prev
        var lastLink: Maybe<Node?> = None
        while (true) {
            val nodePrevLink = node?.loadPrev() ?: tail.load()
            if (nodePrevLink.isBeingDeleted) break
            if (prev != null) {
                val prev2 = prev.loadNext()
                if (prev2.isBeingDeleted) {
                    if (lastLink != None) {
                        lastLink as Some
                        val lastLinkValue = lastLink.value
                        prev.markPrevLink()
                        while (true) {
                            if (lastLinkValue != null) {
                                val link = lastLinkValue.loadNext()
                                if (link.node !== prev || link.isBeingDeleted) break
                                if (lastLinkValue.compareAndSetNext(link, ForwardLink(prev2.node, link.continuation, NotYetDeleted))) {
                                    if (prev2.deletionStatus == ToBeResumedAfterDeletion) prev2.continuation.justResume(onCancellation)
                                    break
                                }
                            } else {
                                val link = loadHeadForwardLinkOrNull()
                                if (link?.node !== prev) break
                                if (head.compareAndSet(link, HeadForwardLink(prev2.node))) break
                            }
                        }
                        prev = lastLinkValue
                        lastLink = None
                        continue
                    }
                    prev = prev.loadPrev().node
                    continue
                }
                if (prev2.node !== node) {
                    lastLink = Some(prev)
                    prev = prev2.node
                    continue
                }
            } else {
                val prev2 = loadHeadForwardLinkOrNull()
                if (prev2?.node !== node) {
                    lastLink = Some(prev)
                    prev = prev2?.node
                    continue
                }
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

    private fun Node.pushEnd(next: Node?) {
        while (true) {
            val link = next?.loadPrev() ?: tail.load()
            if (link.isBeingDeleted || this.loadNext().let { it.node !== next || it.isBeingDeleted }) break
            if (
                if (next != null) next.compareAndSetPrev(link, BackwardLink(this, false))
                else tail.compareAndSet(link, BackwardLink(this, false))
            ) {
                if (this.loadPrev().isBeingDeleted) correctPrev(this, next)
                break
            }
        }
    }

    override fun tryAcquiring(): Boolean {
        while (true) {
            val next = head.load().castOrNull<HeadPermitsLink>() ?: return false
            if (head.compareAndSet(next, if (next.availablePermits > 1u) HeadPermitsLink(availablePermits = next.availablePermits - 1u) else HeadForwardLink(null))) {
                return true
            }
        }
    }

    private fun Node.remove() {
        while (true) {
            val next = this.loadNext()
            if (next.isBeingDeleted) return
            if (this.compareAndSetNext(next, ForwardLink(next.node, next.continuation, ToBeIgnoredAfterDeletion))) {
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

    override suspend fun awaitAcquire() {
        suspendCancellableCoroutine { continuation ->
            val newNode = Node()
            newNode.storePrev(BackwardLink(null, false))
            val nextLinkToNewNode = HeadForwardLink(newNode)
            while (true) {
                val next = head.load()
                when (next) {
                    is HeadPermitsLink ->
                        if (next.availablePermits == 1u) {
                            if (head.compareAndSet(next, HeadForwardLink(null))) {
                                continuation.justResume() // TODO: Should something be released in case of cancellation?
                                break
                            }
                        } else {
                            if (head.compareAndSet(next, HeadPermitsLink(next.availablePermits - 1u))) {
                                continuation.justResume() // TODO: Should something be released in case of cancellation?
                                break
                            }
                        }
                    is HeadForwardLink -> {
                        newNode.storeNext(ForwardLink(next.node, continuation, NotYetDeleted))
                        if (head.compareAndSet(next, nextLinkToNewNode)) {
                            newNode.pushEnd(next.node)
                            continuation.invokeOnCancellation { newNode.remove() }
                            break
                        }
                    }
                }
            }
        }
    }

    override fun tryReleasing(): Boolean {
        var node = tail.load().node
        while (true) {
            if (node !== null) {
                if (node.loadNext().let { it.node !== null || it.isBeingDeleted }) {
                    node = correctPrev(node, null)
                    continue
                }
                while (true) {
                    val link = node.loadNext()
                    if (link.node !== null || link.isBeingDeleted) break
                    if (node.compareAndSetNext(link, ForwardLink(null, link.continuation, ToBeResumedAfterDeletion))) {
                        val prev = node.loadPrev().node
                        correctPrev(prev, null)
                        return true
                    }
                }
            } else {
                val headLink = head.load()
                when (headLink) {
                    is HeadForwardLink ->
                        when {
                            headLink.node !== null -> {
                                node = correctPrev(null, null)
                                continue
                            }
                            head.compareAndSet(headLink, HeadPermitsLink(1u)) -> return true
                        }
                    is HeadPermitsLink ->
                        when {
                            headLink.availablePermits == permits -> return false
                            head.compareAndSet(headLink, HeadPermitsLink(headLink.availablePermits + 1u)) -> return true
                        }
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
        val continuation: CancellableContinuation<Unit>,
        val deletionStatus: DeletionStatus = DeletionStatus.NotYetDeleted,
    ) {
        enum class DeletionStatus {
            NotYetDeleted, ToBeResumedAfterDeletion, ToBeIgnoredAfterDeletion;
        }
    }

    private /*value*/ data class HeadPermitsLink(
        val availablePermits: UInt,
    )
    
    private /*value*/ data class HeadForwardLink(
        val node: Node?,
    )
}