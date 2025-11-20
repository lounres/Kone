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


// TODO: Порезать код на функции
public class KoneSundellTsigasSymmetricRelationExclusion<in Element>(
    private val relation: (Element, Element) -> Boolean,
) : KoneSymmetricRelationExclusion<Element> {
    private val head = AtomicReference(LockForwardLink<Element>(null))
    private val tail = AtomicReference(LockBackwardLink<Element>(null))
    
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
    }
    
    @IgnorableReturnValue
    private fun correctPrev(prev: LockNode<Element>?, node: LockNode<Element>?): LockNode<Element>? {
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
    
    private fun LockNode<Element>.delete() {
        this.state.store(Deleted)
        
        scope {
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
        
        scope {
            var currentNode = this.dependantHead.load().node
            while (currentNode != null) {
                val dependencyNode = currentNode.dependencyNode
                val dependantLockNode = dependencyNode.lockNode
                
                scope {
                    while (true) {
                        val next = dependencyNode.loadNext()
                        if (next.isBeingDeleted) break
                        if (dependencyNode.compareAndSetNext(next, next.copy(isBeingDeleted = true))) {
                            while (true) {
                                val prev = dependencyNode.loadPrev()
                                if (prev.isBeingDeleted || dependencyNode.compareAndSetPrev(prev, prev.copy(isBeingDeleted = true))) {
                                    dependantLockNode.correctPrev(prev.node, next.node)
                                    break
                                }
                            }
                        }
                    }
                }
                
                if (
                    dependantLockNode.dependencyHead.load().node === null
                    && dependantLockNode.state.compareAndSet(Initialized, Resumed)
                ) {
                    dependantLockNode.continuation
                        ?.resume(KoneSymmetricRelationExclusion.Lock { dependantLockNode.delete() }) { _, _, _ -> dependantLockNode.delete() }
                }
                
                currentNode = currentNode.loadNext().node
            }
        }
    }
    
    override fun tryLockingBy(element: Element): KoneSymmetricRelationExclusion.Lock? {
        val newLockNode = LockNode(element, null)
        
        scope {
            newLockNode.storePrev(LockBackwardLink(null))
            val nextLinkToNewNode = LockForwardLink(newLockNode)
            while (true) {
                val next = head.load()
                newLockNode.storeNext(next)
                if (head.compareAndSet(next, nextLinkToNewNode)) {
                    pushEnd(newLockNode, next.node)
                    break
                }
            }
        }
        
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
        
        return { newLockNode.delete() }
    }
    
    override suspend fun awaitLockBy(element: Element): KoneSymmetricRelationExclusion.Lock =
        suspendCancellableCoroutine { continuation ->
            val newLockNode = LockNode(element, continuation)
            
            scope {
                newLockNode.storePrev(LockBackwardLink(null))
                val nextLinkToNewNode = LockForwardLink(newLockNode)
                while (true) {
                    val next = head.load()
                    newLockNode.storeNext(next)
                    if (head.compareAndSet(next, nextLinkToNewNode)) {
                        pushEnd(newLockNode, next.node)
                        break
                    }
                }
            }
            
            continuation.invokeOnCancellation { newLockNode.delete() }
            
            scope {
                var currentNode = newLockNode.loadNext().node
                while (currentNode !== null) {
                    if (currentNode.state.load() != Deleted && relation(element, currentNode.element)) {
                        val dependencyNode = DependencyNode(lockNode = newLockNode, dependencyLockNode = currentNode)
                        val dependantNode = dependencyNode.dependantNode
                        scope {
                            dependencyNode.storePrev(DependencyBackwardLink(null))
                            val nextLinkToNewNode = DependencyForwardLink(dependencyNode)
                            while (true) {
                                val next = newLockNode.dependencyHead.load()
                                dependencyNode.storeNext(next)
                                if (newLockNode.dependencyHead.compareAndSet(next, nextLinkToNewNode)) {
                                    newLockNode.pushEnd(dependencyNode, next.node)
                                    break
                                }
                            }
                        }
                        scope {
                            dependantNode.storePrev(DependantBackwardLink(null))
                            val nextLinkToNewNode = DependantForwardLink(dependantNode)
                            while (true) {
                                val next = newLockNode.dependantHead.load()
                                dependantNode.storeNext(next)
                                if (newLockNode.dependantHead.compareAndSet(next, nextLinkToNewNode)) {
                                    newLockNode.pushEnd(dependantNode, next.node)
                                    break
                                }
                            }
                        }
                        if (currentNode.state.load() == Deleted) {
                            while (true) {
                                val next = dependencyNode.loadNext()
                                if (next.isBeingDeleted) break
                                if (dependencyNode.compareAndSetNext(next, next.copy(isBeingDeleted = true))) {
                                    while (true) {
                                        val prev = dependencyNode.loadPrev()
                                        if (prev.isBeingDeleted || dependencyNode.compareAndSetPrev(prev, prev.copy(isBeingDeleted = true))) {
                                            newLockNode.correctPrev(prev.node, next.node)
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    }
                    currentNode = currentNode.loadNext().node
                }
            }
            
            val _ = newLockNode.state.compareAndSet(Constructed, Initialized)
            
            if (
                newLockNode.dependencyHead.load().node === null
                && newLockNode.state.compareAndSet(Initialized, Resumed)
            ) {
                continuation.resume(KoneSymmetricRelationExclusion.Lock { newLockNode.delete() }) { _, _, _ -> newLockNode.delete() }
            }
        }
    
    private class DependencyNode<Element>(
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
    
    private /*value*/ data class DependencyBackwardLink<Element>(
        val node: DependencyNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    private /*value*/ data class DependencyForwardLink<Element>(
        val node: DependencyNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    private class DependantNode<Element>(
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
    
    private /*value*/ data class DependantBackwardLink<Element>(
        val node: DependantNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    private /*value*/ data class DependantForwardLink<Element>(
        val node: DependantNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    private class LockNode<Element>(
        val element: Element,
        var continuation: CancellableContinuation<KoneSymmetricRelationExclusion.Lock>?,
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
        
        enum class State {
            Constructed,
            Initialized,
            Resumed,
            Deleted,
        }
    }
    
    private /*value*/ data class LockBackwardLink<Element>(
        val node: LockNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
    
    private /*value*/ data class LockForwardLink<Element>(
        val node: LockNode<Element>?,
        val isBeingDeleted: Boolean = false,
    )
}