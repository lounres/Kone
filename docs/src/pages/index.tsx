import React from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageKoneIntroduction from '@site/src/components/HomepageKoneIntroduction';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import Translate, {translate} from '@docusaurus/Translate';

import styles from './index.module.css';

function HomepageHeader() {
    const {siteConfig} = useDocusaurusContext();
    return (
        <header className={styles.heroBanner}>
            <div className="container">
                <img
                    src='img/logos/kone-logo-full-colored.svg'
                    alt={translate({
                        message: "Kone logo",
                        id: "homepage.logo.alt",
                        description: "Big Kone logo alternative text"
                    })}
                    className={clsx(styles.logoBannerBig)}
                />
                <img
                    src='img/logos/kone-logo-original-colored-vov.svg'
                    alt='Kone logo'
                    className={clsx(styles.logoBannerSmall)}
                />
                <h1 className="hero__title">Kone</h1>
                <p className="hero__subtitle">
                    <Translate
                        id="meta.tagline"
                        description="The Kone's tagline"
                    >Making pure math computations available</Translate>
                </p>
                {/*<div className={styles.buttons}>
                    <Link
                        className="button button--secondary button--lg"
                        to="/docs/intro">
                        Docusaurus Tutorial - 5min ⏱️
                    </Link>
                </div>*/}
            </div>
        </header>
    );
}

export default function Home(): JSX.Element {
    const {siteConfig} = useDocusaurusContext();
    return (
        <Layout
            title={translate({
                message: "Home",
                id: "homepage.meta.title",
                description: "Title of the homepage"
            })}
            description={translate({
                message: "Kone is Kotlin Multiplatform library for pure math computations",
                id: "homepage.meta.description",
                description: "Title of the homepage"
            })}>
            <HomepageHeader />
            <main>
                <HomepageKoneIntroduction />
                <HomepageFeatures />
            </main>
        </Layout>
    );
}
