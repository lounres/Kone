/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts

import org.jetbrains.kotlin.name.Name


const val localReceiversFakeValueParameterNameString = "<Kone contexts localReceivers receiver holder>"
const val localContextsFakeValueParameterNameString = "<Kone contexts localContexts context holder>"
const val localContextsActualValueParameterNameString = "<Kone contexts localContexts context itself>"
const val unwrapFakeValueParameterNameString = "<Kone contexts unwrap context holder>"

val localReceiversFakeValueParameterName = Name.special(localReceiversFakeValueParameterNameString)
val localContextsFakeValueParameterName = Name.special(localContextsFakeValueParameterNameString)
val localContextsActualValueParameterName = Name.special(localContextsActualValueParameterNameString)
val unwrapFakeValueParameterName = Name.special(unwrapFakeValueParameterNameString)

val fakeValueParametersNameStrings = setOf<String>(
    localReceiversFakeValueParameterNameString,
    localContextsFakeValueParameterNameString,
    unwrapFakeValueParameterNameString
)