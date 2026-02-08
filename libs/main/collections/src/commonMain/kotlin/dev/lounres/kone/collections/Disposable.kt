/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

/**
 * Represents disposable structure that is optimised for GC when is disposed.
 */
public interface Disposable {
    /**
     * Flag of being disposed.
     */
    public val isDisposed: Boolean
    /**
     * Disposes the structure and its dependent ones.
     */
    public fun dispose()
}