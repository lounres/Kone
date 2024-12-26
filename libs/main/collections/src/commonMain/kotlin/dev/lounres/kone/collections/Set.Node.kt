/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneSetNode<out Element> {
    public val isDetached: Boolean
    
    public val element: Element
}

public interface KoneMutableSetNode<out Element> : KoneSetNode<Element> {
    public fun remove()
}