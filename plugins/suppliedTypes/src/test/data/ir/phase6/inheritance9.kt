// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


interface Raf {
    @Suppliable
    open class Foo<@Supply T>
}

class Hou {
    class Kie {
        @Suppliable
        class Bar<@Supply U> : Raf.Foo<Map<out U, *>>()
    }
}