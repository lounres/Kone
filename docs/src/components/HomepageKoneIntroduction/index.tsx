import styles from "@site/src/components/HomepageKoneIntroduction/styles.module.css";
import React from "react";
import clsx from "clsx";
import Link from "@docusaurus/Link";


export default function HomepageKoneIntroduction(): JSX.Element {
    return (
        <section className={clsx('hero hero--primary', styles.heroBanner)}>
            <div className="container">
                <h1 className="hero__title">Kone is <Link href='https://kotlinlang.org/docs/multiplatform.html' className={styles.link}>Kotlin Multiplatform</Link> library for pure math computations</h1>
                <div className={'row'}>
                    <div className={'col col--4 col--offset-1'}>
                        <h2 className={'hero__subtitle'}>
                            It's <b>NOT</b> yet another common math library!
                        </h2>
                        <p>
                            All common math libraries built in modern programming languages are made for programmers,
                            data scientists, and any other business-driven direction. And there is no fixture to,
                            for example, play with polynomials and Bernoulli number, compute properties of geometric
                            construction, or brute-force some combinatorial problem. This library tries to fix it!
                        </p>
                    </div>
                    <div className={'col col--4 col--offset-2'}>
                        <h2 className={'hero__subtitle'}>
                            Why Kotlin?
                        </h2>
                        <p>
                            There are several advantages from programming on Kotlin:
                            <ol>
                                <li>
                                    Kotlin supports JVM, JS, and Native platforms. JVM is already very comfortable to
                                    use, but you also have ability to move you application computation to web and native
                                    environment.
                                </li>
                                <li>
                                    Kotlin is very idiomatic. At first glimpse it's very simple, but it also has a lot of
                                    interesting complex syntax features that allows you to write very understandable and
                                    hard-to-brake code (if you use it right 😉).
                                </li>
                            </ol>
                        </p>
                    </div>
                </div>
            </div>
        </section>
    );
}
