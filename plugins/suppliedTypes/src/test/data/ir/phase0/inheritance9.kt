// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


interface Raf {
    open class Foo<@Supplied T>
}

class Hou {
    class Kie {
        class Bar<@Supplied U> : Raf.Foo<Map<out U, *>>()
    }
}