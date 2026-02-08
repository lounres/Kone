/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations

import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.deque.popLast
import dev.lounres.kone.collections.iterables.isEmpty
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.list.remove
import dev.lounres.kone.collections.utils.first
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


class KoneSundellTsigasReadWriteExclusionConcurrencyTest {
    companion object {
        val readPermits = 3u
    }
    
    val coroutineScope = CoroutineScope(Dispatchers.Unconfined)
    val mutex = KoneSundellTsigasReadWriteExclusion(readPermits)
//    val jobsDeque = KoneListBackedDeque<Job>()
    
    @Operation
    fun tryReadLocking() = mutex.tryReadLocking()
    
    @Operation
    fun tryWriteLocking() = mutex.tryWriteLocking()
    
    @Operation
    fun awaitReadLock() {
        coroutineScope.launch {
            mutex.awaitReadLock()
        }
    }
    
    @Operation
    fun awaitWriteLock() {
        coroutineScope.launch {
            mutex.awaitWriteLock()
        }
    }
    
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun awaitReadLockPushFirst() {
//        jobsDeque.addFirst(
//            coroutineScope.launch {
//                mutex.awaitReadLock()
//            }
//        )
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun awaitWriteLockPushFirst() {
//        jobsDeque.addFirst(
//            coroutineScope.launch {
//                mutex.awaitWriteLock()
//            }
//        )
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun awaitReadLockPushLast() {
//        jobsDeque.addLast(
//            coroutineScope.launch {
//                mutex.awaitReadLock()
//            }
//        )
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun awaitWriteLockPushLast() {
//        jobsDeque.addLast(
//            coroutineScope.launch {
//                mutex.awaitWriteLock()
//            }
//        )
//    }
    
    @Operation
    fun tryReadUnlocking(): Boolean = mutex.tryReadUnlocking()
    
    @Operation
    fun tryWriteUnlocking(): Boolean = mutex.tryWriteUnlocking()
    
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun cancelFirst() {
//        if (jobsDeque.isNotEmpty()) jobsDeque.popFirst().cancel()
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun cancelLast() {
//        if (jobsDeque.isNotEmpty()) jobsDeque.popLast().cancel()
//    }
    
    class SequentialSpecification {
        enum class LockType {
            Read, Write;
        }
        
        data class Lock(
            val job: UInt,
            val lockType: LockType,
        )
        
        var jobCounter = 0u
        var usedPermits = 0u
        var lockType = LockType.Read
        var deque: KoneMutableList<Lock> = KoneMutableList.of()
//        val jobsDeque = KoneListBackedDeque<UInt>()
        
        @Operation
        fun tryReadLocking() =
            when {
                deque.isNotEmpty() -> false
                usedPermits == 0u -> {
                    lockType = Read
                    usedPermits++
                    true
                }
                lockType == Write -> false
                usedPermits == readPermits -> false
                else -> {
                    usedPermits++
                    true
                }
            }
        
        @Operation
        fun tryWriteLocking() =
            when {
                deque.isNotEmpty() -> false
                usedPermits == 0u -> {
                    lockType = Write
                    usedPermits++
                    true
                }
                else -> false
            }
        
        @Operation
        fun awaitReadLock() {
            when {
                deque.isNotEmpty() ->
                    deque.add(Lock(jobCounter++, Read))
                usedPermits == 0u -> {
                    lockType = Read
                    usedPermits++
                }
                lockType == Write ->
                    deque.add(Lock(jobCounter++, Read))
                usedPermits == readPermits ->
                    deque.add(Lock(jobCounter++, Read))
                else ->
                    usedPermits++
            }
        }
        
        @Operation
        fun awaitWriteLock() {
            when {
                deque.isNotEmpty() ->
                    deque.add(Lock(jobCounter++, Write))
                usedPermits == 0u -> {
                    lockType = Write
                    usedPermits++
                }
                else ->
                    deque.add(Lock(jobCounter++, Write))
            }
        }
        
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun awaitReadLockPushFirst() {
//            val job = jobCounter++
//            jobsDeque.addFirst(job)
//            when {
//                deque.isNotEmpty() ->
//                    deque.add(Lock(job, Read))
//                usedPermits == 0u -> {
//                    lockType = Read
//                    usedPermits++
//                }
//                lockType == Write ->
//                    deque.add(Lock(job, Read))
//                usedPermits == readPermits ->
//                    deque.add(Lock(job, Read))
//                else ->
//                    usedPermits++
//            }
//        }
//
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun awaitWriteLockPushFirst() {
//            val job = jobCounter++
//            jobsDeque.addFirst(job)
//            when {
//                deque.isNotEmpty() ->
//                    deque.add(Lock(job, Write))
//                usedPermits == 0u -> {
//                    lockType = Write
//                    usedPermits++
//                }
//                else ->
//                    deque.add(Lock(job, Write))
//            }
//        }
//
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun awaitReadLockPushLast() {
//            val job = jobCounter++
//            jobsDeque.addLast(job)
//            when {
//                deque.isNotEmpty() ->
//                    deque.add(Lock(job, Read))
//                usedPermits == 0u -> {
//                    lockType = Read
//                    usedPermits++
//                }
//                lockType == Write ->
//                    deque.add(Lock(job, Read))
//                usedPermits == readPermits ->
//                    deque.add(Lock(job, Read))
//                else ->
//                    usedPermits++
//            }
//        }
//
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun awaitWriteLockPushLast() {
//            val job = jobCounter++
//            jobsDeque.addLast(job)
//            when {
//                deque.isNotEmpty() ->
//                    deque.add(Lock(job, Write))
//                usedPermits == 0u -> {
//                    lockType = Write
//                    usedPermits++
//                }
//                else ->
//                    deque.add(Lock(job, Write))
//            }
//        }
        
        @Operation
        fun tryReadUnlocking(): Boolean =
            when {
                usedPermits == 0u -> false
                lockType == Write -> false
                deque.isEmpty() -> {
                    usedPermits--
                    true
                }
                usedPermits == 1u -> {
                    val lock = deque.first()
                    deque.removeAt(0u)
                    when (lock.lockType) {
                        Read -> {
                            check(usedPermits == readPermits)
                        }
                        Write -> {
                            lockType = Write
                        }
                    }
                    true
                }
                else -> {
                    when (deque.first().lockType) {
                        Read -> {
                            check(usedPermits == readPermits)
                            deque.removeAt(0u)
                        }
                        Write -> {
                            usedPermits--
                        }
                    }
                    true
                }
            }
        
        @Operation
        fun tryWriteUnlocking(): Boolean =
            when {
                usedPermits == 0u -> false
                lockType == Read -> false
                deque.isEmpty() -> {
                    usedPermits--
                    true
                }
                else -> {
                    val lock = deque.first()
                    deque.removeAt(0u)
                    when (lock.lockType) {
                        Read -> {
                            lockType = Read
                            while (usedPermits < readPermits && deque.isNotEmpty() && deque.first().lockType == Read) {
                                deque.removeAt(0u)
                                usedPermits++
                            }
                        }
                        Write -> {}
                    }
                    true
                }
            }
        
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun cancelFirst() {
//            if (jobsDeque.isNotEmpty()) {
//                val job = jobsDeque.popFirst()
//                val _ = deque.removeAllThat { it.job == job }
//            }
//        }
//
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun cancelLast() {
//            if (jobsDeque.isNotEmpty()) {
//                val job = jobsDeque.popLast()
//                val _ = deque.removeAllThat { it.job == job }
//            }
//        }
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