/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.suppliedTypes

import kotlin.reflect.KClass
import kotlin.reflect.KVariance


//@Target(AnnotationTarget.CONSTRUCTOR)
//@RequiresOptIn(
//    message = "SuppliedType is not intended for manual instantiation.",
//    level = RequiresOptIn.Level.ERROR,
//)
//public annotation class DelicateSuppliedTypeConstructor

/**
 * Represents a fully defined type expression. Its instances are called "type suppliers".
 */
public sealed interface SuppliedType<T> {
    /**
     * Represents a regular type expression that is a classifier (class or interface) with type arguments.
     *
     * For now, this class's instances can be made only manually.
     * It is planned to make Kotlin compiler plugin that creates them automatically.
     *
     * @param kClass is a [KClass] of the classifier.
     * @param typeArguments is a list of type arguments that are projections of other type suppliers.
     * @param isNullable defines if the type supplier describes nullable type.
     */
    public data class Regular<T> /*@DelicateSuppliedTypeConstructor constructor*/(
        public val kClass: KClass<*>,
        public val typeArguments: List<SuppliedProjection>,
        public val isNullable: Boolean,
    ) : SuppliedType<T>
    
    /**
     * Represents dynamic type expression.
     */
    public data object Dynamic : SuppliedType<Nothing>
}

/**
 * Represents a type expression projection used as an argument for functions' and classifiers' type arguments.
 */
public sealed interface SuppliedProjection {
    /**
     * Represents a regular type expression projection that is a type supplier with defined variance.
     *
     * For now, this class's instances can be made only manually.
     * It is planned to make Kotlin compiler plugin that creates them automatically.
     *
     * @param variance is a variance of the type supplier.
     * @param type ia a backing type supplier.
     */
    public data class Regular(val variance: KVariance, val type: SuppliedType<*>) : SuppliedProjection
    
    /**
     * Represents star projection.
     */
    public data object Star : SuppliedProjection
}

//@Target(AnnotationTarget.TYPE_PARAMETER)
//public annotation class Supplied
//
//public fun <@Supplied T> suppliedTypeOf(): SuppliedType<T> = error("Intrinsic function call!!!")