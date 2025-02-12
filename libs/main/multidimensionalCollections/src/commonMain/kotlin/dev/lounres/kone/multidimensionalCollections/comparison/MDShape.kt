/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(DelicateImmutableArrayConstructor::class)

package dev.lounres.kone.multidimensionalCollections.comparison

import dev.lounres.kone.collections.array.DelicateImmutableArrayConstructor
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.multidimensionalCollections.MDShape
import dev.lounres.kone.repeat


// TODO: Move to kone-collections
internal object MDShapeEquality: Equality<MDShape> {
    override fun MDShape.equalsTo(other: MDShape): Boolean {
        if (this.size != other.size) return false
        
        for (index in 0u ..< this.size) if (this[index] != other[index]) return false
        
        return true
    }
}

internal object MDShapeHashing: Hashing<MDShape> {
    override fun MDShape.hash(): Int {
        var hashCode = 1
        repeat(size) {
            hashCode = 31 * hashCode + this[it].toInt()
        }
        return hashCode
    }
}

public fun mdShapeEquality(): Equality<MDShape> = MDShapeEquality
public fun mdShapeHashing(): Hashing<MDShape> = MDShapeHashing

public fun <Result> mdShapeEquality(block: context(Equality<MDShape>) () -> Result): Result = block(MDShapeEquality)
public fun <Result> mdShapeHashing(block: context(Hashing<MDShape>) () -> Result): Result = block(MDShapeHashing)