// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


class Work(private val n: Int) {
    fun work(): Int = n
}

interface Bar : KoneContextHolder {
    @KoneContextHolderInclude
    val baz: Work get() = Work(179)
}

class Baz : Bar {
    @KoneContextHolderInclude
    val bar = Work(57)
    @KoneContextHolderExclude
    override val baz: Work get() = super.baz
}

fun box(): String {
    KoneContextHolder.unwrapLocallyAsExtensionReceivers(Baz())
    return if (work() == 57) "OK" else "INCORRECT"
}