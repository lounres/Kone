/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.next


internal fun <E> Appendable.appendElement(element: E, transform: ((E) -> CharSequence)?) {
    when {
        transform != null -> append(transform(element))
        element is CharSequence? -> append(element)
        element is Char -> append(element)
        else -> append(element.toString())
    }
}

public fun <E, A : Appendable> KoneIterable<E>.joinTo(buffer: A, separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: UInt = UInt.MAX_VALUE, truncated: CharSequence = "...", transform: ((E) -> CharSequence)? = null): A {
    buffer.append(prefix)
    var count = 0u
    for (element in this) {
        if (++count > 1u) buffer.append(separator)
        if (count <= limit) {
            buffer.appendElement(element, transform)
        } else break
    }
    if (limit in 0u..<count) buffer.append(truncated)
    buffer.append(postfix)
    return buffer
}

public fun <E> KoneIterable<E>.joinToString(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: UInt = UInt.MAX_VALUE, truncated: CharSequence = "...", transform: ((E) -> CharSequence)? = null): String {
    return joinTo(StringBuilder(), separator, prefix, postfix, limit, truncated, transform).toString()
}