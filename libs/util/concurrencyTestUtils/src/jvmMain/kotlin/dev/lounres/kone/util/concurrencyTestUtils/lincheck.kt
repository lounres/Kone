/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.concurrencyTestUtils

import de.infix.testBalloon.framework.core.TestConfig
import de.infix.testBalloon.framework.core.TestSuiteScope
import de.infix.testBalloon.framework.core.testScope
import de.infix.testBalloon.framework.shared.TestElementName
import de.infix.testBalloon.framework.shared.TestRegistering
import org.jetbrains.kotlinx.lincheck.Actor
import org.jetbrains.kotlinx.lincheck.execution.ExecutionGenerator
import org.jetbrains.kotlinx.lincheck.execution.ExecutionScenario
import org.jetbrains.lincheck.datastructures.DSLScenarioBuilder
import org.jetbrains.lincheck.datastructures.DSLThreadScenario
import org.jetbrains.lincheck.datastructures.ManagedStrategyGuarantee
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.StressOptions
import org.jetbrains.lincheck.datastructures.forClasses
import org.jetbrains.lincheck.datastructures.verifier.Verifier
import org.jetbrains.lincheck.util.LoggingLevel
import java.lang.IllegalStateException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod
import kotlin.time.Duration


public class StressOptionsBuilder {
    internal var stressOptions = StressOptions()
    
    public fun iterations(iterations: Int) {
        stressOptions = stressOptions.iterations(iterations)
    }
    
    public fun invocationsPerIteration(invocations: Int) {
        stressOptions = stressOptions.invocationsPerIteration(invocations)
    }
    
    public fun threads(threads: Int) {
        stressOptions = stressOptions.threads(threads)
    }
    
    public fun actorsPerThread(actorsPerThread: Int) {
        stressOptions = stressOptions.actorsPerThread(actorsPerThread)
    }
    
    public fun actorsBefore(actorsBefore: Int) {
        stressOptions = stressOptions.actorsBefore(actorsBefore)
    }
    
    public fun actorsAfter(actorsAfter: Int) {
        stressOptions = stressOptions.actorsAfter(actorsAfter)
    }
    
    public fun executionGenerator(executionGenerator: Class<out ExecutionGenerator?>) {
        stressOptions = stressOptions.executionGenerator(executionGenerator)
    }
    
    public fun verifier(verifier: Class<out Verifier?>) {
        stressOptions = stressOptions.verifier(verifier)
    }
    
    public fun minimizeFailedScenario(minimizeFailedScenario: Boolean) {
        stressOptions = stressOptions.minimizeFailedScenario(minimizeFailedScenario)
    }
    
    public fun logLevel(logLevel: LoggingLevel) {
        stressOptions = stressOptions.logLevel(logLevel)
    }
    
    public fun sequentialSpecification(clazz: Class<*>?) {
        stressOptions = stressOptions.sequentialSpecification(clazz)
    }
    
    public fun sequentialSpecification(clazz: KClass<*>?) {
        sequentialSpecification(clazz?.java)
    }
    
    public inline fun <reified Clazz> sequentialSpecification() {
        sequentialSpecification(Clazz::class)
    }
    
    public fun addCustomScenario(scenario: ExecutionScenario) {
        stressOptions = stressOptions.addCustomScenario(scenario)
    }
    
    public fun addCustomScenario(scenarioBuilder: DSLScenarioBuilder.() -> Unit) {
        stressOptions = stressOptions.addCustomScenario(scenarioBuilder)
    }
}

@TestRegistering
public fun TestSuiteScope.testWithLincheckStress(
    @TestElementName name: String,
    testConfig: TestConfig = TestConfig,
    testClass: Class<*>,
    options: suspend StressOptionsBuilder.() -> Unit = {}
) {
    test(
        name = name,
        testConfig = TestConfig.testScope(isEnabled = true, timeout = Duration.INFINITE).chainedWith(testConfig),
    ) {
        StressOptionsBuilder().apply { options() }.stressOptions.check(testClass)
    }
}

@TestRegistering
public fun TestSuiteScope.testWithLincheckStress(
    @TestElementName name: String,
    testConfig: TestConfig = TestConfig,
    testClass: KClass<*>,
    options: suspend StressOptionsBuilder.() -> Unit = {}
) {
    testWithLincheckStress(
        name = name,
        testConfig = testConfig,
        testClass = testClass.java,
        options = options,
    )
}

@TestRegistering
public inline fun <reified TestClass> TestSuiteScope.testWithLincheckStress(
    @TestElementName name: String,
    testConfig: TestConfig = TestConfig,
    noinline options: suspend StressOptionsBuilder.() -> Unit = {},
) {
    testWithLincheckStress(
        name = name,
        testConfig = testConfig,
        testClass = TestClass::class,
        options = options,
    )
}

public fun DSLThreadScenario.actor(
    f: KFunction<*>,
    vararg args: Any?,
    cancelOnSuspension: Boolean = false,
    blocking: Boolean = false,
    causesBlocking: Boolean = false,
    promptCancellation: Boolean = false,
) {
    val method = f.javaMethod ?: throw IllegalStateException("The function is a constructor or cannot be represented by a Java Method")
    require(method.exceptionTypes.all { Throwable::class.java.isAssignableFrom(it) }) { "Not all declared exceptions are Throwable" }
    val requiredArgsCount = method.parameters.size - if (f.isSuspend) 1 else 0
    require(requiredArgsCount == args.size) { "Invalid number of the operation ${f.name} parameters: $requiredArgsCount expected, ${args.size} provided." }
    add(
        Actor(
            method = method,
            arguments = args.toList(),
            cancelOnSuspension = cancelOnSuspension,
            blocking = blocking,
            causesBlocking = causesBlocking,
            promptCancellation = promptCancellation,
        )
    )
}

public class ManagedStrategyGuaranteeInstanceBuilder {
    @PublishedApi
    internal val classPredicates: MutableList<(String) -> Boolean> = mutableListOf()
    @PublishedApi
    internal val methodPredicates: MutableList<(String) -> Boolean> = mutableListOf()
    
    public fun classThat(predicate: (fullClassName: String) -> Boolean) {
        classPredicates.add(predicate)
    }
    public fun classNamed(name: String) {
        classThat { it == name }
    }
    public inline fun <reified T> classIs() {
        classNamed(T::class.qualifiedName ?: error("The class ${T::class} is local or is a class of an anonymous object"))
    }
    public fun methodThat(predicate: (methodName: String) -> Boolean) {
        methodPredicates.add(predicate)
    }
    public fun allMethods() {
        methodThat { true }
    }
}

public class ManagedStrategyGuaranteesBuilder {
    @PublishedApi
    internal val guarantees: MutableList<ManagedStrategyGuarantee> = mutableListOf()
    
    public inline fun ignore(builder: ManagedStrategyGuaranteeInstanceBuilder.() -> Unit) {
        val instance = ManagedStrategyGuaranteeInstanceBuilder().apply(builder)
        guarantees.add(
            forClasses { name -> instance.classPredicates.any { it(name) } }
                .methods { name -> instance.methodPredicates.any { it(name) }}
                .ignore()
        )
    }
    
    public inline fun treatAsAtomic(builder: ManagedStrategyGuaranteeInstanceBuilder.() -> Unit) {
        val instance = ManagedStrategyGuaranteeInstanceBuilder().apply(builder)
        guarantees.add(
            forClasses { name -> instance.classPredicates.any { it(name) } }
                .methods { name -> instance.methodPredicates.any { it(name) }}
                .treatAsAtomic()
        )
    }
}

public class ModelCheckingOptionsBuilder {
    @PublishedApi
    internal var modelCheckingOptions: ModelCheckingOptions = ModelCheckingOptions()
    
    public fun iterations(iterations: Int) {
        modelCheckingOptions = modelCheckingOptions.iterations(iterations)
    }
    
    public fun invocationsPerIteration(invocations: Int) {
        modelCheckingOptions = modelCheckingOptions.invocationsPerIteration(invocations)
    }
    
    public fun threads(threads: Int) {
        modelCheckingOptions = modelCheckingOptions.threads(threads)
    }
    
    public fun actorsPerThread(actorsPerThread: Int) {
        modelCheckingOptions = modelCheckingOptions.actorsPerThread(actorsPerThread)
    }
    
    public fun actorsBefore(actorsBefore: Int) {
        modelCheckingOptions = modelCheckingOptions.actorsBefore(actorsBefore)
    }
    
    public fun actorsAfter(actorsAfter: Int) {
        modelCheckingOptions = modelCheckingOptions.actorsAfter(actorsAfter)
    }
    
    public fun executionGenerator(executionGenerator: Class<out ExecutionGenerator?>) {
        modelCheckingOptions = modelCheckingOptions.executionGenerator(executionGenerator)
    }
    
    public fun verifier(verifier: Class<out Verifier?>) {
        modelCheckingOptions = modelCheckingOptions.verifier(verifier)
    }
    
    public fun minimizeFailedScenario(minimizeFailedScenario: Boolean) {
        modelCheckingOptions = modelCheckingOptions.minimizeFailedScenario(minimizeFailedScenario)
    }
    
    public fun logLevel(logLevel: LoggingLevel) {
        modelCheckingOptions = modelCheckingOptions.logLevel(logLevel)
    }
    
    public fun sequentialSpecification(clazz: Class<*>?) {
        modelCheckingOptions = modelCheckingOptions.sequentialSpecification(clazz)
    }
    
    public fun sequentialSpecification(clazz: KClass<*>?) {
        sequentialSpecification(clazz?.java)
    }
    
    public inline fun <reified Clazz> sequentialSpecification() {
        sequentialSpecification(Clazz::class)
    }
    
    public fun addCustomScenario(scenario: ExecutionScenario) {
        modelCheckingOptions = modelCheckingOptions.addCustomScenario(scenario)
    }
    
    public fun addCustomScenario(scenarioBuilder: DSLScenarioBuilder.() -> Unit) {
        modelCheckingOptions = modelCheckingOptions.addCustomScenario(scenarioBuilder)
    }
    
    public fun checkObstructionFreedom(checkObstructionFreedom: Boolean = true) {
        modelCheckingOptions = modelCheckingOptions.checkObstructionFreedom(checkObstructionFreedom)
    }
    
//    public fun hangingDetectionThreshold(hangingDetectionThreshold: Int) {
//        modelCheckingOptions = modelCheckingOptions.hangingDetectionThreshold(hangingDetectionThreshold)
//    }
    
    public fun addGuarantee(guarantee: ManagedStrategyGuarantee) {
        modelCheckingOptions = modelCheckingOptions.addGuarantee(guarantee)
    }
    
    public inline fun addGuarantees(guaranteesBuilder: ManagedStrategyGuaranteesBuilder.() -> Unit) {
        val instance = ManagedStrategyGuaranteesBuilder().apply(guaranteesBuilder)
        for (guarantee in instance.guarantees) {
            modelCheckingOptions = modelCheckingOptions.addGuarantee(guarantee)
        }
    }
}

@TestRegistering
public fun TestSuiteScope.testWithLincheckModelChecking(
    @TestElementName name: String,
    testConfig: TestConfig = TestConfig,
    testClass: Class<*>,
    options: suspend ModelCheckingOptionsBuilder.() -> Unit = {}
) {
    test(
        name = name,
        testConfig = TestConfig.testScope(isEnabled = true, timeout = Duration.INFINITE).chainedWith(testConfig),
    ) {
        ModelCheckingOptionsBuilder().apply { options() }.modelCheckingOptions.check(testClass)
    }
}

@TestRegistering
public fun TestSuiteScope.testWithLincheckModelChecking(
    @TestElementName name: String,
    testConfig: TestConfig = TestConfig,
    testClass: KClass<*>,
    options: suspend ModelCheckingOptionsBuilder.() -> Unit = {}
) {
    testWithLincheckModelChecking(
        name = name,
        testConfig = testConfig,
        testClass = testClass.java,
        options = options,
    )
}

@TestRegistering
public inline fun <reified TestClass> TestSuiteScope.testWithLincheckModelChecking(
    @TestElementName name: String,
    testConfig: TestConfig = TestConfig,
    noinline options: suspend ModelCheckingOptionsBuilder.() -> Unit = {},
) {
    testWithLincheckModelChecking(
        name = name,
        testConfig = testConfig,
        testClass = TestClass::class,
        options = options,
    )
}