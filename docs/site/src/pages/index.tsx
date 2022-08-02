import React from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import {useColorMode} from "@docusaurus/theme-common";
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import featureSvg from '@site/src/components/HomepageFeatures/styles.module.css'

import styles from './index.module.css';

function HomepageHeader() {
    const {siteConfig} = useDocusaurusContext();
    return (
        <header className={clsx(styles.heroBanner)}>
            <div className="container">
                <img
                    src='/img/logos/kone-logo-full-colored.svg'
                    alt='Kone logo'
                    className={clsx(styles.logoBannerBig)}
                />
                <img
                    src='/img/logos/kone-logo-original-colored-vov.svg'
                    alt='Kone logo'
                    className={clsx(styles.logoBannerSmall)}
                />
                <h1 className="hero__title">{siteConfig.title}</h1>
                <p className="hero__subtitle">{siteConfig.tagline}</p>
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
            title={`Hello from ${siteConfig.title}`}
            description="Description will go into a meta tag in <head />">
            <HomepageHeader />
            <main>
                <HomepageFeatures />
            </main>
        </Layout>
    );
}
