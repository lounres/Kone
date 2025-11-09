/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isEmpty
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.StressOptions
import kotlin.coroutines.*
import kotlin.test.Test


class KoneSundellTsigasMutexConcurrencyTest {
    val mutex = KoneSundellTsigasMutex()
    
    @Operation
    fun tryLocking() = mutex.tryLocking()
    
    @Operation
    fun awaitLock() {
        suspend {
            mutex.awaitLock()
        }.startCoroutine(Continuation(EmptyCoroutineContext) {})
    }
    
    @Operation
    fun unlock(): Boolean {
        try {
            mutex.unlock()
        } catch (e: IllegalStateException) {
            if (e.message == "Mutex is not locked") return false
            else throw e
        }
        return true
    }
    
    class SequentialSpecification {
        val deque = KoneListBackedDeque<Unit?>()
        
        @Operation
        fun tryLocking() =
            if (deque.isEmpty()) {
                deque.addLast(null)
                true
            } else
                false
        
        @Operation
        fun awaitLock() {
            if (deque.isEmpty())
                deque.addLast(null)
            else
                deque.addLast(Unit)
        }
        
        @Operation
        fun unlock(): Boolean =
            if (deque.isEmpty()) false
            else {
                deque.removeFirst()
                true
            }
    }
    
    @Test
    fun stress() {
        StressOptions()
            .sequentialSpecification(SequentialSpecification::class.java)
            .check(this::class)
    }
    
    @Test
    fun modelChecking() {
        ModelCheckingOptions()
            .sequentialSpecification(SequentialSpecification::class.java)
            .check(this::class)
    }
}