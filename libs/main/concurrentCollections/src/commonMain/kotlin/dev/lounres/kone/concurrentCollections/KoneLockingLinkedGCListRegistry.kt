/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneIterableSet
import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneListWithContext
import dev.lounres.kone.collections.KoneMutableIterableListRegistry
import dev.lounres.kone.collections.KoneMutableRegistration
import dev.lounres.kone.collections.implementations.Disposable
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.collections.noElementException
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


//public class KoneLockingLinkedGCListRegistry<E, EC: Equality<E>>(
//    public val elementContext: EC,
//) : SynchronizedObject(), KoneMutableIterableListRegistry<E>, Disposable {
//    internal sealed interface Start<E> : Disposable {
//        var nextNode: End<E>
//    }
//    internal sealed interface End<E> : Disposable {
//        var previousNode: Start<E>
//    }
//    internal inner class StartStub: Start<E> {
//        private var _nextNode: End<E>? = null
//        override var nextNode: End<E>
//            get() = synchronized(this@KoneLockingLinkedGCListRegistry) { _nextNode!! }
//            set(value) {
//                synchronized(this@KoneLockingLinkedGCListRegistry) {
//                    _nextNode = value
//                }
//            }
//        override fun dispose() {
//            _nextNode = null
//        }
//    }
//    internal inner class EndStub : End<E> {
//        private var _previousNode: Start<E>? = null
//        override var previousNode: Start<E>
//            get() = synchronized(this@KoneLockingLinkedGCListRegistry) { _previousNode!! }
//            set(value) {
//                synchronized(this@KoneLockingLinkedGCListRegistry) {
//                    _previousNode = value
//                }
//            }
//        override fun dispose() {
//            _previousNode = null
//        }
//    }
//    @Suppress("UNCHECKED_CAST")
//    internal inner class Node : Start<E>, End<E>, KoneMutableRegistration<E> {
//        private var _nextNode: End<E>? = null
//        override var nextNode: End<E>
//            get() = synchronized(this@KoneLockingLinkedGCListRegistry) { _nextNode!! }
//            set(value) {
//                synchronized(this@KoneLockingLinkedGCListRegistry) {
//                    _nextNode = value
//                }
//            }
//        private var _previousNode: Start<E>? = null
//        override var previousNode: Start<E>
//            get() = synchronized(this@KoneLockingLinkedGCListRegistry) { _previousNode!! }
//            set(value) {
//                synchronized(this@KoneLockingLinkedGCListRegistry) {
//                    _previousNode = value
//                }
//            }
//        private var _element: E? = null
//        override var element: E
//            get() = synchronized(this@KoneLockingLinkedGCListRegistry) { _element as E }
//            set(value) {
//                synchronized(this@KoneLockingLinkedGCListRegistry) {
//                    _element = value
//                }
//            }
//        override fun dispose() {
//            _nextNode = null
//            _previousNode = null
//            _element = null
//        }
//        override fun remove() {
//            synchronized(this@KoneLockingLinkedGCListRegistry) {
//                previousNode.nextNode = nextNode
//                nextNode.previousNode = previousNode
//                dispose()
//            }
//        }
//    }
//
//    private var size: UInt = 0u
//    private var start: StartStub = StartStub()
//    private var end: EndStub = EndStub()
//
//    init {
//        start.nextNode = end
//        end.previousNode = start
//    }
//
//    override fun dispose() {
//        var currentNode = start.nextNode
//        start.dispose()
//        while (true) {
//            when (currentNode) {
//                is KoneLockingLinkedGCListRegistry<E, EC>.EndStub -> break
//                is KoneLockingLinkedGCListRegistry<E, EC>.Node -> {
//                    val nodeToDispose = currentNode
//                    currentNode = currentNode.nextNode
//                    nodeToDispose.dispose()
//                }
//            }
//        }
//        currentNode.dispose()
//    }
//
//    private fun endNodeByIndex(index: UInt): End<E> =
//        when {
//            index == size -> end
//            index <= (size - 1u) / 2u -> {
//                var currentEndNode = start.nextNode
//                for (i in 0u..<index) {
//                    currentEndNode = (currentEndNode as KoneLockingLinkedGCListRegistry<E, EC>.Node).nextNode
//                }
//                currentEndNode
//            }
//            else -> {
//                var currentEndNode = end.previousNode
//                for (i in index ..< size-1u) {
//                    currentEndNode = (currentEndNode as KoneLockingLinkedGCListRegistry<E, EC>.Node).previousNode
//                }
//                currentEndNode as KoneLockingLinkedGCListRegistry<E, EC>.Node
//            }
//        }
//    private fun justAddBefore(endNode: End<E>, element: E) {
//        val previousNode = endNode.previousNode
//        val newNode = Node()
//        newNode.element = element
//        newNode.previousNode = previousNode
//        newNode.nextNode = end
//        previousNode.nextNode = newNode
//        end.previousNode = newNode
//    }
//
//    override val elementsView: KoneIterableList<E> = Elements()
//    override val registrationsView: KoneIterableSet<KoneMutableRegistration<E>> = Registrations()
//
//    override fun register(element: E): KoneMutableRegistration<E> = synchronized(this) {
//        justAddBefore(end, element)
//        size++
//        end.previousNode as KoneLockingLinkedGCListRegistry<E, EC>.Node
//    }
//    override fun find(element: E): KoneIterableList<KoneMutableRegistration<E>> = synchronized(this) {
//        val accumulator = KoneGrowableArrayList<KoneMutableRegistration<E>>()
//        var currentNode = start.nextNode
//        while (true) {
//            when(currentNode) {
//                is KoneLockingLinkedGCListRegistry<E, EC>.EndStub -> break
//                is KoneLockingLinkedGCListRegistry<E, EC>.Node -> {
//                    if (elementContext.invoke { currentNode.element eq element }) accumulator.add(currentNode)
//                    currentNode = currentNode.nextNode
//                }
//            }
//        }
//        accumulator
//    }
//
//    internal inner class ElementsIterator(var currentIndex: UInt = 0u): KoneLinearIterator<E> {
//        init {
//            if (currentIndex > size) indexException(currentIndex, size)
//        }
//        var currentNode = endNodeByIndex(currentIndex)
//        override fun hasNext(): Boolean = currentIndex < size
//        override fun getNext(): E {
//            if (!hasNext()) noElementException(currentIndex, size)
//            return (currentNode as KoneLockingLinkedGCListRegistry<E, EC>.Node).element
//        }
//        override fun moveNext() {
//            if (!hasNext()) noElementException(currentIndex, size)
//            currentIndex++
//            currentNode = (currentNode as KoneLockingLinkedGCListRegistry<E, EC>.Node).nextNode
//        }
//        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)
//
//        override fun hasPrevious(): Boolean = currentIndex > 0u
//        override fun getPrevious(): E {
//            if (!hasPrevious()) noElementException(currentIndex, size)
//            return (currentNode.previousNode as KoneLockingLinkedGCListRegistry<E, EC>.Node).element
//        }
//        override fun movePrevious() {
//            if (!hasPrevious()) noElementException(currentIndex, size)
//            currentIndex--
//            currentNode = (currentNode.previousNode as KoneLockingLinkedGCListRegistry<E, EC>.Node)
//        }
//        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)
//    }
//
//    internal inner class Elements : KoneIterableList<E>, KoneListWithContext<E, EC> {
//        override val size: UInt get() = synchronized(this@KoneLockingLinkedGCListRegistry) { this@KoneLockingLinkedGCListRegistry.size }
//        override val elementContext: EC get() = synchronized(this@KoneLockingLinkedGCListRegistry) { this@KoneLockingLinkedGCListRegistry.elementContext }
//        override fun indexThat(predicate: (UInt, E) -> Boolean): UInt = synchronized(this@KoneLockingLinkedGCListRegistry) {
//            var node = start.nextNode
//            var index = 0u
//            while (true) {
//                when(node) {
//                    is KoneLockingLinkedGCListRegistry<E, EC>.EndStub -> break
//                    is KoneLockingLinkedGCListRegistry<E, EC>.Node -> {
//                        if (predicate(index, node.element)) break
//                        node = node.nextNode
//                        index++
//                    }
//                }
//            }
//            index
//        }
//        override fun lastIndexThat(predicate: (UInt, E) -> Boolean): UInt = synchronized(this@KoneLockingLinkedGCListRegistry) {
//            var node = end.previousNode
//            var index = this@KoneLockingLinkedGCListRegistry.size - 1u
//            while (true) {
//                when(node) {
//                    is KoneLockingLinkedGCListRegistry<E, EC>.StartStub -> break
//                    is KoneLockingLinkedGCListRegistry<E, EC>.Node -> {
//                        if (predicate(index, node.element)) break
//                        node = node.previousNode
//                        index--
//                    }
//                }
//            }
//            index
//        }
//
//        override fun get(index: UInt): E = synchronized(this@KoneLockingLinkedGCListRegistry) {
//            if (index >= size) indexException(index, size)
//            (endNodeByIndex(index) as KoneLockingLinkedGCListRegistry<E, EC>.Node).element
//        }
//
//        override fun iterator(): KoneLinearIterator<E> = ElementsIterator()
//        override fun iteratorFrom(index: UInt): KoneLinearIterator<E> = ElementsIterator(index)
//    }
//
//    internal inner class RegistrationsIterator: KoneIterator<KoneMutableRegistration<E>> {
//        var currentIndex: UInt = 0u
//        var currentNode: End<E> = start.nextNode
//        override fun hasNext(): Boolean = currentIndex < size
//        override fun getNext(): KoneMutableRegistration<E> {
//            if (!hasNext()) noElementException(currentIndex, size)
//            return currentNode as KoneLockingLinkedGCListRegistry<E, EC>.Node
//        }
//        override fun moveNext() {
//            if (!hasNext()) noElementException(currentIndex, size)
//            currentIndex++
//            currentNode = (currentNode as KoneLockingLinkedGCListRegistry<E, EC>.Node).nextNode
//        }
//    }
//
//    internal inner class Registrations: KoneIterableSet<KoneMutableRegistration<E>> {
//        override val size: UInt get() = synchronized(this@KoneLockingLinkedGCListRegistry) { this@KoneLockingLinkedGCListRegistry.size }
//        override fun contains(registration: KoneMutableRegistration<E>): Boolean = synchronized(this@KoneLockingLinkedGCListRegistry) {
//            var node = start.nextNode
//            while (true) {
//                when (node) {
//                    is KoneLockingLinkedGCListRegistry<E, EC>.EndStub -> break
//                    is KoneLockingLinkedGCListRegistry<E, EC>.Node -> {
//                        if (node == registration) return true
//                        node = node.nextNode
//                    }
//                }
//            }
//            false
//        }
//        override fun iterator(): KoneIterator<KoneMutableRegistration<E>> = RegistrationsIterator()
//    }
//}