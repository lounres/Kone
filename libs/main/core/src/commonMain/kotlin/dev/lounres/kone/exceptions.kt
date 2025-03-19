/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone


/**
 * Exception that describes that some functions' (or properties') implementations are intentionally not implemented
 * but were called.
 *
 * Usually it is antipattern to intentionally not implement API parts.
 * But it is useful for testing or inner implementations purposes.
 */
public open class IllegalCallException(message: String) : RuntimeException(message)

/**
 * Throws [IllegalCallException] exception with provided message.
 */
public fun illegalCall(message: String): Nothing = throw IllegalCallException(message)