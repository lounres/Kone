// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


interface Raf {
    interface Foo<@Supplied T>
}

class Hou {
    class Kie {
        interface Bar<@Supplied U> : Raf.Foo<Map<out U, *>>
    }
}