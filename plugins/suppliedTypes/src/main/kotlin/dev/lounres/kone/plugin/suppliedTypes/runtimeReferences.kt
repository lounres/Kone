/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


const val koneSuppliedTypesPackageFQNameString = "dev.lounres.kone.suppliedTypes"
// Library
const val suppliedTypeClassShortNameString = "SuppliedType"
const val suppliedTypeRegularClassShortNameString = "Regular"
const val suppliedTypeDynamicSingletonShortNameString = "Dynamic"
const val suppliedProjectionClassShortNameString = "SuppliedProjection"
const val suppliedProjectionRegularClassShortNameString = "Regular"
const val suppliedProjectionStarSingletonShortNameString = "Star"
// Public runtime
const val supplyAnnotationShortNameString = "Supply"
const val suppliableAnnotationShortNameString = "Suppliable"
const val suppliableClassClassShortNameString = "SuppliableClass"
const val suppliedTypeOfFunctionShortNameString = "suppliedTypeOf"
//const val supplyFunctionShortNameString = "supply"
const val withSuppliedFunctionShortNameString = "withSupplied"
// Private runtime
const val suppliedTypesPluginExceptionForRuntimeDeclarationsFunctionShortNameString = "suppliedTypesPluginExceptionForRuntimeDeclarations"
const val suppliedTypesPluginExceptionForPluginMachineryFunctionShortNameString = "suppliedTypesPluginExceptionForPluginMachinery"
const val supplianceProvidedAnnotationShortNameString = "SupplianceProvided"
const val noSuppliedTypeParameterInClassStubSingletonShortNameString = "NoSuppliedTypeParameterInClassStub"
const val suppliedTypesStorageDelegateFunctionShortNameString = "suppliedTypesStorageDelegate"

val koneSuppliedTypesPackageFQName = FqName(koneSuppliedTypesPackageFQNameString)
// Library
val suppliedTypeClassShortName = FqName(suppliedTypeClassShortNameString)
val suppliedTypeRegularClassShortName = FqName("$suppliedTypeClassShortNameString.$suppliedTypeRegularClassShortNameString")
val suppliedTypeDynamicSingletonShortName = FqName("$suppliedTypeClassShortNameString.$suppliedTypeDynamicSingletonShortNameString")
val suppliedProjectionClassShortName = FqName(suppliedProjectionClassShortNameString)
val suppliedProjectionRegularClassShortName = FqName("$suppliedProjectionClassShortNameString.$suppliedProjectionRegularClassShortNameString")
val suppliedProjectionStarSingletonShortName = FqName("$suppliedProjectionClassShortNameString.$suppliedProjectionStarSingletonShortNameString")
// Public runtime
val supplyAnnotationShortName = FqName(supplyAnnotationShortNameString)
val suppliableAnnotationShortName = FqName(suppliableAnnotationShortNameString)
val suppliableClassClassShortName = FqName(suppliableClassClassShortNameString)
val suppliedTypeOfFunctionShortName = Name.identifier(suppliedTypeOfFunctionShortNameString)
//val supplyFunctionShortName = Name.identifier(supplyFunctionShortNameString)
val withSuppliedFunctionShortName = Name.identifier(withSuppliedFunctionShortNameString)
// Private runtime
val suppliedTypesPluginExceptionForRuntimeDeclarationsFunctionShortName = Name.identifier(suppliedTypesPluginExceptionForRuntimeDeclarationsFunctionShortNameString)
val suppliedTypesPluginExceptionForPluginMachineryFunctionShortName = Name.identifier(suppliedTypesPluginExceptionForPluginMachineryFunctionShortNameString)
val supplianceProvidedAnnotationShortName = FqName(supplianceProvidedAnnotationShortNameString)
val noSuppliedTypeParameterInClassStubSingletonShortName = FqName(noSuppliedTypeParameterInClassStubSingletonShortNameString)
val suppliedTypesStorageDelegateFunctionShortName = Name.identifier(suppliedTypesStorageDelegateFunctionShortNameString)

// Library
val suppliedTypeClassClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedTypeClassShortName,
    isLocal = false
)
val suppliedTypeRegularClassClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedTypeRegularClassShortName,
    isLocal = false
)
val suppliedTypeDynamicSingletonClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedTypeDynamicSingletonShortName,
    isLocal = false
)
val suppliedProjectionClassClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedProjectionClassShortName,
    isLocal = false,
)
val suppliedProjectionRegularClassClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedProjectionRegularClassShortName,
    isLocal = false,
)
val suppliedProjectionStarSingletonClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliedProjectionStarSingletonShortName,
    isLocal = false,
)
// Public runtime
val supplyAnnotationClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = supplyAnnotationShortName,
    isLocal = false
)
val suppliableAnnotationClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliableAnnotationShortName,
    isLocal = false
)
val suppliableClassClassClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = suppliableClassClassShortName,
    isLocal = false
)
val suppliedTypeOfFunctionCallableId = CallableId(
    packageName = koneSuppliedTypesPackageFQName,
    className = null,
    callableName = suppliedTypeOfFunctionShortName,
)
//val supplyFunctionCallableId = CallableId(
//    packageName = koneSuppliedTypesPackageFQName,
//    className = null,
//    callableName = supplyFunctionShortName,
//)
val withSuppliedFunctionCallableId = CallableId(
    packageName = koneSuppliedTypesPackageFQName,
    className = null,
    callableName = withSuppliedFunctionShortName,
)
// Private runtime
val suppliedTypesPluginExceptionForRuntimeDeclarationsFunctionCallableId = CallableId(
    packageName = koneSuppliedTypesPackageFQName,
    className = null,
    callableName = suppliedTypesPluginExceptionForRuntimeDeclarationsFunctionShortName,
)
val suppliedTypesPluginExceptionForPluginMachineryFunctionCallableId = CallableId(
    packageName = koneSuppliedTypesPackageFQName,
    className = null,
    callableName = suppliedTypesPluginExceptionForPluginMachineryFunctionShortName,
)
val supplianceProvidedAnnotationClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = supplianceProvidedAnnotationShortName,
    isLocal = false
)
val noSuppliedTypeParameterInClassStubSingletonClassId = ClassId(
    packageFqName = koneSuppliedTypesPackageFQName,
    relativeClassName = noSuppliedTypeParameterInClassStubSingletonShortName,
    isLocal = false,
)
val suppliedTypesStorageDelegateFunctionCallableId = CallableId(
    packageName = koneSuppliedTypesPackageFQName,
    className = null,
    callableName = suppliedTypesStorageDelegateFunctionShortName,
)