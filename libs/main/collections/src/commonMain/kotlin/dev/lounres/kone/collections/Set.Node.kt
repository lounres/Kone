/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneSetNode<out Element> {
    public val element: Element
}

public interface KoneMutableSetNode<Element> : KoneSetNode<Element> {
    override var element: Element
    public fun remove()
}