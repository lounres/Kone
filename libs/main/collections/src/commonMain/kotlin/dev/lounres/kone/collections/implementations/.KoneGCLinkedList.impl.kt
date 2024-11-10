/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations


////@Serializable(with = KoneLinkedGCListWithContextSerializer::class)
//public class KoneLinkedGCList<Element> internal constructor() : KoneMutableList<Element>, Disposable {
//    internal sealed interface Start<Element> : Disposable {
//        var nextNode: End<Element>
//    }
//    internal sealed interface End<Element> : Disposable {
//        var previousNode: Start<Element>
//    }
//    internal class StartStub<E>: Start<E> {
//        private var _nextNode: End<E>? = null
//        override var nextNode: End<E>
//            get() = _nextNode!!
//            set(value) {
//                _nextNode = value
//            }
//        override fun dispose() {
//            _nextNode = null
//        }
//    }
//    internal class EndStub<E> : End<E> {
//        private var _previousNode: Start<E>? = null
//        override var previousNode: Start<E>
//            get() = _previousNode!!
//            set(value) {
//                _previousNode = value
//            }
//        override fun dispose() {
//            _previousNode = null
//        }
//    }
//    @Suppress("UNCHECKED_CAST")
//    internal class Node<E> : Start<E>, End<E> {
//        private var _nextNode: End<E>? = null
//        override var nextNode: End<E>
//            get() = _nextNode!!
//            set(value) {
//                _nextNode = value
//            }
//        private var _previousNode: Start<E>? = null
//        override var previousNode: Start<E>
//            get() = _previousNode!!
//            set(value) {
//                _previousNode = value
//            }
//        private var _element: E? = null
//        var element: E
//            get() = _element as E
//            set(element) {
//                _element = element
//            }
//        override fun dispose() {
//            _nextNode = null
//            _previousNode = null
//            _element = null
//        }
//    }
//
//    override var size: UInt = 0u
//        private set
//    private var start: StartStub<Element> = StartStub()
//    private var end: EndStub<Element> = EndStub()
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
//                is EndStub -> break
//                is Node -> {
//                    val nodeToDispose = currentNode
//                    currentNode = currentNode.nextNode
//                    nodeToDispose.dispose()
//                }
//            }
//        }
//        currentNode.dispose()
//    }
//
//    private fun endNodeByIndex(index: UInt): End<Element> =
//        when {
//            index == size -> end
//            index <= (size - 1u) / 2u -> {
//                var currentEndNode = start.nextNode
//                repeat(index) {
//                    currentEndNode = (currentEndNode as Node<Element>).nextNode
//                }
//                currentEndNode
//            }
//            else -> {
//                var currentEndNode = end.previousNode
//                for (i in index ..< size-1u) {
//                    currentEndNode = (currentEndNode as Node<Element>).previousNode
//                }
//                currentEndNode as Node<Element>
//            }
//        }
//    private fun justAddBefore(endNode: End<Element>, element: Element) {
//        val previousNode = endNode.previousNode
//        val newNode = Node<Element>()
//        newNode.element = element
//        newNode.previousNode = previousNode
//        newNode.nextNode = end
//        previousNode.nextNode = newNode
//        end.previousNode = newNode
//    }
//    private fun Node<Element>.remove() {
//        previousNode.nextNode = nextNode
//        nextNode.previousNode = previousNode
//        dispose()
//    }
//
//    override fun get(index: UInt): Element {
//        if (index >= size) indexException(index, size)
//        return (endNodeByIndex(index) as Node<Element>).element
//    }
//
//    override fun set(index: UInt, element: Element) {
//        if (index >= size) indexException(index, size)
//        (endNodeByIndex(index) as Node<Element>).element = element
//    }
//
//    override fun removeAll() {
//        var currentNode = start.nextNode
//        while (true) {
//            when (currentNode) {
//                is EndStub -> break
//                is Node -> {
//                    val nodeToDispose = currentNode
//                    currentNode = currentNode.nextNode
//                    nodeToDispose.dispose()
//                }
//            }
//        }
//        start.nextNode = end
//        end.previousNode = start
//        size = 0u
//    }
//
//    override fun add(element: Element) {
//        justAddBefore(end, element)
//        size++
//    }
//
//    override fun addAt(index: UInt, element: Element) {
//        if (index > size) indexException(index, size)
//        justAddBefore(endNodeByIndex(index), element)
//    }
//
//    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
//        if (number == 0u) return
//
//        var currentNode = end.previousNode
//        repeat(number) {
//            val previousNode = currentNode
//            val newNode = Node<Element>()
//            currentNode = newNode
//            newNode.previousNode = previousNode
//            previousNode.nextNode = newNode
//            newNode.element = builder(it)
//        }
//        end.previousNode = currentNode
//        currentNode.nextNode = end
//    }
//
//    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> Element) {
//        if (index > size) indexException(index, size)
//        if (number == 0u) return
//
//        val endNode = endNodeByIndex(index)
//        var currentNode = endNode.previousNode
//        for (localIndex in 0u ..< number) {
//            val previousNode = currentNode
//            val newNode = Node<Element>()
//            currentNode = newNode
//            newNode.previousNode = previousNode
//            previousNode.nextNode = newNode
//            newNode.element = builder(localIndex)
//        }
//        endNode.previousNode = currentNode
//        currentNode.nextNode = endNode
//    }
//    override fun removeAt(index: UInt) {
//        if (index >= size) indexException(index, size)
//        (endNodeByIndex(index) as Node<Element>).remove()
//    }
//
//    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
//        var currentNode = start.nextNode
//        var index = 0u
//        while (true) {
//            when (currentNode) {
//                is EndStub -> return
//                is Node -> {
//                    val node = currentNode
//                    currentNode = node.nextNode
//                    if (predicate(index, node.element)) node.remove()
//                    index++
//                }
//            }
//        }
//    }
//
//    override fun iterator(): KoneMutableLinearIterator<Element> = Iterator()
//    override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> = Iterator(index)
//
//    override fun toString(): String = buildString {
//        append('[')
//        var currentNode = start.nextNode
//        if (size > 0u) {
//            val node = (currentNode as Node<Element>)
//            append(node.element)
//            currentNode = node.nextNode
//        }
//        while (true) {
//            when (currentNode) {
//                is EndStub -> break
//                is Node -> {
//                    append(", ")
//                    append(currentNode.element)
//                    currentNode = currentNode.nextNode
//                }
//            }
//        }
//        append(']')
//    }
//    override fun hashCode(): Int {
//        var hashCode = 1
//        var currentNode = start.nextNode
//        while (true) {
//            when (currentNode) {
//                is EndStub -> break
//                is Node -> {
//                    hashCode = 31 * hashCode + currentNode.element.hashCode()
//                    currentNode = currentNode.nextNode
//                }
//            }
//        }
//        return hashCode
//    }
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is KoneList<*>) return false
//        if (this.size != other.size) return false
//
//        when (other) {
//            is KoneLinkedGCList<*> -> {
//                var thisCurrentNode = this.start.nextNode
//                var otherCurrentNode = other.start.nextNode
//                repeat(size) {
//                    thisCurrentNode as Node<Element>
//                    otherCurrentNode as Node<*>
//                    if (thisCurrentNode.element != otherCurrentNode.element) return false
//                    thisCurrentNode = thisCurrentNode.nextNode
//                    otherCurrentNode = otherCurrentNode.nextNode
//                }
//            }
//            else -> {
//                var thisCurrentNode = this.start.nextNode
//                val otherIterator = other.iterator()
//                repeat(size) {
//                    thisCurrentNode as Node<Element>
//                    if (thisCurrentNode.element != otherIterator.getAndMoveNext()) return false
//                    thisCurrentNode = thisCurrentNode.nextNode
//                }
//            }
//        }
//
//        return true
//    }
//
//    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<Element> {
//        init {
//            if (currentIndex > size) indexException(currentIndex, size)
//        }
//        var currentNode = endNodeByIndex(currentIndex)
//        override fun hasNext(): Boolean = currentIndex < size
//        override fun getNext(): Element {
//            if (!hasNext()) indexException(currentIndex, size)
//            return (currentNode as Node<Element>).element
//        }
//        override fun moveNext() {
//            if (!hasNext()) indexException(currentIndex, size)
//            currentIndex++
//            currentNode = (currentNode as Node<Element>).nextNode
//        }
//        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexException(currentIndex, size)
//        override fun setNext(element: Element) {
//            if (!hasNext()) indexException(currentIndex, size)
//            (currentNode as Node<Element>).element = element
//        }
//        override fun addNext(element: Element) {
//            justAddBefore(currentNode, element)
//            size++
//            currentNode = (currentNode.previousNode as Node<Element>)
//        }
//        override fun removeNext() {
//            if (!hasNext()) indexException(currentIndex, size)
//            val node = currentNode as Node<Element>
//            currentNode = node.nextNode
//            node.remove()
//            size--
//        }
//
//        override fun hasPrevious(): Boolean = currentIndex > 0u
//        override fun getPrevious(): Element {
//            if (!hasPrevious()) indexException(currentIndex, size)
//            return (currentNode.previousNode as Node<Element>).element
//        }
//        override fun movePrevious() {
//            if (!hasPrevious()) indexException(currentIndex, size)
//            currentIndex--
//            currentNode = (currentNode.previousNode as Node<Element>)
//        }
//        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexException(currentIndex, size)
//        override fun setPrevious(element: Element) {
//            if (!hasPrevious()) indexException(currentIndex, size)
//            (currentNode.previousNode as Node<Element>).element = element
//        }
//        override fun addPrevious(element: Element) {
//            justAddBefore(currentNode, element)
//            size++
//        }
//        override fun removePrevious() {
//            if (!hasPrevious()) indexException(currentIndex, size)
//            (currentNode.previousNode as Node<Element>).remove()
//            size--
//        }
//    }
//}