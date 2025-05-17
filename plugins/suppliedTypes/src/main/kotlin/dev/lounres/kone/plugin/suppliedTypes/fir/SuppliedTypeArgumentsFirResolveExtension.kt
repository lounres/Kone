package dev.lounres.kone.plugin.suppliedTypes.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.name.CallableId


@OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
class SuppliedTypeArgumentsFirResolveExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    init {
        println(session)
    }
    
    override fun getTopLevelCallableIds(): Set<CallableId> {
        return emptySet()
    }
}