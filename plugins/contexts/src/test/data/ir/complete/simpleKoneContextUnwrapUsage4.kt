// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


interface Bar : KoneContext {
    @KoneContextHolderInclude
    val bar: Int get() = 57
}

class Baz : Bar {
    @KoneContextHolderInclude
    override val bar: Int get() = super.bar
    val baz = 179
}

fun box(): String {
    KoneContext.unwrap(Baz())
    return if (contextOf<Int>() == 57) "OK" else "INCORRECT"
}