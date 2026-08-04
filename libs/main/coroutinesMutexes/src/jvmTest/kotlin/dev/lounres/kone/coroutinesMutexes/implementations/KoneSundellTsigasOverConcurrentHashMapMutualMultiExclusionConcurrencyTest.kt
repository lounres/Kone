/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.deque.popLast
import dev.lounres.kone.collections.iterable.isEmpty
import dev.lounres.kone.collections.iterator.next
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.list.remove
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.lincheck.datastructures.*
import kotlin.test.Test


@Param(name = "key", gen = IntGen::class, conf = "0:0")
class KoneSundellTsigasOverConcurrentHashMapMutualMultiExclusionConcurrencyTest {
    typealias Key = Int
    
    val coroutineScope = CoroutineScope(Dispatchers.Unconfined)
    val mutex = KoneSundellTsigasOverConcurrentHashMapMutualMultiExclusion<Key>()
    val jobsDeque = KoneListBackedDeque<Job>()
    
    @Operation
    fun tryLockingFor(@Param(name = "key") key: Key) = mutex.tryLockingFor(key)
    
    @Operation
    fun awaitLockFor(@Param(name = "key") key: Key) {
        coroutineScope.launch {
            mutex.awaitLockFor(key)
        }
    }
    
    @Operation(nonParallelGroup = "jobsDequeue")
    fun awaitLockForPushFirst(@Param(name = "key") key: Key) {
        jobsDeque.addFirst(
            coroutineScope.launch {
                mutex.awaitLockFor(key)
            }
        )
    }
    
    @Operation(nonParallelGroup = "jobsDequeue")
    fun awaitLockForPushLast(@Param(name = "key") key: Key) {
        jobsDeque.addLast(
            coroutineScope.launch {
                mutex.awaitLockFor(key)
            }
        )
    }
    
    @Operation
    fun tryUnlockingFor(@Param(name = "key") key: Key): Boolean = mutex.tryUnlockingFor(key)
    
    @Operation(nonParallelGroup = "jobsDequeue")
    fun cancelFirst() {
        if (jobsDeque.isNotEmpty()) jobsDeque.popFirst().cancel()
    }
    
    @Operation(nonParallelGroup = "jobsDequeue")
    fun cancelLast() {
        if (jobsDeque.isNotEmpty()) jobsDeque.popLast().cancel()
    }
    
    class SequentialSpecification {
        var jobCounter = 0u
        val dequeues = KoneMutableMap.of<Key, KoneMutableList<UInt>>()
        val jobsDeque = KoneListBackedDeque<UInt>()
        
        @Operation
        fun tryLockingFor(key: Key): Boolean {
            val dequeue = dequeues.getOrNull(key)
            if (dequeue == null) {
                dequeues[key] = KoneMutableList.of()
                return true
            } else {
                return false
            }
        }
        
        @Operation
        fun awaitLockFor(key: Key) {
            val dequeue = dequeues.getOrNull(key)
            if (dequeue == null) {
                dequeues[key] = KoneMutableList.of()
            } else {
                dequeue.add(jobCounter++)
            }
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun awaitLockForPushFirst(@Param(name = "key") key: Key) {
            val job = jobCounter++
            jobsDeque.addFirst(job)
            val dequeue = dequeues.getOrNull(key)
            if (dequeue == null) {
                dequeues[key] = KoneMutableList.of()
            } else {
                dequeue.add(job)
            }
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun awaitLockForPushLast(@Param(name = "key") key: Key) {
            val job = jobCounter++
            jobsDeque.addLast(job)
            val dequeue = dequeues.getOrNull(key)
            if (dequeue == null) {
                dequeues[key] = KoneMutableList.of()
            } else {
                dequeue.add(job)
            }
        }
        
        @Operation
        fun tryUnlockingFor(key: Key): Boolean {
            val deque = dequeues.getOrNull(key)
            if (deque == null) return false
            else {
                if (deque.isEmpty()) dequeues.remove(key)
                else deque.removeAt(0u)
                return true
            }
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun cancelFirst() {
            (Equality.defaultFor<UInt>()) {
                if (jobsDeque.isNotEmpty()) {
                    val job = jobsDeque.popFirst()
                    for (dequeue in dequeues.valuesView) dequeue.remove(job)
                }
            }
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun cancelLast() {
            (Equality.defaultFor<UInt>()) {
                if (jobsDeque.isNotEmpty()) {
                    val job = jobsDeque.popLast()
                    for (dequeue in dequeues.valuesView) dequeue.remove(job)
                }
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