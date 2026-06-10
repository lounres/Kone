/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.fiktion

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


const val koneFiktionPackageFQNameString = "dev.lounres.kone.fiktion"
const val fiktionImaginaryRelativeNameString = "Fiktion.Imaginary"
const val fiktionRealRelativeNameString = "Fiktion.Imaginary"
//const val suppliedTypeShortNameString = "SuppliedType"
//const val suppliedTypeRegularShortNameString = "Regular"
//const val suppliedTypeDynamicShortNameString = "Dynamic"
//const val suppliedProjectionShortNameString = "SuppliedProjection"
//const val suppliedProjectionRegularShortNameString = "Regular"
//const val suppliedProjectionStarShortNameString = "Star"
//const val supplyShortNameString = "Supply"
//const val suppliableShortNameString = "Suppliable"
//const val supplianceProvidedShortNameString = "SupplianceProvided"
//const val noSuppliedTypeParameterInClassStubShortNameString = "NoSuppliedTypeParameterInClassStub"
//const val suppliedTypesStorageDelegateShortNameString = "suppliedTypesStorageDelegate"
//const val suppliableClassShortNameString = "SuppliableClass"
//const val suppliedTypeOfShortNameString = "suppliedTypeOf"

val koneFiktionPackageFQName = FqName(koneFiktionPackageFQNameString)
val fiktionImaginaryRelativeName = FqName(fiktionImaginaryRelativeNameString)
val fiktionRealRelativeName = FqName(fiktionRealRelativeNameString)
//val suppliedTypeShortName = FqName(suppliedTypeShortNameString)
//val suppliedTypeRegularShortName = FqName("$suppliedTypeShortNameString.$suppliedTypeRegularShortNameString")
//val suppliedTypeDynamicShortName = FqName("$suppliedTypeShortNameString.$suppliedTypeDynamicShortNameString")
//val suppliedProjectionShortName = FqName(suppliedProjectionShortNameString)
//val suppliedProjectionRegularShortName = FqName("$suppliedProjectionShortNameString.$suppliedProjectionRegularShortNameString")
//val suppliedProjectionStarShortName = FqName("$suppliedProjectionShortNameString.$suppliedProjectionStarShortNameString")
//val supplyShortName = FqName(supplyShortNameString)
//val suppliableShortName = FqName(suppliableShortNameString)
//val supplianceProvidedShortName = FqName(supplianceProvidedShortNameString)
//val noSuppliedTypeParameterInClassStubShortName = FqName(noSuppliedTypeParameterInClassStubShortNameString)
//val suppliedTypesStorageDelegateShortName = Name.identifier(suppliedTypesStorageDelegateShortNameString)
//val suppliableClassShortName = FqName(suppliableClassShortNameString)
//val suppliedTypeOfName = Name.identifier(suppliedTypeOfShortNameString)

val fiktionImaginaryClassId = ClassId(
    packageFqName = koneFiktionPackageFQName,
    relativeClassName = fiktionImaginaryRelativeName,
    isLocal = false,
)
val fiktionRealClassId = ClassId(
    packageFqName = koneFiktionPackageFQName,
    relativeClassName = fiktionRealRelativeName,
    isLocal = false,
)
//val suppliedTypeClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = suppliedTypeShortName,
//    isLocal = false
//)
//val suppliedTypeRegularClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = suppliedTypeRegularShortName,
//    isLocal = false
//)
//val suppliedTypeDynamicClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = suppliedTypeDynamicShortName,
//    isLocal = false
//)
//val suppliedProjectionClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = suppliedProjectionShortName,
//    isLocal = false,
//)
//val suppliedProjectionRegularClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = suppliedProjectionRegularShortName,
//    isLocal = false,
//)
//val suppliedProjectionStarClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = suppliedProjectionStarShortName,
//    isLocal = false,
//)
//val supplyClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = supplyShortName,
//    isLocal = false
//)
//val suppliableClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = suppliableShortName,
//    isLocal = false
//)
//val supplianceProvidedClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = supplianceProvidedShortName,
//    isLocal = false
//)
//val noSuppliedTypeParameterInClassStubClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = noSuppliedTypeParameterInClassStubShortName,
//    isLocal = false,
//)
//val suppliedTypesStorageDelegateCallableId = CallableId(
//    packageName = koneSuppliedTypesPackageFQName,
//    className = null,
//    callableName = suppliedTypesStorageDelegateShortName,
//)
//val suppliableClassClassId = ClassId(
//    packageFqName = koneSuppliedTypesPackageFQName,
//    relativeClassName = suppliableClassShortName,
//    isLocal = false
//)
//val suppliedTypeOfCallableId = CallableId(
//    packageName = koneSuppliedTypesPackageFQName,
//    className = null,
//    callableName = suppliedTypeOfName,
//)