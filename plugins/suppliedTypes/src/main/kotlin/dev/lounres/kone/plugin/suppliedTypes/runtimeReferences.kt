/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


const val koneRegistryPackageFQNameString = "dev.lounres.kone.suppliedTypes"
const val suppliedTypeShortNameString = "SuppliedType"
const val suppliedTypeRegularShortNameString = "Regular"
const val suppliedTypeDynamicShortNameString = "Dynamic"
const val suppliedProjectionShortNameString = "SuppliedProjection"
const val suppliedProjectionRegularShortNameString = "Regular"
const val suppliedProjectionStarShortNameString = "Star"
const val suppliedShortNameString = "Supplied"
const val suppliedTypeOfShortNameString = "suppliedTypeOf"

val koneRegistryPackageFQName = FqName(koneRegistryPackageFQNameString)
val suppliedTypeShortName = FqName(suppliedTypeShortNameString)
val suppliedTypeRegularShortName = FqName("$suppliedTypeShortNameString.$suppliedTypeRegularShortNameString")
val suppliedTypeDynamicShortName = FqName("$suppliedTypeShortNameString.$suppliedTypeDynamicShortNameString")
val suppliedProjectionShortName = FqName(suppliedProjectionShortNameString)
val suppliedProjectionRegularShortName = FqName("$suppliedProjectionShortNameString.$suppliedProjectionRegularShortNameString")
val suppliedProjectionStarShortName = FqName("$suppliedProjectionShortNameString.$suppliedProjectionStarShortNameString")
val suppliedShortName = FqName(suppliedShortNameString)
val suppliedTypeOfName = Name.identifier(suppliedTypeOfShortNameString)

val suppliedTypeClassId = ClassId(
    packageFqName = koneRegistryPackageFQName,
    relativeClassName = suppliedTypeShortName,
    isLocal = false
)
val suppliedTypeRegularClassId = ClassId(
    packageFqName = koneRegistryPackageFQName,
    relativeClassName = suppliedTypeRegularShortName,
    isLocal = false
)
val suppliedTypeDynamicClassId = ClassId(
    packageFqName = koneRegistryPackageFQName,
    relativeClassName = suppliedTypeDynamicShortName,
    isLocal = false
)
val suppliedProjectionClassId = ClassId(
    packageFqName = koneRegistryPackageFQName,
    relativeClassName = suppliedProjectionShortName,
    isLocal = false,
)
val suppliedProjectionRegularClassId = ClassId(
    packageFqName = koneRegistryPackageFQName,
    relativeClassName = suppliedProjectionRegularShortName,
    isLocal = false,
)
val suppliedProjectionStarClassId = ClassId(
    packageFqName = koneRegistryPackageFQName,
    relativeClassName = suppliedProjectionStarShortName,
    isLocal = false,
)
val suppliedClassId = ClassId(
    packageFqName = koneRegistryPackageFQName,
    relativeClassName = suppliedShortName,
    isLocal = false
)
val suppliedTypeOfCallableId = CallableId(packageName = koneRegistryPackageFQName, className = null, callableName = suppliedTypeOfName)