import React from 'react';
import clsx from 'clsx';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import Translate, {translate} from '@docusaurus/Translate';

import KoneIntro from '@site/src/pages/indexComponents/koneIntro';
import Features from '@site/src/pages/indexComponents/features';
import Banner from "@site/src/pages/indexComponents/banner";

import styles from './index.module.css';

export default function Home(): JSX.Element {
    return (
        <Layout
            title={translate({
                message: "Home",
                id: "homepage.meta.title",
                description: "Title of the homepage"
            })}
            description={translate({
                message: "Kone is a set of Kotlin Multiplatform libraries that are made to simplify your experience of mathematical computer experiments",
                id: "homepage.meta.description",
                description: "Title of the homepage"
            })}>
            <Banner/>
            <main>
                <KoneIntro/>
                <Features/>
            </main>
        </Layout>
    );
}
