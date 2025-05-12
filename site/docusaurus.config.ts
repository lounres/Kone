import {koneUrl, koneBaseUrl} from './inputData'
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';
import {themes} from 'prism-react-renderer'
import remarkMath from 'remark-math'
import rehypeKatex from 'rehype-katex'

const config: Config = {
    title: 'Kone',
    tagline: 'Making pure math computations available',
    favicon: '/img/logos/kone-mark-themed.svg',

    url: koneUrl,
    baseUrl: koneBaseUrl,

    onBrokenLinks: 'warn',
    onBrokenAnchors: 'warn',
    onBrokenMarkdownLinks: 'warn',
    onDuplicateRoutes: 'warn',

    i18n: {
        defaultLocale: 'en',
        locales: ['en', 'ru'],
        localeConfigs: {
            en: {
                label: 'English',
                direction: 'ltr',
                htmlLang: 'en-GB',
                calendar: 'gregory',
                path: 'en',
            },
            ru: {
                label: 'Русский',
                direction: 'ltr',
                htmlLang: 'ru',
                calendar: 'gregory',
                path: 'ru',
            },
        },
    },

    presets: [
        [
            'classic',
            {
                docs: {
                    path: "docs",
                    routeBasePath: '/',
                    sidebarPath: './docsSidebars.ts',
                    remarkPlugins: [remarkMath],
                    rehypePlugins: [rehypeKatex],
                    versions: {
                        current: {
                            label: '🏗 Experiment'
                        }
                    }
                },
                blog: {
                    showReadingTime: true,
                    feedOptions: {
                        type: ['rss', 'atom'],
                        xslt: true,
                    },
                    onInlineTags: 'warn',
                    onInlineAuthors: 'warn',
                    onUntruncatedBlogPosts: 'warn',
                    remarkPlugins: [remarkMath],
                    rehypePlugins: [rehypeKatex],
                },
                theme: {
                    customCss: './src/css/custom.css',
                },
            } satisfies Preset.Options,
        ],
    ],

    stylesheets: [
        {
            href: 'https://cdn.jsdelivr.net/npm/katex@0.16.21/dist/katex.min.css',
            type: 'text/css',
            integrity: 'sha384-zh0CIslj+VczCZtlzBcjt5ppRcsAmDnRem7ESsYwWwg3m/OaJ2l4x7YBZl9Kxxib',
            crossorigin: 'anonymous',
        },
    ],

    themeConfig: {
        image: 'img/logos/kone-logo-colored.png',
        // metadata: [],
        // announcementBar: {},
        docs: {
            sidebar: {
                hideable: true,
            }
        },
        navbar: {
            title: 'Kone',
            logo: {
                alt: 'Kone site logo',
                src: 'img/logos/kone-mark-violet.svg',
                srcDark: 'img/logos/kone-mark-orange.svg',
            },
            items: [
                // {
                //     type: 'docSidebar',
                //     sidebarId: 'tutorials',
                //     position: 'left',
                //     label: 'Tutorials',
                // },
                {
                    type: 'docSidebar',
                    sidebarId: 'docs',
                    position: 'left',
                    label: 'Docs',
                },
                {
                    href: `${koneUrl}${koneBaseUrl}api/`,
                    label: 'API',
                    position: 'left',
                },
                // {
                //     type: 'docSidebar',
                //     sidebarId: 'algorithms',
                //     position: 'left',
                //     label: 'Algorithms',
                // },
                {
                    to: '/blog',
                    label: 'Blog',
                    position: 'left'
                },
                {
                    href: 'https://lounres.dev/MEDia',
                    label: 'MEDia',
                    position: 'left'
                },
                // Right Side
                // { // TODO
                //     type: 'docsVersionDropdown',
                //     position: 'right',
                // },
                // { // TODO
                //   type: 'localeDropdown',
                //   position: 'right',
                // },
                {
                    href: 'https://github.com/lounres/Kone',
                    position: 'right',
                    className: 'header-github-link',
                    'aria-label': 'GitHub repository',
                },
            ],
            hideOnScroll: true,
        },
        footer: {
            style: 'dark',
            // links: [ // TODO
            //   {
            //     title: 'Docs',
            //     items: [
            //       {
            //         label: 'Tutorial',
            //         to: '/docs/intro',
            //       },
            //     ],
            //   },
            //   {
            //     title: 'Community',
            //     items: [
            //       {
            //         label: 'Stack Overflow',
            //         href: 'https://stackoverflow.com/questions/tagged/docusaurus',
            //       },
            //       {
            //         label: 'Discord',
            //         href: 'https://discordapp.com/invite/docusaurus',
            //       },
            //       {
            //         label: 'Twitter',
            //         href: 'https://twitter.com/docusaurus',
            //       },
            //     ],
            //   },
            //   {
            //     title: 'More',
            //     items: [
            //       {
            //         label: 'Blog',
            //         to: '/blog',
            //       },
            //       {
            //         label: 'GitHub',
            //         href: 'https://github.com/facebook/docusaurus',
            //       },
            //     ],
            //   },
            // ],
            copyright: `Copyright © ${new Date().getFullYear()} Gleb Minaev <br> All rights reserved. Licensed under the Apache License, Version 2.0 <br> Built with Docusaurus.`,
        },
        prism: {
            defaultLanguage: 'kotlin',
            additionalLanguages: ['kotlin', 'groovy', 'markup'],
            theme: themes.github,
            darkTheme: themes.dracula,
        },
    },
}

export default config
