// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

// MODULE: foo
import dev.lounres.kone.suppliedTypes.*


interface Foo<@Supply T>

// MODULE: bar(foo)
import dev.lounres.kone.suppliedTypes.*


class Bar<@Supply U> : Foo<Map<out U, *>>