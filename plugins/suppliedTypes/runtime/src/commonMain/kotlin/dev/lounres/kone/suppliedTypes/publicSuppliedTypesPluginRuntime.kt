/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.suppliedTypes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@Target(AnnotationTarget.TYPE_PARAMETER)
public annotation class Supply

@Target(
    AnnotationTarget.FUNCTION,
//    AnnotationTarget.PROPERTY,
    AnnotationTarget.CLASS,
)
public annotation class Suppliable

@Suppliable
public interface SuppliableClass {
    @Deprecated(message = internalsDeprecationAnnotationMessage, level = HIDDEN)
    public var suppliedTypesStorage: Map<String, List<SuppliedType>>
    
    public fun afterSuppliance() {}
}

// TODO: Replace with 'SuppliedType.Companion.of'
@Suppliable
public fun <@Supply T> suppliedTypeOf(): SuppliedType = suppliedTypesPluginExceptionForRuntimeDeclarations()

//public fun <T> supply(suppliedType: SuppliedType) {
//    suppliedTypesPluginExceptionForRuntimeDeclarations()
//}

@Suppress("WRONG_INVOCATION_KIND", "unused")
public inline fun <T1, R> withSupplied(suppliedType1: SuppliedType, block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        returnsResultOf(block)
    }
    suppliedTypesPluginExceptionForRuntimeDeclarations()
}

@Suppress("WRONG_INVOCATION_KIND", "unused")
public inline fun <T1, T2, R> withSupplied(suppliedType1: SuppliedType, suppliedType2: SuppliedType, block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        returnsResultOf(block)
    }
    suppliedTypesPluginExceptionForRuntimeDeclarations()
}

@Suppress("WRONG_INVOCATION_KIND", "unused")
public inline fun <T1, T2, T3, R> withSupplied(suppliedType1: SuppliedType, suppliedType2: SuppliedType, suppliedType3: SuppliedType, block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        returnsResultOf(block)
    }
    suppliedTypesPluginExceptionForRuntimeDeclarations()
}