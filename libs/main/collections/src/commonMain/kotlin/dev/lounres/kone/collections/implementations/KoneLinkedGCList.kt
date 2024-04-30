/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.scope


@Suppress("UNCHECKED_CAST")
public class KoneLinkedGCList<E, EC: Equality<E>> internal constructor(
    override val elementContext: EC,
) : KoneMutableIterableList<E>, KoneListWithContext<E, EC>, Disposable {
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
    internal class Node<E> : Start<E>, End<E> {
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
        private var _value: E? = null
        var value: E
            get() = _value as E
            set(value) {
                _value = value
            }
        override fun dispose() {
            _nextNode = null
            _previousNode = null
            _value = null
        }
    }

    override var size: UInt = 0u
        private set
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
        newNode.value = element
        newNode.previousNode = previousNode
        newNode.nextNode = end
        previousNode.nextNode = newNode
        end.previousNode = newNode
    }
    private fun Node<E>.remove() {
        previousNode.nextNode = nextNode
        nextNode.previousNode = previousNode
        dispose()
    }

    override fun get(index: UInt): E {
        if (index >= size) indexException(index, size)
        return (endNodeByIndex(index) as Node<E>).value
    }

    override fun set(index: UInt, element: E) {
        if (index >= size) indexException(index, size)
        (endNodeByIndex(index) as Node<E>).value = element
    }

    override fun removeAll() {
        var currentNode = start.nextNode
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
        start.nextNode = end
        end.previousNode = start
        size = 0u
    }

    override fun add(element: E) {
        justAddBefore(end, element)
        size++
    }

    override fun addAt(index: UInt, element: E) {
        if (index > size) indexException(index, size)
        justAddBefore(endNodeByIndex(index), element)
    }

    override fun addSeveral(number: UInt, builder: (UInt) -> E) {
        if (number == 0u) return

        var currentNode = end.previousNode
        for (localIndex in 0u ..< number) {
            val previousNode = currentNode
            val newNode = Node<E>()
            currentNode = newNode
            newNode.previousNode = previousNode
            previousNode.nextNode = newNode
            newNode.value = builder(localIndex)
        }
        end.previousNode = currentNode
        currentNode.nextNode = end
    }
    override fun addAllFrom(elements: KoneIterableCollection<E>) {
        if (elements.size == 0u) return

        var currentNode = end.previousNode
        for (element in elements) {
            val previousNode = currentNode
            val newNode = Node<E>()
            currentNode = newNode
            newNode.previousNode = previousNode
            previousNode.nextNode = newNode
            newNode.value = element
        }
        end.previousNode = currentNode
        currentNode.nextNode = end
    }

    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> E) {
        if (index > size) indexException(index, size)
        if (number == 0u) return

        val endNode = endNodeByIndex(index)
        var currentNode = endNode.previousNode
        for (localIndex in 0u ..< number) {
            val previousNode = currentNode
            val newNode = Node<E>()
            currentNode = newNode
            newNode.previousNode = previousNode
            previousNode.nextNode = newNode
            newNode.value = builder(localIndex)
        }
        endNode.previousNode = currentNode
        currentNode.nextNode = endNode
    }
    override fun addAllFromAt(index: UInt, elements: KoneIterableCollection<E>) {
        if (index > size) indexException(index, size)
        if (elements.size == 0u) return

        val endNode = endNodeByIndex(index)
        var currentNode = endNode.previousNode
        for (element in elements) {
            val previousNode = currentNode
            val newNode = Node<E>()
            currentNode = newNode
            newNode.previousNode = previousNode
            previousNode.nextNode = newNode
            newNode.value = element
        }
        endNode.previousNode = currentNode
        currentNode.nextNode = endNode
    }
    override fun remove(element: E) {
        val targetNode: Node<E>
        scope {
            var currentNode = start.nextNode
            while (true) {
                when (currentNode) {
                    is EndStub -> return
                    is Node -> {
                        // FIXME: KT-32313; wait until `.invoke` will get lambda contract
                        if (elementContext { currentNode.value eq element }) {
                            targetNode = currentNode
                            break
                        }
                        currentNode = currentNode.nextNode
                    }
                }
            }
        }
        targetNode.remove()
    }
    override fun removeAt(index: UInt) {
        if (index >= size) indexException(index, size)
        (endNodeByIndex(index) as Node<E>).remove()
    }

    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) {
        var currentNode = start.nextNode
        var index = 0u
        while (true) {
            when (currentNode) {
                is EndStub -> return
                is Node -> {
                    val node = currentNode
                    currentNode = node.nextNode
                    if (predicate(index, node.value)) node.remove()
                    index++
                }
            }
        }
    }

    override fun iterator(): KoneMutableLinearIterator<E> = Iterator()
    override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> = Iterator(index)

    override fun toString(): String = buildString {
        append('[')
        var currentNode = start.nextNode
        if (size > 0u) {
            val node = (currentNode as Node<E>)
            append(node.value)
            currentNode = node.nextNode
        }
        while (true) {
            when (currentNode) {
                is EndStub -> break
                is Node -> {
                    append(", ")
                    append(currentNode.value)
                    currentNode = currentNode.nextNode
                }
            }
        }
        append(']')
    }
    override fun hashCode(): Int {
        var hashCode = 1
        var currentNode = start.nextNode
        while (true) {
            when (currentNode) {
                is EndStub -> break
                is Node -> {
                    hashCode = 31 * hashCode + currentNode.value.hashCode()
                    currentNode = currentNode.nextNode
                }
            }
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        when (other) {
            is KoneLinkedGCList<*, *> -> {
                var thisCurrentNode = this.start.nextNode
                var otherCurrentNode = other.start.nextNode
                for (i in 0u..<size) {
                    thisCurrentNode as Node<E>
                    otherCurrentNode as Node<E>
                    if (thisCurrentNode.value != otherCurrentNode.value) return false
                    thisCurrentNode = thisCurrentNode.nextNode
                    otherCurrentNode = otherCurrentNode.nextNode
                }
            }
            is KoneIterableList<*> -> {
                var thisCurrentNode = this.start.nextNode
                val otherIterator = other.iterator()
                for (i in 0u..<size) {
                    thisCurrentNode as Node<E>
                    if (thisCurrentNode.value != otherIterator.getAndMoveNext()) return false
                    thisCurrentNode = thisCurrentNode.nextNode
                }
            }
            else -> {
                var thisCurrentNode = this.start.nextNode
                for (i in 0u..<size) {
                    thisCurrentNode as Node<E>
                    if (thisCurrentNode.value != other[i]) return false
                    thisCurrentNode = thisCurrentNode.nextNode
                }
            }
        }

        return true
    }

    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<E> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        var currentNode = endNodeByIndex(currentIndex)
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): E {
            if (!hasNext()) noElementException(currentIndex, size)
            return (currentNode as Node<E>).value
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            currentIndex++
            currentNode = (currentNode as Node<E>).nextNode
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noElementException(currentIndex, size)
        override fun setNext(element: E) {
            if (!hasNext()) noElementException(currentIndex, size)
            (currentNode as Node<E>).value = element
        }
        override fun addNext(element: E) {
            justAddBefore(currentNode, element)
            size++
            currentNode = (currentNode.previousNode as Node<E>)
        }
        override fun removeNext() {
            if (!hasNext()) noElementException(currentIndex, size)
            val node = currentNode as Node<E>
            currentNode = node.nextNode
            node.remove()
            size--
        }

        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(currentIndex, size)
            return (currentNode.previousNode as Node<E>).value
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            currentIndex--
            currentNode = (currentNode.previousNode as Node<E>)
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noElementException(currentIndex, size)
        override fun setPrevious(element: E) {
            if (!hasPrevious()) noElementException(currentIndex, size)
            (currentNode.previousNode as Node<E>).value = element
        }
        override fun addPrevious(element: E) {
            justAddBefore(currentNode, element)
            size++
        }
        override fun removePrevious() {
            if (!hasPrevious()) noElementException(currentIndex, size)
            (currentNode.previousNode as Node<E>).remove()
            size--
        }
    }
}