import styles from "@site/src/pages/indexComponents/koneIntro.module.css";
import React from "react";
import clsx from "clsx";
import Link from "@docusaurus/Link";
import Translate, {translate} from '@docusaurus/Translate';


export default function KoneIntro(): JSX.Element {
    return (
        <section className={clsx('hero hero--primary', styles.heroBanner)}>
            <div className="container">
                <h1 className="hero__title">
                    <Translate
                        id="homepage.koneIntroduction.title"
                        description="Title line of Kone introduction block"
                        values={{
                            kotlinMultiplatformLink: (<Link href='https://kotlinlang.org/docs/multiplatform.html' className={styles.link}>Kotlin Multiplatform</Link>)
                        }}
                    >
                        {"Kone is {kotlinMultiplatformLink} library for pure math computations"}
                    </Translate>

                </h1>
                <div className={'row'}>
                    <div className={'col col--4 col--offset-1'}>
                        <h2 className={'hero__subtitle'}>
                            <Translate
                                id="homepage.koneIntroduction.notYetAnother.title"
                                description='Title of "Not Yet Another Math Lib" block'
                                values={{
                                    highlightedNot: (
                                        <b><Translate
                                            id="homepage.koneIntroduction.notYetAnother.title.highlightedNot"
                                            description='Highlighted "not" part of label of "Not Yet Another Math Lib" block'
                                        >{"NOT"}</Translate></b>)
                                }}
                            >
                                {"It's {highlightedNot} yet another common math library!"}
                            </Translate>
                        </h2>
                        <p>
                            <Translate
                                id="homepage.koneIntroduction.notYetAnother.content"
                                description='Content of "Not Yet Another Math Lib" block'
                            >
                                {
                                    `All common math libraries built in modern programming languages are made for programmers,
                                    data scientists, and any other business-driven direction. And there is no fixture to,
                                    for example, play with polynomials and Bernoulli number, compute properties of geometric
                                    construction, or brute-force some combinatorial problem. This library tries to fix it!`
                                }
                            </Translate>
                        </p>
                    </div>
                    <div className={'col col--4 col--offset-2'}>
                        <h2 className={'hero__subtitle'}>
                            <Translate
                                id="homepage.koneIntroduction.whyKotlin.title"
                                description='Title of "Why Kotlin?" block'
                            >
                                {"Why Kotlin?"}
                            </Translate>
                        </h2>
                        <p style={{margin: "0"}}>
                            <Translate
                                id="homepage.koneIntroduction.whyKotlin.content.beginning"
                                description='Beginning of content of "Why Kotlin?" block'
                            >
                                {"There are several advantages from programming on Kotlin:"}
                            </Translate>
                        </p>
                        <ul>
                            <li>
                                <Translate
                                    id="homepage.koneIntroduction.whyKotlin.content.list.1"
                                    description='First item of list in content of "Why Kotlin?" block'
                                >
                                    {
                                        `Kotlin supports JVM, JS, and Native platforms. JVM is already very comfortable to
                                        use, but you also have ability to migrate you application's computations to web and native
                                        environment.`
                                    }
                                </Translate>
                            </li>
                            <li>
                                <Translate
                                    id="homepage.koneIntroduction.whyKotlin.content.list.2"
                                    description='Second item of list in content of "Why Kotlin?" block'
                                >
                                    {
                                        `Kotlin is very idiomatic. At first glimpse it's very simple, but it also has a lot of
                                        interesting complex syntax features that allows you to write very understandable and
                                        hard-to-brake code (if you use it right 😉).`
                                    }
                                </Translate>

                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </section>
    );
}
