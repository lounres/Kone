@file:OptIn(ExperimentalStdlibApi::class)

/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.io.RandomAccessFile
import java.lang.Integer.min
import java.lang.NullPointerException
import java.time.LocalTime
import java.util.*
import kotlin.streams.toList
import kotlin.system.measureNanoTime


fun mex(values: Iterable<UInt>) : UInt {
    var currentValue = 0u
    val impossibleValues = TreeSet<UInt>() // TODO: Replace with heap (because only insertion, getting min and removing min are needed)
    for (value in values) when {
        value == currentValue -> {
            currentValue++
            while (currentValue == impossibleValues.firstOrNull()) {
                impossibleValues.remove(currentValue)
                currentValue++
            }
        }
        value > currentValue -> impossibleValues.add(value)
    }
    return currentValue
}

fun prod1(left: UInt, right: UInt): UInt = mex(buildList {
    for (left2 in 0u until left) for (right2 in 0u until right)
        add(prod1(left2, right) xor prod1(left2, right2) xor prod1(left, right2))
})

fun prod2(left: UInt, right: UInt): UInt = mex((0u until left * right).map { prod2(it % left, it / left) xor prod2(left, it / left) xor prod2(it % left, right) })


val limit3 = 16
val cache3 = Array(limit3) { Array<UInt?>(limit3) { null } }

fun prod3(left: UInt, right: UInt): UInt = cache3[left.toInt()][right.toInt()] ?:
    mex(buildList((left * right).toInt()) {
        for (left2 in 0u until left) for (right2 in 0u until right)
            add(prod1(left2, right) xor prod1(left2, right2) xor prod1(left, right2))
    }).also { cache3[left.toInt()][right.toInt()] = it }

val limit4 = 16
val cache4 = Array(limit3) { Array<UInt?>(limit3) { null } }

fun prod4(left: UInt, right: UInt): UInt = cache3[left.toInt()][right.toInt()] ?:
mex((0u until left * right).map { prod3(it % left, it / left) xor prod3(left, it / left) xor prod3(it % left, right) })
    .also { cache3[left.toInt()][right.toInt()] = it }

val limit5 = 16
val powers5 = List(limit5) { 1uL shl it }
val cache5 = Array(limit5) { Array<ULong?>(limit5) { null } }
fun prod5(left: ULong, right: ULong): ULong {
    val leftBits = BitSet.valueOf(longArrayOf(left.toLong())).stream().toList()
    val rightBIts = BitSet.valueOf(longArrayOf(right.toLong())).stream().toList()
    var result = 0uL
    for (i in leftBits) for (j in rightBIts) result = result xor cache5[i][j]!!
    return result
}
fun computePowersProds5() {
    for (i in 0 ..< limit5) {
        val left = powers5[i]
        for (j in i ..< limit5) {
            val right = powers5[j]
            var currentValue = 0uL
            val impossibleValues = TreeSet<ULong>() // TODO: Replace with heap (because only insertion, getting min and removing min are needed)
            for (left2 in 0uL ..< left) for (right2 in 0uL ..< right) {
                val value = prod5(left2, right2) xor prod5(left, right2) xor prod5(left2, right)
                when {
                    value == currentValue -> {
                        currentValue++
                        while (currentValue == impossibleValues.firstOrNull()) {
                            impossibleValues.remove(currentValue)
                            currentValue++
                        }
                    }
                    value > currentValue -> impossibleValues.add(value)
                }
            }
            cache5[i][j] = currentValue
            cache5[j][i] = currentValue
        }
    }
}

val limit6 = 16
val powers6 = List(limit6) { 1uL shl it }
val cache6 = Array(limit6) { Array<ULong?>(limit6) { null } }
fun prod6(left: ULong, right: ULong): ULong {
    val leftBits = BitSet.valueOf(longArrayOf(left.toLong())).stream().toList()
    val rightBIts = BitSet.valueOf(longArrayOf(right.toLong())).stream().toList()
    var result = 0uL
    for (i in leftBits) for (j in rightBIts) result = result xor cache6[i][j]!!
    return result
}
@OptIn(ExperimentalStdlibApi::class)
fun computePowersProds6() = runBlocking(Dispatchers.Default) {
    for (s in 0 .. (limit6 - 1) * 2) {
        ((s + 1)/2 .. min(s, limit6 - 1)).map { i ->
            val j = s - i
            val left = powers6[i]
            val right = powers6[j]
            launch {
                var currentValue = 0uL
                val impossibleValues =
                    TreeSet<ULong>() // TODO: Replace with heap (because only insertion, getting min and removing min are needed)
                for (left2 in 0uL ..< left) for (right2 in 0uL ..< right) {
                    val value = prod6(left2, right2) xor prod6(left, right2) xor prod6(left2, right)
                    when {
                        value == currentValue -> {
                            currentValue++
                            while (currentValue == impossibleValues.firstOrNull()) {
                                impossibleValues.remove(currentValue)
                                currentValue++
                            }
                        }

                        value > currentValue -> impossibleValues.add(value)
                    }
                }
                cache6[i][j] = currentValue
                cache6[j][i] = currentValue
            }
        }.joinAll()
    }
}

val limit7 = 64
val powers7 = List(limit7) { 1uL shl it }
val cache7 = Array(limit7) { Array<ULong?>(limit7) { null } }
fun prod7(left: ULong, right: ULong): ULong {
    val leftBits = BitSet.valueOf(longArrayOf(left.toLong())).stream().toList()
    val rightBIts = BitSet.valueOf(longArrayOf(right.toLong())).stream().toList()
    var result = 0uL
    for (i in leftBits) for (j in rightBIts) result = result xor cache7[i][j]!!
    return result
}
@OptIn(ExperimentalStdlibApi::class)
fun computePowersProds7() = runBlocking(Dispatchers.Default) {
    val jobs = Array(limit7) { Array<Job?>(limit7) { null } }
    for (i in 0 ..< limit7) {
        val left = powers7[i]
        for (j in i ..< limit7) {
            val right = powers7[j]
            jobs[i][j] = launch {
                buildList(2) {
                    if (i > 0) add(jobs[i-1][j]!!)
                    try { if (j > i) add(jobs[i][j - 1]!!) }
                    catch (e: NullPointerException) {
                        println("$i, $j")
                        throw e
                    }
                }.joinAll()
                var currentValue = 0uL
                val impossibleValues =
                    TreeSet<ULong>() // TODO: Replace with heap (because only insertion, getting min and removing min are needed)
                for (left2 in 0uL ..< left) for (right2 in 0uL ..< right) {
                    val value = prod7(left2, right2) xor prod7(left, right2) xor prod7(left2, right)
                    when {
                        value == currentValue -> {
                            currentValue++
                            while (currentValue == impossibleValues.firstOrNull()) {
                                impossibleValues.remove(currentValue)
                                currentValue++
                            }
                        }

                        value > currentValue -> impossibleValues.add(value)
                    }
                }
                cache7[i][j] = currentValue
                cache7[j][i] = currentValue
            }
        }
    }
    jobs.last().last()!!.join()
}

val limit8 = 64
val powers8 = List(limit8) { 1uL shl it }
val cache8 = Array(limit8) { Array<ULong?>(limit8) { null } }
val cacheFileName8 = "C:\\Programming\\Kone\\kone-gameTheory\\src\\commonMain\\resources\\prod8-table-size-$limit8-at-${LocalTime.now().toString().substringBefore(".").replace(":", "-")}"
val cacheFile8 = RandomAccessFile(File(cacheFileName8), "rw").apply {
    repeat(limit8 * limit8) { writeLong(0) }
}
data class Query8(val i: Int, val j: Int, val value: Long)
val cacheFileWriteQueries8 = Channel<Query8>(Channel.UNLIMITED)
fun prod8(left: ULong, right: ULong): ULong {
    val leftBits = BitSet.valueOf(longArrayOf(left.toLong())).stream().toList()
    val rightBIts = BitSet.valueOf(longArrayOf(right.toLong())).stream().toList()
    var result = 0uL
    for (i in leftBits) for (j in rightBIts) result = result xor cache8[i][j]!!
    return result
}
@OptIn(ExperimentalStdlibApi::class)
fun computePowersProds8() = runBlocking(Dispatchers.Default) {
    launch {
        while (true) {
            val (i, j, value) = cacheFileWriteQueries8.receive()
            cacheFile8.apply {
                seek((i + j * limit8) * 8L)
                writeLong(value)
            }
        }
    }
    val jobs = Array(limit8) { Array<Job?>(limit8) { null } }
    for (i in 0 ..< limit8) {
        val left = powers8[i]
        for (j in i ..< limit8) {
            val right = powers8[j]
            jobs[i][j] = launch {
                buildList(2) {
                    if (i > 0) add(jobs[i-1][j]!!)
                    try { if (j > i) add(jobs[i][j - 1]!!) }
                    catch (e: NullPointerException) {
                        println("$i, $j")
                        throw e
                    }
                }.joinAll()
                var currentValue = 0uL
                val impossibleValues = PriorityQueue<ULong>()
                for (left2 in 0uL ..< left) for (right2 in 0uL ..< right) {
                    val value = prod8(left2, right2) xor prod8(left, right2) xor prod8(left2, right)
                    when {
                        value == currentValue -> {
                            currentValue++
                            while (currentValue == impossibleValues.peek()) {
                                impossibleValues.poll()
                                currentValue++
                            }
                        }

                        value > currentValue -> impossibleValues.add(value)
                    }
                }
                cache8[i][j] = currentValue
                cache8[j][i] = currentValue
                cacheFileWriteQueries8.send(Query8(i, j, currentValue.toLong()))
                cacheFileWriteQueries8.send(Query8(j, i, currentValue.toLong()))
            }
        }
    }
    jobs.last().last()!!.join()
}

val limit9 = 64
val powers9 = List(limit9) { 1uL shl it }
val oldCacheFileName9 = "C:\\Programming\\Kone\\kone-gameTheory\\src\\commonMain\\resources\\prod8-table-size-64-at-13-59-05"
val oldCacheFile = RandomAccessFile(File(oldCacheFileName9), "rw")
val cache9 = Array(limit9) { Array(limit9) { oldCacheFile.readLong().toULong().let { if (it == 0uL) null else it } } }
val cacheFileName9 = "C:\\Programming\\Kone\\kone-gameTheory\\src\\commonMain\\resources\\prod9-table-size-$limit9-at-${LocalTime.now().toString().substringBefore(".").replace(":", "-")}"
val cacheFile9 = RandomAccessFile(File(cacheFileName9), "rw").apply {
    for (line in cache9) for (value in line) writeLong((value ?: 0u).toLong())
}
data class Query9(val i: Int, val j: Int, val value: Long)
val cacheFileWriteQueries9 = Channel<Query9>(Channel.UNLIMITED)
fun prod9(left: ULong, right: ULong): ULong {
    val leftBits = BitSet.valueOf(longArrayOf(left.toLong())).stream().toList()
    val rightBIts = BitSet.valueOf(longArrayOf(right.toLong())).stream().toList()
    var result = 0uL
    for (i in leftBits) for (j in rightBIts) result = result xor cache9[i][j]!!
    return result
}
@OptIn(ExperimentalStdlibApi::class)
fun computePowersProds9() = runBlocking(Dispatchers.Default) {
    launch {
        while (true) {
            val (i, j, value) = cacheFileWriteQueries9.receive()
            cacheFile9.apply {
                seek((i + j * limit9) * 9L)
                writeLong(value)
            }
        }
    }
    val jobs = Array(limit9) { Array<Job?>(limit9) { null } }
    for (i in 0 ..< limit9) {
        val left = powers9[i]
        for (j in i ..< limit9) {
            val right = powers9[j]
            jobs[i][j] = launch {
                if (cache9[i][j] != null) return@launch
                buildList(2) {
                    if (i > 0) add(jobs[i-1][j]!!)
                    try { if (j > i) add(jobs[i][j - 1]!!) }
                    catch (e: NullPointerException) {
                        println("$i, $j")
                        throw e
                    }
                }.joinAll()
                var currentValue = 0uL
                val impossibleValues = PriorityQueue<ULong>()
                for (left2 in 0uL ..< left) for (right2 in 0uL ..< right) {
                    val value = prod9(left2, right2) xor prod9(left, right2) xor prod9(left2, right)
                    when {
                        value == currentValue -> {
                            currentValue++
                            while (currentValue == impossibleValues.peek()) {
                                impossibleValues.poll()
                                currentValue++
                            }
                        }

                        value > currentValue -> impossibleValues.add(value)
                    }
                }
                cache9[i][j] = currentValue
                cache9[j][i] = currentValue
                cacheFileWriteQueries9.send(Query9(i, j, currentValue.toLong()))
                cacheFileWriteQueries9.send(Query9(j, i, currentValue.toLong()))
            }
        }
    }
    jobs.last().last()!!.join()
}

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val time = measureNanoTime {
        computePowersProds8()
    }
    File("C:\\Programming\\Kone\\kone-gameTheory\\src\\commonMain\\resources\\prod9-uptime-size-$limit9-at-${LocalTime.now().toString().substringBefore(".").replace(":", "-")}")
        .bufferedWriter().use {
            it.write(time.toString())
        }
}