/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.fiktion.ir

import dev.lounres.kone.plugin.fiktion.ir.erasure.FiktionErasureIrGenerationExtension
import dev.lounres.kone.plugin.fiktion.ir.interception.FiktionInterceptionIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector


fun fiktionIrGenerationExtension(
    messageCollector: MessageCollector,
    eraseFiktion: Boolean,
): IrGenerationExtension =
    if (eraseFiktion) FiktionErasureIrGenerationExtension(messageCollector)
    else FiktionInterceptionIrGenerationExtension(messageCollector)