/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneIterableSet
import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneListWithContext
import dev.lounres.kone.collections.KoneMutableIterableListRegistry
import dev.lounres.kone.collections.KoneMutableRegistration
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.collections.noElementException
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke


public class KoneLinkedGCListRegistry<E, EC: Equality<E>>(
    public val elementContext: EC,
) : KoneMutableIterableListRegistry<E>, Disposable {
    internal sealed interface Start<E> : Disposable {
        var nextNode: End<E>
    }
    internal sealed interface End<E> : Disposable {
        var previousNode: Start<E>
    }
    internal class StartStub<E>: Start<E> {
        private var _nextNode: End<E>? = null
        override var nextNode: End<E>
            get() = _nextNode!!
            set(value) {
                _nextNode = value
            }
        override fun dispose() {
            _nextNode = null
        }
    }
    internal class EndStub<E> : End<E> {
        private var _previousNode: Start<E>? = null
        override var previousNode: Start<E>
            get() = _previousNode!!
            set(value) {
                _previousNode = value
            }
        override fun dispose() {
            _previousNode = null
        }
    }
    @Suppress("UNCHECKED_CAST")
    internal class Node<E> : Start<E>, End<E>, KoneMutableRegistration<E> {
        private var _nextNode: End<E>? = null
        override var nextNode: End<E>
            get() = _nextNode!!
            set(value) {
                _nextNode = value
            }
        private var _previousNode: Start<E>? = null
        override var previousNode: Start<E>
            get() = _previousNode!!
            set(value) {
                _previousNode = value
            }
        private var _element: E? = null
        override var element: E
            get() = _element as E
            set(value) {
                _element = value
            }
        override fun dispose() {
            _nextNode = null
            _previousNode = null
            _element = null
        }
        override fun remove() {
            previousNode.nextNode = nextNode
            nextNode.previousNode = previousNode
            dispose()
        }
    }

    private var size: UInt = 0u
    private var start: StartStub<E> = StartStub()
    private var end: EndStub<E> = EndStub()

    init {
        start.nextNode = end
        end.previousNode = start
    }

    override fun dispose() {
        var currentNode = start.nextNode
        start.dispose()
        while (true) {
            when (currentNode) {
                is EndStub -> break
                is Node -> {
                    val nodeToDispose = currentNode
                    currentNode = currentNode.nextNode
                    nodeToDispose.dispose()
                }
            }
        }
        currentNode.dispose()
    }

    private fun endNodeByIndex(index: UInt): End<E> =
        when {
            index == size -> end
            index <= (size - 1u) / 2u -> {
                var currentEndNode = start.nextNode
                for (i in 0u..<index) {
                    currentEndNode = (currentEndNode as Node<E>).nextNode
                }
                currentEndNode
            }
            else -> {
                var currentEndNode = end.previousNode
                for (i in index ..< size-1u) {
                    currentEndNode = (currentEndNode as Node<E>).previousNode
                }
                currentEndNode as Node<E>
            }
        }
    private fun justAddBefore(endNode: End<E>, element: E) {
        val previousNode = endNode.previousNode
        val newNode = Node<E>()
        newNode.element = element
        newNode.previousNode = previousNode
        newNode.nextNode = end
        previousNode.nextNode = newNode
        end.previousNode = newNode
    }

    override val elements: KoneIterableList<E> = Elements()
    override val registrations: KoneIterableSet<KoneMutableRegistration<E>> = Registrations()

    override fun register(element: E): KoneMutableRegistration<E> {
        justAddBefore(end, element)
        size++
        return end.previousNode as Node<E>
    }
    override fun find(element: E): KoneIterableList<KoneMutableRegistration<E>> {
        val accumulator = KoneGrowableArrayList<KoneMutableRegistration<E>>()
        var currentNode = start.nextNode
        while (true) {
            when(currentNode) {
                is EndStub -> break
                is Node<E> -> {
                    if (elementContext { currentNode.element eq element }) accumulator.add(currentNode)
                    currentNode = currentNode.nextNode
                }
            }
        }
        return accumulator
    }

    internal inner class ElementsIterator(var currentIndex: UInt = 0u): KoneLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        var currentNode = endNodeByIndex(currentIndex)
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) noElementException(currentIndex, size)
            return (currentNode as Node<E>).element
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
            currentNode = (currentNode as Node<E>).nextNode
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(currentIndex, size)
            return (currentNode.previousNode as Node<E>).element
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            currentIndex--
            currentNode = (currentNode.previousNode as Node<E>)
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)
    }

    internal inner class Elements : KoneIterableList<E>, KoneListWithContext<E, EC> {
        override val size: UInt get() = this@KoneLinkedGCListRegistry.size
        override val elementContext: EC get() = this@KoneLinkedGCListRegistry.elementContext
        override fun indexThat(predicate: (UInt, E) -> Boolean): UInt {
            var node = start.nextNode
            var index = 0u
            while (true) {
                when(node) {
                    is EndStub -> break
                    is Node<E> -> {
                        if (predicate(index, node.element)) break
                        node = node.nextNode
                        index++
                    }
                }
            }
            return index
        }
        override fun lastIndexThat(predicate: (UInt, E) -> Boolean): UInt {
            var node = end.previousNode
            var index = this@KoneLinkedGCListRegistry.size - 1u
            while (true) {
                when(node) {
                    is StartStub -> break
                    is Node -> {
                        if (predicate(index, node.element)) break
                        node = node.previousNode
                        index--
                    }
                }
            }
            return index
        }

        override fun get(index: UInt): E {
            if (index >= size) indexException(index, size)
            return (endNodeByIndex(index) as Node<E>).element
        }

        override fun iterator(): KoneLinearIterator<E> = ElementsIterator()
        override fun iteratorFrom(index: UInt): KoneLinearIterator<E> = ElementsIterator(index)
    }

    internal inner class RegistrationsIterator: KoneIterator<KoneMutableRegistration<E>> {
        var currentIndex: UInt = 0u
        var currentNode: End<E> = start.nextNode
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): KoneMutableRegistration<E> {
            if (!hasNext()) noElementException(currentIndex, size)
            return currentNode as Node<E>
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
            currentNode = (currentNode as Node<E>).nextNode
        }
    }

    internal inner class Registrations: KoneIterableSet<KoneMutableRegistration<E>> {
        override val size: UInt get() = this@KoneLinkedGCListRegistry.size
        override fun contains(registration: KoneMutableRegistration<E>): Boolean {
            var node = start.nextNode
            while (true) {
                when(node) {
                    is EndStub -> break
                    is Node -> {
                        if (node == registration) return true
                        node = node.nextNode
                    }
                }
            }
            return false
        }
        override fun iterator(): KoneIterator<KoneMutableRegistration<E>> = RegistrationsIterator()
    }
}