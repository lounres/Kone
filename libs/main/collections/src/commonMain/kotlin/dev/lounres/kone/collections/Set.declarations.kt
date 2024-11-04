/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


// TODO: KoneSet is marked as covariant, but element context can't check equality for element of supertype
//  So there is need in checking that element context can work with arbitrary argument
public interface KoneSet<out E> : KoneIterable<E> {
    public val size: UInt
    
    public operator fun contains(element: @UnsafeVariance E): Boolean
}

public interface KoneMutableSet<E> : KoneSet<E> {
    public fun add(element: E)
    public fun addSeveral(number: UInt, builder: (index: UInt) -> E)
    
    public fun remove(element: E)
    public fun removeAllThat(predicate: (element: E) -> Boolean)
    public fun removeAll()
}

public interface KoneNoddedSet<out E> : KoneSet<E> {
    public val nodes: KoneIterable<KoneSetNode<E>>
}

public interface KoneNoddedMutableSet<E> : KoneMutableSet<E>, KoneNoddedSet<E> {
    override val nodes: KoneIterable<KoneMutableSetNode<E>>
    
    public fun addNode(element: E): KoneMutableSetNode<E>
    override fun add(element: E) { addNode(element) }
}