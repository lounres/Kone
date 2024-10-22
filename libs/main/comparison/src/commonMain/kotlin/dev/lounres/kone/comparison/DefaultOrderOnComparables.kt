/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.comparison


@Suppress("UNCHECKED_CAST")
internal object DefaultOrderOnComparables: Order<Any?> {
    override fun Any?.compareTo(other: Any?): Int =
        (this as Comparable<Any?>).compareTo(other)
}