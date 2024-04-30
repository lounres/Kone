/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone


public open class IllegalCallException(message: String) : RuntimeException(message)

public fun illegalCall(message: String): Nothing {
    throw IllegalCallException(message)
}