/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.common

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.component1
import dev.lounres.kone.collections.common.utils.component2
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.experiment1.common.implementations.ArrayMDListTransformer
import dev.lounres.kone.multidimensionalCollections.requireShapeEquality


public interface MDListTransformer<E>: KoneContext {
    public fun mdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): MDList<E>
    public fun mdList1(size: UInt, initializer: (index: UInt) -> E): MDList1<E> =
        mdList(Shape(size)) { (index) -> initializer(index) }.as1D()
    public fun mdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): MDList2<E> =
        mdList(Shape(rowNumber, columnNumber)) { (rowIndex, columnIndex) -> initializer(rowIndex, columnIndex) }.as2D()

    public fun MDList<E>.map(transform: (E) -> E): MDList<E> = mdList(shape) { index -> transform(get(index)) }
    public fun MDList<E>.mapIndexed(transform: (index: KoneUIntArray, E) -> E): MDList<E> = mdList(shape) { index -> transform(index, get(index)) }
    public fun zip(left: MDList<E>, right: MDList<E>, transform: (left: E, right: E) -> E): MDList<E> {
        requireShapeEquality(left.shape, right.shape)
        return mdList(left.shape) { index -> transform(left[index], right[index]) }
    }

    public fun MDList<E>.all(predicate: (E) -> Boolean): Boolean = ShapeStrides(shape).asSequence().all { predicate(get(it)) }
    public fun MDList<E>.allIndexed(predicate: (index: KoneUIntArray, E) -> Boolean): Boolean = ShapeStrides(shape).asSequence().all { predicate(it, get(it)) }
    public fun zippingAll(left: MDList<E>, right: MDList<E>, predicate: (left: E, right: E) -> Boolean): Boolean {
        requireShapeEquality(left.shape, right.shape)
        return ShapeStrides(left.shape).asSequence().all { predicate(left[it], right[it]) }
    }
    public fun zippingAllIndexed(left: MDList<E>, right: MDList<E>, predicate: (index: KoneUIntArray, left: E, right: E) -> Boolean): Boolean {
        requireShapeEquality(left.shape, right.shape)
        return ShapeStrides(left.shape).asSequence().all { predicate(it, left[it], right[it]) }
    }

    public fun MDList<E>.none(predicate: (E) -> Boolean): Boolean = this.all { !predicate(it) }
    public fun MDList<E>.noneIndexed(predicate: (index: KoneUIntArray, E) -> Boolean): Boolean = this.allIndexed { index, it -> !predicate(index, it) }
    public fun zippingNone(left: MDList<E>, right: MDList<E>, predicate: (left: E, right: E) -> Boolean): Boolean =
        zippingAll(left, right) { leftElement, rightElement -> !predicate(leftElement, rightElement) }
    public fun zippingNoneIndexed(left: MDList<E>, right: MDList<E>, predicate: (index: KoneUIntArray, left: E, right: E) -> Boolean): Boolean =
        zippingAllIndexed(left, right) { index, leftElement, rightElement -> !predicate(index, leftElement, rightElement) }

    public fun MDList<E>.any(predicate: (E) -> Boolean): Boolean = !this.all { !predicate(it) }
    public fun MDList<E>.anyIndexed(predicate: (index: KoneUIntArray, E) -> Boolean): Boolean = !this.allIndexed { index, it -> !predicate(index, it) }
    public fun zippingAny(left: MDList<E>, right: MDList<E>, predicate: (left: E, right: E) -> Boolean): Boolean =
        !zippingAll(left, right) { leftElement, rightElement -> !predicate(leftElement, rightElement) }
    public fun zippingAnyIndexed(left: MDList<E>, right: MDList<E>, predicate: (index: KoneUIntArray, left: E, right: E) -> Boolean): Boolean =
        !zippingAllIndexed(left, right) { index, leftElement, rightElement -> !predicate(index, leftElement, rightElement) }
}

public interface SettableMDListTransformer<E> : MDListTransformer<E> {
    override fun mdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): MDList<E> =
        settableMdList(shape = shape, initializer = initializer)
    override fun mdList1(size: UInt, initializer: (index: UInt) -> E): MDList1<E> =
        settableMdList1(size = size, initializer = initializer)
    override fun mdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): MDList2<E> =
        settableMdList2(rowNumber = rowNumber, columnNumber = columnNumber, initializer = initializer)

    public fun settableMdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): SettableMDList<E>
    public fun settableMdList1(size: UInt, initializer: (index: UInt) -> E): SettableMDList1<E> =
        settableMdList(Shape(size)) { (index) -> initializer(index) }.as1D()
    public fun settableMdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): SettableMDList2<E> =
        settableMdList(Shape(rowNumber, columnNumber)) { (rowIndex, columnIndex) -> initializer(rowIndex, columnIndex) }.as2D()
}

public fun <E> MDListTransformer(): MDListTransformer<E> = ArrayMDListTransformer()
public fun <E> SettableMDListTransformer(): SettableMDListTransformer<E> = ArrayMDListTransformer()