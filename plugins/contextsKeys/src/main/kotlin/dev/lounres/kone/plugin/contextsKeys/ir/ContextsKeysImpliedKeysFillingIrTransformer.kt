/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.ir

import dev.lounres.kone.plugin.contextsKeys.generatedContextKeyName
import dev.lounres.kone.plugin.contextsKeys.registryKeyImpliedKeysPropertyShortName
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypesBuilder
import dev.lounres.kone.plugin.suppliedTypes.ir.fqNameStringForSuppliedTypes
import dev.lounres.kone.scope
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrValueParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import dev.lounres.kone.plugin.suppliedTypes.ir.IrRuntimeReferences as SuppliedTypesIrRuntimeReferences


class ContextsKeysImpliedKeysFillingIrTransformer(
    private val pluginContext: IrPluginContext,
    private val suppliedTypesIrRuntimeReferences: SuppliedTypesIrRuntimeReferences,
    private val irRuntimeReferences: IrRuntimeReferences,
) : IrTransformer<Nothing?>() {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "ContextsKeysImpliedKeysFillingIrTransformer.Key"
    }
    companion object {
        private val declarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(Key)
    }
    
    override fun visitProperty(declaration: IrProperty, data: Nothing?): IrStatement {
        scope {
            if (declaration.name != registryKeyImpliedKeysPropertyShortName) return@scope
            val constructedClass = declaration.parent as? IrClass ?: return@scope
            if (constructedClass.name != generatedContextKeyName) return@scope
            val containingClass = constructedClass.parent as? IrClass ?: return@scope
            if (!containingClass.isGenerateKey) return@scope
            val backingField = declaration.backingField
            val getter = declaration.getter
            check(backingField != null) { TODO() }
            check(getter != null) { TODO() }
            check(backingField.initializer == null) { TODO() }
            check(getter.body == null) { TODO() }
            backingField.type = irRuntimeReferences.lazyIrClassSymbol.typeWith(
                irRuntimeReferences.impliedKeysRegistryIrClassSymbol.typeWith(
                    containingClass.typeWith(
                        constructedClass.typeParameters.map { it.defaultType }
                    )
                )
            )
            backingField.initializer = DeclarationIrBuilder(pluginContext, backingField.symbol).run {
                irExprBody(
                    irCall(irRuntimeReferences.lazyIrSimpleFunction).apply {
                        typeArguments[0] = irRuntimeReferences.impliedKeysRegistryIrClassSymbol.typeWith(
                            containingClass.typeWith(
                                constructedClass.typeParameters.map { it.defaultType }
                            )
                        )
                        arguments[0] = IrFunctionExpressionImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = irRuntimeReferences.function0IrClassSymbol.typeWith(
                                irRuntimeReferences.impliedKeysRegistryIrClassSymbol.typeWith(
                                    containingClass.typeWith(
                                        constructedClass.typeParameters.map { it.defaultType }
                                    )
                                )
                            ),
                            function = pluginContext.irFactory.createSimpleFunction(
                                startOffset = UNDEFINED_OFFSET,
                                endOffset = UNDEFINED_OFFSET,
                                origin = declarationOrigin,
                                name = Name.identifier("lazyInitializerLambda"),
                                visibility = DescriptorVisibilities.LOCAL,
                                isInline = false,
                                isExpect = false,
                                returnType = irRuntimeReferences.impliedKeysRegistryIrClassSymbol.typeWith(
                                    containingClass.typeWith(
                                        constructedClass.typeParameters.map { it.defaultType }
                                    )
                                ),
                                modality = Modality.FINAL,
                                symbol = IrSimpleFunctionSymbolImpl(),
                                isTailrec = false,
                                isSuspend = false,
                                isOperator = false,
                                isInfix = false,
                                isExternal = false,
                                containerSource = null,
                                isFakeOverride = false,
                                companionExtensionClass = null
                            ).apply {
                                parent = backingField
                                val lazyLambda = this
                                body = DeclarationIrBuilder(pluginContext, symbol).run {
                                    irBlockBody {
                                        +irReturn(
                                            irCall(irRuntimeReferences.impliedKeysRegistryIrSimpleFunctionSymbol).apply {
                                                typeArguments[0] = containingClass.typeWith(
                                                    constructedClass.typeParameters.map { it.defaultType }
                                                )
                                                arguments[0] = IrFunctionExpressionImpl(
                                                    startOffset = UNDEFINED_OFFSET,
                                                    endOffset = UNDEFINED_OFFSET,
                                                    type = irRuntimeReferences.function1IrClassSymbol.typeWith(
                                                        irRuntimeReferences.impliedKeysRegistryBuilderIrClassSymbol.typeWith(
                                                            containingClass.typeWith(
                                                                constructedClass.typeParameters.map { it.defaultType }
                                                            )
                                                        ),
                                                        pluginContext.irBuiltIns.unitType
                                                    ),
                                                    function = pluginContext.irFactory.createSimpleFunction(
                                                        startOffset = UNDEFINED_OFFSET,
                                                        endOffset = UNDEFINED_OFFSET,
                                                        origin = declarationOrigin,
                                                        name = Name.identifier("impliedKeysRegistryBuilderLambda"),
                                                        visibility = DescriptorVisibilities.LOCAL,
                                                        isInline = false,
                                                        isExpect = false,
                                                        returnType = pluginContext.irBuiltIns.unitType,
                                                        modality = Modality.FINAL,
                                                        symbol = IrSimpleFunctionSymbolImpl(),
                                                        isTailrec = false,
                                                        isSuspend = false,
                                                        isOperator = false,
                                                        isInfix = false,
                                                        isExternal = false,
                                                        containerSource = null,
                                                        isFakeOverride = false,
                                                        companionExtensionClass = null
                                                    ).apply {
                                                        parent = lazyLambda
                                                        val builderParameter = pluginContext.irFactory.createValueParameter(
                                                            startOffset = UNDEFINED_OFFSET,
                                                            endOffset = UNDEFINED_OFFSET,
                                                            origin = declarationOrigin,
                                                            kind = ExtensionReceiver,
                                                            name = SpecialNames.THIS,
                                                            type = irRuntimeReferences.impliedKeysRegistryBuilderIrClassSymbol.typeWith(
                                                                containingClass.typeWith(
                                                                    constructedClass.typeParameters.map { it.defaultType }
                                                                )
                                                            ),
                                                            isAssignable = false,
                                                            symbol = IrValueParameterSymbolImpl(),
                                                            varargElementType = null,
                                                            isCrossinline = false,
                                                            isNoinline = false,
                                                            isHidden = false,
                                                        )
                                                        builderParameter.parent = this
                                                        parameters = listOf(builderParameter)
                                                        body = DeclarationIrBuilder(pluginContext, symbol).run {
                                                            irBlockBody {
                                                                val supplyTypeParameters = containingClass.typeParameters
                                                                
                                                                val suppliedTypesBuilder = SuppliedTypesBuilder(
                                                                    pluginContext = pluginContext,
                                                                    irRuntimeReferences = suppliedTypesIrRuntimeReferences,
                                                                    irStatementsBuilder = this,
                                                                    initialSuppliedTypes = buildMap {
                                                                        val fqNameString = constructedClass.fqNameStringForSuppliedTypes
                                                                        val dispatchReceiver = constructedClass.thisReceiver!!
                                                                        
                                                                        for (i in supplyTypeParameters.indices)
                                                                            put(
                                                                                supplyTypeParameters[i].defaultType,
                                                                                lazy {
                                                                                    irTemporary(
                                                                                        irCall(irRuntimeReferences.listGetIrSimpleFunctionSymbol).apply {
                                                                                            arguments[0] = irImplicitCast(
                                                                                                argument = irCall(irRuntimeReferences.mapGetIrSimpleFunctionSymbol).apply {
                                                                                                    arguments[0] = irCall(suppliedTypesIrRuntimeReferences.suppliableClassSuppliedTypesStorageGetterIrSimpleFunctionSymbol).apply {
                                                                                                        arguments[0] = irGet(dispatchReceiver)
                                                                                                    }
                                                                                                    arguments[1] = irString(fqNameString)
                                                                                                    type = irRuntimeReferences.listOfSuppliedTypeIrType.withNullability(true)
                                                                                                },
                                                                                                type = irRuntimeReferences.listOfSuppliedTypeIrType
                                                                                            )
                                                                                            arguments[1] = irInt(i)
                                                                                            type = irRuntimeReferences.suppliedTypeIrType
                                                                                        }
                                                                                    )
                                                                                }
                                                                            )
                                                                    }
                                                                )
                                                                
                                                                for (superType in containingClass.superTypes) {
                                                                    check(superType is IrSimpleType) { TODO() }
                                                                    val superClass = superType.classifier.owner
                                                                    val superClassTypeArguments = superType.arguments
                                                                    check(superClass is IrClass) { TODO() }
                                                                    if (!superClass.isGenerateKey) continue
                                                                    val superKey = superClass.declarations.first { it is IrClass && it.name == generatedContextKeyName } as IrClass
                                                                    val superKeySuppliedConstructor = superKey.declarations.first { it is IrConstructor && !it.isPrimary } as IrConstructor
                                                                    +irCall(irRuntimeReferences.impliedKeysRegistryBuilderImpliesSameIrSimpleFunctionSymbol).apply {
                                                                        arguments[0] = irGet(builderParameter)
                                                                        arguments[1] = irCallConstructor(
                                                                            callee = superKeySuppliedConstructor.symbol,
                                                                            typeArguments = superClassTypeArguments.map {
                                                                                it.typeOrNull
                                                                                    ?.substitute(
                                                                                        containingClass.typeParameters,
                                                                                        constructedClass.typeParameters.map { it.defaultType },
                                                                                    )
                                                                                    ?: contextsKeysIrPluginException(TODO())
                                                                            },
                                                                        ).apply {
                                                                            for (i in superClassTypeArguments.indices)
                                                                                arguments[i] = irGet(
                                                                                    suppliedTypesBuilder.resolve(
                                                                                        superClassTypeArguments[i].typeOrNull
                                                                                            ?: contextsKeysIrPluginException(TODO())
                                                                                    )
                                                                                )
                                                                            type = superKey.typeWith(
                                                                                superClassTypeArguments.map {
                                                                                    it.typeOrNull
                                                                                        ?.substitute(
                                                                                            containingClass.typeParameters,
                                                                                            constructedClass.typeParameters.map { it.defaultType },
                                                                                        )
                                                                                        ?: contextsKeysIrPluginException(TODO())
                                                                                },
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    },
                                                    origin = IrStatementOrigin.LAMBDA,
                                                )
                                                type = irRuntimeReferences.impliedKeysRegistryIrClassSymbol.typeWith(
                                                    containingClass.typeWith(
                                                        constructedClass.typeParameters.map { it.defaultType }
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            },
                            origin = IrStatementOrigin.LAMBDA,
                        )
                        type = irRuntimeReferences.lazyIrClassSymbol.typeWith(
                            irRuntimeReferences.impliedKeysRegistryIrClassSymbol.typeWith(
                                containingClass.typeWith(
                                    constructedClass.typeParameters.map { it.defaultType }
                                )
                            )
                        )
                    }
                )
            }
            getter.body = DeclarationIrBuilder(pluginContext, getter.symbol).run {
                irBlockBody {
                    +irReturn(
                        irCall(irRuntimeReferences.lazyValueGetterIrSimpleFunction).apply {
                            arguments[0] = irGetField(
                                receiver = irGet(getter.parameters.first { it.kind == DispatchReceiver }),
                                field = backingField,
                            )
                            type = irRuntimeReferences.impliedKeysRegistryIrClassSymbol.typeWith(
                                containingClass.typeWith(
                                    constructedClass.typeParameters.map { it.defaultType }
                                )
                            )
                        }
                    )
                }
            }
        }
        return super.visitProperty(declaration, data)
    }
}