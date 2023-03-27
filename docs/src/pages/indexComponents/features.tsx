import React from 'react';
import clsx from 'clsx';
import styles from './features.module.css';
import Translate, {translate} from '@docusaurus/Translate';

type FeatureItem = {
    title: string;
    Svg: React.ComponentType<React.ComponentProps<'svg'>>;
    description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
    {
        title: translate({
            message: 'Built on Kotlin Multiplatform',
            id: "homepage.features.kotlin.title",
            description: 'Title of "Built on Kotlin Multiplatform" block'
        }),
        Svg: require('@site/static/img/kotlin-logo.svg').default,
        description: (
            <Translate
                id="homepage.features.kotlin.content"
                description='Content of "Built on Kotlin Multiplatform" block'
            >
                {"Kone is designed using Kotlin Multiplatform, so it's available on JVM, JS and Native platforms."}
            </Translate>
        ),
    },
    {
        title: translate({
            message: 'Abstracted with algebraic contexts',
            id: "homepage.features.contexts.title",
            description: 'Title of "Algebraic contexts" block'
        }),
        Svg: require('@site/static/img/integers-with-square-root-of-5.svg').default,
        description: (
            <Translate
                id="homepage.features.contexts.content"
                description='Content of "Algebraic contexts" block'
            >
                {
                    `All algebraic algorithms in Kone are implemented on abstract algebraic contexts where possible.
                    So you can easily use them as blocks of Lego.`
                }
            </Translate>
        ),
    },
];

function Feature({title, Svg, description}: FeatureItem) {
    return (
        <div className={clsx('col col--4')}>
            <div className="text--center">
                <Svg className={styles.featureSvg} role="img" />
            </div>
            <div className="text--center padding-horiz--md">
                <h3>{title}</h3>
                <p>{description}</p>
            </div>
        </div>
    );
}

export default function HomepageFeatures(): JSX.Element {
    return (
        <section className={styles.features}>
            <div className="container">
                <h2 className={clsx("hero__title", styles.featuresHeader)}>
                    <Translate
                        id="homepage.features.title"
                        description="Title of features section"
                    >
                        {"Main features"}
                    </Translate>
                </h2>
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    );
}
