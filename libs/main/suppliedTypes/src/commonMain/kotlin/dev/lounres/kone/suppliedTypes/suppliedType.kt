/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.suppliedTypes

import kotlinx.serialization.Serializable
import kotlin.reflect.KVariance


@Target(AnnotationTarget.CONSTRUCTOR)
@RequiresOptIn(
    message = "SuppliedType is not intended for manual instantiation.",
    level = RequiresOptIn.Level.ERROR,
)
public annotation class DelicateSuppliedTypeConstructor

/**
 * Represents a fully defined type expression. Its instances are called "type suppliers".
 */
@Serializable
public sealed interface SuppliedType<T> {
    /**
     * Represents a regular type expression that is a classifier (class or interface) with type arguments.
     *
     * For now, this class's instances can be made only manually.
     * It is planned to make Kotlin compiler plugin that creates them automatically.
     *
     * @param fullyQualifiedName is a fully qualified name of the classifier.
     * @param typeArguments is a list of type arguments that are projections of other type suppliers.
     * @param isNullable defines if the type supplier describes nullable type.
     */
    @Serializable
    public class Regular<T> @DelicateSuppliedTypeConstructor constructor(
        public val fullyQualifiedName: String,
        public val typeArguments: List<SuppliedProjection>,
        public val isNullable: Boolean,
    ) : SuppliedType<T> {
        override fun toString(): String =
            "$fullyQualifiedName${
                if (typeArguments.isEmpty()) ""
                else "<${typeArguments.joinToString(separator = ", ")}>"
            }${if (isNullable) "?" else ""}"
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Regular<*>) return false
            
            if (fullyQualifiedName != other.fullyQualifiedName) return false
            if (typeArguments != other.typeArguments) return false
            if (isNullable != other.isNullable) return false
            return true
        }
        override fun hashCode(): Int = (fullyQualifiedName.hashCode() * 31 + typeArguments.hashCode()) * 31 + isNullable.hashCode()
    }
    
    /**
     * Represents dynamic type expression.
     */
    @Serializable
    public data object Dynamic : SuppliedType<Nothing> {
        override fun toString(): String = "dynamic"
    }
}

/**
 * Represents a type expression projection used as an argument for functions' and classifiers' type arguments.
 */
@Serializable
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
    @Serializable
    public data class Regular(val variance: KVariance, val type: SuppliedType<*>) : SuppliedProjection {
        override fun toString(): String =
            "${
                when (variance) {
                    KVariance.INVARIANT -> ""
                    KVariance.IN -> "in "
                    KVariance.OUT -> "out "
                }
            }$type"
    }
    
    /**
     * Represents star projection.
     */
    @Serializable
    public data object Star : SuppliedProjection {
        override fun toString(): String = "*"
    }
}

@Target(AnnotationTarget.TYPE_PARAMETER)
public annotation class Supplied(/*val parameterName: String = ""*/)

public fun <@Supplied T> suppliedTypeOf(): SuppliedType<T> =
    error("Intrinsic function call was not substituted. Be sure to apply supplied types compiler plugin.")