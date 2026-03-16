/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import kotlin.collections.set


class SuppliabilityCollectionVisitor(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
): IrVisitorVoid() {
    val functionsSuppliableToSupplianceMapping: Map<IrSimpleFunction, IrSimpleFunction>
        field: MutableMap<IrSimpleFunction, IrSimpleFunction> = mutableMapOf()
    val functionsSupplianceToSuppliableMapping: Map<IrSimpleFunction, IrSimpleFunction>
        field: MutableMap<IrSimpleFunction, IrSimpleFunction> = mutableMapOf()
    val constructorsSuppliableToSupplianceMapping: Map<IrConstructor, IrConstructor>
        field: MutableMap<IrConstructor, IrConstructor> = mutableMapOf()
    val constructorsSupplianceToSuppliableMapping: Map<IrConstructor, IrConstructor>
        field: MutableMap<IrConstructor, IrConstructor> = mutableMapOf()
    
    fun checkSuppliable(function: IrSimpleFunction): Boolean {
        if (!function.isSuppliable) return false
        val suppliableTypeParameters = function.typeParameters.filter { it.isSupply }
        if (suppliableTypeParameters.isEmpty()) return false
        return true
    }
    fun checkSuppliance(function: IrSimpleFunction): Boolean = function.isSupplianceProvided
    fun checkSuppliable(constructor: IrConstructor): Boolean {
        if (constructor.isSupplianceProvided) return false
        val classSymbol = constructor.parentAsClass
        if (!classSymbol.isSuppliable) return false
        val suppliableTypeParameters = classSymbol.typeParameters.filter { it.isSupply }
        if (suppliableTypeParameters.isEmpty()) return false
        return true
    }
    fun checkSuppliance(constructor: IrConstructor): Boolean = constructor.isSupplianceProvided
    
    fun mapSuppliableToSuppliance(function: IrSimpleFunction): IrSimpleFunction =
        functionsSuppliableToSupplianceMapping[function] ?: error(TODO())
    fun mapSupplianceToSuppliable(function: IrSimpleFunction): IrSimpleFunction =
        functionsSupplianceToSuppliableMapping[function] ?: error(TODO())
    fun mapSuppliableToSuppliance(constructor: IrConstructor): IrConstructor =
        constructorsSuppliableToSupplianceMapping[constructor] ?: error(TODO())
    fun mapSupplianceToSuppliable(constructor: IrConstructor): IrConstructor =
        constructorsSupplianceToSuppliableMapping[constructor] ?: error(TODO())
    
    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }
    
    override fun visitSimpleFunction(declaration: IrSimpleFunction) {
        if (checkSuppliance(declaration)) {
            val parametersBeforeSuppliance = declaration.parameters.filter { !it.isSupplianceProvided }.map { it.type }
            
            val parent = declaration.parent as? IrDeclarationContainer ?: error(TODO())
            
            val suppliableDeclaration = parent.declarations.single {
                it is IrSimpleFunction && it.parameters.map { it.type } == parametersBeforeSuppliance
            } as IrSimpleFunction
            
            functionsSuppliableToSupplianceMapping[suppliableDeclaration] = declaration
            functionsSupplianceToSuppliableMapping[declaration] = suppliableDeclaration
        }
        
        super.visitSimpleFunction(declaration)
    }
    
    override fun visitConstructor(declaration: IrConstructor) {
        if (checkSuppliance(declaration)) {
            val parametersBeforeSuppliance = declaration.parameters.filter { !it.isSupplianceProvided }.map { it.type }
            
            val classSymbol = declaration.parentAsClass
            
            val suppliableDeclaration = classSymbol.constructors.single {
                it.parameters.map { it.type } == parametersBeforeSuppliance
            }
            
            constructorsSuppliableToSupplianceMapping[suppliableDeclaration] = declaration
            constructorsSupplianceToSuppliableMapping[declaration] = suppliableDeclaration
        }
        
        super.visitConstructor(declaration)
    }
}