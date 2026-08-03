/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.ir

import dev.lounres.kone.plugin.contextsKeys.generateKoneContextKeyAnnotationClassId
import dev.lounres.kone.plugin.contextsKeys.impliedKeysRegistryBuilderClassId
import dev.lounres.kone.plugin.contextsKeys.impliedKeysRegistryClassId
import dev.lounres.kone.plugin.contextsKeys.impliedKeysRegistryFunctionCallableId
import dev.lounres.kone.plugin.contextsKeys.suppliedTypeRegistryKeyClassId
import dev.lounres.kone.plugin.suppliedTypes.*
import org.jetbrains.kotlin.backend.common.extensions.DeclarationFinder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.impl.IrAnnotationImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.*


class ContextsKeysIrPluginException(message: String? = null, reason: Throwable? = null) : RuntimeException(message, reason)
fun contextsKeysIrPluginException(message: String? = null, reason: Throwable? = null): Nothing = throw ContextsKeysIrPluginException(message, reason)

fun couldNotFindClass(classId: ClassId): Nothing = error("Could not find class '${classId.asFqNameString()}'")
fun couldNotFindCorrespondingCallable(callableId: CallableId): Nothing = error("Could not find corresponding callable '${callableId.asSingleFqName()}'.")
fun couldNotFindCorrespondingFunctionInClass(classId: ClassId, functionName: Name): Nothing = error("Could not find corresponding function '$functionName' in class '${classId.asFqNameString()}'.")
fun couldNotFindCorrespondingPropertyInClass(classId: ClassId, functionName: Name): Nothing = error("Could not find corresponding property '$functionName' in class '${classId.asFqNameString()}'.")

fun DeclarationFinder.referenceClassOrFail(classId: ClassId): IrClassSymbol =
    findClass(classId) ?: couldNotFindClass(classId)
inline fun DeclarationFinder.referenceFunctionThatOrFail(callableId: CallableId, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    findFunctions(callableId)
        .also {
            val filtered = it.filter(predicate)
            if (filtered.size != 1) error("${filtered.size}!!!\n${filtered.joinToString(separator = "") { "  $it\n" }}")
        }
        .singleOrNull(predicate) ?: couldNotFindCorrespondingCallable(callableId)
inline fun IrClassSymbol.referenceFunctionThatOrFail(name: Name, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    owner.declarations.asSequence().filterIsInstance<IrSimpleFunction>().map { it.symbol }.singleOrNull { it.owner.name == name && predicate(it) } ?: couldNotFindCorrespondingFunctionInClass(owner.classIdOrFail, name)
inline fun IrClassSymbol.referenceFunctionThatOrFail(name: String, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    referenceFunctionThatOrFail(Name.identifier(name), predicate)
inline fun IrClassSymbol.referencePropertyThatOrFail(name: Name, predicate: (IrPropertySymbol) -> Boolean = { true }): IrPropertySymbol =
    owner.declarations.asSequence().filterIsInstance<IrProperty>().map { it.symbol }.singleOrNull { it.owner.name == name && predicate(it) } ?: couldNotFindCorrespondingPropertyInClass(owner.classIdOrFail, name)
inline fun IrClassSymbol.referencePropertyThatOrFail(name: String, predicate: (IrPropertySymbol) -> Boolean = { true }): IrPropertySymbol =
    referencePropertyThatOrFail(Name.identifier(name), predicate)

val IrDeclarationWithName.fqName: FqName get() = fqNameWhenAvailable ?: error("Expected declaration with available FQ name")
val IrClass.fqNameStringForContextsKeys: String get() = fqNameWhenAvailable?.toString() ?: "${SpecialNames.LOCAL}.$name" // TODO: Think about local classes more.

val IrClass.isGenerateKey: Boolean get() = hasAnnotation(generateKoneContextKeyAnnotationClassId)

class IrRuntimeReferences(private val pluginContext: IrPluginContext) {
    private val finder = pluginContext.finderForBuiltins()
    
    // Kotlin
    val anyIrClassSymbol = pluginContext.irBuiltIns.anyClass
    val anyToStringIrSimpleFunctionSymbol = anyIrClassSymbol.referenceFunctionThatOrFail(Name.identifier("toString"))
    val function0IrClassSymbol = pluginContext.irBuiltIns.functionN(0)
    val function1IrClassSymbol = pluginContext.irBuiltIns.functionN(1)
//    val errorIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin"), callableName = Name.identifier("error"))) {
//        it.owner.parameters[0].type == pluginContext.irBuiltIns.anyType
//    }
    val runIrSimpleFunctionSymbol = finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin"), callableName = Name.identifier("run"))) {
        it.owner.parameters.size == 1
    }
    val readWritePropertyIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.properties"), topLevelName = Name.identifier("ReadWriteProperty")))
    val readWritePropertyGetValueIrSimpleFunctionSymbol = readWritePropertyIrClassSymbol.referenceFunctionThatOrFail("getValue")
    val readWritePropertySetValueIrSimpleFunctionSymbol = readWritePropertyIrClassSymbol.referenceFunctionThatOrFail("setValue")
    val listIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("List")))
    val listGetIrSimpleFunctionSymbol = listIrClassSymbol.referenceFunctionThatOrFail("get")
    val listOfIrSimpleFunction = finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))) { symbol ->
        val parameters = symbol.owner.parameters
        parameters.size == 1 && parameters[0].isVararg
    }
    val mapIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("Map")))
    val mapGetIrSimpleFunctionSymbol = mapIrClassSymbol.referenceFunctionThatOrFail("get")
    val mapOfIrSimpleFunction = finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("mapOf"))) { symbol ->
        val parameters = symbol.owner.parameters
        parameters.size == 1 && parameters[0].isVararg
    }
    val pairIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin"), topLevelName = Name.identifier("Pair")))
    val pairIrConstructorSymbol = pairIrClassSymbol.constructors.single()
    val lazyIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin"), topLevelName = Name.identifier("Lazy")))
    val lazyValueIrProperty = lazyIrClassSymbol.owner.declarations.first { it is IrProperty && it.name == Name.identifier("value") } as IrProperty
    val lazyValueGetterIrSimpleFunction = lazyValueIrProperty.getter!!
    val lazyIrSimpleFunction = finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin"), callableName = Name.identifier("lazy"))) {
        it.owner.parameters.size == 1
    }
    
    //  Library
    val suppliedTypeIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeClassClassId)
    val suppliedTypeIrType: IrType = suppliedTypeIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedTypeRegularIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeRegularClassClassId)
    val suppliedTypeRegularIrType: IrType = suppliedTypeRegularIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedTypeDynamicIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeDynamicSingletonClassId)
    val suppliedTypeDynamicIrType: IrType = suppliedTypeDynamicIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedProjectionIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedProjectionClassClassId)
    val suppliedProjectionRegularIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedProjectionRegularClassClassId)
    val suppliedProjectionStarIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedProjectionStarSingletonClassId)
    val suppliedTypesRegistryKeyIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeRegistryKeyClassId)
    val suppliedTypesRegistryKeyPrimaryConstructorIrConstructor: IrConstructor = suppliedTypesRegistryKeyIrClassSymbol.owner.primaryConstructor!!
    val impliedKeysRegistryIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(impliedKeysRegistryClassId)
    val impliedKeysRegistryBuilderIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(impliedKeysRegistryBuilderClassId)
    val impliedKeysRegistryBuilderImpliesSameIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = impliedKeysRegistryBuilderIrClassSymbol.referenceFunctionThatOrFail(Name.identifier("impliesSame"))
    val impliedKeysRegistryIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = finder.referenceFunctionThatOrFail(impliedKeysRegistryFunctionCallableId)
    // Public runtime
    
    // Private runtime
    val supplianceProvidedAnnotationIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(supplianceProvidedAnnotationClassId)
    fun newSupplianceProvidedAnnotation(): IrAnnotationImpl = IrAnnotationImpl.fromSymbolOwner(
        supplianceProvidedAnnotationIrClassSymbol.defaultType,
        supplianceProvidedAnnotationIrClassSymbol.constructors.single(),
    )
    val noSuppliedTypeParameterInClassStubIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(noSuppliedTypeParameterInClassStubSingletonClassId)
    val suppliedProjectionIrType: IrSimpleType = suppliedProjectionIrClassSymbol.defaultType
    val listOfSuppliedTypeIrType = listIrClassSymbol.createType(hasQuestionMark = false, arguments = listOf(suppliedTypeIrType))
    val listOfSuppliedProjectionIrType = listIrClassSymbol.createType(hasQuestionMark = false, arguments = listOf(suppliedProjectionIrType))
    val mapOfStringAndListOfSuppliedTypeIrType = mapIrClassSymbol.createType(hasQuestionMark = false, arguments = listOf(pluginContext.irBuiltIns.stringType, listOfSuppliedTypeIrType))
    val contextsKeysStoragePropertyReturnIrType: IrType = readWritePropertyIrClassSymbol.createType(
        hasQuestionMark = false,
        arguments = listOf(
            pluginContext.irBuiltIns.anyNType,
            mapOfStringAndListOfSuppliedTypeIrType,
        )
    )
    val pairOfStringAndListOfSuppliedTypeIrType = pairIrClassSymbol.createType(
        hasQuestionMark = false, arguments = listOf(
            pluginContext.irBuiltIns.stringType,
            listOfSuppliedTypeIrType
        )
    )
    val readWritePropertyOfNullableAnyAndMapOfStringAndListOfSuppliedTypeIrType = readWritePropertyIrClassSymbol.createType(hasQuestionMark = false, arguments = listOf(pluginContext.irBuiltIns.anyNType, mapOfStringAndListOfSuppliedTypeIrType))
}