/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment2

import dev.lounres.kone.collections.standard.aliases.UIntArray
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.requireShapeEquality


public interface MDSpace<E, F>: KoneContext {
    public val F.shape: Shape
    public val F.dimension: UInt get() = shape.size
    public operator fun F.get(index: UIntArray): E

    public fun mdFormation(shape: Shape, initializer: (index: UIntArray) -> E): F

    public fun F.map(transform: (E) -> E): F = mdFormation(shape) { index -> transform(get(index)) }
    public fun F.mapIndexed(transform: (index: UIntArray, E) -> E): F = mdFormation(shape) { index -> transform(index, get(index)) }
    public fun zip(left: F, right: F, transform: (left: E, right: E) -> E): F {
        requireShapeEquality(left.shape, right.shape)
        return mdFormation(left.shape) { index -> transform(left[index], right[index]) }
    }
    public fun F.all(predicate: (E) -> Boolean): Boolean
    public fun zippingAll(left: F, right: F, predicate: (left: E, right: E) -> Boolean): Boolean
}