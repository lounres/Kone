// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Kone',
  tagline: 'Making pure math computations available',
  url: 'https://lounres.github.io',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/logos/kone-logo-original-orange.svg',

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
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links. // TODO
          editUrl:
            'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links. // TODO
          editUrl:
            'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
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
          src: 'img/logos/kone-logo-original-violet.svg',
          srcDark: 'img/logos/kone-logo-original-orange.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'intro',
            position: 'left',
            label: 'Tutorial',
          },
          {to: '/blog', label: 'Blog', position: 'left'},
          {
            type: 'docsVersionDropdown',
            position: 'right',
          },
          {
            type: 'localeDropdown',
            position: 'right',
          },
          {
            href: 'https://github.com/lounres/Kone',
            position: 'right',
            className: 'header-github-link',
            'aria-label': 'GitHub repository',
          },
          // {
          //   href: 'https://github.com/lounres/Kone',
          //   label: 'GitHub',
          //   position: 'right',
          // },
          {
            type: 'search',
            position: 'right',
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
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
      },
    }),
};

module.exports = config;
