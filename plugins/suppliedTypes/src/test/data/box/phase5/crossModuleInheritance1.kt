// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

// MODULE: foo
import dev.lounres.kone.suppliedTypes.*


interface Foo<@Supplied T>
interface Foo2<T> {
    val foo2: T
}

// MODULE: bar(foo)
import dev.lounres.kone.suppliedTypes.*


interface Bar<@Supplied U> : Foo<Map<out U, *>>
interface Bar2<U> : Foo2<Map<out U, *>>