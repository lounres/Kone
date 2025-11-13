/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.deque.popLast
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.list.remove
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.StressOptions
import kotlin.test.Test


class KoneSundellTsigasSemaphoreConcurrencyTest {
    companion object {
        val permits = 3u
    }
    
    val coroutineScope = CoroutineScope(Dispatchers.Unconfined)
    val mutex = KoneSundellTsigasSemaphore(permits)
    val jobsDeque = KoneListBackedDeque<Job>()
    
    @Operation
    fun tryAcquiring() = mutex.tryAcquiring()
    
    @Operation
    fun awaitAcquire() {
        coroutineScope.launch {
            mutex.awaitAcquire()
        }
    }
    
    @Operation(nonParallelGroup = "jobsDequeue")
    fun awaitAcquirePushFirst() {
        jobsDeque.addFirst(
            coroutineScope.launch {
                mutex.awaitAcquire()
            }
        )
    }
    
    @Operation(nonParallelGroup = "jobsDequeue")
    fun awaitAcquirePushLast() {
        jobsDeque.addLast(
            coroutineScope.launch {
                mutex.awaitAcquire()
            }
        )
    }
    
    @Operation
    fun tryReleasing(): Boolean = mutex.tryReleasing()
    
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
        var availablePermits = permits
        var deque: KoneMutableList<UInt>? = null
        val jobsDeque = KoneListBackedDeque<UInt>()
        
        @Operation
        fun tryAcquiring() =
            when {
                availablePermits > 1u -> {
                    availablePermits--
                    true
                }
                availablePermits == 1u -> {
                    availablePermits = 0u
                    deque = KoneMutableList.of()
                    true
                }
                else -> false
            }
        
        @Operation
        fun awaitAcquire() {
            when {
                availablePermits > 1u ->
                    availablePermits--
                availablePermits == 1u -> {
                    availablePermits = 0u
                    deque = KoneMutableList.of()
                }
                else -> deque!!.add(jobCounter++)
            }
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun awaitAcquirePushFirst() {
            val job = jobCounter++
            jobsDeque.addFirst(job)
            when {
                availablePermits > 1u ->
                    availablePermits--
                availablePermits == 1u -> {
                    availablePermits = 0u
                    deque = KoneMutableList.of()
                }
                else -> deque!!.add(job)
            }
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun awaitAcquirePushLast() {
            val job = jobCounter++
            jobsDeque.addLast(job)
            when {
                availablePermits > 1u ->
                    availablePermits--
                availablePermits == 1u -> {
                    availablePermits = 0u
                    deque = KoneMutableList.of()
                }
                else -> deque!!.add(job)
            }
        }
        
        @Operation
        fun tryReleasing(): Boolean =
            when {
                deque != null && deque!!.isNotEmpty() -> {
                    deque!!.removeAt(0u)
                    true
                }
                availablePermits < permits -> {
                    deque = null
                    availablePermits++
                    true
                }
                else -> false
            }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun cancelFirst() {
            if (jobsDeque.isNotEmpty()) {
                val job = jobsDeque.popFirst()
                (Equality.defaultFor<UInt>()) {
                    val _ = deque?.remove(job)
                }
            }
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun cancelLast() {
            if (jobsDeque.isNotEmpty()) {
                val job = jobsDeque.popLast()
                (Equality.defaultFor<UInt>()) {
                    val _ = deque?.remove(job)
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