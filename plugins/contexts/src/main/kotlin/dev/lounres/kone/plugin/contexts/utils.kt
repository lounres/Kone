/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts

import org.jetbrains.kotlin.name.Name


const val useLocallyAsExtensionReceiversFakeValueParameterNameString = "<Kone contexts useLocallyAsExtensionReceivers receiver holder>"
const val unwrapLocallyAsExtensionReceiversFakeValueParameterNameString = "<Kone contexts unwrapLocallyAsExtensionReceivers receiver holder>"

val useLocallyAsExtensionReceiversFakeValueParameterName = Name.special(useLocallyAsExtensionReceiversFakeValueParameterNameString)
val unwrapLocallyAsExtensionReceiversFakeValueParameterName = Name.special(unwrapLocallyAsExtensionReceiversFakeValueParameterNameString)

val fakeValueParametersNameStrings = setOf<String>(
    useLocallyAsExtensionReceiversFakeValueParameterNameString,
    unwrapLocallyAsExtensionReceiversFakeValueParameterNameString
)