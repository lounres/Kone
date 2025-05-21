package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierPropertyName
import dev.lounres.kone.plugin.suppliedTypes.suppliedClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassId
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.buildDeprecationAnnotationInfoPerUseSiteStorage
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.deserialization.buildFirConstant
import org.jetbrains.kotlin.fir.diagnostics.ConeDiagnostic
import org.jetbrains.kotlin.fir.expressions.buildConstOrErrorExpression
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationCall
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirPredicateBasedProvider
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.ConstantValueKind


@OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
class SuppliedTypeArgumentsFirResolveExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "SuppliedTypeMemberGeneratorKey"
    }
    
    companion object {
//        private val SUPPLIED_TYPES_PREDICATE = LookupPredicate.create {
////            annotated(suppliedClassId.asSingleFqName().also { println(it) })
//            annotated(FqName("dev.lounres.kone.suppliedTypes.Supplied").also { println(it) })
//        }
    }
    
    private val symbolProvider by lazy { session.symbolProvider }
    private val suppliedTypeConeKotlinType by lazy { symbolProvider.getClassLikeSymbolByClassId(suppliedTypeClassId)!!.defaultType() }
//    private val predicateBasedProvider: FirPredicateBasedProvider = session.predicateBasedProvider
//    private val matchedTypeParameters by lazy {
//        predicateBasedProvider.getSymbolsByPredicate(SUPPLIED_TYPES_PREDICATE).also { println(it) }
//    }
    private val deprecatedAnnotationClassId = ClassId(packageFqName = FqName("kotlin"), relativeClassName = FqName("Deprecated"), isLocal = false)
    private val deprecatedAnnotationSymbol by lazy { symbolProvider.getClassLikeSymbolByClassId(deprecatedAnnotationClassId)!! }
    private val deprecationLevelClassId = ClassId(packageFqName = FqName("kotlin"), relativeClassName = FqName("Deprecated"), isLocal = false)
    private val deprecatedLevelSymbol by lazy { symbolProvider.getClassLikeSymbolByClassId(deprecationLevelClassId)!! }
    
    @OptIn(SymbolInternals::class)
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
        classSymbol.typeParameterSymbols
            .filter { it.annotations.any { it.fqName(session) == suppliedClassId.asSingleFqName() } }
            .mapTo(mutableSetOf()) { internalSupplierPropertyName(classSymbol.packageFqName().child(classSymbol.name), it.name) }
    
    @OptIn(SymbolInternals::class)
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
            returnType = suppliedTypeConeKotlinType,
            hasBackingField = !classSymbol.isInterface,
        )
        property.replaceAnnotations(
            listOf(
                buildAnnotationCall {
                    containingDeclarationSymbol = property.symbol
                    annotationTypeRef = buildResolvedTypeRef {
                        coneType = deprecatedAnnotationSymbol.defaultType()
                    }
                    calleeReference = buildResolvedNamedReference {
                        name = deprecatedAnnotationClassId.shortClassName
                        resolvedSymbol = deprecatedAnnotationSymbol
                    }
//                    argumentList = buildResolvedArgumentList {
//                        arguments += buildLiteralExpression(
//                            source = null,
//                            kind = ConstantValueKind.String,
//                            value = "",
//                            setType = true,
//                        )
//                        arguments += buildFunctionCall {
//                            calleeReference = buildResolvedNamedReference {
//                                name = Name.identifier("ERROR")
//                                resolvedSymbol = deprecatedLevelSymbol
//                            }
//                            explicitReceiver = null
//                            coneTypeOrNull = deprecatedLevelSymbol.defaultType()
//                        }
//                    }
                }
            )
        )
//        println(property.backingField)
//        println(property.render())
//        println(classSymbol.fir.render())
        return listOf(property.symbol)
    }
}