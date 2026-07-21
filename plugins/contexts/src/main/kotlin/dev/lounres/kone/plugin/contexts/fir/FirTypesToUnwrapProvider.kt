/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.koneContextHolderExcludeAnnotationClassId
import dev.lounres.kone.plugin.contexts.koneContextHolderIncludeAnnotationClassId
import dev.lounres.kone.scope
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.declaredProperties
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeAliasSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.Name


class FirTypesToUnwrapProvider(private val session: FirSession) {
    private fun ConeKotlinType.allFirClassSymbolsWithTheirTypes() = buildMap {
        val toCheck = ArrayDeque<ConeKotlinType>()
        toCheck.addLast(this@allFirClassSymbolsWithTheirTypes)
        
        while (toCheck.isNotEmpty()) {
            val type = toCheck.removeLast()
            when (val unwrappedType = type.unwrapToSimpleTypeUsingLowerBound()) {
                is ConeCapturedType -> (unwrappedType.constructor.supertypes ?: emptyList()).mapTo(toCheck) { it }
                is ConeIntegerConstantOperatorType -> {}
                is ConeIntegerLiteralConstantType -> {}
                is ConeIntersectionType -> unwrappedType.intersectedTypes.mapTo(toCheck) { it }
                is ConeStubTypeForTypeVariableInSubtyping -> {}
                is ConeTypeVariableType -> {}
                is ConeLookupTagBasedType -> when (unwrappedType) {
                    is ConeTypeParameterType -> unwrappedType.lookupTag.typeParameterSymbol.resolvedBounds.mapTo(toCheck) { it.coneType }
                    is ConeClassLikeType -> {
                        val classId = unwrappedType.lookupTag.classId
                        val classSymbol = session.symbolProvider.getClassLikeSymbolByClassId(classId) ?: continue
                        when(classSymbol) {
                            is FirClassSymbol -> {
                                put(classSymbol, unwrappedType.typeArguments.map { it.type!! })
                            }
                            is FirTypeAliasSymbol -> {
                                val substitutor = substitutorByMap(
                                    substitution = classSymbol.typeParameterSymbols.zip(unwrappedType.typeArguments.map { it.type!! }).toMap(),
                                    useSiteSession = session,
                                )
                                toCheck.add(substitutor.substituteOrSelf(classSymbol.resolvedExpandedTypeRef.coneType))
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
    
    private class PossiblePropertyDescription(
        val originalClass: FirClassSymbol<*>,
        val typeToUnwrap: ConeKotlinType,
        val referredClasses: Map<FirClassSymbol<*>, List<ConeKotlinType>>,
    )
    
    private class FirClassDescription(
        val superClassSymbols: MutableMap<FirClassSymbol<*>, ConeClassLikeType>,
        val propertiesByName: Map<Name, FirPropertySymbol>,
        val properties: Map<FirPropertySymbol, MutableSet<FirPropertySymbol>>,
    ) {
        val possiblePropertiesToUnwrap = mutableMapOf<FirPropertySymbol, PossiblePropertyDescription>()
    }
    
    private val computedDescriptions = mutableMapOf<FirClassSymbol<*>, FirClassDescription>()
    
    private fun FirPropertySymbol.isInclude() = hasAnnotation(koneContextHolderIncludeAnnotationClassId, session)
    private fun FirPropertySymbol.isExclude() = hasAnnotation(koneContextHolderExcludeAnnotationClassId, session)
    
    private fun FirClassSymbol<*>.initializeReferencesAndTypesToUnwrap() {
        class ClassWithProperty(
            val classSymbol: FirClassSymbol<*>,
            val classType: ConeClassLikeType,
            val propertySymbol: FirPropertySymbol,
            val overriddenPropertySymbol: Set<FirPropertySymbol>,
        )
        val superClassesAndSelf = computedDescriptions[this]!!.superClassSymbols + (this to this.defaultType())
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
        val topPossiblePropertiesToUnwrap = buildMap<FirPropertySymbol, ClassWithProperty> {
            possiblePropertiesToUnwrap.forEach { put(it.propertySymbol, it) }
            possiblePropertiesToUnwrap.forEach { it.overriddenPropertySymbol.forEach { override -> remove(override) } }
        }
        val propertiesToUnwrap = topPossiblePropertiesToUnwrap.values
            .groupBy { it.propertySymbol.name }
            .filter { it.value.size == 1 }
            .values
            .map { it.single() }
            .filter { (it.overriddenPropertySymbol + it.propertySymbol).any { it in decidingProperties && it.isInclude() } }
        val propertiesToUnwrapMapped = propertiesToUnwrap
            .associate {
                val coneSubstitutor = substitutorByMap(
                    substitution = it.classSymbol.typeParameterSymbols.zip(it.classType.typeArguments.map { it.type!! }).toMap(),
                    useSiteSession = session,
                )
                it.propertySymbol to PossiblePropertyDescription(
                    originalClass = it.classSymbol,
                    typeToUnwrap = coneSubstitutor.substituteOrSelf(it.propertySymbol.resolvedReturnType),
                    referredClasses = it.propertySymbol.resolvedReturnType.allFirClassSymbolsWithTheirTypes().mapValues {
                        it.value.map { coneSubstitutor.substituteOrSelf(it) }
                    }
                )
            }
        computedDescriptions[this]!!.possiblePropertiesToUnwrap.putAll(propertiesToUnwrapMapped)
    }
    
    private fun createDescriptionsFor(initialClassSymbol: FirClassSymbol<*>): Set<FirClassSymbol<*>> {
        if (initialClassSymbol in computedDescriptions) return emptySet()
        
        val newFirClassSymbols = mutableSetOf<FirClassSymbol<*>>(initialClassSymbol)
        
        class TypeToVisit(
            val type: ConeKotlinType,
            val subClassSymbols: List<FirClassSymbol<*>>,
            val subClassSubstitutors: List<ConeSubstitutor>,
        )
        val toCheck = ArrayDeque<TypeToVisit>()
        
        scope {
            val classProperties = initialClassSymbol
                .declaredProperties(session)
                .filter { it.receiverParameterSymbol == null && it.contextParameterSymbols.isEmpty() }
            computedDescriptions[initialClassSymbol] = FirClassDescription(
                mutableMapOf(),
                classProperties.associateBy { it.name },
                classProperties.associateWith { mutableSetOf() },
            )
            newFirClassSymbols.add(initialClassSymbol)
            initialClassSymbol.resolvedSuperTypes.mapTo(toCheck) {
                TypeToVisit(
                    it,
                    listOf(initialClassSymbol),
                    emptyList(),
                )
            }
        }
        
        while (toCheck.isNotEmpty()) {
            val (type, subClassSymbols, subClassSubstitutors) = toCheck.removeLast()
            when (val unwrappedType = type.unwrapToSimpleTypeUsingLowerBound()) {
                is ConeCapturedType -> (unwrappedType.constructor.supertypes ?: emptyList()).mapTo(toCheck) { TypeToVisit(it, subClassSymbols, subClassSubstitutors) }
                is ConeIntegerConstantOperatorType -> {}
                is ConeIntegerLiteralConstantType -> {}
                is ConeIntersectionType -> unwrappedType.intersectedTypes.mapTo(toCheck) { TypeToVisit(it, subClassSymbols, subClassSubstitutors) }
                is ConeStubTypeForTypeVariableInSubtyping -> {}
                is ConeTypeVariableType -> {}
                is ConeLookupTagBasedType -> when (unwrappedType) {
                    is ConeTypeParameterType -> unwrappedType.lookupTag.typeParameterSymbol.resolvedBounds.mapTo(toCheck) { TypeToVisit(it.coneType, subClassSymbols, subClassSubstitutors) }
                    is ConeClassLikeType -> {
                        val classId = unwrappedType.lookupTag.classId
                        val classSymbol = session.symbolProvider.getClassLikeSymbolByClassId(classId) ?: continue
                        when(classSymbol) {
                            is FirClassSymbol -> {
                                val classDescription = computedDescriptions[classSymbol]
                                if (classDescription != null) {
                                    val superClassSymbols = buildMap {
                                        putAll(classDescription.superClassSymbols)
                                        put(classSymbol, classSymbol.defaultType())
                                    }
                                    val coneSubstitutor = substitutorByMap(
                                        substitution = classSymbol.typeParameterSymbols.zip(unwrappedType.typeArguments.map { it.type!! }).toMap(),
                                        useSiteSession = session,
                                    )
                                    for ([superClassSymbol, superType] in superClassSymbols) {
                                        var superType = coneSubstitutor.substituteOrSelf(superType) as ConeClassLikeType
                                        for (i in subClassSymbols.lastIndex downTo 0) {
                                            val subClassSymbol = subClassSymbols[i]
                                            val subClassDescription = computedDescriptions[subClassSymbol]!!
                                            subClassDescription.superClassSymbols[superClassSymbol] = superType
                                            if (i != 0) {
                                                superType = subClassSubstitutors[i - 1].substituteOrSelf(superType) as ConeClassLikeType
                                            }
                                            for ([superProperty, superSuperProperties] in computedDescriptions[superClassSymbol]!!.properties) {
                                                for (property in superSuperProperties + superProperty) {
                                                    val subProperty = subClassDescription.propertiesByName[property.name] ?: continue
                                                    subClassDescription.properties[subProperty]!!.add(property)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    val classProperties = classSymbol
                                        .declaredProperties(session)
                                        .filter { it.receiverParameterSymbol == null && it.contextParameterSymbols.isEmpty() }
                                    computedDescriptions[classSymbol] = FirClassDescription(
                                        mutableMapOf(),
                                        classProperties.associateBy { it.name },
                                        classProperties.associateWith { mutableSetOf() },
                                    )
                                    newFirClassSymbols.add(classSymbol)
                                    scope {
                                        var superType: ConeClassLikeType = unwrappedType
                                        for (i in subClassSymbols.lastIndex downTo 0) {
                                            val subClassSymbol = subClassSymbols[i]
                                            val subClassDescription = computedDescriptions[subClassSymbol]!!
                                            subClassDescription.superClassSymbols[classSymbol] = superType
                                            if (i != 0) {
                                                superType = subClassSubstitutors[i - 1].substituteOrSelf(superType) as ConeClassLikeType
                                            }
                                            for (property in classProperties) {
                                                val subProperty = subClassDescription.propertiesByName[property.name] ?: continue
                                                subClassDescription.properties[subProperty]!!.add(property)
                                            }
                                        }
                                    }
                                    classSymbol.resolvedSuperTypes.mapTo(toCheck) {
                                        TypeToVisit(
                                            it,
                                            subClassSymbols + classSymbol,
                                            subClassSubstitutors + substitutorByMap(
                                                substitution = classSymbol.typeParameterSymbols.zip(unwrappedType.typeArguments.map { it.type!! }).toMap(),
                                                useSiteSession = session,
                                            )
                                        )
                                    }
                                }
                            }
                            is FirTypeAliasSymbol -> {
                                val substitutor = substitutorByMap(
                                    substitution = classSymbol.typeParameterSymbols.zip(unwrappedType.typeArguments.map { it.type!! }).toMap(),
                                    useSiteSession = session,
                                )
                                toCheck.add(
                                    TypeToVisit(
                                        substitutor.substituteOrSelf(classSymbol.resolvedExpandedTypeRef.coneType),
                                        subClassSymbols,
                                        subClassSubstitutors,
                                    )
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
        
        for (newFirClassSymbol in newFirClassSymbols) newFirClassSymbol.initializeReferencesAndTypesToUnwrap()
        
        return newFirClassSymbols
    }
    
    operator fun get(coneKotlinType: ConeKotlinType): List<ConeKotlinType> {
        val initialFirClassSymbolsWithTheirTypes = coneKotlinType.allFirClassSymbolsWithTheirTypes()
        val initialFirClassSymbols = initialFirClassSymbolsWithTheirTypes.keys
        
        val visitedFirClassSymbols = mutableSetOf<FirClassSymbol<*>>()
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
            val classSymbols: Set<FirClassSymbol<*>>,
            val referredClasses: MutableSet<FirClassSymbol<*>>,
        )
        val visitedFirClassSymbolsGroups = visitedFirClassSymbolsList.mapTo(mutableSetOf()) {
            Group(
                mutableSetOf(it),
                mutableSetOf<FirClassSymbol<*>>().apply {
                    computedDescriptions[it]!!.possiblePropertiesToUnwrap.values.forEach {
                        it.referredClasses.keys.filterTo(this) { it in visitedFirClassSymbols }
                    }
                },
            )
        }
        val visitedFirClassSymbolsToGroups = mutableMapOf<FirClassSymbol<*>, Group>().apply {
            for (group in visitedFirClassSymbolsGroups) for (classSymbol in group.classSymbols)
                put(classSymbol, group)
        }
        
        val sortedGroups = mutableListOf<Set<FirClassSymbol<*>>>()
        
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
                    mutableSetOf<FirClassSymbol<*>>().apply {
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
            add(coneKotlinType)
            
            val classesToAdd = initialFirClassSymbolsWithTheirTypes.toMutableMap()
            while (classesToAdd.isNotEmpty()) {
                val [currentClass, typeArguments] = classesToAdd.entries.first()
                classesToAdd.remove(currentClass)
                val currentClassSubstitutor = substitutorByMap(
                    currentClass.typeParameterSymbols.zip(typeArguments).toMap(),
                    session,
                )
                val classDescription = computedDescriptions[currentClass]!!
                for (propertyDescription in classDescription.possiblePropertiesToUnwrap.values) {
                    val originalClass = propertyDescription.originalClass
                    if (originalClass != currentClass) {
                        val superClassSubstitutor = substitutorByMap(
                            originalClass.typeParameterSymbols.zip(classDescription.superClassSymbols[originalClass]!!.typeArguments.map { it.type!! }).toMap(),
                            session,
                        )
                        add(currentClassSubstitutor.substituteOrSelf(superClassSubstitutor.substituteOrSelf(propertyDescription.typeToUnwrap)))
                        classesToAdd += propertyDescription.referredClasses.mapValues {
                            it.value.map { currentClassSubstitutor.substituteOrSelf(superClassSubstitutor.substituteOrSelf(it)) }
                        }
                    } else {
                        add(currentClassSubstitutor.substituteOrSelf(propertyDescription.typeToUnwrap))
                        classesToAdd += propertyDescription.referredClasses.mapValues {
                            it.value.map { currentClassSubstitutor.substituteOrSelf(it) }
                        }
                    }
                }
            }
        }
    }
}