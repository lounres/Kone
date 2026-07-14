/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name


class ContextsFakeValueParametersReplacementTransformer(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
) : IrElementTransformerVoid() {
    private val declarationSymbolsStack = mutableListOf<IrSymbol>()
    data class UsedOrUnwrappedExpression(
        val type: IrType,
        val producer: () -> IrExpression,
    )
    private val usedAndUnwrappedExpressionsStack = mutableListOf<MutableList<UsedOrUnwrappedExpression>>()
    
    private inline fun <T> withinScope(declaration: IrSymbolOwner, block: () -> T): T {
        declarationSymbolsStack.add(declaration.symbol)
        val result = block()
        declarationSymbolsStack.removeLast()
        return result
    }
    
    override fun visitFile(declaration: IrFile): IrFile =
        withinScope(declaration) { super.visitFile(declaration) }
    
    override fun visitClass(declaration: IrClass): IrStatement =
        withinScope(declaration) { super.visitClass(declaration) }
    
    override fun visitProperty(declaration: IrProperty): IrStatement =
        withinScope(declaration) { super.visitProperty(declaration) }
    
    override fun visitField(declaration: IrField): IrStatement =
        withinScope(declaration) { super.visitField(declaration) }
    
    override fun visitFunction(declaration: IrFunction): IrStatement =
        withinScope(declaration) { super.visitFunction(declaration) }
    
    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer): IrStatement =
        withinScope(declaration) { super.visitAnonymousInitializer(declaration) }
    
    override fun visitValueParameter(declaration: IrValueParameter): IrStatement =
        withinScope(declaration) { super.visitValueParameter(declaration) }
    
    override fun visitScript(declaration: IrScript): IrStatement =
        withinScope(declaration) { super.visitScript(declaration) }
    
    private inline fun <T> withinBlock(block: (MutableList<UsedOrUnwrappedExpression>) -> T): T {
        val expressions = mutableListOf<UsedOrUnwrappedExpression>()
        usedAndUnwrappedExpressionsStack.add(expressions)
        val result = block(expressions)
        usedAndUnwrappedExpressionsStack.removeLast()
        return result
    }
    
    companion object {
        private data class ClassSuperClassesAndTypeRealisation(
            val classSymbol: IrClassSymbol,
            val superClassSymbols: MutableSet<IrClassSymbol>,
            val type: IrSimpleType
        )
        private fun IrType.superClassTypes(): Map<IrClassSymbol, ClassSuperClassesAndTypeRealisation> = buildMap {
            data class TypeToCheck(
                val type: IrType,
                val subClassSymbols: Set<IrClassSymbol>,
            )
            val toCheck = ArrayDeque<TypeToCheck>()
            toCheck.addLast(TypeToCheck(this@superClassTypes, emptySet()))
            
            data class ClassMutableSuperClassesAndTypeRealisation(
                val classSymbol: IrClassSymbol,
                val superClassSymbols: MutableSet<IrClassSymbol>,
                val type: IrSimpleType
            )
            
            while (toCheck.isNotEmpty()) {
                val (type, subClassSymbols) = toCheck.removeLast()
                when (type) {
                    is IrDynamicType -> {}
                    is IrErrorType -> {}
                    is IrSimpleType -> when (val classSymbol = type.classifier) {
                        is IrScriptSymbol -> {}
                        is IrTypeParameterSymbol -> classSymbol.owner.superTypes.mapTo(toCheck) { TypeToCheck(it, subClassSymbols) }
                        is IrClassSymbol -> {
                            put(classSymbol, ClassMutableSuperClassesAndTypeRealisation(classSymbol, mutableSetOf(), type))
                            subClassSymbols.forEach { get(it)!!.superClassSymbols.add(classSymbol) }
                            val subClassSymbols = subClassSymbols + classSymbol
                            val substitutionMap = classSymbol.owner.typeParameters.map { it.symbol }.zip(type.arguments.map { it.typeOrNull ?: contextsIrPluginException() }).toMap()
                            classSymbol.owner.superTypes.mapTo(toCheck) { TypeToCheck(it.substitute(substitutionMap), subClassSymbols) }
                        }
                    }
                }
            }
        }.mapValues {
            ClassSuperClassesAndTypeRealisation(
                classSymbol = it.value.classSymbol,
                superClassSymbols = it.value.superClassSymbols,
                type = it.value.type,
            )
        }
        private data class PropertyOverriddenClassSuperClassesAndType(
            val propertySymbol: IrPropertySymbol,
            val overridden: Set<IrPropertySymbol>,
            val classSuperClassesAndTypeRealisation: ClassSuperClassesAndTypeRealisation
        )
        private fun IrType.allProperties(): Map<IrClassSymbol, Map<Name, PropertyOverriddenClassSuperClassesAndType>> {
            val classes = superClassTypes()
            
            data class PropertyMutableOverriddenClassSuperClassesAndType(
                val propertySymbol: IrPropertySymbol,
                val overridden: MutableSet<IrPropertySymbol>,
                val classSuperClassesAndTypeRealisation: ClassSuperClassesAndTypeRealisation
            )
            
            val classesProperties = classes.mapValues { (classSuperClassesAndTypeRealisation = value) ->
                classSuperClassesAndTypeRealisation.classSymbol.owner.properties
                    .filter {
                        val getter = it.getter
                        getter != null && getter.parameters.size == 1 && getter.parameters[0].kind == DispatchReceiver
                    }.associate {
                        it.name to PropertyMutableOverriddenClassSuperClassesAndType(
                            propertySymbol = it.symbol,
                            overridden = mutableSetOf(),
                            classSuperClassesAndTypeRealisation = classSuperClassesAndTypeRealisation,
                        )
                    }
            }
            for ([classSymbol, classSuperClassesAndTypeRealisation] in classes) {
                val classPropertyOverriddenClassSuperClassesAndType = classesProperties[classSymbol]!!
                for (superClassSymbol in classSuperClassesAndTypeRealisation.superClassSymbols) {
                    for ([superClassPropertyName, superClassPropertyOverriddenClassSuperClassesAndType] in classesProperties[superClassSymbol]!!) {
                        classPropertyOverriddenClassSuperClassesAndType[superClassPropertyName]?.overridden?.add(
                            superClassPropertyOverriddenClassSuperClassesAndType.propertySymbol,
                        )
                    }
                }
            }
            return classesProperties.mapValues {
                it.value.mapValues { (combination = value) ->
                    PropertyOverriddenClassSuperClassesAndType(
                        propertySymbol = combination.propertySymbol,
                        overridden = combination.overridden,
                        classSuperClassesAndTypeRealisation = combination.classSuperClassesAndTypeRealisation,
                    )
                }
            }
        }
    }
    
    private fun <T : IrStatementContainer> visitStatementContainer(container: T) = withinBlock { expressions ->
        val iterator = container.statements.listIterator()
        while (iterator.hasNext()) {
            val statement = iterator.next()
            val newStatement = statement.transform(this, null)
            if (newStatement is IrCall) when(newStatement.symbol) {
                irRuntimeReferences.useLocallyAsExtensionReceiversIrSimpleFunctionSymbol -> {
                    iterator.remove()
                    check(newStatement.arguments.size == 2)
                    val vararg = newStatement.arguments.last() as IrVararg
                    for (context in vararg.elements) {
                        if (context !is IrExpression) continue
                        val contextVariable = Scope(declarationSymbolsStack.last()).createTemporaryVariable(context)
                        iterator.add(contextVariable)
                        expressions.add(
                            UsedOrUnwrappedExpression(contextVariable.type) {
                                IrGetValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, contextVariable.symbol)
                            }
                        )
                    }
                }
//                irRuntimeReferences.useLocallyAsContextsIrSimpleFunctionSymbol -> {
//                    iterator.remove()
//                    check(newStatement.arguments.size == 2)
//                    val vararg = newStatement.arguments.last() as IrVararg
//                    for (context in vararg.elements) {
//                        if (context !is IrExpression) continue
//                        val contextVariable = Scope(declarationSymbolsStack.last()).createTemporaryVariable(context)
//                        iterator.add(contextVariable)
//                        expressions.add(
//                            UsedOrUnwrappedExpression(contextVariable.type) {
//                                IrGetValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, contextVariable.symbol)
//                            }
//                        )
//                    }
//                }
                irRuntimeReferences.unwrapLocallyAsExtensionReceiversIrSimpleFunctionSymbol -> {
                    iterator.remove()
                    check(newStatement.arguments.size == 2)
                    val vararg = newStatement.arguments.last() as IrVararg
                    for (holder in vararg.elements) {
                        if (holder !is IrExpression) continue
                        val holderVariable = Scope(declarationSymbolsStack.last()).createTemporaryVariable(holder)
                        iterator.add(holderVariable)
                        
                        when(val type = holder.type) {
                            is IrDynamicType -> {}
                            is IrErrorType -> {}
                            is IrSimpleType -> run {
                                val classesProperties = type.allProperties()
                                val possiblePropertiesToUnwrap = classesProperties.values.flatMap { it.values }
                                val decidingProperties = buildSet<IrPropertySymbol> {
                                    val markedProperties = possiblePropertiesToUnwrap.filter { it.propertySymbol.isInclude() || it.propertySymbol.isExclude() }
                                    markedProperties.forEach { add(it.propertySymbol) }
                                    markedProperties.forEach { it.overridden.forEach { override -> remove(override) } }
                                }
                                val topPossiblePropertiesToUnwrap = buildMap<IrPropertySymbol, PropertyOverriddenClassSuperClassesAndType> {
                                    possiblePropertiesToUnwrap.forEach { put(it.propertySymbol, it) }
                                    possiblePropertiesToUnwrap.forEach { it.overridden.forEach { override -> remove(override) } }
                                }
                                val propertiesToUnwrap = topPossiblePropertiesToUnwrap.values
                                    .groupBy { it.propertySymbol.owner.name }
                                    .filter { it.value.size == 1 }
                                    .values
                                    .map { it.single() }
                                    .filter { (it.overridden + it.propertySymbol).any { it in decidingProperties && it.isInclude() } }
                                
                                propertiesToUnwrap.mapNotNullTo(expressions) { (propertySymbol, classSuperClassesAndTypeRealisation) ->
                                    val (classSymbol, type) = classSuperClassesAndTypeRealisation
                                    val getter = propertySymbol.owner.getter ?: return@mapNotNullTo null
                                    val propertyReturnType = getter.returnType.substitute(classSymbol.owner.typeParameters, type.arguments.map { it.typeOrNull ?: contextsIrPluginException() })
                                    UsedOrUnwrappedExpression(propertyReturnType) {
                                        IrCallImpl.fromSymbolOwner(
                                            startOffset = UNDEFINED_OFFSET,
                                            endOffset = UNDEFINED_OFFSET,
                                            type = propertyReturnType,
                                            symbol = getter.symbol,
                                        ).apply {
                                            arguments[0] = IrGetValueImpl(
                                                UNDEFINED_OFFSET,
                                                UNDEFINED_OFFSET,
                                                holderVariable.symbol
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        container
    }
    
    override fun visitContainerExpression(expression: IrContainerExpression) = visitStatementContainer(expression)
    override fun visitBlockBody(body: IrBlockBody) = visitStatementContainer(body)
    
    override fun visitErrorCallExpression(expression: IrErrorCallExpression): IrExpression {
        if (expression.description !in fakeValueParametersErrorCallsDescriptions)
            return super.visitErrorCallExpression(expression)
        
        return usedAndUnwrappedExpressionsStack
            .asReversed()
            .firstNotNullOfOrNull { variables -> variables.lastOrNull { it.type == expression.type } }
            ?.producer?.invoke()
            ?: super.visitErrorCallExpression(expression)
    }
}