/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.aliases.UIntArray
import dev.lounres.kone.context.KoneContext
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.requireShapeEquality


public interface MDSpace<E>: KoneContext {
    public fun mdFormation(shape: Shape, initializer: (index: UIntArray) -> E): MDFormation<E>

    public fun MDFormation<E>.map(transform: (E) -> E): MDFormation<E> = mdFormation(shape) { index -> transform(get(index)) }
    public fun MDFormation<E>.mapIndexed(transform: (index: UIntArray, E) -> E): MDFormation<E> = mdFormation(shape) { index -> transform(index, get(index)) }
    public fun zip(left: MDFormation<E>, right: MDFormation<E>, transform: (left: E, right: E) -> E): MDFormation<E> {
        requireShapeEquality(left.shape, right.shape)
        return mdFormation(left.shape) { index -> transform(left[index], right[index]) }
    }
    public fun MDFormation<E>.all(predicate: (E) -> Boolean): Boolean
    public fun zippingAll(left: MDFormation<E>, right: MDFormation<E>, predicate: (left: E, right: E) -> Boolean): Boolean
}