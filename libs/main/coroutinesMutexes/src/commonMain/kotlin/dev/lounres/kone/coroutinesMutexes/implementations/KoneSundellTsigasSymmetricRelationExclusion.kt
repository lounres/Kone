/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.coroutinesMutexes.KoneSymmetricRelationExclusion
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.scope
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.atomics.AtomicReference
import kotlin.coroutines.CoroutineContext


public class KoneSundellTsigasSymmetricRelationExclusion<in Element>(
    private val relation: (Element, Element) -> Boolean,
) : KoneSymmetricRelationExclusion<Element> {
    internal val head: AtomicReference<LockForwardLink<@UnsafeVariance Element>> = AtomicReference(LockForwardLink<Element>(null))
    internal val tail: AtomicReference<LockBackwardLink<@UnsafeVariance Element>> = AtomicReference(LockBackwardLink<Element>(null))
    
    public companion object {
        // SetMark for `prev` `Link`
        private fun <Element> DependencyNode<Element>.markPrevLink() {
            while (true) {
                val link = loadPrev()
                if (link.isBeingDeleted || compareAndSetPrev(link, link.copy(isBeingDeleted = true))) break
            }
        }
        
        // SetMark for `prev` `Link`
        private fun <Element> DependantNode<Element>.markPrevLink() {
            while (true) {
                val link = loadPrev()
                if (link.isBeingDeleted || compareAndSetPrev(link, link.copy(isBeingDeleted = true))) break
            }
        }
        
        // SetMark for `prev` `Link`
        private fun <Element> LockNode<Element>.markPrevLink() {
            while (true) {
                val link = loadPrev()
                if (link.isBeingDeleted || compareAndSetPrev(link, LockBackwardLink(link.node, true))) break
            }
        }
        
        @IgnorableReturnValue
        private fun <Element> LockNode<Element>.correctPrev(prev: DependencyNode<Element>?, node: DependencyNode<Element>?): DependencyNode<Element>? {
            var prev = prev
            var lastLink: Maybe<DependencyNode<Element>?> = None
            while (true) {
                val link = node?.loadPrev() ?: dependencyTail.load()
                if (link.isBeingDeleted) break
                if (prev !== null) {
                    val prev2 = prev.loadNext()
                    if (prev2.isBeingDeleted) {
                        if (lastLink != None) {
                            lastLink as Some
                            val lastLinkValue = lastLink.value
                            prev.markPrevLink()
                            if (lastLinkValue !== null) {
                                while (true) {
                                    val link = lastLinkValue.loadNext()
                                    if (link.node !== prev || link.isBeingDeleted) break
                                    if (lastLinkValue.compareAndSetNext(link, DependencyForwardLink(prev2.node))) break
                                }
                            } else {
                                while (true) {
                                    val link = dependencyHead.load()
                                    if (link.node !== prev || link.isBeingDeleted) break
                                    if (dependencyHead.compareAndSet(link, DependencyForwardLink(prev2.node))) break
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
                    val prev2 = dependencyHead.load()
                    if (prev2.node !== node) {
                        lastLink = Some(prev)
                        prev = prev2.node
                        continue
                    }
                }
                if (
                    if (node !== null) node.compareAndSetPrev(link, DependencyBackwardLink(prev))
                    else dependencyTail.compareAndSet(link, DependencyBackwardLink(prev))
                ) {
                    if (prev?.loadPrev()?.isBeingDeleted == true) continue
                    break
                }
            }
            return prev
        }
        
        @IgnorableReturnValue
        private fun <Element> LockNode<Element>.correctPrev(prev: DependantNode<Element>?, node: DependantNode<Element>?): DependantNode<Element>? {
            var prev = prev
            var lastLink: Maybe<DependantNode<Element>?> = None
            while (true) {
                val link = node?.loadPrev() ?: dependantTail.load()
                if (link.isBeingDeleted) break
                if (prev !== null) {
                    val prev2 = prev.loadNext()
                    if (prev2.isBeingDeleted) {
                        if (lastLink != None) {
                            lastLink as Some
                            val lastLinkValue = lastLink.value
                            prev.markPrevLink()
                            if (lastLinkValue !== null) {
                                while (true) {
                                    val link = lastLinkValue.loadNext()
                                    if (link.node !== prev || link.isBeingDeleted) break
                                    if (lastLinkValue.compareAndSetNext(link, DependantForwardLink(prev2.node))) break
                                }
                            } else {
                                while (true) {
                                    val link = dependantHead.load()
                                    if (link.node !== prev || link.isBeingDeleted) break
                                    if (dependantHead.compareAndSet(link, DependantForwardLink(prev2.node))) break
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
                    val prev2 = dependantHead.load()
                    if (prev2.node !== node) {
                        lastLink = Some(prev)
                        prev = prev2.node
                        continue
                    }
                }
                if (
                    if (node !== null) node.compareAndSetPrev(link, DependantBackwardLink(prev))
                    else dependantTail.compareAndSet(link, DependantBackwardLink(prev))
                ) {
                    if (prev?.loadPrev()?.isBeingDeleted == true) continue
                    break
                }
            }
            return prev
        }
        
        private fun <Element> LockNode<Element>.pushEnd(node: DependencyNode<Element>, next: DependencyNode<Element>?) {
            while (true) {
                val link = next?.loadPrev() ?: dependencyTail.load()
                if (link.isBeingDeleted || node.loadNext().let { it.node !== next || it.isBeingDeleted }) break
                if (
                    if (next != null) next.compareAndSetPrev(link, DependencyBackwardLink(node, false))
                    else dependencyTail.compareAndSet(link, DependencyBackwardLink(node, false))
                ) {
                    if (node.loadPrev().isBeingDeleted) correctPrev(node, next)
                    break
                }
            }
        }
        
        private fun <Element> LockNode<Element>.pushEnd(node: DependantNode<Element>, next: DependantNode<Element>?) {
            while (true) {
                val link = next?.loadPrev() ?: dependantTail.load()
                if (link.isBeingDeleted || node.loadNext().let { it.node !== next || it.isBeingDeleted }) break
                if (
                    if (next != null) next.compareAndSetPrev(link, DependantBackwardLink(node, false))
                    else dependantTail.compareAndSet(link, DependantBackwardLink(node, false))
                ) {
                    if (node.loadPrev().isBeingDeleted) correctPrev(node, next)
                    break
                }
            }
        }
        
        private fun <Element> LockNode<Element>.remove(node: DependencyNode<Element>) {
            while (true) {
                val next = node.loadNext()
                if (next.isBeingDeleted) break
                if (node.compareAndSetNext(next, next.copy(isBeingDeleted = true))) {
                    while (true) {
                        val prev = node.loadPrev()
                        if (prev.isBeingDeleted || node.compareAndSetPrev(prev, prev.copy(isBeingDeleted = true))) {
                            correctPrev(prev.node, next.node)
                            break
                        }
                    }
                }
            }
        }
        
        private fun <Element> LockNode<Element>.add(node: DependencyNode<Element>) {
            node.storePrev(DependencyBackwardLink(null))
            val nextLinkToNewNode = DependencyForwardLink(node)
            while (true) {
                val next = dependencyHead.load()
                node.storeNext(next)
                if (dependencyHead.compareAndSet(next, nextLinkToNewNode)) {
                    pushEnd(node, next.node)
                    break
                }
            }
        }
        
        private fun <Element> LockNode<Element>.add(node: DependantNode<Element>) {
            node.storePrev(DependantBackwardLink(null))
            val nextLinkToNewNode = DependantForwardLink(node)
            while (true) {
                val next = dependantHead.load()
                node.storeNext(next)
                if (dependantHead.compareAndSet(next, nextLinkToNewNode)) {
                    pushEnd(node, next.node)
                    break
                }
            }
        }
    }
    
    @IgnorableReturnValue
    internal fun correctPrev(prev: LockNode<@UnsafeVariance Element>?, node: LockNode<@UnsafeVariance Element>?): LockNode<@UnsafeVariance Element>? {
        var prev = prev
        var lastLink: Maybe<LockNode<Element>?> = None
        while (true) {
            val link = node?.loadPrev() ?: tail.load()
            if (link.isBeingDeleted) break
            if (prev !== null) {
                val prev2 = prev.loadNext()
                if (prev2.isBeingDeleted) {
                    if (lastLink != None) {
                        lastLink as Some
                        val lastLinkValue = lastLink.value
                        prev.markPrevLink()
                        if (lastLinkValue !== null) {
                            while (true) {
                                val link = lastLinkValue.loadNext()
                                if (link.node !== prev || link.isBeingDeleted) break
                                if (lastLinkValue.compareAndSetNext(link, LockForwardLink(prev2.node))) break
                            }
                        } else {
                            while (true) {
                                val link = head.load()
                                if (link.node !== prev || link.isBeingDeleted) break
                                if (head.compareAndSet(link, LockForwardLink(prev2.node))) break
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
                if (node !== null) node.compareAndSetPrev(link, LockBackwardLink(prev))
                else tail.compareAndSet(link, LockBackwardLink(prev))
            ) {
                if (prev?.loadPrev()?.isBeingDeleted == true) continue
                break
            }
        }
        return prev
    }
    
    private fun pushEnd(node: LockNode<Element>, next: LockNode<Element>?) {
        while (true) {
            val link = next?.loadPrev() ?: tail.load()
            if (link.isBeingDeleted || node.loadNext().let { it.node !== next || it.isBeingDeleted }) break
            if (
                if (next != null) next.compareAndSetPrev(link, LockBackwardLink(node, false))
                else tail.compareAndSet(link, LockBackwardLink(node, false))
            ) {
                if (node.loadPrev().isBeingDeleted) correctPrev(node, next)
                break
            }
        }
    }
    
    private fun LockNode<Element>.remove() {
        while (true) {
            val next = this.loadNext()
            if (next.isBeingDeleted) break
            if (this.compareAndSetNext(next, next.copy(isBeingDeleted = true))) {
                while (true) {
                    val prev = this.loadPrev()
                    if (prev.isBeingDeleted || this.compareAndSetPrev(prev, prev.copy(isBeingDeleted = true))) {
                        correctPrev(prev.node, next.node)
                        break
                    }
                }
            }
        }
    }
    
    private fun add(node: LockNode<Element>) {
        node.storePrev(LockBackwardLink(null))
        val nextLinkToNewNode = LockForwardLink(node)
        while (true) {
            val next = head.load()
            node.storeNext(next)
            if (head.compareAndSet(next, nextLinkToNewNode)) {
                pushEnd(node, next.node)
                break
            }
        }
    }
    
    internal fun LockNode<@UnsafeVariance Element>.delete() {
        this.state.store(Deleted)
        
        this.remove()
        
        var currentNode = this.dependantHead.load().node
        while (currentNode != null) {
            val dependencyNode = currentNode.dependencyNode
            val dependantLockNode = dependencyNode.lockNode
            
            dependantLockNode.remove(dependencyNode)
            
            if (
                dependantLockNode.dependencyHead.load().node === null
                && dependantLockNode.state.compareAndSet(Initialized, Resumed)
            ) {
                dependantLockNode.continuation?.resume(dependantLockNode.lock, dependantLockNode.onCancellation)
                // TODO
//                dependantLockNode.continuation = null
            }
            
            currentNode = currentNode.loadNext().node
        }
    }
    
    override fun tryLockingBy(element: Element): KoneSymmetricRelationExclusion.Lock? {
        val newLockNode = LockNode(element, null, this)
        
        add(newLockNode)
        
        scope {
            var currentNode = newLockNode.loadNext().node
            while (currentNode !== null) {
                if (currentNode.state.load() != Deleted && relation(element, currentNode.element)) {
                    newLockNode.delete()
                    return null
                }
                currentNode = currentNode.loadNext().node
            }
        }
        
        val _ = newLockNode.state.compareAndSet(Constructed, Resumed)
        
        return newLockNode.lock
    }
    
    override suspend fun awaitLockBy(element: Element): KoneSymmetricRelationExclusion.Lock =
        suspendCancellableCoroutine { continuation ->
            val newLockNode = LockNode(element, continuation, this)
            
            add(newLockNode)
            
            continuation.invokeOnCancellation { newLockNode.delete() }
            
            scope {
                var currentNode = newLockNode.loadNext().node
                while (currentNode !== null) {
                    if (currentNode.state.load() != Deleted && relation(element, currentNode.element)) {
                        val dependencyNode = DependencyNode(lockNode = newLockNode, dependencyLockNode = currentNode)
                        val dependantNode = dependencyNode.dependantNode
                        newLockNode.add(dependencyNode)
                        currentNode.add(dependantNode)
                        if (currentNode.state.load() == Deleted) newLockNode.remove(dependencyNode)
                    }
                    currentNode = currentNode.loadNext().node
                }
            }
            
            val _ = newLockNode.state.compareAndSet(Constructed, Initialized)
            
            if (
                newLockNode.dependencyHead.load().node === null
                && newLockNode.state.compareAndSet(Initialized, Resumed)
            ) {
                continuation.resume(newLockNode.lock, newLockNode.onCancellation)
                // TODO
//                newLockNode.continuation = null
            }
        }
    
    internal class DependencyNode<Element>(
        val lockNode: LockNode<Element>,
        dependencyLockNode: LockNode<Element>,
    ) {
        val dependantNode: DependantNode<Element> = DependantNode(dependencyLockNode = dependencyLockNode, dependencyNode = this)
        
        private val prev: AtomicReference<DependencyBackwardLink<Element>?> = AtomicReference(null)
        fun loadPrev(): DependencyBackwardLink<Element> = prev.load()!!
        fun storePrev(newValue: DependencyBackwardLink<Element>) {
            prev.store(newValue)
        }
        fun compareAndSetPrev(expectedValue: DependencyBackwardLink<Element>, newValue: DependencyBackwardLink<Element>) =
            prev.compareAndSet(expectedValue, newValue)
        private val next: AtomicReference<DependencyForwardLink<Element>?> = AtomicReference(null)
        fun loadNext(): DependencyForwardLink<Element> = next.load()!!
        fun storeNext(newValue: DependencyForwardLink<Element>) {
            next.store(newValue)
        }
        fun compareAndSetNext(expectedValue: DependencyForwardLink<Element>, newValue: DependencyForwardLink<Element>) =
            next.compareAndSet(expectedValue, newValue)
    }
    
    internal /*value*/ data class DependencyBackwardLink<Element>(
        val node: DependencyNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    internal /*value*/ data class DependencyForwardLink<Element>(
        val node: DependencyNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    internal class DependantNode<Element>(
        val dependencyLockNode: LockNode<Element>,
        val dependencyNode: DependencyNode<Element>,
    ) {
        private val prev: AtomicReference<DependantBackwardLink<Element>?> = AtomicReference(null)
        fun loadPrev(): DependantBackwardLink<Element> = prev.load()!!
        fun storePrev(newValue: DependantBackwardLink<Element>) {
            prev.store(newValue)
        }
        fun compareAndSetPrev(expectedValue: DependantBackwardLink<Element>, newValue: DependantBackwardLink<Element>) =
            prev.compareAndSet(expectedValue, newValue)
        private val next: AtomicReference<DependantForwardLink<Element>?> = AtomicReference(null)
        fun loadNext(): DependantForwardLink<Element> = next.load()!!
        fun storeNext(newValue: DependantForwardLink<Element>) {
            next.store(newValue)
        }
        fun compareAndSetNext(expectedValue: DependantForwardLink<Element>, newValue: DependantForwardLink<Element>) =
            next.compareAndSet(expectedValue, newValue)
    }
    
    internal /*value*/ data class DependantBackwardLink<Element>(
        val node: DependantNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    internal /*value*/ data class DependantForwardLink<Element>(
        val node: DependantNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    internal class LockNode<Element>(
        val element: Element,
        var continuation: CancellableContinuation<KoneSymmetricRelationExclusion.Lock>?,
        mutex: KoneSundellTsigasSymmetricRelationExclusion<Element>,
    ) {
        private val prev: AtomicReference<LockBackwardLink<Element>?> = AtomicReference(null)
        fun loadPrev(): LockBackwardLink<Element> = prev.load()!!
        fun storePrev(newValue: LockBackwardLink<Element>) {
            prev.store(newValue)
        }
        fun compareAndSetPrev(expectedValue: LockBackwardLink<Element>, newValue: LockBackwardLink<Element>) =
            prev.compareAndSet(expectedValue, newValue)
        private val next: AtomicReference<LockForwardLink<Element>?> = AtomicReference(null)
        fun loadNext(): LockForwardLink<Element> = next.load()!!
        fun storeNext(newValue: LockForwardLink<Element>) {
            next.store(newValue)
        }
        fun compareAndSetNext(expectedValue: LockForwardLink<Element>, newValue: LockForwardLink<Element>) =
            next.compareAndSet(expectedValue, newValue)
        
        val state = AtomicReference(State.Constructed)
        
        val dependencyHead = AtomicReference(DependencyForwardLink<Element>(null))
        val dependencyTail = AtomicReference(DependencyBackwardLink<Element>(null))
        
        val dependantHead = AtomicReference(DependantForwardLink<Element>(null))
        val dependantTail = AtomicReference(DependantBackwardLink<Element>(null))
        
        val lock = KoneSymmetricRelationExclusion.Lock { with(mutex) { this@LockNode.delete() } }
        val onCancellation: (cause: Throwable, value: KoneSymmetricRelationExclusion.Lock, context: CoroutineContext) -> Unit = { _, _, _ -> with(mutex) { this@LockNode.delete() } }
        
        enum class State {
            Constructed,
            Initialized,
            Resumed,
            Deleted,
        }
    }
    
    internal /*value*/ data class LockBackwardLink<Element>(
        val node: LockNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    internal /*value*/ data class LockForwardLink<Element>(
        val node: LockNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
}