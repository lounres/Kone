/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


object KotestProjectConfig : AbstractProjectConfig() {
    override val timeout: Duration = 5.seconds
    override val parallelism: Int = 4
}