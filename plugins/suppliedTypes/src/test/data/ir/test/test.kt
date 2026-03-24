// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS


class Foo {
    val gee: Int = 57
    
    inner class Bar {
        inner class Baz {
            fun doo(): Int = gee + 179
        }
    }
}