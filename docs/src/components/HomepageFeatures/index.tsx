import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

type FeatureItem = {
    title: string;
    Svg: React.ComponentType<React.ComponentProps<'svg'>>;
    description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
    {
        title: 'Built on Kotlin Multiplatform',
        Svg: require('@site/static/img/kotlin-logo.svg').default,
        description: (
            <>
                Kone is designed using Kotlin Multiplatform, so it's available on JVM, JS and Native platforms.
            </>
        ),
    },
    {
        title: 'Abstracted with algebraic contexts',
        Svg: require('@site/static/img/integers-with-square-root-of-5.svg').default,
        description: (
            <>
                All algebraic algorithms in Kone are implemented on abstract algebraic contexts where possible.
                So you can easily use them as blocks of Lego.
            </>
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
                <h2 className={clsx("hero__title", styles.featuresHeader)}>Main features</h2>
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    );
}
