// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.fiktion.*


@Fiktion.Imaginary
val Int.foo: Int get() = this

fun bar() {
    var value = 57
    while (value != 179) {
        println(value)
        value = value.<!FIKTION_IMAGINARY_CALLABLE_REAL_CALL!>foo<!>
    }
}
