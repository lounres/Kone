/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial


/**
 * Marks declarations that give access to internal entities of polynomials delicate structure. Thus, it allows to
 * optimize performance a bit by skipping standard steps, but such skips may cause critical errors if something is
 * implemented badly. Make sure you fully read and understand documentation and don't break internal contracts.
 */
@RequiresOptIn(
    message = "This declaration gives access to delicate internal structure of polynomials. " +
            "It allows to optimize performance by skipping unnecessary arguments check. " +
            "But at the same time makes it easy to make a mistake " +
            "that will cause wrong computation result or even runtime error. " +
            "Make sure you fully read and understand documentation.",
    level = RequiresOptIn.Level.ERROR
)
public annotation class DelicatePolynomialAPI