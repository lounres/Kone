/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.operations

import dev.lounres.kone.collections.array.KoneMutableBooleanArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.list.KoneList


internal fun KoneList<UInt>.isEvenPermutation(): Boolean {
    var permutationIsEven = true
    val visited = KoneMutableBooleanArray.generate(size) { false } // TODO: Can be replaced with specialised array
    for (i in 0u ..< size) if (!visited[i]) {
        var current = i
        visited[i] = true
        var cycleIsEven = false
        while (true) {
            current = this[current]
            if (current == i) break
            cycleIsEven = !cycleIsEven
            visited[current] = true
        }
        if (cycleIsEven) permutationIsEven = !permutationIsEven
    }
    return permutationIsEven
}