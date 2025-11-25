/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes.implementations



//@Param(name = "elementIndex", gen = IntGen::class, conf = "0:${KoneSundellTsigasSymmetricRelationExclusionConcurrencyTest.lastElementIndex}")
//class KoneSundellTsigasSymmetricRelationExclusionConcurrencyTest {
//    companion object {
//        const val lastElementIndex = 4u
//        typealias Element = UInt
//        val elements = KoneList.induce<Element>(lastElementIndex + 1u, 3u) { _, previous -> previous * 2u }
//        val relation = { element1: Element, element2: Element -> (element1 and element2) != 0u }
//    }
//
//    val coroutineScope = CoroutineScope(Dispatchers.Unconfined)
//    val mutex = KoneSundellTsigasSymmetricRelationExclusion<Element>(relation)
//    val jobsDeque = KoneListBackedDeque<Job>()
//
//    @Operation
//    fun tryLockingBy(@Param(name = "elementIndex") elementIndex: Int): Boolean {
//        val element = elements[elementIndex.toUInt()]
//        return mutex.tryLockingBy(element) != null
//    }
//
//    @Operation
//    fun awaitLockBy(@Param(name = "elementIndex") elementIndex: Int) {
//        coroutineScope.launch {
//            val element = elements[elementIndex.toUInt()]
//            val _ = mutex.awaitLockBy(element)
//        }
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun awaitLockByPushFirst(@Param(name = "elementIndex") elementIndex: Int) {
//        jobsDeque.addFirst(
//            coroutineScope.launch {
//                val element = elements[elementIndex.toUInt()]
//                val _ = mutex.awaitLockBy(element)
//            }
//        )
//    }
//
//    @Operation(nonParallelGroup = "jobsDequeue")
//    fun awaitLockByPushLast(@Param(name = "elementIndex") elementIndex: Int) {
//        jobsDeque.addLast(
//            coroutineScope.launch {
//                val element = elements[elementIndex.toUInt()]
//                val _ = mutex.awaitLockBy(element)
//            }
//        )
//    }
//
//    @Operation
//    fun releaseLastLock(): Boolean {
//        var current: KoneSundellTsigasSymmetricRelationExclusion.LockNode<Element>? = null
//        while (true) {
//            var node: KoneSundellTsigasSymmetricRelationExclusion.LockNode<Element>? = current
//            while (true) {
//                val prev = if (node != null) node.loadPrev().node else mutex.tail.load().node
//                when {
//                    (if (prev != null) prev.loadNext() else mutex.head.load()).let { it.node === node && !it.isBeingDeleted }
//                            && node?.loadNext()?.isBeingDeleted == false -> {
//                        if (prev != null) {
//                            node = prev
//                            break
//                        } else {
//                            return false
//                        }
//                    }
//                    node?.loadNext()?.isBeingDeleted == true -> {
//                        while (true) {
//                            if (node == null) break
//                            val next = node.loadNext().node
//                            val d = next?.loadNext()?.isBeingDeleted ?: false
//                            if (d && node.loadNext().let { it.node !== next || !it.isBeingDeleted })
//                        }
//                    }
//                    else -> {
//                        mutex.correctPrev(prev, node)
//                    }
//                }
//            }
//            current = node
//            TODO()
//        }
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