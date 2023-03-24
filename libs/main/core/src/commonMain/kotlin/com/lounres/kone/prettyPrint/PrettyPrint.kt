/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */


package com.lounres.kone.prettyPrint


public interface PrettyPrintable {
    public fun prettyPrint(): String
}

public fun Any?.prettyPrint(): String =
    if (this is PrettyPrintable) this.prettyPrint()
    else this.toString()
