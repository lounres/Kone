/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.DeclarationFinder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.callableId
import org.jetbrains.kotlin.ir.util.classIdOrFail
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.ir.visitors.IrVisitor
import org.jetbrains.kotlin.name.Name


class SuppliabilityMapper(
    private val irRuntimeReferences: IrRuntimeReferences,
    private val declarationFinder: DeclarationFinder,
    val moduleFunctionsSuppliableToSupplianceMapping: Map<IrSimpleFunction, IrSimpleFunction>,
    val moduleFunctionsSupplianceToSuppliableMapping: Map<IrSimpleFunction, IrSimpleFunction>,
    val moduleConstructorsSuppliableToSupplianceMapping: Map<IrConstructor, IrConstructor>,
    val moduleConstructorsSupplianceToSuppliableMapping: Map<IrConstructor, IrConstructor>,
) {
    val externalFunctionsSuppliableToSupplianceMapping: Map<IrSimpleFunction, IrSimpleFunction>
        field: MutableMap<IrSimpleFunction, IrSimpleFunction> = mutableMapOf()
    val externalFunctionsSupplianceToSuppliableMapping: Map<IrSimpleFunction, IrSimpleFunction>
        field: MutableMap<IrSimpleFunction, IrSimpleFunction> = mutableMapOf()
    val externalConstructorsSuppliableToSupplianceMapping: Map<IrConstructor, IrConstructor>
        field: MutableMap<IrConstructor, IrConstructor> = mutableMapOf()
    val externalConstructorsSupplianceToSuppliableMapping: Map<IrConstructor, IrConstructor>
        field: MutableMap<IrConstructor, IrConstructor> = mutableMapOf()
        
    val moduleSuppliableFunctions: Set<IrSimpleFunction> get() = moduleFunctionsSuppliableToSupplianceMapping.keys
    val moduleSupplianceFunctions: Set<IrSimpleFunction> get() = moduleFunctionsSupplianceToSuppliableMapping.keys
    val moduleSuppliableConstructors: Set<IrConstructor> get() = moduleConstructorsSuppliableToSupplianceMapping.keys
    val moduleSupplianceConstructors: Set<IrConstructor> get() = moduleConstructorsSupplianceToSuppliableMapping.keys
    
    private val IrSimpleFunction.isReallySuppliable: Boolean get() = isSuppliable && typeParameters.any { it.isSupply } && symbol != irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol
    private val IrSimpleFunction.isReallySuppliance: Boolean get() = isSupplianceProvided
    private val IrConstructor.isReallySuppliable: Boolean get() = !isSupplianceProvided && parentAsClass.let { it.isSuppliable && it.kind in listOf<ClassKind>(CLASS) }
    private val IrConstructor.isReallySuppliance: Boolean get() = isSupplianceProvided
    
    fun mapSuppliableToSupplianceOrNull(function: IrSimpleFunction): IrSimpleFunction? =
        if (!function.isReallySuppliable) null
        else moduleFunctionsSuppliableToSupplianceMapping[function] ?:
        externalFunctionsSuppliableToSupplianceMapping.getOrPut(function) {
            val parameters = function.parameters
            val typeParameters = function.typeParameters
            val candidates = declarationFinder.findFunctions(function.callableId).map { it.owner }.filter {
                if (!it.isReallySuppliance) return@filter false
                val otherParameters = it.parameters.filter { !it.isSupplianceProvided }
                val otherTypeParameters = it.typeParameters
                if (parameters.size != otherParameters.size || typeParameters.size != otherTypeParameters.size) return@filter false
                val typeParametersSubstitution = typeParameters.map { it.symbol }.zip(otherTypeParameters.map { it.defaultType }).toMap()
                for (index in parameters.indices) {
                    val parameter = parameters[index]
                    val otherParameter = otherParameters[index]
                    if (parameter.kind != otherParameter.kind || parameter.type.substitute(typeParametersSubstitution) != otherParameter.type) return@filter false
                }
                true
            }
            check(candidates.isNotEmpty()) { "Did not receive any suppliance of function.\n  CallableId: ${function.callableId}\n  Suppliable: ${function.symbol}" }
            check(candidates.size <= 1) { "Received several suppliances of function.\n  CallableId: ${function.callableId}\n  Suppliable: ${function.symbol}\n  Suppliances:${candidates.joinToString(separator = "") { "\n    ${it.symbol}" }}" }
            candidates.first().also { externalFunctionsSupplianceToSuppliableMapping[it] = function }
        }
    fun mapSupplianceToSuppliableOrNull(function: IrSimpleFunction): IrSimpleFunction? =
        if (!function.isReallySuppliance) null
        else moduleFunctionsSupplianceToSuppliableMapping[function] ?:
        externalFunctionsSupplianceToSuppliableMapping.getOrPut(function) {
            val argumentTypes = function.parameters.filter { !it.isSupplianceProvided }.map { it.type }
            declarationFinder.findFunctions(function.callableId).map { it.owner }.single {
                it.isReallySuppliable && it.parameters.map { it.type } == argumentTypes
            }.also { externalFunctionsSuppliableToSupplianceMapping[it] = function }
        }
    fun mapSuppliableToSupplianceOrNull(constructor: IrConstructor): IrConstructor? =
        if (!constructor.isReallySuppliable) null
        else moduleConstructorsSuppliableToSupplianceMapping[constructor] ?:
        externalConstructorsSuppliableToSupplianceMapping.getOrPut(constructor) {
            val argumentTypes = constructor.parameters.map { it.type }
            declarationFinder.findConstructors(constructor.parentAsClass.classIdOrFail).map { it.owner }.single {
                it.isReallySuppliance && it.parameters.filter { !it.isSupplianceProvided }.map { it.type } == argumentTypes
            }.also { externalConstructorsSupplianceToSuppliableMapping[it] = constructor }
        }
    fun mapSupplianceToSuppliableOrNull(constructor: IrConstructor): IrConstructor? =
        if(!constructor.isReallySuppliance) null
        else moduleConstructorsSupplianceToSuppliableMapping[constructor] ?:
        externalConstructorsSupplianceToSuppliableMapping.getOrPut(constructor) {
            val argumentTypes = constructor.parameters.filter { !it.isSupplianceProvided }.map { it.type }
            declarationFinder.findConstructors(constructor.parentAsClass.classIdOrFail).map { it.owner }.single {
                it.isReallySuppliable && it.parameters.map { it.type } == argumentTypes
            }.also { externalConstructorsSuppliableToSupplianceMapping[it] = constructor }
        }
    
    fun mapSuppliableToSuppliance(function: IrSimpleFunction): IrSimpleFunction =
        mapSuppliableToSupplianceOrNull(function = function) ?: TODO()
    fun mapSupplianceToSuppliable(function: IrSimpleFunction): IrSimpleFunction =
        mapSupplianceToSuppliableOrNull(function = function) ?: TODO()
    fun mapSuppliableToSuppliance(constructor: IrConstructor): IrConstructor =
        mapSuppliableToSupplianceOrNull(constructor = constructor) ?: TODO()
    fun mapSupplianceToSuppliable(constructor: IrConstructor): IrConstructor =
        mapSupplianceToSuppliableOrNull(constructor = constructor) ?: TODO()
}

private sealed interface Level {
    data object Top : Level
    data object Body : Level
    data class Declaration(val declaration: IrDeclaration) : Level
}

private data class IrSimpleFunctionSignature(
    val name: Name,
    val arguments: List<IrType>,
)
private typealias IrConstructorSignature = List<IrType>

private class IrSimpleFunctionSuppliabilityScope {
    val suppliable: Map<IrSimpleFunctionSignature, IrSimpleFunction>
        field: MutableMap<IrSimpleFunctionSignature, IrSimpleFunction> = mutableMapOf()
    val suppliance: Map<IrSimpleFunctionSignature, IrSimpleFunction>
        field: MutableMap<IrSimpleFunctionSignature, IrSimpleFunction> = mutableMapOf()
    
    private val IrSimpleFunction.suppliableSignature: IrSimpleFunctionSignature
        get() = IrSimpleFunctionSignature(
            name = this.name,
            arguments = this.parameters.map { it.type }
        )
    private val IrSimpleFunction.supplianceSignature: IrSimpleFunctionSignature
        get() = IrSimpleFunctionSignature(
            name = this.name,
            arguments = parameters.filter { !it.isSupplianceProvided }.map { it.type },
        )
    
    fun addSuppliable(function: IrSimpleFunction) {
        val signature = function.suppliableSignature
        check(signature !in suppliable) { TODO() }
        suppliable[signature] = function
    }
    fun addSuppliance(function: IrSimpleFunction) {
        val signature = function.supplianceSignature
        check(signature !in suppliance) { TODO() }
        suppliance[signature] = function
    }
}
private class IrConstructorSuppliabilityScope {
    val suppliable: Map<IrConstructorSignature, IrConstructor>
        field: MutableMap<IrConstructorSignature, IrConstructor> = mutableMapOf()
    val suppliance: Map<IrConstructorSignature, IrConstructor>
        field: MutableMap<IrConstructorSignature, IrConstructor> = mutableMapOf()
    
    private val IrConstructor.suppliableSignature: IrConstructorSignature
        get() = this.parameters.map { it.type }
    private val IrConstructor.supplianceSignature: IrConstructorSignature
        get() = parameters.filter { !it.isSupplianceProvided }.map { it.type }
    
    fun addSuppliable(function: IrConstructor) {
        val signature = function.suppliableSignature
        check(signature !in suppliable) { TODO() }
        suppliable[signature] = function
    }
    fun addSuppliance(function: IrConstructor) {
        val signature = function.supplianceSignature
        check(signature !in suppliance) { TODO() }
        suppliance[signature] = function
    }
}

fun SuppliabilityMapper(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
): SuppliabilityMapper {
    fun IrSimpleFunction.isReallySuppliable(): Boolean = isSuppliable && typeParameters.any { it.isSupply } && this != irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol
    fun IrSimpleFunction.isReallySuppliance(): Boolean = isSupplianceProvided
    fun IrConstructor.isReallySuppliable(): Boolean = !isSupplianceProvided && parentAsClass.let { it.isSuppliable && it.kind in listOf<ClassKind>(CLASS) }
    fun IrConstructor.isReallySuppliance(): Boolean = isSupplianceProvided
    
    val moduleScopeToSuppliabilityFunctions = mutableMapOf<IrDeclaration, IrSimpleFunctionSuppliabilityScope>()
    val moduleScopeToSuppliabilityConstructors = mutableMapOf<IrClass, IrConstructorSuppliabilityScope>()
    
    val moduleTopLevelSuppliabilityFunctions = IrSimpleFunctionSuppliabilityScope()
    
    val moduleLocalSuppliableFunctions = mutableSetOf<IrSimpleFunction>()
    
    moduleFragment.accept(
        object : IrVisitor<Unit, Level>() {
            override fun visitElement(element: IrElement, data: Level) {
                element.acceptChildren(this, data)
            }
            
            override fun visitDeclaration(declaration: IrDeclarationBase, data: Level) {
                super.visitDeclaration(declaration, Level.Declaration(declaration))
            }
            
            override fun visitBody(body: IrBody, data: Level) {
                super.visitBody(body, Level.Body)
            }
            
            override fun visitSimpleFunction(declaration: IrSimpleFunction, data: Level) {
                when (data) {
                    is Level.Declaration ->
                        when {
                            declaration.isReallySuppliance() -> moduleScopeToSuppliabilityFunctions.getOrPut(data.declaration) { IrSimpleFunctionSuppliabilityScope() }.addSuppliance(declaration)
                            declaration.isReallySuppliable() -> moduleScopeToSuppliabilityFunctions.getOrPut(data.declaration) { IrSimpleFunctionSuppliabilityScope() }.addSuppliable(declaration)
                        }
                    Level.Body ->
                        when {
                            declaration.isReallySuppliance() -> TODO()
                            declaration.isReallySuppliable() -> moduleLocalSuppliableFunctions.add(declaration)
                        }
                    Level.Top ->
                        when {
                            declaration.isReallySuppliance() -> moduleTopLevelSuppliabilityFunctions.addSuppliance(declaration)
                            declaration.isReallySuppliable() -> moduleTopLevelSuppliabilityFunctions.addSuppliable(declaration)
                        }
                }
                
                super.visitSimpleFunction(declaration, data)
            }
            
            override fun visitConstructor(declaration: IrConstructor, data: Level) {
                check(data is Level.Declaration && data.declaration == declaration.parent && data.declaration is IrClass) { TODO() }
                
                when {
                    declaration.isReallySuppliance() -> moduleScopeToSuppliabilityConstructors.getOrPut(data.declaration) { IrConstructorSuppliabilityScope() }.addSuppliance(declaration)
                    declaration.isReallySuppliable() -> moduleScopeToSuppliabilityConstructors.getOrPut(data.declaration) { IrConstructorSuppliabilityScope() }.addSuppliable(declaration)
                }
                
                super.visitConstructor(declaration, data)
            }
        },
        Level.Top,
    )
    
    val moduleFunctionsSuppliableToSupplianceMapping = mutableMapOf<IrSimpleFunction, IrSimpleFunction>()
    val moduleFunctionsSupplianceToSuppliableMapping = mutableMapOf<IrSimpleFunction, IrSimpleFunction>()
    val moduleConstructorsSuppliableToSupplianceMapping = mutableMapOf<IrConstructor, IrConstructor>()
    val moduleConstructorsSupplianceToSuppliableMapping = mutableMapOf<IrConstructor, IrConstructor>()
    
    for (scope in moduleScopeToSuppliabilityFunctions.values) {
        check(scope.suppliable.size == scope.suppliance.size) { TODO() }
        for ([signature, suppliable] in scope.suppliable) {
            val suppliance = scope.suppliance[signature]
            check(suppliance != null) { TODO() }
            check(suppliable !in moduleFunctionsSuppliableToSupplianceMapping) { TODO() }
            check(suppliance !in moduleFunctionsSupplianceToSuppliableMapping) { TODO() }
            moduleFunctionsSuppliableToSupplianceMapping[suppliable] = suppliance
            moduleFunctionsSupplianceToSuppliableMapping[suppliance] = suppliable
        }
    }
    check(moduleTopLevelSuppliabilityFunctions.suppliable.size == moduleTopLevelSuppliabilityFunctions.suppliance.size) { TODO() }
    for ([signature, suppliable] in moduleTopLevelSuppliabilityFunctions.suppliable) {
        val suppliance = moduleTopLevelSuppliabilityFunctions.suppliance[signature]
        check(suppliance != null) { TODO() }
        check(suppliable !in moduleFunctionsSuppliableToSupplianceMapping) { TODO() }
        check(suppliance !in moduleFunctionsSupplianceToSuppliableMapping) { TODO() }
        moduleFunctionsSuppliableToSupplianceMapping[suppliable] = suppliance
        moduleFunctionsSupplianceToSuppliableMapping[suppliance] = suppliable
    }
    for (scope in moduleScopeToSuppliabilityConstructors.values) {
        check(scope.suppliable.size == scope.suppliance.size) { TODO() }
        for ([signature, suppliable] in scope.suppliable) {
            val suppliance = scope.suppliance[signature]
            check(suppliance != null) { TODO() }
            check(suppliable !in moduleConstructorsSuppliableToSupplianceMapping) { TODO() }
            check(suppliance !in moduleConstructorsSupplianceToSuppliableMapping) { TODO() }
            moduleConstructorsSuppliableToSupplianceMapping[suppliable] = suppliance
            moduleConstructorsSupplianceToSuppliableMapping[suppliance] = suppliable
        }
    }
    
    // TODO: Think about moduleLocalSuppliableFunctions
    
    return SuppliabilityMapper(
        irRuntimeReferences = irRuntimeReferences,
        declarationFinder = pluginContext.finderForBuiltins(),
        moduleFunctionsSuppliableToSupplianceMapping = moduleFunctionsSuppliableToSupplianceMapping,
        moduleFunctionsSupplianceToSuppliableMapping = moduleFunctionsSupplianceToSuppliableMapping,
        moduleConstructorsSuppliableToSupplianceMapping = moduleConstructorsSuppliableToSupplianceMapping,
        moduleConstructorsSupplianceToSuppliableMapping = moduleConstructorsSupplianceToSuppliableMapping,
    )
}