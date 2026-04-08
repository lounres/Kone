/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import de.infix.testBalloon.framework.core.testSuite
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.StressOptions


class KoneConcurrentMichaelScottQueueOperations {
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
}

val KoneConcurrentMichaelScottQueueTest by testSuite {
    test("stress") {
        StressOptions().check(KoneConcurrentMichaelScottQueueOperations::class)
    }
    test("modelChecking") {
        ModelCheckingOptions().check(KoneConcurrentMichaelScottQueueOperations::class)
    }
}