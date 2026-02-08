/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque.implementations

import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList


public fun <Element> KoneListBackedDeque(): KoneListBackedDeque<Element> = KoneListBackedDeque(KoneArrayResizableLinkedList())