/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.utils

import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.list.KoneList


internal fun KoneList<UInt>.isEvenPermutation(): Boolean {
    var permutationIsEven = true
    val visited = KoneMutableArray(size) { false } // TODO: Can be replaced with specialised array
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