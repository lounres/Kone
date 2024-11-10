/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableListRegistry
import dev.lounres.kone.collections.KoneMutableRegistration
import dev.lounres.kone.collections.indexOutOfBoundsException
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.repeat


public class KoneLinkedGCListRegistry<Element, EC: Equality<Element>>(
    public val elementContext: EC,
) : KoneMutableListRegistry<Element>, Disposable {
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

    override var size: UInt = 0u
        private set
    private var start: StartStub<Element> = StartStub()
    private var end: EndStub<Element> = EndStub()

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

    private fun endNodeByIndex(index: UInt): End<Element> =
        when {
            index == size -> end
            index <= (size - 1u) / 2u -> {
                var currentEndNode = start.nextNode
                repeat(index) {
                    currentEndNode = (currentEndNode as Node<Element>).nextNode
                }
                currentEndNode
            }
            else -> {
                var currentEndNode = end.previousNode
                for (i in index ..< size-1u) {
                    currentEndNode = (currentEndNode as Node<Element>).previousNode
                }
                currentEndNode as Node<Element>
            }
        }
    private fun justAddBefore(endNode: End<Element>, element: Element) {
        val previousNode = endNode.previousNode
        val newNode = Node<Element>()
        newNode.element = element
        newNode.previousNode = previousNode
        newNode.nextNode = end
        previousNode.nextNode = newNode
        end.previousNode = newNode
    }

    override val elementsView: KoneList<Element> = Elements()
    override val registrationsView: KoneIterable<KoneMutableRegistration<Element>> = Registrations()

    override fun register(element: Element): KoneMutableRegistration<Element> {
        justAddBefore(end, element)
        size++
        return end.previousNode as Node<Element>
    }
//    override fun find(element: E): KoneIterableList<KoneMutableRegistration<E>> {
//        val accumulator = KoneGrowableArrayList<KoneMutableRegistration<E>>()
//        var currentNode = start.nextNode
//        while (true) {
//            when(currentNode) {
//                is EndStub -> break
//                is Node<E> -> {
//                    if (elementContext { currentNode.element eq element }) accumulator.add(currentNode)
//                    currentNode = currentNode.nextNode
//                }
//            }
//        }
//        return accumulator
//    }

    internal inner class ElementsIterator(var currentIndex: UInt = 0u): KoneLinearIterator<Element> {
        init {
            if (currentIndex > size) indexOutOfBoundsException(currentIndex, size)
        }
        var currentNode = endNodeByIndex(currentIndex)
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): Element {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            return (currentNode as Node<Element>).element
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            currentIndex++
            currentNode = (currentNode as Node<Element>).nextNode
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexOutOfBoundsException(currentIndex, size)

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            return (currentNode.previousNode as Node<Element>).element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, size)
            currentIndex--
            currentNode = (currentNode.previousNode as Node<Element>)
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexOutOfBoundsException(currentIndex, size)
    }

    internal inner class Elements : KoneList<Element> {
        override val size: UInt get() = this@KoneLinkedGCListRegistry.size

        override fun get(index: UInt): Element {
            if (index >= size) indexOutOfBoundsException(index, size)
            return (endNodeByIndex(index) as Node<Element>).element
        }

        override fun iterator(): KoneLinearIterator<Element> = ElementsIterator()
        override fun iteratorFrom(index: UInt): KoneLinearIterator<Element> = ElementsIterator(index)
    }

    internal inner class RegistrationsIterator: KoneLinearIterator<Node<Element>> {
        var currentIndex: UInt = 0u
        var currentNode: End<Element> = start.nextNode
        
        override fun hasNext(): Boolean = currentIndex < size
        override fun nextIndex(): UInt {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            return currentIndex
        }
        override fun getNext(): Node<Element> {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            return currentNode as Node<Element>
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, size)
            currentIndex++
            currentNode = (currentNode as Node<Element>).nextNode
        }
        
        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun previousIndex(): UInt {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex - 1u, size)
            return currentIndex - 1u
        }
        override fun getPrevious(): Node<Element> {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex - 1u, size)
            return currentNode.previousNode as Node<Element>
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex - 1u, size)
            currentIndex--
            currentNode = currentNode.previousNode as Node<Element>
        }
    }

    internal inner class Registrations: KoneIterable<Node<Element>> {
        override val size: UInt get() = this@KoneLinkedGCListRegistry.size
        override fun iterator(): KoneLinearIterator<Node<Element>> = RegistrationsIterator()
    }
}