package foo.bar

import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <@Supply Gee> baz(): Gee? = null

fun bar() {
    println(baz<Int>(<!NAMED_PARAMETER_NOT_FOUND!>suppliedTypeParameterForGee<!> = TODO()))
}
