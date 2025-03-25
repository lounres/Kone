import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import React from 'react';
import {koneGroup, koneVersion} from '@site/inputData';

interface Dependency {
    group: string
    artifact: string
    version: string
}

export default function DependencyTabs ({group=koneGroup, artifact, version=koneVersion}: Dependency): React.ReactElement {
    return (
        <Tabs groupId="buildSystem">
            <TabItem value="gradle-kotlin" label="Gradle Kotlin DSL" default>
                <CodeBlock language="kotlin" title="build.gradle.kts">
                    {
                        `dependencies {
                        |    // highlight-next-line
                        |    implementation("${group}:${artifact}:${version}")
                        |}`.replaceAll(/ *\|/g, "")
                    }
                </CodeBlock>
            </TabItem>
            <TabItem value="gradle-groovy" label="Gradle Groovy DSL">
                <CodeBlock language="groovy" title="build.gradle">
                    {
                        `dependencies {
                        |    // highlight-next-line
                        |    implementation '${group}:${artifact}:${version}'
                        |}`.replaceAll(/ *\|/g, "")
                    }
                </CodeBlock>
            </TabItem>
            <TabItem value="maven" label="Maven" default>
                <CodeBlock language="xml" title="pom.xml">
                {
                    `<dependency>
                    |    <!-- highlight-start -->
                    |    <groupId>${group}</groupId>
                    |    <artifactId>${artifact}</artifactId>
                    |    <version>${version}</version>
                    |    <!-- highlight-end -->
                    |</dependency>`.replaceAll(/ *\|/g, "")
                }
                </CodeBlock>
            </TabItem>
        </Tabs>
    )
}