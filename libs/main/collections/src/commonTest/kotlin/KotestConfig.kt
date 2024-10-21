/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

import io.kotest.core.config.AbstractProjectConfig

object KotestConfig : AbstractProjectConfig() {
    override val parallelism: Int = 16
}