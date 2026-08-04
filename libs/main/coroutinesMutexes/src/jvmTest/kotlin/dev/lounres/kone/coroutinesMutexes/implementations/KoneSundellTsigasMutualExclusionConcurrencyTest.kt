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


class KoneSundellTsigasMutualExclusionConcurrencyTest {
    val coroutineScope = CoroutineScope(Dispatchers.Unconfined)
    val mutex = KoneSundellTsigasMutualExclusion()
    val jobsDeque = KoneListBackedDeque<Job>()
    
    @Operation
    fun tryLocking() = mutex.tryLocking()
    
    @Operation
    fun awaitLock() {
        coroutineScope.launch {
            mutex.awaitLock()
        }
    }
    
    @Operation(nonParallelGroup = "jobsDequeue")
    fun awaitLockPushFirst() {
        jobsDeque.addFirst(
            coroutineScope.launch {
                mutex.awaitLock()
            }
        )
    }
    
    @Operation(nonParallelGroup = "jobsDequeue")
    fun awaitLockPushLast() {
        jobsDeque.addLast(
            coroutineScope.launch {
                mutex.awaitLock()
            }
        )
    }
    
    @Operation
    fun tryUnlocking(): Boolean = mutex.tryUnlocking()
    
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
        var deque: KoneMutableList<UInt>? = null
        val jobsDeque = KoneListBackedDeque<UInt>()
        
        @Operation
        fun tryLocking() =
            if (deque == null) {
                deque = KoneMutableList.of()
                true
            } else
                false
        
        @Operation
        fun awaitLock() {
            if (deque == null)
                deque = KoneMutableList.of()
            else
                deque!!.add(jobCounter++)
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun awaitLockPushFirst() {
            val job = jobCounter++
            jobsDeque.addFirst(job)
            if (deque == null)
                deque = KoneMutableList.of()
            else
                deque!!.add(job)
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun awaitLockPushLast() {
            val job = jobCounter++
            jobsDeque.addLast(job)
            if (deque == null)
                deque = KoneMutableList.of()
            else
                deque!!.add(job)
        }
        
        @Operation
        fun tryUnlocking(): Boolean =
            if (deque == null) false
            else {
                if (deque!!.isEmpty()) deque = null
                else deque!!.removeAt(0u)
                true
            }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun cancelFirst() {
            (Equality.defaultFor<UInt>()) {
                if (jobsDeque.isNotEmpty()) deque?.remove(jobsDeque.popFirst())
            }
        }
        
        @Operation(nonParallelGroup = "jobsDequeue")
        fun cancelLast() {
            (Equality.defaultFor<UInt>()) {
                if (jobsDeque.isNotEmpty()) deque?.remove(jobsDeque.popLast())
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