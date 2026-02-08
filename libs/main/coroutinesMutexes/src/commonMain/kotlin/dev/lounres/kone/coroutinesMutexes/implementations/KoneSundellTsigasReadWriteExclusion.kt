/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.coroutinesMutexes.KoneReadWriteExclusion
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.atomics.AtomicReference
import kotlin.coroutines.CoroutineContext


public class KoneSundellTsigasReadWriteExclusion(
    private val readPermits: UInt = UInt.MAX_VALUE,
) : KoneReadWriteExclusion {
    init {
        require(readPermits > 0u) { "KoneSundellTsigasReadWriteExclusion does not support 0 read permits" }
    }

    private val head = AtomicReference<HeadForwardLink>(HeadTailLink(0u, Read))
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

    private val onReadCancellation: (cause: Throwable, value: Unit, context: CoroutineContext) -> Unit = { _, _, _ -> val _ = tryReadUnlocking() }
    private val onWriteCancellation: (cause: Throwable, value: Unit, context: CoroutineContext) -> Unit = { _, _, _ -> val _ = tryWriteUnlocking() }
    
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
                        if (lastLinkValue != null) {
                            while (true) {
                                val link = lastLinkValue.loadNext()
                                if (link.node !== prev || link.isBeingDeleted) break
                                if (
                                    lastLinkValue.compareAndSetNext(
                                        link,
                                        when (prev2) {
                                            is NextNodeLink -> NextNodeLink(
                                                node = prev2.node,
                                                continuation = link.continuation,
                                                continuationLockType = link.continuationLockType,
                                            )
                                            is TailLink -> TailLink(
                                                continuation = link.continuation,
                                                continuationLockType = link.continuationLockType,
                                                lockedTimes = prev2.lockedTimes,
                                                lockType = prev2.lockType,
                                            )
                                        }
                                    )
                                ) {
                                    when (prev2) {
                                        is NextNodeLink -> {}
                                        is TailLink -> {
                                            if (prev2.deletionStatus == ToBeResumedAfterDeletion)
                                                prev2.continuation.justResume(
                                                    when (prev2.continuationLockType) {
                                                        Read -> onReadCancellation
                                                        Write -> onWriteCancellation
                                                    }
                                                )
                                        }
                                    }
                                    break
                                }
                            }
                        } else {
                            while (true) {
                                val link = head.load()
                                if (link.node !== prev || link.isBeingDeleted) break
                                if (
                                    head.compareAndSet(
                                        link,
                                        when (prev2) {
                                            is NextNodeLink -> HeadNodeLink(
                                                node = prev2.node,
                                            )
                                            is TailLink -> HeadTailLink(
                                                lockedTimes = prev2.lockedTimes,
                                                lockType = prev2.lockType,
                                            )
                                        }
                                    )
                                ) {
                                    when (prev2) {
                                        is NextNodeLink -> {}
                                        is TailLink -> {
                                            if (prev2.deletionStatus == ToBeResumedAfterDeletion)
                                                prev2.continuation.justResume(
                                                    when (prev2.continuationLockType) {
                                                        Read -> onReadCancellation
                                                        Write -> onWriteCancellation
                                                    }
                                                )
                                        }
                                    }
                                    break
                                }
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
                val prev2 = head.load()
                if (prev2.node !== node) {
                    lastLink = Some(prev)
                    prev = prev2.node
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
    
    @IgnorableReturnValue
    private fun maintainTail(): Boolean {
        var node = tail.load().node
        while (true) {
            if (node !== null) {
                while (true) {
                    when (val link = node!!.loadNext()) {
                        is NextNodeLink -> {
                            node = correctPrev(node, null)
                            break
                        }
                        is TailLink -> {
                            when {
//                                link.deletionStatus == ToBeIgnoredAfterDeletion -> {
//                                    node = correctPrev(node, null)
//                                    break
//                                }
//                                link.deletionStatus == ToBeResumedAfterDeletion -> return
                                link.isBeingDeleted -> {
                                    node = correctPrev(node, null)
                                    break
                                }
                                link.lockedTimes == 0u -> error("The RWE contains non-resumed continuation while being fully unlocked")
                                else -> when (link.lockType) {
                                    Read -> when {
                                        link.lockedTimes == readPermits -> return false
                                        link.continuationLockType == Write -> return false
                                        else -> if (
                                            node.compareAndSetNext(
                                                link,
                                                link.copy(
                                                    lockedTimes = link.lockedTimes + 1u,
                                                    deletionStatus = ToBeResumedAfterDeletion,
                                                ),
                                            )
                                        ) {
                                            val prev = node.loadPrev().node
                                            node = correctPrev(prev, null)
                                            break
                                        }
                                    }
                                    Write -> return false
                                }
                            }
                        }
                    }
                }
            } else {
                if (head.load().let { it.node !== null || it.isBeingDeleted }) {
                    node = correctPrev(node, null)
                    continue
                }
                return true
            }
        }
    }

    override fun tryReadLocking(): Boolean {
        while (true) {
            val next = head.load()
            val newNext = when (next) {
                is HeadNodeLink ->
                    if (maintainTail()) continue
                    else return false
                is HeadTailLink -> when {
                    next.lockedTimes == 0u -> HeadTailLink(
                        lockedTimes = 1u,
                        lockType = Read,
                    )
                    next.lockType == Write -> return false
                    next.lockedTimes == readPermits -> return false
                    else -> HeadTailLink(
                        lockedTimes = next.lockedTimes + 1u,
                        lockType = Read,
                    )
                }
            }
            if (head.compareAndSet(next, newNext)) return true
        }
    }
    
    override fun tryWriteLocking(): Boolean {
        while (true) {
            val next = head.load()
            val newNext = when (next) {
                is HeadNodeLink ->
                    if (maintainTail()) continue
                    else return false
                is HeadTailLink ->
                    if (next.lockedTimes == 0u) HeadTailLink(
                        lockedTimes = 1u,
                        lockType = Write,
                    )
                    else return false
            }
            if (head.compareAndSet(next, newNext)) return true
        }
    }

    private fun Node.remove() {
        while (true) {
            val next = this.loadNext()
            if (next.isBeingDeleted) return
            if (
                this.compareAndSetNext(
                    next,
                    when (next) {
                        is NextNodeLink -> next.copy(isBeingDeleted = true)
                        is TailLink -> next.copy(deletionStatus = ToBeIgnoredAfterDeletion)
                    }
                )
            ) {
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

    override suspend fun awaitReadLock() {
        suspendCancellableCoroutine { continuation ->
            val newNode = Node()
            newNode.storePrev(BackwardLink(null, false))
            val nextLinkToNewNode = HeadNodeLink(newNode)
            while (true) {
                when (val next = head.load()) {
                    is HeadNodeLink -> {
                        if (maintainTail()) continue
                        newNode.storeNext(
                            NextNodeLink(
                                node = next.node,
                                continuation = continuation,
                                continuationLockType = Read,
                            )
                        )
                        if (head.compareAndSet(next, nextLinkToNewNode)) {
                            pushEnd(newNode, next.node)
                            continuation.invokeOnCancellation { newNode.remove() }
                            break
                        }
                    }
                    is HeadTailLink -> when {
                        next.lockedTimes == 0u -> {
                            if (head.compareAndSet(next, HeadTailLink(lockedTimes = 1u, lockType = Read))) {
                                continuation.justResume()
                                break
                            }
                        }
                        next.lockType == Write -> {
                            newNode.storeNext(
                                TailLink(
                                    continuation = continuation,
                                    continuationLockType = Read,
                                    lockedTimes = next.lockedTimes,
                                    lockType = next.lockType,
                                )
                            )
                            if (head.compareAndSet(next, nextLinkToNewNode)) {
                                pushEnd(newNode, next.node)
                                continuation.invokeOnCancellation { newNode.remove() }
                                break
                            }
                        }
                        next.lockedTimes == readPermits -> {
                            newNode.storeNext(
                                TailLink(
                                    continuation = continuation,
                                    continuationLockType = Read,
                                    lockedTimes = next.lockedTimes,
                                    lockType = next.lockType,
                                )
                            )
                            if (head.compareAndSet(next, nextLinkToNewNode)) {
                                pushEnd(newNode, next.node)
                                continuation.invokeOnCancellation { newNode.remove() }
                                break
                            }
                        }
                        else -> {
                            if (head.compareAndSet(next, HeadTailLink(lockedTimes = next.lockedTimes + 1u, lockType = Read))) {
                                continuation.justResume()
                                break
                            }
                        }
                    }
                }
            }
        }
    }
    
    override suspend fun awaitWriteLock() {
        suspendCancellableCoroutine { continuation ->
            val newNode = Node()
            newNode.storePrev(BackwardLink(null, false))
            val nextLinkToNewNode = HeadNodeLink(newNode)
            while (true) {
                when (val next = head.load()) {
                    is HeadNodeLink -> {
                        if (maintainTail()) continue
                        newNode.storeNext(
                            NextNodeLink(
                                node = next.node,
                                continuation = continuation,
                                continuationLockType = Write,
                            )
                        )
                        if (head.compareAndSet(next, nextLinkToNewNode)) {
                            pushEnd(newNode, next.node)
                            continuation.invokeOnCancellation { newNode.remove() }
                            break
                        }
                    }
                    is HeadTailLink ->
                        if (next.lockedTimes == 0u) {
                            if (head.compareAndSet(next, HeadTailLink(lockedTimes = 1u, lockType = Write))) {
                                continuation.justResume()
                                break
                            }
                        } else {
                            newNode.storeNext(
                                TailLink(
                                    continuation = continuation,
                                    continuationLockType = Write,
                                    lockedTimes = next.lockedTimes,
                                    lockType = next.lockType,
                                )
                            )
                            if (head.compareAndSet(next, nextLinkToNewNode)) {
                                pushEnd(newNode, next.node)
                                continuation.invokeOnCancellation { newNode.remove() }
                                break
                            }
                        }
                }
            }
        }
    }

    override fun tryReadUnlocking(): Boolean {
        var node = tail.load().node
        while (true) {
            if (node !== null) {
                while (true) {
                    when (val link = node!!.loadNext()) {
                        is NextNodeLink -> {
                            node = correctPrev(node, null)
                            break
                        }
                        is TailLink -> {
                            when {
                                link.isBeingDeleted -> {
                                    node = correctPrev(node, null)
                                    break
                                }
                                link.lockedTimes == 0u -> error("The RWE contains non-resumed continuation while being fully unlocked")
                                link.lockType == Write -> return false
                                else -> when (link.continuationLockType) {
                                    Read -> when {
                                        link.lockedTimes != readPermits -> {
                                            maintainTail()
                                            node = correctPrev(node, null)
                                            break
                                        }
                                        node.compareAndSetNext(
                                            link,
                                            link.copy(deletionStatus = ToBeResumedAfterDeletion)
                                        ) -> {
                                            val prev = node.loadPrev().node
                                            correctPrev(prev, null)
                                            maintainTail()
                                            return true
                                        }
                                    }
                                    Write -> if (link.lockedTimes == 1u) {
                                        if (
                                            node.compareAndSetNext(
                                                link,
                                                link.copy(
                                                    lockType = Write,
                                                    deletionStatus = ToBeResumedAfterDeletion
                                                )
                                            )
                                        ) {
                                            val prev = node.loadPrev().node
                                            correctPrev(prev, null)
                                            maintainTail()
                                            return true
                                        }
                                    } else {
                                        if (
                                            node.compareAndSetNext(
                                                link,
                                                link.copy(
                                                    lockedTimes = link.lockedTimes - 1u,
                                                )
                                            )
                                        ) {
                                            return true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                while (true) {
                    when (val link = head.load()) {
                        is HeadNodeLink -> {
                            node = correctPrev(node, null)
                            break
                        }
                        is HeadTailLink -> {
                            when {
                                link.isBeingDeleted -> {
                                    node = correctPrev(node, null)
                                    break
                                }
                                link.lockedTimes == 0u -> return false
                                link.lockType == Write -> return false
                                else -> if (
                                    head.compareAndSet(
                                        link,
                                        link.copy(lockedTimes = link.lockedTimes - 1u)
                                    )
                                ) return true
                            }
                        }
                    }
                }
            }
        }
    }
    
    override fun tryWriteUnlocking(): Boolean {
        var node = tail.load().node
        while (true) {
            if (node !== null) {
                while (true) {
                    when (val link = node!!.loadNext()) {
                        is NextNodeLink -> {
                            node = correctPrev(node, null)
                            break
                        }
                        is TailLink -> {
                            when {
                                link.isBeingDeleted -> {
                                    node = correctPrev(node, null)
                                    break
                                }
                                link.lockedTimes == 0u -> error("The RWE contains non-resumed continuation while being fully unlocked")
                                link.lockType == Read ->
                                    if (link.lockedTimes != readPermits && link.continuationLockType == Read) {
                                        maintainTail()
                                        break
                                    }
                                    else return false
                                else -> when (link.continuationLockType) {
                                    Write -> if (
                                        node.compareAndSetNext(
                                            link,
                                            link.copy(deletionStatus = ToBeResumedAfterDeletion)
                                        )
                                    ) {
                                        val prev = node.loadPrev().node
                                        correctPrev(prev, null)
                                        maintainTail()
                                        return true
                                    }
                                    Read -> if (
                                        node.compareAndSetNext(
                                            link,
                                            link.copy(
                                                lockType = Read,
                                                deletionStatus = ToBeResumedAfterDeletion,
                                            )
                                        )
                                    ) {
                                        val prev = node.loadPrev().node
                                        correctPrev(prev, null)
                                        maintainTail()
                                        return true
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                while (true) {
                    when (val link = head.load()) {
                        is HeadNodeLink -> {
                            node = correctPrev(node, null)
                            break
                        }
                        is HeadTailLink -> {
                            when {
                                link.isBeingDeleted -> {
                                    node = correctPrev(node, null)
                                    break
                                }
                                link.lockedTimes == 0u -> return false
                                link.lockType == Read -> return false
                                else -> if (
                                    head.compareAndSet(
                                        link,
                                        link.copy(lockedTimes = 0u)
                                    )
                                ) return true
                            }
                        }
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
        private val next: AtomicReference<NodeForwardLink?> = AtomicReference(null)
        fun loadNext(): NodeForwardLink = next.load()!!
        fun storeNext(newValue: NodeForwardLink) {
            next.store(newValue)
        }
        fun compareAndSetNext(expectedValue: NodeForwardLink, newValue: NodeForwardLink) =
            next.compareAndSet(expectedValue, newValue)
    }
    
    private enum class LockType {
        Read, Write;
    }

    private /*value*/ data class BackwardLink(
        val node: Node?,
        val isBeingDeleted: Boolean = false,
    )
    
    private interface ForwardLink {
        val node: Node?
        val isBeingDeleted: Boolean
    }

    private sealed interface NodeForwardLink : ForwardLink {
        val continuation: CancellableContinuation<Unit>
        val continuationLockType: LockType
    }

    private /*value*/ data class NextNodeLink(
        override val node: Node,
        override val continuation: CancellableContinuation<Unit>,
        override val continuationLockType: LockType,
        override val isBeingDeleted: Boolean = false,
    ) : NodeForwardLink

    private /*value*/ data class TailLink(
        override val continuation: CancellableContinuation<Unit>,
        override val continuationLockType: LockType,
        val lockedTimes: UInt,
        val lockType: LockType,
        val deletionStatus: DeletionStatus = NotYetDeleted,
    ) : NodeForwardLink {
        override val node: Nothing? get() = null
        override val isBeingDeleted: Boolean get() = deletionStatus != NotYetDeleted
        
        enum class DeletionStatus {
            NotYetDeleted, ToBeResumedAfterDeletion, ToBeIgnoredAfterDeletion;
        }
    }
    
    private sealed interface HeadForwardLink : ForwardLink {
        override val isBeingDeleted: Boolean get() = false
    }
    
    private /*value*/ data class HeadNodeLink(
        override val node: Node,
    ): HeadForwardLink
    
    private /*value*/ data class HeadTailLink(
        val lockedTimes: UInt,
        val lockType: LockType,
    ): HeadForwardLink {
        override val node: Nothing? get() = null
    }
}