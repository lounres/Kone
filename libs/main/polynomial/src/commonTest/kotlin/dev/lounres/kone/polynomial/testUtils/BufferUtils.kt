/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial.testUtils

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asBuffer


fun <T> bufferOf(vararg elements: T): Buffer<T> = elements.asBuffer()