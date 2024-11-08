/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public fun <Element> KoneIterator<Element>.getAndMoveNext(): Element = getNext().also { moveNext() }

@Suppress("NOTHING_TO_INLINE")
public inline operator fun <Element> KoneIterator<Element>.next(): Element = getAndMoveNext()

public fun <Element> KoneReversibleIterator<Element>.getAndMovePrevious(): Element = getPrevious().also { movePrevious() }