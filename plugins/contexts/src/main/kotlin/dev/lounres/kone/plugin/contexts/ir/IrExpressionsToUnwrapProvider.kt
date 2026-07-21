/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.ir

import dev.lounres.kone.scope
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrScriptSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrDynamicType
import org.jetbrains.kotlin.ir.types.IrErrorType
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.name.Name


class UsedOrUnwrappedExpression(
    val type: IrType,
    val variable: IrVariable,
)

class IrExpressionsToUnwrapProvider {
    private fun IrType.allFirClassSymbolsWithTheirTypes() = buildMap {
        val toCheck = ArrayDeque<IrType>()
        toCheck.addLast(this@allFirClassSymbolsWithTheirTypes)
        
        while (toCheck.isNotEmpty()) {
            val type = toCheck.removeLast()
            when (type) {
                is IrDynamicType -> {}
                is IrErrorType -> {}
                is IrSimpleType -> when (val classSymbol = type.classifier) {
                    is IrScriptSymbol -> {}
                    is IrTypeParameterSymbol -> classSymbol.owner.superTypes.mapTo(toCheck) { it }
                    is IrClassSymbol -> {
                        put(classSymbol, type.arguments.map { it.typeOrNull ?: contextsIrPluginException() })
                    }
                }
            }
        }
    }
    
    private class PossiblePropertyDescription(
        val originalClass: IrClassSymbol,
        val typeToUnwrap: IrType,
        val referredClasses: Map<IrClassSymbol, List<IrType>>,
    )
    
    private class FirClassDescription(
        val superClassSymbols: MutableMap<IrClassSymbol, IrSimpleType>,
        val propertiesByName: Map<Name, IrPropertySymbol>,
        val properties: Map<IrPropertySymbol, MutableSet<IrPropertySymbol>>,
    ) {
        val possiblePropertiesToUnwrap = mutableMapOf<IrPropertySymbol, PossiblePropertyDescription>()
    }
    
    private val computedDescriptions = mutableMapOf<IrClassSymbol, FirClassDescription>()
    
    private fun IrClassSymbol.initializeReferencesAndTypesToUnwrap() {
        class ClassWithProperty(
            val classSymbol: IrClassSymbol,
            val classType: IrSimpleType,
            val propertySymbol: IrPropertySymbol,
            val overriddenPropertySymbol: Set<IrPropertySymbol>,
        )
        val superClassesAndSelf = computedDescriptions[this]!!.superClassSymbols + (this to this.defaultType)
        val possiblePropertiesToUnwrap = superClassesAndSelf
            .flatMap { [classSymbol, classType] ->
                computedDescriptions[classSymbol]!!.properties.map { [property, overrides] ->
                    ClassWithProperty(classSymbol, classType, property, overrides)
                }
            }
        val decidingProperties = buildSet {
            val markedProperties = possiblePropertiesToUnwrap.filter { it.propertySymbol.isInclude() || it.propertySymbol.isExclude() }
            markedProperties.forEach { add(it.propertySymbol) }
            markedProperties.forEach { it.overriddenPropertySymbol.forEach { override -> remove(override) } }
        }
        val topPossiblePropertiesToUnwrap = buildMap<IrPropertySymbol, ClassWithProperty> {
            possiblePropertiesToUnwrap.forEach { put(it.propertySymbol, it) }
            possiblePropertiesToUnwrap.forEach { it.overriddenPropertySymbol.forEach { override -> remove(override) } }
        }
        val propertiesToUnwrap = topPossiblePropertiesToUnwrap.values
            .groupBy { it.propertySymbol.owner.name }
            .filter { it.value.size == 1 }
            .values
            .map { it.single() }
            .filter { (it.overriddenPropertySymbol + it.propertySymbol).any { it in decidingProperties && it.isInclude() } }
        val propertiesToUnwrapMapped = propertiesToUnwrap
            .associate {
                val (classSymbol, classType, propertySymbol) = it
                val propertyGetter = propertySymbol.owner.getter!!
                val substitutionMap = classSymbol.owner.typeParameters.map { it.symbol }.zip(classType.arguments.map { it.typeOrNull ?: contextsIrPluginException() }).toMap()
                propertySymbol to PossiblePropertyDescription(
                    originalClass = classSymbol,
                    typeToUnwrap = propertyGetter.returnType.substitute(substitutionMap),
                    referredClasses = propertyGetter.returnType.allFirClassSymbolsWithTheirTypes().mapValues {
                        it.value.map { it.substitute(substitutionMap) }
                    }
                )
            }
        computedDescriptions[this]!!.possiblePropertiesToUnwrap.putAll(propertiesToUnwrapMapped)
    }
    
    private fun createDescriptionsFor(initialClassSymbol: IrClassSymbol): Set<IrClassSymbol> {
        if (initialClassSymbol in computedDescriptions) return emptySet()
        
        val newFirClassSymbols = mutableSetOf<IrClassSymbol>(initialClassSymbol)
        
        class TypeToVisit(
            val type: IrType,
            val subClassSymbols: List<IrClassSymbol>,
            val subClassSubstitutors: List<Map<IrTypeParameterSymbol, IrType>>,
        )
        val toCheck = ArrayDeque<TypeToVisit>()
        
        scope {
            val classProperties = initialClassSymbol.owner.properties
                .filter {
                    val getter = it.getter
                    getter != null && getter.parameters.size == 1 && getter.parameters[0].kind == DispatchReceiver
                }
            computedDescriptions[initialClassSymbol] = FirClassDescription(
                mutableMapOf(),
                classProperties.associate { it.name to it.symbol },
                classProperties.associate { it.symbol to mutableSetOf() },
            )
            newFirClassSymbols.add(initialClassSymbol)
            initialClassSymbol.owner.superTypes.mapTo(toCheck) {
                TypeToVisit(
                    it,
                    listOf(initialClassSymbol),
                    emptyList(),
                )
            }
        }
        
        while (toCheck.isNotEmpty()) {
            val (type, subClassSymbols, subClassSubstitutors) = toCheck.removeLast()
            when (type) {
                is IrDynamicType -> {}
                is IrErrorType -> {}
                is IrSimpleType -> when (val classSymbol = type.classifier) {
                    is IrScriptSymbol -> {}
                    is IrTypeParameterSymbol -> classSymbol.owner.superTypes.mapTo(toCheck) { TypeToVisit(it, subClassSymbols, subClassSubstitutors) }
                    is IrClassSymbol -> {
                        val classDescription = computedDescriptions[classSymbol]
                        if (classDescription != null) {
                            val superClassSymbols = buildMap {
                                putAll(classDescription.superClassSymbols)
                                put(classSymbol, classSymbol.defaultType)
                            }
                            val substitutionMap = classSymbol.owner.typeParameters.map { it.symbol }.zip(type.arguments.map { it.typeOrNull ?: contextsIrPluginException() }).toMap()
                            for ([superClassSymbol, superType] in superClassSymbols) {
                                var superType = superType.substitute(substitutionMap) as IrSimpleType
                                for (i in subClassSymbols.lastIndex downTo 0) {
                                    val subClassSymbol = subClassSymbols[i]
                                    val subClassDescription = computedDescriptions[subClassSymbol]!!
                                    subClassDescription.superClassSymbols[superClassSymbol] = superType
                                    if (i != 0) {
                                        superType = superType.substitute(subClassSubstitutors[i - 1]) as IrSimpleType
                                    }
                                    for ([superProperty, superSuperProperties] in computedDescriptions[superClassSymbol]!!.properties) {
                                        for (property in superSuperProperties + superProperty) {
                                            val subProperty = subClassDescription.propertiesByName[property.owner.name] ?: continue
                                            subClassDescription.properties[subProperty]!!.add(property)
                                        }
                                    }
                                }
                            }
                        } else {
                            val classProperties = classSymbol.owner.properties
                                .filter {
                                    val getter = it.getter
                                    getter != null && getter.parameters.size == 1 && getter.parameters[0].kind == DispatchReceiver
                                }
                            computedDescriptions[classSymbol] = FirClassDescription(
                                mutableMapOf(),
                                classProperties.associate { it.name to it.symbol },
                                classProperties.associate { it.symbol to mutableSetOf() },
                            )
                            newFirClassSymbols.add(classSymbol)
                            scope {
                                var superType: IrSimpleType = type
                                for (i in subClassSymbols.lastIndex downTo 0) {
                                    val subClassSymbol = subClassSymbols[i]
                                    val subClassDescription = computedDescriptions[subClassSymbol]!!
                                    subClassDescription.superClassSymbols[classSymbol] = superType
                                    if (i != 0) {
                                        superType = superType.substitute(subClassSubstitutors[i - 1]) as IrSimpleType
                                    }
                                    for (property in classProperties) {
                                        val subProperty = subClassDescription.propertiesByName[property.name] ?: continue
                                        subClassDescription.properties[subProperty]!!.add(property.symbol)
                                    }
                                }
                            }
                            classSymbol.owner.superTypes.mapTo(toCheck) {
                                TypeToVisit(
                                    it,
                                    subClassSymbols + classSymbol,
                                    subClassSubstitutors + classSymbol.owner.typeParameters.map { it.symbol }.zip(type.arguments.map { it.typeOrNull ?: contextsIrPluginException() }).toMap()
                                )
                            }
                        }
                    }
                }
            }
        }
        
        for (newFirClassSymbol in newFirClassSymbols) newFirClassSymbol.initializeReferencesAndTypesToUnwrap()
        
        return newFirClassSymbols
    }
    
    operator fun get(declarationSymbol: IrSymbol, irExpression: IrExpression): List<UsedOrUnwrappedExpression> {
        val initialFirClassSymbolsWithTheirTypes = irExpression.type.allFirClassSymbolsWithTheirTypes()
        val initialFirClassSymbols = initialFirClassSymbolsWithTheirTypes.keys
        
        val visitedFirClassSymbols = mutableSetOf<IrClassSymbol>()
        val firClassSymbolsToVisit = initialFirClassSymbols.filterTo(mutableSetOf()) { it !in computedDescriptions }
        
        while (firClassSymbolsToVisit.isNotEmpty()) {
            val firClassSymbol = firClassSymbolsToVisit.first()
            
            val newFirClassSymbolsWithDescriptions = createDescriptionsFor(firClassSymbol)
            visitedFirClassSymbols.addAll(newFirClassSymbolsWithDescriptions)
            firClassSymbolsToVisit.removeAll(newFirClassSymbolsWithDescriptions)
            
            val referredClasses = buildSet {
                newFirClassSymbolsWithDescriptions.forEach {
                    computedDescriptions[it]!!.possiblePropertiesToUnwrap.values.flatMapTo(this) { it.referredClasses.keys }
                }
            }
            
            referredClasses.filterTo(firClassSymbolsToVisit) { it !in computedDescriptions }
        }
        
        val visitedFirClassSymbolsList = visitedFirClassSymbols.toList()
        class Group(
            val classSymbols: Set<IrClassSymbol>,
            val referredClasses: MutableSet<IrClassSymbol>,
        )
        val visitedFirClassSymbolsGroups = visitedFirClassSymbolsList.mapTo(mutableSetOf()) {
            Group(
                mutableSetOf(it),
                mutableSetOf<IrClassSymbol>().apply {
                    computedDescriptions[it]!!.possiblePropertiesToUnwrap.values.forEach {
                        it.referredClasses.keys.filterTo(this) { it in visitedFirClassSymbols }
                    }
                },
            )
        }
        val visitedFirClassSymbolsToGroups = mutableMapOf<IrClassSymbol, Group>().apply {
            for (group in visitedFirClassSymbolsGroups) for (classSymbol in group.classSymbols)
                put(classSymbol, group)
        }
        
        val sortedGroups = mutableListOf<Set<IrClassSymbol>>()
        
        while (visitedFirClassSymbolsGroups.isNotEmpty()) {
            var currentGroup = visitedFirClassSymbolsGroups.first()
            val groupsPath = mutableListOf<Group>(currentGroup)
            val visitedGroups = mutableMapOf<Group, Int>(currentGroup to 0)
            
            while (currentGroup.referredClasses.isNotEmpty()) {
                currentGroup = visitedFirClassSymbolsToGroups[currentGroup.referredClasses.first()]!!
                if (currentGroup in visitedGroups) break
                groupsPath.add(currentGroup)
                visitedGroups[currentGroup] = groupsPath.lastIndex
            }
            
            if (currentGroup.referredClasses.isNotEmpty()) {
                val groupsToUnite = groupsPath.drop(visitedGroups[currentGroup]!!)
                val newGroupClassSymbols = groupsToUnite.flatMapTo(mutableSetOf()) { it.classSymbols }
                val unitedGroup = Group(
                    newGroupClassSymbols,
                    mutableSetOf<IrClassSymbol>().apply {
                        groupsToUnite.flatMapTo(this) { it.referredClasses }
                        removeAll(newGroupClassSymbols)
                    },
                )
                
                for (oldGroup in groupsToUnite) {
                    visitedFirClassSymbolsGroups.remove(oldGroup)
                }
                visitedFirClassSymbolsGroups.add(unitedGroup)
                for (classSymbol in unitedGroup.classSymbols) {
                    visitedFirClassSymbolsToGroups[classSymbol] = unitedGroup
                }
            } else {
                visitedFirClassSymbolsGroups.remove(currentGroup)
                visitedFirClassSymbolsGroups.forEach { it.referredClasses -= currentGroup.classSymbols }
                visitedFirClassSymbolsToGroups -= currentGroup.classSymbols
                sortedGroups.add(currentGroup.classSymbols)
            }
        }
        
        for (group in sortedGroups) {
            for (classSymbol in group) {
                val classDescription = computedDescriptions[classSymbol]!!
                val conflictingProperties = classDescription.possiblePropertiesToUnwrap.entries
                    .filter { it.value.referredClasses.any { it.key in group } }
                for ([property] in conflictingProperties) classDescription.possiblePropertiesToUnwrap.remove(property)
            }
        }
        
        return buildList {
            val scope = Scope(declarationSymbol)
            val initialVariable = scope.createTemporaryVariable(irExpression)
            add(
                UsedOrUnwrappedExpression(
                    irExpression.type,
                    initialVariable
                )
            )
            
            class ClassToAddDescription(
                val typeArguments: List<IrType>,
                val variable: IrVariable,
            )
            val classesToAdd = initialFirClassSymbolsWithTheirTypes.mapValuesTo(mutableMapOf()) {
                ClassToAddDescription(
                    it.value,
                    initialVariable
                )
            }
            while (classesToAdd.isNotEmpty()) {
                val [currentClass, currentClassDescription] = classesToAdd.entries.first()
                classesToAdd.remove(currentClass)
                val currentClassSubstitutionMap = currentClass.owner.typeParameters.map { it.symbol }.zip(currentClassDescription.typeArguments).toMap()
                val classDescription = computedDescriptions[currentClass]!!
                for ([property, propertyDescription] in classDescription.possiblePropertiesToUnwrap) {
                    val originalClass = propertyDescription.originalClass
                    if (originalClass != currentClass) {
                        val superClassSubstitutionMap = originalClass.owner.typeParameters.map { it.symbol }.zip(classDescription.superClassSymbols[originalClass]!!.arguments.map { it.typeOrNull ?: contextsIrPluginException() }).toMap()
                        val unwrappedType = propertyDescription.typeToUnwrap.substitute(superClassSubstitutionMap).substitute(currentClassSubstitutionMap)
                        val unwrappedVariable = scope.createTemporaryVariable(
                            IrCallImpl.fromSymbolOwner(
                                startOffset = UNDEFINED_OFFSET,
                                endOffset = UNDEFINED_OFFSET,
                                type = unwrappedType,
                                symbol = property.owner.getter!!.symbol,
                            ).apply {
                                arguments[0] = IrGetValueImpl(
                                    UNDEFINED_OFFSET,
                                    UNDEFINED_OFFSET,
                                    currentClassDescription.variable.symbol,
                                )
                            }
                        )
                        add(
                            UsedOrUnwrappedExpression(
                                unwrappedType,
                                unwrappedVariable
                            )
                        )
                        classesToAdd += propertyDescription.referredClasses.mapValues {
                            ClassToAddDescription(
                                it.value.map { it.substitute(superClassSubstitutionMap).substitute(currentClassSubstitutionMap) },
                                unwrappedVariable,
                            )
                        }
                    } else {
                        val unwrappedType = propertyDescription.typeToUnwrap.substitute(currentClassSubstitutionMap)
                        val unwrappedVariable = scope.createTemporaryVariable(
                            IrCallImpl.fromSymbolOwner(
                                startOffset = UNDEFINED_OFFSET,
                                endOffset = UNDEFINED_OFFSET,
                                type = unwrappedType,
                                symbol = property.owner.getter!!.symbol,
                            ).apply {
                                arguments[0] = IrGetValueImpl(
                                    UNDEFINED_OFFSET,
                                    UNDEFINED_OFFSET,
                                    currentClassDescription.variable.symbol,
                                )
                            }
                        )
                        add(
                            UsedOrUnwrappedExpression(
                                unwrappedType,
                                unwrappedVariable
                            )
                        )
                        classesToAdd += propertyDescription.referredClasses.mapValues {
                            ClassToAddDescription(
                                it.value.map { it.substitute(currentClassSubstitutionMap) },
                                unwrappedVariable,
                            )
                        }
                    }
                }
            }
        }
    }
}