package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierParameterName
import dev.lounres.kone.plugin.suppliedTypes.internalSupplierPropertyName
import dev.lounres.kone.plugin.suppliedTypes.ir.copyMapToBy
import dev.lounres.kone.plugin.suppliedTypes.ir.copyToBy
import dev.lounres.kone.plugin.suppliedTypes.suppliedClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTargetClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassId
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.getContainingClassSymbol
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.constructors
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.primaryConstructorIfAny
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.deserialization.toQualifiedPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildEnumEntryDeserializedAccessExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.types.ConstantValueKind


class ClassSuppliedTypeParametersPropertiesGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "SuppliedTypeMemberGeneratorKey"
    }
    
    companion object {
        private val SUPPLIED_TARGET_PREDICATE = LookupPredicate.create {
            annotated(suppliedTargetClassId.asSingleFqName())
        }
    }
    
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(SUPPLIED_TARGET_PREDICATE)
    }
    
    private val predicateBasedProvider by lazy { session.predicateBasedProvider }
    private val symbolProvider by lazy { session.symbolProvider }
    private val suppliedTypeConeClassLikeType by lazy { symbolProvider.getClassLikeSymbolByClassId(suppliedTypeClassId)!!.defaultType() }
    private val suppliedTypeFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = suppliedTypeConeClassLikeType
        }
    }
    private val deprecatedAnnotationClassId = StandardClassIds.Annotations.Deprecated
    private val deprecatedAnnotationConeClassLikeType by lazy { symbolProvider.getClassLikeSymbolByClassId(deprecatedAnnotationClassId)!!.defaultType() }
    private val deprecatedAnnotationFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = deprecatedAnnotationConeClassLikeType
        }
    }
    private val deprecationLevelClassId = StandardClassIds.DeprecationLevel
    private val deprecationLevelConeClassLikeType by lazy { symbolProvider.getClassLikeSymbolByClassId(deprecationLevelClassId)!!.defaultType() }
    private val deprecationLevelFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = deprecationLevelConeClassLikeType
        }
    }
    private fun buildDeprecatedAnnotation(
        message: String,
//        replaceWith: String = "",
        level: DeprecationLevel = DeprecationLevel.WARNING
    ): FirAnnotation =
        buildAnnotation {
            annotationTypeRef = deprecatedAnnotationFirResolvedTypeRef
            argumentMapping = buildAnnotationArgumentMapping {
                mapping[Name.identifier("message")] = buildLiteralExpression(
                    source = null,
                    kind = ConstantValueKind.String,
                    value = message,
                    setType = true,
                )
                mapping[Name.identifier("level")] = buildEnumEntryDeserializedAccessExpression {
                    enumClassId = deprecationLevelClassId
                    enumEntryName = Name.identifier(level.toString())
                }.toQualifiedPropertyAccessExpression(session)
            }
        }
    
    private val suppliedTargets by lazy {
        predicateBasedProvider.getSymbolsByPredicate(SUPPLIED_TARGET_PREDICATE)
    }
    private val functionsWithSuppliedTypeParameters by lazy {
        suppliedTargets.filterIsInstance<FirNamedFunctionSymbol>()
    }
    private val constructorsWithSuppliedTypeParameters by lazy {
        suppliedTargets.filterIsInstance<FirConstructorSymbol>()
    }
    private val constructorsWithSuppliedTypeParametersByContainingClass by lazy {
        constructorsWithSuppliedTypeParameters.groupBy { it.getContainingClassSymbol() as FirClassSymbol<*> }
    }
    
    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelCallableIds(): Set<CallableId> {
        return super.getTopLevelCallableIds()
    }
    
    private data class TypeParametersInfo(
        val origin: FirClassSymbol<*>,
        val appearances: Set<FirClassSymbol<*>>,
    )
    private val allSuperClassesSuppliedTypeParametersInfoRegistry: MutableMap<FirClassSymbol<*>, Map<FirTypeParameterSymbol, TypeParametersInfo>> = mutableMapOf()
    private val FirClassSymbol<*>.allSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
        get() = buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
            val typeParameters = typeParameterSymbols
            typeParameters
                .filter { it.hasAnnotation(suppliedClassId, session) }
                .associateWith {
                    TypeParametersInfo(
                        origin = this@allSuppliedTypeParametersInfo,
                        appearances = setOf(this@allSuppliedTypeParametersInfo),
                    )
                }.copyToBy(
                    destination = this,
                    resolve = { _, currentInfo, newInfo ->
                        check(currentInfo.origin == newInfo.origin)
                        TypeParametersInfo(
                            origin = currentInfo.origin,
                            appearances = currentInfo.appearances + newInfo.appearances,
                        )
                    },
                )
            allSuperClassesSuppliedTypeParametersInfo
                .copyMapToBy(
                    destination = this,
                    transform = {
                        TypeParametersInfo(
                            origin = it.value.origin,
                            appearances = it.value.appearances + this@allSuppliedTypeParametersInfo
                        )
                    },
                    resolve = { _, currentInfo, newInfo ->
                        check(currentInfo.origin == newInfo.origin)
                        TypeParametersInfo(
                            origin = currentInfo.origin,
                            appearances = currentInfo.appearances + newInfo.appearances + this@allSuppliedTypeParametersInfo
                        )
                    }
                )
        }
    private val FirClassSymbol<*>.allSuperClassesSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
        get() = allSuperClassesSuppliedTypeParametersInfoRegistry.getOrPut(this) {
            buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
                for (superType in resolvedSuperTypes) {
                    val superClassSymbol = superType.toClassSymbol(session)!!
                    superClassSymbol.allSuppliedTypeParametersInfo
                        .copyToBy(
                            destination = this,
                            resolve = { _, currentInfo, newInfo ->
                                check(currentInfo.origin == newInfo.origin)
                                TypeParametersInfo(
                                    origin = currentInfo.origin,
                                    appearances = currentInfo.appearances + newInfo.appearances,
                                )
                            }
                        )
                }
            }
        }
    
    @OptIn(SymbolInternals::class)
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
        buildSet {
            classSymbol.allSuppliedTypeParametersInfo.mapTo(this) { internalSupplierPropertyName(it.value.origin.classId, it.key.name) }
            if (classSymbol.typeParameterSymbols.any { it.hasAnnotation(suppliedClassId, session) }) add(SpecialNames.INIT)
//            if (classSymbol in constructorsWithSuppliedTypeParametersByContainingClass) add(SpecialNames.INIT)
        }
    
    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirPropertySymbol> {
        if (context == null) return emptyList()
        val classSymbol = context.owner
        
        val property = createMemberProperty(
            owner = classSymbol,
            key = Key,
            name = callableId.callableName,
            returnType = suppliedTypeConeClassLikeType,
            hasBackingField = !classSymbol.isInterface,
        ) {
            modality = if (classSymbol.isInterface) Modality.ABSTRACT else Modality.FINAL
        }.apply {
            replaceAnnotations(listOf(buildDeprecatedAnnotation(message = "Supplied type internal property", level = DeprecationLevel.HIDDEN)))
        }
        
//        val property = buildProperty {
//            moduleData = session.moduleData
//            origin = Key.origin
//
//            symbol = FirPropertySymbol(callableId)
//            name = callableId.callableName
//
//            returnTypeRef = suppliedTypeFirResolvedTypeRef
//            annotations += buildDeprecatedAnnotation(message = "Supplied type internal property", level = DeprecationLevel.HIDDEN)
//        }
        return listOf(property.symbol)
    }
    
    @OptIn(DirectDeclarationsAccess::class, SymbolInternals::class)
    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        functionsWithSuppliedTypeParameters
        val classSymbol = context.owner
        val suppliedTypeParameterSymbols = classSymbol.typeParameterSymbols.filter { it.hasAnnotation(suppliedClassId, session) }
        if (suppliedTypeParameterSymbols.isEmpty()) return emptyList()
        val constructorsToCopy = constructorsWithSuppliedTypeParametersByContainingClass.getOrDefault(classSymbol, emptyList())
        
//        return super.generateConstructors(context)
        
        println(classSymbol.fir.declarations.filterIsInstance<FirConstructor>().map { it.body })
        
        return constructorsToCopy.map { oldConstructor ->
            createConstructor(
                owner = classSymbol,
                key = Key,
            ) {
                for (typeParameter in suppliedTypeParameterSymbols)
                    valueParameter(
                        name = internalSupplierParameterName(typeParameter.name),
                        type = suppliedTypeConeClassLikeType,
                    )
                for (valueParameterSymbol in oldConstructor.valueParameterSymbols)
                    valueParameter(
                        name = valueParameterSymbol.name,
                        type = valueParameterSymbol.resolvedReturnType,
                    )
            }.apply {
                replaceAnnotations(listOf(buildDeprecatedAnnotation(message = "Supplied type internal constructor", level = DeprecationLevel.HIDDEN)))
            }.symbol
        }
    }
}