// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

// MODULE: foo
import dev.lounres.kone.suppliedTypes.*


//class Foo<@Supplied T>
@SuppliedTarget
fun <@Supplied T> Foo() {}
class Bar<@Supplied T>() {
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