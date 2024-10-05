/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.logging

import dev.lounres.logKube.core.CurrentPlatformLogger


public val koneLogger: CurrentPlatformLogger = CurrentPlatformLogger(name = "Kone core logger")