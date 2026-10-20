package foo.bar

import dev.lounres.kone.contexts.*


class Baz : KoneContext {
    val gee = 57
}

fun box(): String {
    receivers(Baz()) {
        return if (gee == 57) "OK" else "INCORRECT"
    }
}