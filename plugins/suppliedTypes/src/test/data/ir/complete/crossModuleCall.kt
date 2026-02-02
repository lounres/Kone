// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

// MODULE: foo
import dev.lounres.kone.suppliedTypes.*


//class Foo<@Supply T>
@Suppliable
fun <@Supply T> Foo() {}
class Bar<@Supply T>() {
    val a = 5
    init {
    
    }
    constructor(b: Int) : this() {
    
    }
}

// MODULE: bar(foo)

fun box() {
    Foo<String>()
}