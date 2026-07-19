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
    val baz = 57
}

fun box(): String {
    KoneContext.unwrap(Baz())
    return if (<!NO_CONTEXT_ARGUMENT!>contextOf<!><Int>() == 57) "OK" else "INCORRECT"
}