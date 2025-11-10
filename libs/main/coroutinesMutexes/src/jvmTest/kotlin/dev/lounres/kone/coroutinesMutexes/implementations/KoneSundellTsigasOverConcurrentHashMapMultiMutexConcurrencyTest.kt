/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isEmpty
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrSet
import dev.lounres.kone.collections.map.of
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck.datastructures.StressOptions
import kotlin.coroutines.*
import kotlin.test.Test


@Param(name = "key", gen = IntGen::class, conf = "0:0")
class KoneSundellTsigasOverConcurrentHashMapMultiMutexConcurrencyTest {
    typealias Key = Int
    
    val mutex = KoneSundellTsigasOverConcurrentHashMapMultiMutex<Key>()
    
    @Operation
    fun tryLockingFor(@Param(name = "key") key: Key) = mutex.tryLockingFor(key)
    
    @Operation
    fun awaitLockFor(@Param(name = "key") key: Key) {
        suspend {
            mutex.awaitLockFor(key)
        }.startCoroutine(Continuation(EmptyCoroutineContext) {})
    }
    
    @Operation
    fun unlockFor(@Param(name = "key") key: Key): Boolean {
        try {
            mutex.unlockFor(key)
        } catch (e: IllegalStateException) {
            if (e.message == "Mutex is not locked") return false
            else throw e
        }
        return true
    }
    
    class SequentialSpecification {
        val deques = KoneMutableMap.of<Key, KoneListBackedDeque<Unit?>>()
        
        @Operation
        fun tryLockingFor(key: Key) =
            deques.getOrSet(key) { KoneListBackedDeque() }.let { deque ->
                if (deque.isEmpty()) {
                    deque.addLast(null)
                    true
                } else
                    false
            }
        
        @Operation
        fun awaitLockFor(key: Key) {
            deques.getOrSet(key) { KoneListBackedDeque() }.let { deque ->
                if (deque.isEmpty())
                    deque.addLast(null)
                else
                    deque.addLast(Unit)
            }
        }
        
        @Operation
        fun unlockFor(key: Key): Boolean =
            deques.getOrSet(key) { KoneListBackedDeque() }.let { deque ->
                if (deque.isEmpty()) false
                else {
                    deque.removeFirst()
                    true
                }
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