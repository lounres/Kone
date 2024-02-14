/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.standard.aliases.UIntArray
import dev.lounres.kone.collections.standard.utils.component1
import dev.lounres.kone.collections.standard.utils.component2
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.experiment1.implementations.ArrayMDListTransformer
import dev.lounres.kone.multidimensionalCollections.requireShapeEquality


public interface MDListTransformer: KoneContext {
    public fun <E> mdList(shape: Shape, initializer: (index: UIntArray) -> E): MDList<E>
    public fun <E> mdList1(size: UInt, initializer: (index: UInt) -> E): MDList1<E> =
        mdList(Shape(size)) { (index) -> initializer(index) }.as1D()
    public fun <E> mdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): MDList2<E> =
        mdList(Shape(rowNumber, columnNumber)) { (rowIndex, columnIndex) -> initializer(rowIndex, columnIndex) }.as2D()

    public fun <E> MDList<E>.map(transform: (E) -> E): MDList<E> = mdList(shape) { index -> transform(get(index)) }
    public fun <E> MDList<E>.mapIndexed(transform: (index: UIntArray, E) -> E): MDList<E> = mdList(shape) { index -> transform(index, get(index)) }
    public fun <E> zip(left: MDList<E>, right: MDList<E>, transform: (left: E, right: E) -> E): MDList<E> {
        requireShapeEquality(left.shape, right.shape)
        return mdList(left.shape) { index -> transform(left[index], right[index]) }
    }

    public fun <E> MDList<E>.all(predicate: (E) -> Boolean): Boolean = ShapeStrides(shape).asSequence().all { predicate(get(it)) }
    public fun <E> MDList<E>.allIndexed(predicate: (index: UIntArray, E) -> Boolean): Boolean = ShapeStrides(shape).asSequence().all { predicate(it, get(it)) }
    public fun <E> zippingAll(left: MDList<E>, right: MDList<E>, predicate: (left: E, right: E) -> Boolean): Boolean {
        requireShapeEquality(left.shape, right.shape)
        return ShapeStrides(left.shape).asSequence().all { predicate(left[it], right[it]) }
    }
    public fun <E> zippingAllIndexed(left: MDList<E>, right: MDList<E>, predicate: (index: UIntArray, left: E, right: E) -> Boolean): Boolean {
        requireShapeEquality(left.shape, right.shape)
        return ShapeStrides(left.shape).asSequence().all { predicate(it, left[it], right[it]) }
    }

    public fun <E> MDList<E>.none(predicate: (E) -> Boolean): Boolean = this.all { !predicate(it) }
    public fun <E> MDList<E>.noneIndexed(predicate: (index: UIntArray, E) -> Boolean): Boolean = this.allIndexed { index, it -> !predicate(index, it) }
    public fun <E> zippingNone(left: MDList<E>, right: MDList<E>, predicate: (left: E, right: E) -> Boolean): Boolean =
        zippingAll(left, right) { leftElement, rightElement -> !predicate(leftElement, rightElement) }
    public fun <E> zippingNoneIndexed(left: MDList<E>, right: MDList<E>, predicate: (index: UIntArray, left: E, right: E) -> Boolean): Boolean =
        zippingAllIndexed(left, right) { index, leftElement, rightElement -> !predicate(index, leftElement, rightElement) }

    public fun <E> MDList<E>.any(predicate: (E) -> Boolean): Boolean = !this.all { !predicate(it) }
    public fun <E> MDList<E>.anyIndexed(predicate: (index: UIntArray, E) -> Boolean): Boolean = !this.allIndexed { index, it -> !predicate(index, it) }
    public fun <E> zippingAny(left: MDList<E>, right: MDList<E>, predicate: (left: E, right: E) -> Boolean): Boolean =
        !zippingAll(left, right) { leftElement, rightElement -> !predicate(leftElement, rightElement) }
    public fun <E> zippingAnyIndexed(left: MDList<E>, right: MDList<E>, predicate: (index: UIntArray, left: E, right: E) -> Boolean): Boolean =
        !zippingAllIndexed(left, right) { index, leftElement, rightElement -> !predicate(index, leftElement, rightElement) }
}

public interface SettableMDListTransformer: MDListTransformer {
    override fun <E> mdList(shape: Shape, initializer: (index: UIntArray) -> E): MDList<E> =
        settableMdList(shape = shape, initializer = initializer)
    override fun <E> mdList1(size: UInt, initializer: (index: UInt) -> E): MDList1<E> =
        settableMdList1(size = size, initializer = initializer)
    override fun <E> mdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): MDList2<E> =
        settableMdList2(rowNumber = rowNumber, columnNumber = columnNumber, initializer = initializer)

    public fun <E> settableMdList(shape: Shape, initializer: (index: UIntArray) -> E): SettableMDList<E>
    public fun <E> settableMdList1(size: UInt, initializer: (index: UInt) -> E): SettableMDList1<E> =
        settableMdList(Shape(size)) { (index) -> initializer(index) }.as1D()
    public fun <E> settableMdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): SettableMDList2<E> =
        settableMdList(Shape(rowNumber, columnNumber)) { (rowIndex, columnIndex) -> initializer(rowIndex, columnIndex) }.as2D()
}

public fun MDListTransformer(): MDListTransformer = ArrayMDListTransformer
public fun SettableMDListTransformer(): SettableMDListTransformer = ArrayMDListTransformer