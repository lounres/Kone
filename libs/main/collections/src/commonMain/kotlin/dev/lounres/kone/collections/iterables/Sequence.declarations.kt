/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterables


public interface KoneSequence<out Element> {
    public operator fun iterator(): KoneIterator<Element>
    
    public companion object
}