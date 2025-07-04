package dev.lounres.util.atomicfu

import kotlinx.atomicfu.locks.ReentrantLock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.atomicfu.locks.withLock as originalWithLock


@Suppress("WRONG_INVOCATION_KIND")
public inline fun <Result> ReentrantLock.withLock(block: () -> Result): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return originalWithLock { block() }
}