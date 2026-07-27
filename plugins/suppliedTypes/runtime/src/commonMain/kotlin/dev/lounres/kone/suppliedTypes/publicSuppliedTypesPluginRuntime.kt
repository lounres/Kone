/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.suppliedTypes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * Marks a type parameter as a supplied type parameter.
 *
 * Supplied type parameters are bound to concrete [SuppliedType] values at compile time by the
 * supplied types compiler plugin, either through suppliance parameters of suppliable declarations
 * or through [withSupplied] scopes.
 */
@Target(AnnotationTarget.TYPE_PARAMETER)
public annotation class Supply

/**
 * Marks a declaration as suppliable.
 *
 * Suppliable functions and classes can declare supplied type parameters with [Supply], use
 * [suppliedTypeOf] to obtain runtime type representations, and receive supplied types through
 * generated suppliance parameters.
 */
@Target(
    AnnotationTarget.FUNCTION,
//    AnnotationTarget.PROPERTY,
    AnnotationTarget.CLASS,
)
public annotation class Suppliable

/**
 * Base interface for suppliable classes.
 *
 * The supplied types compiler plugin generates storage and initialization logic for classes
 * annotated with [Suppliable] via extending this interface.
 */
@Suppliable
public interface SuppliableClass {
    /**
     * Storage for supplied types of this class, keyed by the fully qualified name of the class.
     *
     * Each value is a list of [SuppliedType] instances that correspond, in order, to the
     * [Supply]-annotated type parameters of the class. This property is initialized by the
     * supplied types compiler plugin and must not be accessed before initialization completes.
     */
    @Deprecated(message = internalsDeprecationAnnotationMessage, level = HIDDEN)
    public var suppliedTypesStorage: Map<String, List<SuppliedType>>
    
    /**
     * Called after supplied types of this instance have been initialized.
     *
     * Override this method in a suppliable class to run logic that depends on supplied types
     * being available. The default implementation does nothing.
     */
    public fun afterSuppliance() {}
}

// TODO: Replace with 'SuppliedType.Companion.of'
/**
 * Creates a [SuppliedType] that represents the compile-time type [T].
 *
 * This function is an intrinsic stub replaced by the supplied types compiler plugin. If the plugin
 * is not applied, calling it throws [IllegalStateException].
 *
 * @param T The type expression to represent. Must be a fully defined type in the current scope;
 * type parameters marked with [Supply] are resolved from the enclosing suppliable declaration or
 * [withSupplied] scope.
 * @return A [SuppliedType] describing [T].
 */
@Suppliable
public fun <@Supply T> suppliedTypeOf(): SuppliedType = suppliedTypesPluginExceptionForRuntimeDeclarations()

//public fun <T> supply(suppliedType: SuppliedType) {
//    suppliedTypesPluginExceptionForRuntimeDeclarations()
//}

/**
 * Executes [block] in a scope where the supplied type parameter [T1] is bound to [suppliedType1].
 *
 * Within [block], [suppliedTypeOf] calls and suppliable calls can use [T1] as if it were a
 * concrete type described by [suppliedType1]. This function is an intrinsic stub replaced by the
 * supplied types compiler plugin. If the plugin is not applied, calling it throws
 * [IllegalStateException].
 *
 * @param T1 The supplied type parameter to bind in [block]. Must be specified as an explicit type
 * argument at the call site and must refer to a [Supply]-annotated type parameter visible in the
 * enclosing scope.
 * @param R The return type of [block], inferred from its result.
 * @param suppliedType1 The [SuppliedType] value to bind to [T1] for the duration of [block].
 * @param block The code to execute with [T1] supplied. Invoked exactly once.
 * @return The result returned by [block].
 */
@Suppress("WRONG_INVOCATION_KIND", "unused")
public inline fun <T1, R> withSupplied(suppliedType1: SuppliedType, block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        returnsResultOf(block)
    }
    suppliedTypesPluginExceptionForRuntimeDeclarations()
}

/**
 * Executes [block] in a scope where the supplied type parameters [T1] and [T2] are bound to
 * [suppliedType1] and [suppliedType2], respectively.
 *
 * Within [block], [suppliedTypeOf] calls and suppliable calls can use [T1] and [T2] as if they
 * were concrete types described by the corresponding [SuppliedType] arguments. This function is an
 * intrinsic stub replaced by the supplied types compiler plugin. If the plugin is not applied,
 * calling it throws [IllegalStateException].
 *
 * @param T1 The first supplied type parameter to bind in [block]. Must be specified as an explicit
 * type argument at the call site and must refer to a [Supply]-annotated type parameter visible in
 * the enclosing scope.
 * @param T2 The second supplied type parameter to bind in [block]. Must be specified as an explicit
 * type argument at the call site and must refer to a [Supply]-annotated type parameter visible in
 * the enclosing scope.
 * @param R The return type of [block], inferred from its result.
 * @param suppliedType1 The [SuppliedType] value to bind to [T1] for the duration of [block].
 * @param suppliedType2 The [SuppliedType] value to bind to [T2] for the duration of [block].
 * @param block The code to execute with [T1] and [T2] supplied. Invoked exactly once.
 * @return The result returned by [block].
 */
@Suppress("WRONG_INVOCATION_KIND", "unused")
public inline fun <T1, T2, R> withSupplied(suppliedType1: SuppliedType, suppliedType2: SuppliedType, block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        returnsResultOf(block)
    }
    suppliedTypesPluginExceptionForRuntimeDeclarations()
}

/**
 * Executes [block] in a scope where the supplied type parameters [T1], [T2], and [T3] are bound to
 * [suppliedType1], [suppliedType2], and [suppliedType3], respectively.
 *
 * Within [block], [suppliedTypeOf] calls and suppliable calls can use [T1], [T2], and [T3] as if
 * they were concrete types described by the corresponding [SuppliedType] arguments. This function is
 * an intrinsic stub replaced by the supplied types compiler plugin. If the plugin is not applied,
 * calling it throws [IllegalStateException].
 *
 * @param T1 The first supplied type parameter to bind in [block]. Must be specified as an explicit
 * type argument at the call site and must refer to a [Supply]-annotated type parameter visible in
 * the enclosing scope.
 * @param T2 The second supplied type parameter to bind in [block]. Must be specified as an explicit
 * type argument at the call site and must refer to a [Supply]-annotated type parameter visible in
 * the enclosing scope.
 * @param T3 The third supplied type parameter to bind in [block]. Must be specified as an explicit
 * type argument at the call site and must refer to a [Supply]-annotated type parameter visible in
 * the enclosing scope.
 * @param R The return type of [block], inferred from its result.
 * @param suppliedType1 The [SuppliedType] value to bind to [T1] for the duration of [block].
 * @param suppliedType2 The [SuppliedType] value to bind to [T2] for the duration of [block].
 * @param suppliedType3 The [SuppliedType] value to bind to [T3] for the duration of [block].
 * @param block The code to execute with [T1], [T2], and [T3] supplied. Invoked exactly once.
 * @return The result returned by [block].
 */
@Suppress("WRONG_INVOCATION_KIND", "unused")
public inline fun <T1, T2, T3, R> withSupplied(suppliedType1: SuppliedType, suppliedType2: SuppliedType, suppliedType3: SuppliedType, block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        returnsResultOf(block)
    }
    suppliedTypesPluginExceptionForRuntimeDeclarations()
}