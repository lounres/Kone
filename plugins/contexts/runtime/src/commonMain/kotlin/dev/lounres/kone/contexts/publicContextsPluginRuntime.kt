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


/**
 * Marks a property of a class, an interface, or an object to be included in unwrapping of the containing class/interface/object
 * during the call of [localUnwrap] or [koneLocalUnwrap].
 *
 * When applied to a property of type [KoneContext], this annotation instructs the
 * Kone contexts compiler plugin to include that property's value in the generated
 * [KoneContextRegistry] for the containing class.
 */
@Target(AnnotationTarget.PROPERTY)
public annotation class KoneContextInclude

/**
 * Marks a property to be excluded from the context registry during contexts plugin processing.
 *
 * When applied to a property of type [KoneContext], this annotation instructs the
 * Kone contexts compiler plugin to exclude that property's value from the generated
 * [KoneContextRegistry] for the containing class, even if it would otherwise be included
 * by default.
 */
@Target(AnnotationTarget.PROPERTY)
public annotation class KoneContextExclude

/**
 * Runtime declaration for the `localReceivers` contexts plugin function.
 *
 * This function is a stub that is replaced at compile time by the Kone contexts compiler plugin.
 * It registers the provided [receivers] as local receivers available in the current scope
 * for context resolution.
 *
 * @param receivers The objects to register as local receivers.
 * @throws UnsupportedOperationException Always thrown at runtime; this function must be
 *         transformed by the Kone contexts compiler plugin before execution.
 */
public fun localReceivers(vararg receivers: Any?) {
    contextsPluginExceptionForRuntimeDeclarations()
}

/**
 * Runtime declaration for the `localContexts` contexts plugin function.
 *
 * This function is a stub that is replaced at compile time by the Kone contexts compiler plugin.
 * It registers the provided [contexts] as local contexts available in the current scope.
 *
 * @param contexts The context instances to register as local contexts.
 * @throws UnsupportedOperationException Always thrown at runtime; this function must be
 *         transformed by the Kone contexts compiler plugin before execution.
 */
public fun localContexts(vararg contexts: Any?) {
    contextsPluginExceptionForRuntimeDeclarations()
}

/**
 * This function is a stub that is replaced at compile time by the Kone contexts compiler plugin.
 * It "unwraps" the provided [koneContexts] into their underlying [KoneContext] instances.
 *
 * "Unwrapping" means that the instance is used as a local context and all its included properties are unwrapped as well.
 * A property is considered as included if it is marked with [KoneContextInclude] which is not overrided by [KoneContextExclude]
 * (i.e. it does not have override property with the [KoneContextExclude] annotaition),
 *
 * The properties to use during the unwrapping are found statically (i.e. by compiler by the provided type),
 * but not dynamically (i.e. not by the actual type that is a subtype of the provided type).
 *
 * @receiver The [KoneContext.Companion] object.
 * @param koneContexts The context instances to unwrap.
 * @throws UnsupportedOperationException Always thrown at runtime; this function must be
 *         transformed by the Kone contexts compiler plugin before execution.
 */
public fun KoneContext.Companion.localUnwrap(vararg koneContexts: KoneContext) {
    contextsPluginExceptionForRuntimeDeclarations()
}

/**
 * This function is a stub that is replaced at compile time by the Kone contexts compiler plugin.
 * It "unwraps" values corresponding to the specified [keys] in the [KoneContextRegistry] receiver.
 *
 * "Unwrapping" means that the instance is used as a local context and all its included properties are unwrapped as well.
 * A property is considered as included if it is marked with [KoneContextInclude] which is not overrided by [KoneContextExclude]
 * (i.e. it does not have override property with the [KoneContextExclude] annotaition),
 *
 * The properties to use during the unwrapping are found statically (i.e. by compiler by the provided type),
 * but not dynamically (i.e. not by the actual type that is a subtype of the provided type).
 *
 * @receiver The [KoneContextRegistry] which values to retrieve and to unwrap.
 * @param keys The registry keys identifying which values to unwrap.
 * @throws UnsupportedOperationException Always thrown at runtime; this function must be
 *         transformed by the Kone contexts compiler plugin before execution.
 */
public fun KoneContextRegistry.koneLocalUnwrap(vararg keys: RegistryKey<out KoneContext>) {
    contextsPluginExceptionForRuntimeDeclarations()
}

/**
 * This function is a stub that is replaced at compile time by the Kone contexts compiler plugin.
 * It "unwraps" values corresponding to the specified [keys] in the [KoneContextRegistry] context parameter.
 *
 * "Unwrapping" means that the instance is used as a local context and all its included properties are unwrapped as well.
 * A property is considered as included if it is marked with [KoneContextInclude] which is not overrided by [KoneContextExclude]
 * (i.e. it does not have override property with the [KoneContextExclude] annotaition),
 *
 * The properties to use during the unwrapping are found statically (i.e. by compiler by the provided type),
 * but not dynamically (i.e. not by the actual type that is a subtype of the provided type).
 *
 * @param koneContextRegistry The [KoneContextRegistry] which values to retrieve and to unwrap.
 * @param keys The registry keys identifying which values to unwrap.
 * @throws UnsupportedOperationException Always thrown at runtime; this function must be
 *         transformed by the Kone contexts compiler plugin before execution.
 */
@JvmName("koneLocalUnwrapContextual")
context(koneContextRegistry: KoneContextRegistry)
public fun koneLocalUnwrap(vararg keys: RegistryKey<out KoneContext>) {
    contextsPluginExceptionForRuntimeDeclarations()
}

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
//public inline fun <Result> KoneContextRegistry.koneUnwrap(vararg keys: RegistryKey<out KoneContext>, block: () -> Result) {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}
//@JvmName("koneUnwrapContextual")
//context(koneContextRegistry: KoneContextRegistry)
//public inline fun <Result> koneUnwrap(vararg keys: RegistryKey<out KoneContext>, block: () -> Result) {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        returnsResultOf(block)
//    }
//    contextsPluginExceptionForRuntimeDeclarations()
//}