/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("WRONG_INVOCATION_KIND")

package dev.lounres.kone.contexts


@Target(AnnotationTarget.PROPERTY)
public annotation class KoneContextHolderContext

//public inline fun <Result> KoneContext.Companion.useInBlockAsExtensionReceivers(vararg koneContext: KoneContext, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    TODO()
//}
//public inline fun <Result> KoneContext.Companion.useInBlockAsContexts(vararg koneContext: KoneContext, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    TODO()
//}
public fun KoneContext.Companion.useLocallyAsExtensionReceivers(vararg koneContext: KoneContext) {
    TODO()
}
//public fun KoneContext.Companion.useLocallyAsContexts(vararg koneContext: KoneContext) {
//    TODO()
//}
//public inline fun <Result> KoneContextHolder.Companion.unwrapInBlockAsExtensionReceivers(vararg koneContextHolder: KoneContextHolder, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    TODO()
//}
//public inline fun <Result> KoneContextHolder.Companion.unwrapInBlockAsContexts(vararg koneContextHolder: KoneContextHolder, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    TODO()
//}
public fun KoneContextHolder.Companion.unwrapLocallyAsExtensionReceivers(vararg koneContextHolder: KoneContextHolder) {
    TODO()
}
//public fun KoneContextHolder.Companion.unwrapLocallyAsContexts(vararg koneContextHolder: KoneContextHolder) {
//    TODO()
//}