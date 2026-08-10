/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque

import dev.lounres.kone.collections.deque.implementations.KoneArrayResizableCircularDeque


public fun <Element> KoneDeque.Companion.empty(): KoneDeque<Element> = KoneArrayResizableCircularDeque()