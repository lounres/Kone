/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableCollection
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.KoneMutableIterableList
import dev.lounres.kone.collections.KoneMutableUIntArray


private /*const*/ val powersOf2: UIntArray = UIntArray(32) { 1u shl it }

public class KoneResizableLinkedList<E>: KoneMutableIterableList<E> {
    override var size: UInt = 0u
        private set
    private var dataSizeNumber = 1
    private var sizeLowerBound = 0u
    private var sizeUpperBound = 2u
    private var data = KoneMutableArray<Any?>(sizeUpperBound) { null }
    private var nextCellIndex = KoneMutableUIntArray(sizeUpperBound) { 0u }
    private var previousCellIndex = KoneMutableUIntArray(sizeUpperBound) { 0u }
    private var start = 0u
    private var end = 0u
    private fun KoneMutableArray<Any?>.dispose(size: UInt) {
        for (i in 0u ..< size) this[i] = null
    }

    override fun isEmpty(): Boolean = size == 0u
    override fun contains(element: E): Boolean = data.contains(element)
    override fun containsAll(elements: KoneIterableCollection<E>): Boolean {
        for (e in elements) if (e !in data) return false
        return true
    }
}