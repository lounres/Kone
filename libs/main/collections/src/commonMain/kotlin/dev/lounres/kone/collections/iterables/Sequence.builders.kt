/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterables


public fun <Element> KoneSequence.Companion.build(@BuilderInference builder: suspend KoneIteratorBuilder<Element>.() -> Unit): KoneSequence<Element> =
    KoneSequence { KoneIterator.build(builder) }