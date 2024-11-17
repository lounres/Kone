/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.composeCanvas

import dev.lounres.kone.algebraic.field
import dev.lounres.kone.computationalGeometry.EuclideanKategory
import dev.lounres.kone.computationalGeometry.euclideanKategory


@PublishedApi
internal val euclideanKategory: EuclideanKategory<Float> = Float.field.euclideanKategory