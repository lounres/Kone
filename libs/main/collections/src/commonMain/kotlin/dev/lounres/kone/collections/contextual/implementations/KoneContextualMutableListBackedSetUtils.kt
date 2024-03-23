/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations


public fun <E> KoneContextualMutableListBackedSet(): KoneContextualMutableListBackedSet<E> = KoneContextualMutableListBackedSet(KoneContextualResizableLinkedArrayList())