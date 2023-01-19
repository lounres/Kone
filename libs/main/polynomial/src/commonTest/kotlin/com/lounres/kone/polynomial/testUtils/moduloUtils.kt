/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.testUtils

import com.lounres.kone.algebraic.IntBox


operator fun Int.not(): IntBox = IntBox(this)
operator fun List<Int>.not(): List<IntBox> = map { !it }
operator fun <T> Map<T, Int>.not(): Map<T, IntBox> = mapValues { !it.value }