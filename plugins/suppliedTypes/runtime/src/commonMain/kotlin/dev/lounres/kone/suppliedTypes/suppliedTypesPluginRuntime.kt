/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.suppliedTypes

import kotlin.concurrent.atomics.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


@Target(AnnotationTarget.TYPE_PARAMETER)
public annotation class Supply(/*val parameterName: String = ""*/)

@Target(
    AnnotationTarget.FUNCTION,
//    AnnotationTarget.PROPERTY,
    AnnotationTarget.CLASS,
)
public annotation class Suppliable

@Deprecated(
    message = "Internal supplied types API.",
    level = DeprecationLevel.HIDDEN,
)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
)
@Retention(AnnotationRetention.BINARY)
private annotation class SupplianceProvided

private class SuppliedTypesStorageDelegate : ReadWriteProperty<Any?, Map<String, List<SuppliedType>>> {
    private val field = AtomicReference<Map<String, List<SuppliedType>>?>(null)
    override fun getValue(thisRef: Any?, property: KProperty<*>): Map<String, List<SuppliedType>> =
        field.load() ?: error("Supplied types storage is not yet initialized.")
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Map<String, List<SuppliedType>>) {
        if (!field.compareAndSet(null, value)) error("Supplied types storage is already initialized.")
    }
}

@Deprecated(
    message = "Internal supplied types API.",
    level = DeprecationLevel.HIDDEN,
)
public fun suppliedTypesStorageDelegate(): ReadWriteProperty<Any?, Map<String, List<SuppliedType>>> = SuppliedTypesStorageDelegate()

@Suppliable
public interface SuppliableClass {
    @Deprecated(
        message = "Internal supplied types API.",
        level = DeprecationLevel.HIDDEN,
    )
    public var suppliedTypesStorage: Map<String, List<SuppliedType>>
    
    public fun afterSuppliance() {}
}

@Suppliable
public fun <@Supply T> suppliedTypeOf(): SuppliedType =
    error("Intrinsic function call was not substituted. Be sure to apply supplied types compiler plugin.")