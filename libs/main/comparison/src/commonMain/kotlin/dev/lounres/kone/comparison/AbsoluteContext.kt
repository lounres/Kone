/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.comparison


internal object AbsoluteContext: Hashing<Any?> {
    override fun Any?.equalsTo(other: Any?): Boolean = this === other
    override fun Any?.hash(): Int = this.hashCode()
}