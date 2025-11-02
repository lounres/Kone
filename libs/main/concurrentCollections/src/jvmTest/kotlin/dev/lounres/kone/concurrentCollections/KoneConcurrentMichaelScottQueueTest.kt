/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.StressOptions
import kotlin.test.Test


class KoneConcurrentMichaelScottQueueTest {
    private val queue = KoneConcurrentMichaelScottQueue<Int>()
    
    @Operation
    fun addLast(element: Int) {
        queue.addLast(element)
    }
    
    @Operation
    fun removeFirstIfPresent() {
        queue.removeFirstIfPresent()
    }
    
    @Operation
    fun popFirstMaybe() = queue.popFirstMaybe()
    
    @Test
    fun stress() {
        StressOptions()
            .check(this::class)
    }
    
    @Test
    fun modelChecking() {
        ModelCheckingOptions()
            .check(this::class)
    }
}