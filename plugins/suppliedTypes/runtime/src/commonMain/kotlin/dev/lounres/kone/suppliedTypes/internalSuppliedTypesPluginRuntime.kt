/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused")

package dev.lounres.kone.suppliedTypes


private class SuppliedTypesPluginException(message: String? = null, reason: Throwable? = null) : IllegalStateException(message, reason)

@PublishedApi
internal fun suppliedTypesPluginExceptionForRuntimeDeclarations(): Nothing = throw SuppliedTypesPluginException(suppliedTypesPluginExceptionForRuntimeDeclarationsMessage)

@Deprecated(message = internalsDeprecationAnnotationMessage, level = HIDDEN)
public fun suppliedTypesPluginExceptionForPluginMachinery(message: String? = null, reason: Throwable? = null): Nothing = throw SuppliedTypesPluginException(message, reason)

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
)
@Retention(AnnotationRetention.BINARY)
internal annotation class SupplianceProvided

@Deprecated(message = internalsDeprecationAnnotationMessage, level = HIDDEN)
public object NoSuppliedTypeParameterInClassStub

@Deprecated(message = internalsDeprecationAnnotationMessage, level = HIDDEN)
public class SuppliedTypesStorageInitializationException(message: String? = null, reason: Throwable? = null) : IllegalStateException(message, reason)

@Deprecated(message = internalsDeprecationAnnotationMessage, level = HIDDEN)
public const val suppliedTypesStorageDelegateInitializationExceptionForGetter: String = "Supplied types storage is not yet initialized."

@Deprecated(message = internalsDeprecationAnnotationMessage, level = HIDDEN)
public const val suppliedTypesStorageDelegateInitializationExceptionForSetter: String = "Supplied types storage is already initialized."