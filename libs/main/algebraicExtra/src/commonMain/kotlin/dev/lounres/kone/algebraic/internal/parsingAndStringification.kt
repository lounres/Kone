/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.internal

import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.utils.firstIndexOf
import dev.lounres.kone.collections.utils.flatMap
import dev.lounres.kone.relations.defaultEquality


internal val possibleDigits = KoneList.of('0'..'9', 'A'..'Z').flatMap { it.toKoneList() }

internal fun Char.asDigit(): UInt = context(defaultEquality<Char>()) { possibleDigits.firstIndexOf(this.uppercaseChar()) }