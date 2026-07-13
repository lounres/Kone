/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts

import org.jetbrains.kotlin.name.Name


const val useLocallyAsExtensionReceiversFakeValueParameterNameString = "<Kone contexts useLocallyAsExtensionReceivers receiver holder>"
const val useLocallyAsContextsFakeValueParameterNameString = "<Kone contexts useLocallyAsContexts receiver holder>"
const val useLocallyAsContextsActualValueParameterNameString = "<Kone contexts useLocallyAsContexts receiver itself>"
const val unwrapLocallyAsExtensionReceiversFakeValueParameterNameString = "<Kone contexts unwrapLocallyAsExtensionReceivers receiver holder>"

val useLocallyAsExtensionReceiversFakeValueParameterName = Name.special(useLocallyAsExtensionReceiversFakeValueParameterNameString)
val useLocallyAsContextsFakeValueParameterName = Name.special(useLocallyAsContextsFakeValueParameterNameString)
val useLocallyAsContextsActualValueParameterName = Name.special(useLocallyAsContextsActualValueParameterNameString)
val unwrapLocallyAsExtensionReceiversFakeValueParameterName = Name.special(unwrapLocallyAsExtensionReceiversFakeValueParameterNameString)

val fakeValueParametersNameStrings = setOf<String>(
    useLocallyAsExtensionReceiversFakeValueParameterNameString,
    useLocallyAsContextsFakeValueParameterNameString,
    unwrapLocallyAsExtensionReceiversFakeValueParameterNameString
)