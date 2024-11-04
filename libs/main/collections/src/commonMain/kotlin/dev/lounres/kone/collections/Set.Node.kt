/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneSetNode<out E> {
    public val element: E
}

public interface KoneMutableSetNode<E> : KoneSetNode<E> {
    override var element: E
    public fun remove()
}