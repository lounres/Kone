import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import React from 'react';

interface Dependency {
    group: string
    artifact: string
    version: string
}

export default function DependenciesTabs ({group="com.lounres.kone", artifact, version="0.1.0-pre-1"}: Dependency): JSX.Element {
    return (
        <Tabs groupId="buildSystem">
            <TabItem value="gradle-groovy" label="Gradle Groovy DSL">
                <CodeBlock
                    language="groovy"
                    title="build.gradle"
                    showLineNumbers
                >
{`dependencies {
    // highlight-next-line
    implementation '${group}:${artifact}:${version}'
}`}
                </CodeBlock>
            </TabItem>
            <TabItem value="gradle-kotlin" label="Gradle Kotlin DSL" default>
                <CodeBlock
                    language="kotlin"
                    title="build.gradle.kts"
                    showLineNumbers
                >
{`dependencies {
    // highlight-next-line
    implementation("${group}:${artifact}:${version}")
}`}
                </CodeBlock>
            </TabItem>
            <TabItem value="maven" label="Maven" default>
                <CodeBlock
                    language="xml"
                    title="pom.xml"
                    showLineNumbers
                >
{`<dependencies>
    <!-- highlight-start -->
    <groupId>${group}</groupId>
    <artifactId>${artifact}</artifactId>
    <version>${version}</version>
    <!-- highlight-end -->
</dependencies>`}
                </CodeBlock>
            </TabItem>
        </Tabs>
    )
}