import React from 'react';
import clsx from 'clsx';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import Translate, {translate} from '@docusaurus/Translate';

import KoneIntro from '@site/src/pages/indexComponents/koneIntro';
import Features from '@site/src/pages/indexComponents/features';
import Header from "@site/src/pages/indexComponents/header";

import styles from './index.module.css';

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
            <Header/>
            <main>
                <KoneIntro/>
                <Features/>
            </main>
        </Layout>
    );
}