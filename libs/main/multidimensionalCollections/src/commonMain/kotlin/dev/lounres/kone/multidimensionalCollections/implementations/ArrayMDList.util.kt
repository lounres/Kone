/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.implementations

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.producers.MDList1Producer
import dev.lounres.kone.multidimensionalCollections.producers.MDList2Producer
import dev.lounres.kone.multidimensionalCollections.producers.MDListProducer


public inline fun <E> ArrayMDList(
    size: MDSize,
    offsetting: MDSizeOffsetting = MDSizeStrides(size),
    initializer: (MDIndex) -> E,
): ArrayMDList<E> {
    val data = KoneMutableArray<Any?>(offsetting.size) { null }

    var offset = 0u
    for (index in offsetting) data[offset++] = initializer(index)

    return ArrayMDList(
        size = size,
        offsetting = offsetting,
        data = data,
    )
}

public object ArrayMDListProducer : MDListProducer {
    override fun <Element> produceBy(
        size: MDSize,
        offsetting: MDSizeOffsetting,
        initializer: (MDIndex) -> Element
    ): MDList<Element> = ArrayMDList(size, offsetting, initializer)
}

public inline fun <E> ArrayMDList1(
    contentSize: UInt,
    initializer: (index: UInt) -> E,
): ArrayMDList1<E> {
    val data = KoneMutableArray<Any?>(contentSize) { null }

    for (index in  0u ..< contentSize) data[index] = initializer(index)

    return ArrayMDList1(
        contentSize = contentSize,
        data = data,
    )
}

public object ArrayMDList1Producer : MDList1Producer {
    override fun <Element> produceBy(size: UInt, initializer: (UInt) -> Element): MDList1<Element> =
        ArrayMDList1(size, initializer)
}

public inline fun <E> ArrayMDList2(
    rowNumber: UInt,
    columnNumber: UInt,
    initializer: (rowIndex: UInt, columnIndex: UInt) -> E,
): ArrayMDList2<E> {
    val data = KoneMutableArray<Any?>(rowNumber * columnNumber) { null }

    var row = 0u
    var column = 0u
    var index = 0u
    while (true) {
        data[index] = initializer(row, column)
        when {
            column < columnNumber - 1u -> {
                column++
                index++
            }
            row < rowNumber - 1u -> {
                column = 0u
                row++
                index++
            }
            else -> break
        }
    }

    return ArrayMDList2(
        rowNumber = rowNumber,
        columnNumber = columnNumber,
        data = data,
    )
}

public object ArrayMDList2Producer : MDList2Producer {
    override fun <Element> produceBy(
        rowNumber: UInt,
        columnNumber: UInt,
        initializer: (UInt, UInt) -> Element
    ): MDList2<Element> =
        ArrayMDList2(rowNumber, columnNumber, initializer)
}