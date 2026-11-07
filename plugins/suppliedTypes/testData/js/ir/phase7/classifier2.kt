package foo.bar

import dev.lounres.kone.suppliedTypes.*


@Suppliable
class Baz<@Supply Gee>(arg1: Int, arg2: Int) {
    constructor(arg3: Long) : this(arg3.toInt(), -arg3.toInt())
    constructor() : this(57, 179)
}