package io.heapy.ktc.plugins.dokka

import org.jetbrains.amper.plugins.Configurable
import org.jetbrains.amper.plugins.EnumValue
import java.nio.file.Path

/** Settings for the standalone JVM HTML documentation generator. */
@Configurable
public interface DokkaSettings {
    /** JDK version used for links to the Java standard library. */
    public val jdkVersion: Int get() = 17
    public val documentedVisibilities: List<Visibility> get() = listOf(Visibility.PUBLIC)
    public val reportUndocumented: Boolean get() = false
    public val failOnWarning: Boolean get() = false
    public val skipDeprecated: Boolean get() = false
    public val suppressInheritedMembers: Boolean get() = false
    /** Avoid downloading package lists; external documentation links will be unavailable. */
    public val offlineMode: Boolean get() = true
    /** Markdown files with module or package documentation. */
    public val includes: List<Path> get() = emptyList()
}

public enum class Visibility {
    @EnumValue("public") PUBLIC,
    @EnumValue("protected") PROTECTED,
    @EnumValue("internal") INTERNAL,
    @EnumValue("private") PRIVATE,
    @EnumValue("package") PACKAGE,
}
