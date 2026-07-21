/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused", "UnusedReceiverParameter", "WRONG_INVOCATION_KIND")

package dev.lounres.kone.contexts

import dev.lounres.kone.registry.RegistryKey
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName


@Target(AnnotationTarget.PROPERTY)
public annotation class KoneContextInclude
@Target(AnnotationTarget.PROPERTY)
public annotation class KoneContextExclude

public fun localReceivers(vararg receivers: Any?) {
    contextsPluginExceptionForRuntimeDeclarations()
}
public fun localContexts(vararg contexts: Any?) {
    contextsPluginExceptionForRuntimeDeclarations()
}
public fun KoneContext.Companion.localUnwrap(vararg koneContexts: KoneContext) {
    contextsPluginExceptionForRuntimeDeclarations()
}
//public fun KoneContextRegistry.koneLocalUnwrap(vararg keys: RegistryKey<out KoneContext>) {
//    contextsPluginExceptionForRuntimeDeclarations()
//}
//@JvmName("koneLocalUnwrapContextual")
//context(koneContextRegistry: KoneContextRegistry)
//public fun koneLocalUnwrap(vararg keys: RegistryKey<out KoneContext>) {
//    contextsPluginExceptionForRuntimeDeclarations()
//}
//public inline fun <Result> receivers(vararg receivers: Any?, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}
//public inline fun <Result> contexts(vararg receivers: Any?, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}
//public inline fun <Result> KoneContext.Companion.unwrap(vararg receivers: Any?, block: () -> Result): Result {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}
//public fun <Result> KoneContextRegistry.koneUnwrap(vararg keys: RegistryKey<out KoneContext>, block: () -> Result) {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}
//@JvmName("koneUnwrapContextual")
//context(koneContextRegistry: KoneContextRegistry)
//public fun <Result> koneUnwrap(vararg keys: RegistryKey<out KoneContext>, block: () -> Result) {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}