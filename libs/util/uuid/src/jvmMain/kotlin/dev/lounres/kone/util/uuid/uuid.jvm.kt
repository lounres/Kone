/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.uuid

import java.util.UUID as UUIDJvm


public actual class UUID(public val uuid: UUIDJvm) {
    public actual companion object {
        public actual fun random(): UUID = UUID(UUIDJvm.randomUUID())
    }
}