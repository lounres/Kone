/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("WRONG_INVOCATION_KIND")

package dev.lounres.kone.contexts


@Target(AnnotationTarget.PROPERTY)
public annotation class KoneContextHolderInclude
@Target(AnnotationTarget.PROPERTY)
public annotation class KoneContextHolderExclude

//public inline fun <Result> KoneContext.Companion.useInBlockAsExtensionReceivers(vararg koneContext: KoneContext, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}
//public inline fun <Result> KoneContext.Companion.useInBlockAsContexts(vararg koneContext: KoneContext, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}
public fun KoneContext.Companion.useLocallyAsExtensionReceivers(vararg koneContext: KoneContext) {
    contextsPluginExceptionForRuntimeDeclarations()
}
public fun KoneContext.Companion.useLocallyAsContexts(vararg koneContext: KoneContext) {
    contextsPluginExceptionForRuntimeDeclarations()
}
//public inline fun <Result> KoneContextHolder.Companion.unwrapInBlockAsExtensionReceivers(vararg koneContextHolder: KoneContextHolder, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}
//public inline fun <Result> KoneContextHolder.Companion.unwrapInBlockAsContexts(vararg koneContextHolder: KoneContextHolder, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}
public fun KoneContextHolder.Companion.unwrapLocallyAsExtensionReceivers(vararg koneContextHolder: KoneContextHolder) {
    contextsPluginExceptionForRuntimeDeclarations()
}
//public fun KoneContextHolder.Companion.unwrapLocallyAsContexts(vararg koneContextHolder: KoneContextHolder) {
//    contextsPluginExceptionForRuntimeDeclarations()
//}