// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');
const math = require('remark-math');
const katex = require('rehype-katex');

/** @type {import('@docusaurus/types').Config} */
module.exports = {
  title: 'Kone',
  tagline: 'Making pure math computations available',
  url: 'https://lounres.github.io',
  baseUrl: '/Kone/docs/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: '/img/logos/kone-mark-themed.svg',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  // organizationName: 'facebook', // Usually your GitHub org/user name.
  // projectName: 'docusaurus', // Usually your repo name.

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
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
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          path: "docs",
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: 'https://github.com/lounres/Kone/tree/master/docs/',
          remarkPlugins: [math],
          rehypePlugins: [katex],
        },
        blog: {
          showReadingTime: true,
          remarkPlugins: [math],
          rehypePlugins: [katex],
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  stylesheets: [
    {
      href: 'https://cdn.jsdelivr.net/npm/katex@0.13.24/dist/katex.min.css',
      type: 'text/css',
      integrity: 'sha384-odtC+0UGzzFL/6PNoE8rX/SPcQDXBJ+uRepguP4QkPCm2LBxH3FA3y+fKSiJ+AmM',
      crossorigin: 'anonymous',
    },
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      image: 'img/logos/kone-logo-full-colored.png',
      // metadata: [],
      // announcementBar: {
      //   content: "Here we are!"
      // },
      docs: {
        sidebar: {
          hideable: true,
        }
      },
      navbar: {
        title: 'Kone',
        logo: {
          alt: 'Kone',
          src: 'img/logos/kone-mark-violet.svg',
          srcDark: 'img/logos/kone-mark-orange.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'index',
            label: 'Docs',
            position: 'left',
          },
          {
            href: `https://lounres.github.io/Kone/api`,
            label: 'API',
            position: 'left',
          },
          // { // TODO
          //   to: '/tutorials',
          //   label: 'Tutorials',
          //   position: 'left'
          // },
          {
            to: '/blog',
            label: 'Blog',
            position: 'left'
          },
          {
            href: 'https://github.com/lounres/math-kotlin-experiments', // Replace with 'https://lounres.github.io/math-kotlin-experiments',
            label: 'Math Kotlin Experiments',
            position: 'left'
          },
          // Right Side
          // { // TODO
          //   type: 'docsVersionDropdown',
          //   position: 'right',
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
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
      },
    }),
};
