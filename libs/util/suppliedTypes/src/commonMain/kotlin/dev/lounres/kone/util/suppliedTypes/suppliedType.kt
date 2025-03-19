/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.suppliedTypes

import kotlin.reflect.KClass
import kotlin.reflect.KVariance


//@Target(AnnotationTarget.CONSTRUCTOR)
//@RequiresOptIn(
//    message = "SuppliedType is not intended for manual instantiation.",
//    level = RequiresOptIn.Level.ERROR,
//)
//public annotation class DelicateSuppliedTypeConstructor

public sealed interface SuppliedType<T> {
    public data class Regular<T> /*@DelicateSuppliedTypeConstructor constructor*/(
        public val kClass: KClass<*>,
        public val typeArguments: List<SuppliedProjection>,
        public val isNullable: Boolean,
    ) : SuppliedType<T>
    public data object Dynamic : SuppliedType<Nothing>
}

public sealed interface SuppliedProjection {
    public data class Regular(val variance: KVariance, val type: SuppliedType<*>) : SuppliedProjection
    public data object Star : SuppliedProjection
}

//@Target(AnnotationTarget.TYPE_PARAMETER)
//public annotation class Supplied
//
//public fun <@Supplied T> suppliedTypeOf(): SuppliedType<T> = error("Intrinsic function call!!!")