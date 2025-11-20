/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations


//const val lastElementIndex = 4u
//
//@Param(name = "elementIndex", gen = IntGen::class, conf = "0:$lastElementIndex")
//class KoneSundellTsigasSymmetricRelationExclusionConcurrencyTest {
//    companion object {
//        typealias Element = UInt
//        val elements = KoneList.induce<Element>(lastElementIndex + 1u, 3u) { _, previous -> previous * 2u }
//        val relation = { element1: Element, element2: Element -> (element1 and element2) != 0u }
//    }
//
//    val coroutineScope = CoroutineScope(Dispatchers.Unconfined)
//    val mutex = KoneSundellTsigasSymmetricRelationExclusion<Element>(relation)
//    val locks = ConcurrentHashMap<Element, KoneSymmetricRelationExclusion.Lock>()
//    val jobsDeque = KoneListBackedDeque<Job>()
//
//    @Operation
//    fun tryLockingBy(@Param(name = "elementIndex") elementIndex: Int): Boolean {
//        val element = elements[elementIndex.toUInt()]
//        val lock = mutex.tryLockingBy(element) ?: return false
//        locks[element] = lock
//        return true
//    }
//
//    @Operation
//    fun awaitLockBy(@Param(name = "elementIndex") elementIndex: Int) {
//        coroutineScope.launch {
//            val element = elements[elementIndex.toUInt()]
//            val lock = mutex.awaitLockBy(element)
//            locks[element] = lock
//        }
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun awaitLockByPushFirst(@Param(name = "elementIndex") elementIndex: Int) {
//        jobsDeque.addFirst(
//            coroutineScope.launch {
//                val element = elements[elementIndex.toUInt()]
//                val lock = mutex.awaitLockBy(element)
//                locks[element] = lock
//            }
//        )
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun awaitLockByPushLast(@Param(name = "elementIndex") elementIndex: Int) {
//        jobsDeque.addLast(
//            coroutineScope.launch {
//                val element = elements[elementIndex.toUInt()]
//                val lock = mutex.awaitLockBy(element)
//                locks[element] = lock
//            }
//        )
//    }
//
//    @Operation
//    fun lockRelease(@Param(name = "elementIndex") elementIndex: Int) {
//        locks[elements[elementIndex.toUInt()]]?.release()
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun cancelFirst() {
//        if (jobsDeque.isNotEmpty()) jobsDeque.popFirst().cancel()
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun cancelLast() {
//        if (jobsDeque.isNotEmpty()) jobsDeque.popLast().cancel()
//    }
//
//    class SequentialSpecification {
//        var elementCounter = 0u
//        data class ElementWithId(
//            val element: Element,
//            val id: UInt,
//        )
//        val initialized = KoneMutableList.of<ElementWithId>()
//        val resumed = KoneMutableList.of<ElementWithId>()
//        val locks = KoneMutableMap.of<Element, KoneSymmetricRelationExclusion.Lock>(
//            keyEquality = Equality.defaultFor(),
//            keyHashing = Hashing.defaultFor(),
//            keyOrder = Order.defaultFor(),
//        )
//        val jobsDeque = KoneListBackedDeque<UInt>()
//
//        fun remove(id: UInt) {
//            initialized.removeAllThat { it.id == id }
//            resumed.removeAllThat { it.id == id }
//
//            var i = 0u
//            while (i < initialized.size) {
//                val elementWithId = initialized[i]
//                if (resumed.any { relation(elementWithId.element, it.element) } || initialized.take(i).any { relation(elementWithId.element, it.element) }) {
//                    i++
//                } else {
//                    initialized.removeAt(i)
//                    resumed.add(elementWithId)
//                    locks[elementWithId.element] = { remove(elementWithId.id) }
//                }
//            }
//        }
//
//        @Operation
//        fun tryLockingBy(@Param(name = "elementIndex") elementIndex: Int): Boolean {
//            val element = elements[elementIndex.toUInt()]
//            if (initialized.any { relation(element, it.element) } || resumed.any { relation(element, it.element) }) return false
//            val newId = elementCounter++
//            resumed.add(ElementWithId(element, newId))
//            locks[element] = { resumed.removeAllThat { it.id == newId } }
//            return true
//        }
//
//        @Operation
//        fun awaitLockBy(@Param(name = "elementIndex") elementIndex: Int) {
//            val element = elements[elementIndex.toUInt()]
//            val newId = elementCounter++
//            if (initialized.any { relation(element, it.element) } || resumed.any { relation(element, it.element) }) {
//                initialized.add(ElementWithId(element, newId))
//            } else {
//                resumed.add(ElementWithId(element, newId))
//                locks[element] = { remove(newId) }
//            }
//        }
//
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun awaitLockPushFirst() {
//            val job = jobCounter++
//            jobsDeque.addFirst(job)
//            if (deque == null)
//                deque = KoneMutableList.of()
//            else
//                deque!!.add(job)
//        }
//
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun awaitLockPushLast() {
//            val job = jobCounter++
//            jobsDeque.addLast(job)
//            if (deque == null)
//                deque = KoneMutableList.of()
//            else
//                deque!!.add(job)
//        }
//
//        @Operation
//        fun tryUnlocking(): Boolean =
//            if (deque == null) false
//            else {
//                if (deque!!.isEmpty()) deque = null
//                else deque!!.removeAt(0u)
//                true
//            }
//
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun cancelFirst() {
//            (Equality.defaultFor<UInt>()) {
//                if (jobsDeque.isNotEmpty()) deque?.remove(jobsDeque.popFirst())
//            }
//        }
//
//        @Operation(nonParallelGroup = "jobsDequeue")
//        fun cancelLast() {
//            (Equality.defaultFor<UInt>()) {
//                if (jobsDeque.isNotEmpty()) deque?.remove(jobsDeque.popLast())
//            }
//        }
//    }
//
//    @Test
//    fun stress() {
//        StressOptions()
//            .sequentialSpecification(SequentialSpecification::class.java)
//            .check(this::class)
//    }
//
//    @Test
//    fun modelChecking() {
//        ModelCheckingOptions()
//            .sequentialSpecification(SequentialSpecification::class.java)
//            .check(this::class)
//    }
//}