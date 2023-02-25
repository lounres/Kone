/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package io.kotest.matchers

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.failure


public fun shouldNotBeExecuted() {
    assertionCounter.inc()
    failure("Unexpected execution encountered")
}