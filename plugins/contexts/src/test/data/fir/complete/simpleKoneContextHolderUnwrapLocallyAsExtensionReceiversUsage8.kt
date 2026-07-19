// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


class Work(private val n: Int) {
    fun work(): Int = n
}

context(work: Work)
fun work(): Int = work.work()

interface Bar : KoneContext {
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
    KoneContext.unwrap(Baz())
    return if (work() == 57) "OK" else "INCORRECT"
}