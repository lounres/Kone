// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


interface Foo<@Supplied T>

interface Bar<@Supplied U> : Foo<Map<out U, *>>