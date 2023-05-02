/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.computationalGeometry


/**
 * See [polytope](https://en.wikipedia.org/wiki/Polytope) and [abstract polytope](https://en.wikipedia.org/wiki/Abstract_polytope)
 */
public interface Polytope {
    public val rank: UInt
    public val faces: List<Set<Polytope>>
    public val vertices: List<Vertex>
}

public interface Vertex {

}
