package foo.bar

import dev.lounres.kone.contexts.*


class Work(private val n: Int) {
    fun work(): Int = n
}

context(work: Work)
fun work(): Int = work.work()

interface Bar : KoneContext {
    @KoneContextInclude
    val baz: Work get() = Work(179)
}

class Baz : Bar {
    @KoneContextInclude
    val bar = Work(57)
    @KoneContextExclude
    override val baz: Work get() = super.baz
}

fun box(): String {
    KoneContext.localUnwrap(Baz())
    return if (work() == 57) "OK" else "INCORRECT"
}