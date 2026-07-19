/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataKey
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataRegistry
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol


data object FakeReceiverKey : FirDeclarationDataKey()

val FirValueParameterSymbol.fakeReceiver: FirReceiverParameter? by FirDeclarationDataRegistry.symbolAccessor(FakeReceiverKey)
var FirValueParameter.fakeReceiver: FirReceiverParameter? by FirDeclarationDataRegistry.data(FakeReceiverKey)