/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections.implementations

import com.lounres.kone.collections.KoneIterableCollection
import com.lounres.kone.collections.KoneMutableIterableList
import com.lounres.kone.collections.KoneMutableLinearIterator
import com.lounres.kone.collections.wrappers.asKone
import com.lounres.kone.collections.wrappers.asKotlinStdlib


//// TODO: Доделать
//@OptIn(ExperimentalUnsignedTypes::class)
//private val powersOf2: UIntArray = UIntArray(34) { if (it == 0) 0u else 1u shl (it - 1) }
//
//@Suppress("UNCHECKED_CAST")
//public class KoneArrayList<E> : KoneMutableIterableList<E> {
//    private var _size = 0u
//    private var dataSizeNumber = 1
//    private var dataSize = 1u
//    private var sizeLowerBound = 0u
//    private var sizeUpperBound = 2u
//    private var data = Array<Any?>(dataSize.toInt()) { null }
//
//    override val size: UInt get() = _size
//
//    override fun isEmpty(): Boolean = _size == 0u
//    override fun contains(element: E): Boolean = data.contains(element)
//    override fun containsAll(elements: KoneIterableCollection<E>): Boolean {
//        // TODO: Replace with specialized `all`
//        for (e in elements) if (!data.contains(e)) return false
//        return true
//    }
//
//    override fun get(index: UInt): E = data.get(index.toInt()) as E
//    override fun indexOf(element: E): UInt = data.indexOf(element).toUInt()
//    override fun lastIndexOf(element: E): UInt = data.lastIndexOf(element).toUInt()
//
//    override fun clear() {
//        _size = 0u
//        dataSizeNumber = 1
//        sizeLowerBound = 0u
//        dataSize = 1u
//        sizeUpperBound = 2u
//        data = Array(dataSize.toInt()) { null }
//    }
//    @OptIn(ExperimentalUnsignedTypes::class)
//    override fun add(element: E) {
//        if (_size == sizeUpperBound) {
//            dataSizeNumber++
//            dataSize = powersOf2[dataSizeNumber]
//            sizeLowerBound = powersOf2[dataSizeNumber-1]
//            sizeUpperBound = powersOf2[dataSizeNumber+1]
//            data = Array(dataSize.toInt()) {
//                val size = _size.toInt()
//                when {
//                    it < size -> data[it]
//                    it == size -> element
//                    else -> null
//                }
//            }
//            _size++
//        } else {
//            data[_size.toInt()] = element
//            _size++
//        }
//    }
//    @OptIn(ExperimentalUnsignedTypes::class)
//    override fun add(index: UInt, element: E) {
//        val index = index.toInt()
//        if (_size == sizeUpperBound) {
//            dataSizeNumber++
//            dataSize = powersOf2[dataSizeNumber]
//            sizeLowerBound = powersOf2[dataSizeNumber-1]
//            sizeUpperBound = powersOf2[dataSizeNumber+1]
//            data = Array(dataSize.toInt()) {
//                val size = _size.toInt()
//                when {
//                    it < index -> data[it]
//                    it == index -> element
//                    it <= size -> data[it-1]
//                    else -> null
//                }
//            }
//            _size++
//        } else {
//            val size = _size.toInt()
//            for (i in size downTo index + 1) data[i] = data[i-1]
//            data[index] = element
//            _size++
//        }
//    }
//    @OptIn(ExperimentalUnsignedTypes::class)
//    override fun addAll(elements: KoneIterableCollection<E>) {
//        if (_size + elements.size > sizeUpperBound) {
//            while (_size + elements.size > sizeUpperBound) {
//                dataSizeNumber++
//                sizeUpperBound = powersOf2[dataSizeNumber+1]
//            }
//            dataSize = powersOf2[dataSizeNumber]
//            sizeLowerBound = powersOf2[dataSizeNumber-1]
//            data = Array(dataSize.toInt()) {
//                val size = _size.toInt()
//                val newSize = size + elements.size.toInt()
//                val iter = elements.iterator()
//                when {
//                    it < size -> data[it]
//                    iter.hasNext() -> iter.next()
//                    else -> null
//                }
//            }
//            _size += elements.size
//        } else {
//            var index = _size.toInt()
//            val iter = elements.iterator()
//            while (iter.hasNext()) {
//                data[index] = iter.next()
//                index++
//            }
//        }
//    }
//    override fun addAll(index: UInt, elements: KoneIterableCollection<E>) { list.addAll(index.toInt(), elements.asKotlinStdlib()) }
//    override fun set(index: UInt, element: E) { list.set(index.toInt(), element)}
//    override fun remove(element: E) { list.remove(element) }
//    override fun removeAt(index: UInt) { list.removeAt(index.toInt()) }
//    override fun removeAll(elements: KoneIterableCollection<E>) { list.removeAll(elements.asKotlinStdlib()) }
//    override fun retainAll(elements: KoneIterableCollection<E>) { list.retainAll(elements.asKotlinStdlib()) }
//
//    override fun iterator(): KoneMutableLinearIterator<E> = list.listIterator().asKone()
//}