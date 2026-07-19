// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


interface Bar : KoneContext {
    @KoneContextHolderInclude
    val bar: Int get() = 179
}

class Baz : Bar {
    @KoneContextHolderExclude
    override val bar: Int get() = super.bar
    @KoneContextHolderInclude
    val baz = 57
}

fun box(): String {
    KoneContext.unwrap(Baz())
    return if (contextOf<Int>() == 57) "OK" else "INCORRECT"
}