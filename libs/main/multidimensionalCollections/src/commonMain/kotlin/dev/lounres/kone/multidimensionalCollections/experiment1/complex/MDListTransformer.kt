/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.complex

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.component1
import dev.lounres.kone.collections.common.utils.component2
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.implementations.ArrayMDListTransformer
import dev.lounres.kone.multidimensionalCollections.requireShapeEquality


public interface MDListTransformer : KoneContext {
    public fun <E> mdList(shape: Shape, context: Equality<E>, initializer: (index: KoneUIntArray) -> E): MDList<E>
    public fun <E> mdList1(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): MDList1<E> =
        mdList(Shape(size), context = context) { (index) -> initializer(index) }.as1D()
    public fun <E> mdList2(rowNumber: UInt, columnNumber: UInt, context: Equality<E>, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): MDList2<E> =
        mdList(Shape(rowNumber, columnNumber), context = context) { (rowIndex, columnIndex) -> initializer(rowIndex, columnIndex) }.as2D()

    public fun <E> MDList<E>.map(context: Equality<E> = defaultEquality(), transform: (E) -> E): MDList<E> = mdList(shape, context = context) { index -> transform(get(index)) }
    public fun <E> MDList<E>.mapIndexed(context: Equality<E> = defaultEquality(), transform: (index: KoneUIntArray, E) -> E): MDList<E> = mdList(shape, context = context) { index -> transform(index, get(index)) }
    public fun <E> zip(left: MDList<E>, right: MDList<E>, context: Equality<E>, transform: (left: E, right: E) -> E): MDList<E> {
        requireShapeEquality(left.shape, right.shape)
        return mdList(left.shape, context = context) { index -> transform(left[index], right[index]) }
    }

    public fun <E> MDList<E>.all(predicate: (E) -> Boolean): Boolean = ShapeStrides(shape).asSequence().all { predicate(get(it)) }
    public fun <E> MDList<E>.allIndexed(predicate: (index: KoneUIntArray, E) -> Boolean): Boolean = ShapeStrides(shape).asSequence().all { predicate(it, get(it)) }
    public fun <E> zippingAll(left: MDList<E>, right: MDList<E>, predicate: (left: E, right: E) -> Boolean): Boolean {
        requireShapeEquality(left.shape, right.shape)
        return ShapeStrides(left.shape).asSequence().all { predicate(left[it], right[it]) }
    }
    public fun <E> zippingAllIndexed(left: MDList<E>, right: MDList<E>, predicate: (index: KoneUIntArray, left: E, right: E) -> Boolean): Boolean {
        requireShapeEquality(left.shape, right.shape)
        return ShapeStrides(left.shape).asSequence().all { predicate(it, left[it], right[it]) }
    }

    public fun <E> MDList<E>.none(predicate: (E) -> Boolean): Boolean = this.all { !predicate(it) }
    public fun <E> MDList<E>.noneIndexed(predicate: (index: KoneUIntArray, E) -> Boolean): Boolean = this.allIndexed { index, it -> !predicate(index, it) }
    public fun <E> zippingNone(left: MDList<E>, right: MDList<E>, predicate: (left: E, right: E) -> Boolean): Boolean =
        zippingAll(left, right) { leftElement, rightElement -> !predicate(leftElement, rightElement) }
    public fun <E> zippingNoneIndexed(left: MDList<E>, right: MDList<E>, predicate: (index: KoneUIntArray, left: E, right: E) -> Boolean): Boolean =
        zippingAllIndexed(left, right) { index, leftElement, rightElement -> !predicate(index, leftElement, rightElement) }

    public fun <E> MDList<E>.any(predicate: (E) -> Boolean): Boolean = !this.all { !predicate(it) }
    public fun <E> MDList<E>.anyIndexed(predicate: (index: KoneUIntArray, E) -> Boolean): Boolean = !this.allIndexed { index, it -> !predicate(index, it) }
    public fun <E> zippingAny(left: MDList<E>, right: MDList<E>, predicate: (left: E, right: E) -> Boolean): Boolean =
        !zippingAll(left, right) { leftElement, rightElement -> !predicate(leftElement, rightElement) }
    public fun <E> zippingAnyIndexed(left: MDList<E>, right: MDList<E>, predicate: (index: KoneUIntArray, left: E, right: E) -> Boolean): Boolean =
        !zippingAllIndexed(left, right) { index, leftElement, rightElement -> !predicate(index, leftElement, rightElement) }
}

public interface SettableMDListTransformer : MDListTransformer {
    override fun <E> mdList(shape: Shape, context: Equality<E>, initializer: (index: KoneUIntArray) -> E): MDList<E> =
        settableMdList(shape = shape, context = context, initializer = initializer)
    override fun <E> mdList1(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): MDList1<E> =
        settableMdList1(size = size, context = context, initializer = initializer)
    override fun <E> mdList2(rowNumber: UInt, columnNumber: UInt, context: Equality<E>, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): MDList2<E> =
        settableMdList2(rowNumber = rowNumber, columnNumber = columnNumber, context = context, initializer = initializer)

    public fun <E> settableMdList(shape: Shape, context: Equality<E>, initializer: (index: KoneUIntArray) -> E): SettableMDList<E>
    public fun <E> settableMdList1(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): SettableMDList1<E> =
        settableMdList(Shape(size), context = context) { (index) -> initializer(index) }.as1D()
    public fun <E> settableMdList2(rowNumber: UInt, columnNumber: UInt, context: Equality<E>, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): SettableMDList2<E> =
        settableMdList(Shape(rowNumber, columnNumber), context = context) { (rowIndex, columnIndex) -> initializer(rowIndex, columnIndex) }.as2D()
}

public fun MDListTransformer(): MDListTransformer = ArrayMDListTransformer
public fun SettableMDListTransformer(): SettableMDListTransformer = ArrayMDListTransformer