/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial


public class ZeroPolynomialDegreeException : IllegalArgumentException("Degree of zero polynomial is not determined.")

public fun zeroPolynomialDegreeException(): Nothing = throw ZeroPolynomialDegreeException()