/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual

import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.component1
import dev.lounres.kone.collections.common.utils.component2
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.implementations.ContextualArrayMDListTransformer
import dev.lounres.kone.multidimensionalCollections.requireShapeEquality


public interface ContextualMDListTransformer<E, in EE: Equality<E>>: KoneContext {
    public fun mdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): ContextualMDList<E, EE>
    public fun mdList1(size: UInt, initializer: (index: UInt) -> E): ContextualMDList1<E, EE> =
        mdList(Shape(size)) { (index) -> initializer(index) }.as1D()
    public fun mdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): ContextualMDList2<E, EE> =
        mdList(Shape(rowNumber, columnNumber)) { (rowIndex, columnIndex) -> initializer(rowIndex, columnIndex) }.as2D()

    public fun ContextualMDList<E, *>.map(transform: (E) -> E): ContextualMDList<E, EE> = mdList(shape) { index -> transform(get(index)) }
    public fun ContextualMDList<E, *>.mapIndexed(transform: (index: KoneUIntArray, E) -> E): ContextualMDList<E, EE> = mdList(shape) { index -> transform(index, get(index)) }
    public fun zip(left: ContextualMDList<E, *>, right: ContextualMDList<E, *>, transform: (left: E, right: E) -> E): ContextualMDList<E, EE> {
        requireShapeEquality(left.shape, right.shape)
        return mdList(left.shape) { index -> transform(left[index], right[index]) }
    }

    public fun ContextualMDList<E, *>.all(predicate: (E) -> Boolean): Boolean = ShapeStrides(shape).asSequence().all { predicate(get(it)) }
    public fun ContextualMDList<E, *>.allIndexed(predicate: (index: KoneUIntArray, E) -> Boolean): Boolean = ShapeStrides(shape).asSequence().all { predicate(it, get(it)) }
    public fun zippingAll(left: ContextualMDList<E, *>, right: ContextualMDList<E, *>, predicate: (left: E, right: E) -> Boolean): Boolean {
        requireShapeEquality(left.shape, right.shape)
        return ShapeStrides(left.shape).asSequence().all { predicate(left[it], right[it]) }
    }
    public fun zippingAllIndexed(left: ContextualMDList<E, *>, right: ContextualMDList<E, *>, predicate: (index: KoneUIntArray, left: E, right: E) -> Boolean): Boolean {
        requireShapeEquality(left.shape, right.shape)
        return ShapeStrides(left.shape).asSequence().all { predicate(it, left[it], right[it]) }
    }

    public fun ContextualMDList<E, *>.none(predicate: (E) -> Boolean): Boolean = this.all { !predicate(it) }
    public fun ContextualMDList<E, *>.noneIndexed(predicate: (index: KoneUIntArray, E) -> Boolean): Boolean = this.allIndexed { index, it -> !predicate(index, it) }
    public fun zippingNone(left: ContextualMDList<E, *>, right: ContextualMDList<E, *>, predicate: (left: E, right: E) -> Boolean): Boolean =
        zippingAll(left, right) { leftElement, rightElement -> !predicate(leftElement, rightElement) }
    public fun zippingNoneIndexed(left: ContextualMDList<E, *>, right: ContextualMDList<E, *>, predicate: (index: KoneUIntArray, left: E, right: E) -> Boolean): Boolean =
        zippingAllIndexed(left, right) { index, leftElement, rightElement -> !predicate(index, leftElement, rightElement) }

    public fun ContextualMDList<E, *>.any(predicate: (E) -> Boolean): Boolean = !this.all { !predicate(it) }
    public fun ContextualMDList<E, *>.anyIndexed(predicate: (index: KoneUIntArray, E) -> Boolean): Boolean = !this.allIndexed { index, it -> !predicate(index, it) }
    public fun zippingAny(left: ContextualMDList<E, *>, right: ContextualMDList<E, *>, predicate: (left: E, right: E) -> Boolean): Boolean =
        !zippingAll(left, right) { leftElement, rightElement -> !predicate(leftElement, rightElement) }
    public fun zippingAnyIndexed(left: ContextualMDList<E, *>, right: ContextualMDList<E, *>, predicate: (index: KoneUIntArray, left: E, right: E) -> Boolean): Boolean =
        !zippingAllIndexed(left, right) { index, leftElement, rightElement -> !predicate(index, leftElement, rightElement) }
}

public interface ContextualSettableMDListTransformer<E, in EE: Equality<E>> : ContextualMDListTransformer<E, EE> {
    override fun mdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): ContextualMDList<E, EE> =
        settableMdList(shape = shape, initializer = initializer)
    override fun mdList1(size: UInt, initializer: (index: UInt) -> E): ContextualMDList1<E, EE> =
        settableMdList1(size = size, initializer = initializer)
    override fun mdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): ContextualMDList2<E, EE> =
        settableMdList2(rowNumber = rowNumber, columnNumber = columnNumber, initializer = initializer)

    public fun settableMdList(shape: Shape, initializer: (index: KoneUIntArray) -> E): ContextualSettableMDList<E, EE>
    public fun settableMdList1(size: UInt, initializer: (index: UInt) -> E): ContextualSettableMDList1<E, EE> =
        settableMdList(Shape(size)) { (index) -> initializer(index) }.as1D()
    public fun settableMdList2(rowNumber: UInt, columnNumber: UInt, initializer: (rowIndex: UInt, columnIndex: UInt) -> E): ContextualSettableMDList2<E, EE> =
        settableMdList(Shape(rowNumber, columnNumber)) { (rowIndex, columnIndex) -> initializer(rowIndex, columnIndex) }.as2D()
}

public fun <E, EE: Equality<E>> ContextualMDListTransformer(): ContextualMDListTransformer<E, EE> = ContextualArrayMDListTransformer()
public fun <E, EE: Equality<E>> ContextualSettableMDListTransformer(): ContextualSettableMDListTransformer<E, EE> = ContextualArrayMDListTransformer()