/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public fun <E> KoneIterator<E>.getAndMoveNext(): E = getNext().also { moveNext() }

public operator fun <E> KoneIterator<E>.next(): E = getAndMoveNext()

public fun <E> KoneReversibleIterator<E>.getAndMovePrevious(): E = getPrevious().also { movePrevious() }