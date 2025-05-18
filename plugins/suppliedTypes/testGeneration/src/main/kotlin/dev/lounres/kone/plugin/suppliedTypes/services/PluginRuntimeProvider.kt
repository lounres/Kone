package dev.lounres.kone.plugin.suppliedTypes.services

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions
import java.io.File
import java.io.FilenameFilter


class PluginRuntimeProvider(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    companion object {
        private const val RUNTIME_JAR_DIR = "../../libs/main/suppliedTypes/build/libs/"
        private val RUNTIME_JAR_FILTER = FilenameFilter { _, name -> name.startsWith("suppliedTypes") && name.endsWith(".jar") }
    }

    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
        val libDir = File(RUNTIME_JAR_DIR)
        testServices.assertions.assertTrue(libDir.exists() && libDir.isDirectory, failMessage)
        val jar = libDir.listFiles(RUNTIME_JAR_FILTER)?.firstOrNull() ?: testServices.assertions.fail(failMessage)
        configuration.addJvmClasspathRoot(jar)
    }

    private val failMessage = { "Jar with plugin runtime does not exist." }
}
