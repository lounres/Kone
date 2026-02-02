/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


const val koneSuppliedTypesPackageFQNameString = "dev.lounres.kone.suppliedTypes"
const val suppliedTypeShortNameString = "SuppliedType"
const val suppliedTypeRegularShortNameString = "Regular"
const val suppliedTypeDynamicShortNameString = "Dynamic"
const val suppliedProjectionShortNameString = "SuppliedProjection"
const val suppliedProjectionRegularShortNameString = "Regular"
const val suppliedProjectionStarShortNameString = "Star"
const val supplyShortNameString = "Supply"
const val suppliableShortNameString = "Suppliable"
//const val supplianceProvidedShortNameString = "SupplianceProvided"
const val suppliedTypeOfShortNameString = "suppliedTypeOf"

val koneSuppliedTypesPackageFQName = FqName(koneSuppliedTypesPackageFQNameString)
val suppliedTypeShortName = FqName(suppliedTypeShortNameString)
val suppliedTypeRegularShortName = FqName("$suppliedTypeShortNameString.$suppliedTypeRegularShortNameString")
val suppliedTypeDynamicShortName = FqName("$suppliedTypeShortNameString.$suppliedTypeDynamicShortNameString")
val suppliedProjectionShortName = FqName(suppliedProjectionShortNameString)
val suppliedProjectionRegularShortName = FqName("$suppliedProjectionShortNameString.$suppliedProjectionRegularShortNameString")
val suppliedProjectionStarShortName = FqName("$suppliedProjectionShortNameString.$suppliedProjectionStarShortNameString")
val supplyShortName = FqName(supplyShortNameString)
val suppliableShortName = FqName(suppliableShortNameString)
//val supplianceProvidedShortName = FqName(supplianceProvidedShortNameString)
val suppliedTypeOfName = Name.identifier(suppliedTypeOfShortNameString)

val suppliedTypeClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedTypeShortName,
    isLocal = false
)
val suppliedTypeRegularClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedTypeRegularShortName,
    isLocal = false
)
val suppliedTypeDynamicClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedTypeDynamicShortName,
    isLocal = false
)
val suppliedProjectionClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedProjectionShortName,
    isLocal = false,
)
val suppliedProjectionRegularClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedProjectionRegularShortName,
    isLocal = false,
)
val suppliedProjectionStarClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedProjectionStarShortName,
    isLocal = false,
)
val supplyClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = supplyShortName,
    isLocal = false
)
val suppliableClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliableShortName,
    isLocal = false
)
//val supplianceProvidedClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = supplianceProvidedShortName,
//    isLocal = false
//)
val suppliedTypeOfCallableId = CallableId(packageName = koneSuppliedTypesPackageFQName, className = null, callableName = suppliedTypeOfName)