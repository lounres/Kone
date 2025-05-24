// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

// MODULE: foo
import dev.lounres.kone.suppliedTypes.*


open class Foo<@Supplied T>

// MODULE: bar(foo)
import dev.lounres.kone.suppliedTypes.*


class Bar<@Supplied U> : Foo<Map<out U, *>>()