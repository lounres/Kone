/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused")

package dev.lounres.kone.suppliedTypes

import kotlin.concurrent.atomics.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


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

private class SuppliedTypesStorageDelegate : ReadWriteProperty<Any?, Map<String, List<SuppliedType>>> {
    private val field = AtomicReference<Map<String, List<SuppliedType>>?>(null)
    override fun getValue(thisRef: Any?, property: KProperty<*>): Map<String, List<SuppliedType>> =
        field.load() ?: throw InitializationException(suppliedTypesStorageDelegateInitializationExceptionForGetter)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Map<String, List<SuppliedType>>) {
        if (!field.compareAndSet(null, value)) throw InitializationException(suppliedTypesStorageDelegateInitializationExceptionForSetter)
    }
    
    private class InitializationException(message: String? = null, reason: Throwable? = null) : IllegalStateException(message, reason)
}

@Deprecated(message = internalsDeprecationAnnotationMessage, level = HIDDEN)
public fun suppliedTypesStorageDelegate(): ReadWriteProperty<Any?, Map<String, List<SuppliedType>>> = SuppliedTypesStorageDelegate()