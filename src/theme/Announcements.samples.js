// Sample data of Announcements for the view catalogue (doc 02 section 7). Pure data: import only view helpers and
// relative .js fixtures.
/** @type {string[]} */
export const notApplicable = ['error', 'loading'];

/** @type {import('@panomc/plugin-kit').Samples} */
export default {
  filled: {
    props: {
      hookName: 'theme:top',
      announcements: [
        {
          id: 1,
          type: 'BANNER',
          location: 'GLOBAL',
          alertStyle: 'INFO',
          textAlign: 'CENTER',
          effectType: 'NONE',
          closeable: true,
          title: 'Weekend event',
          contents: ['<strong>Double XP</strong> all weekend on the survival server.'],
          link: null,
          external: false,
          customCss: null,
        },
        {
          id: 2,
          type: 'BANNER',
          location: 'GLOBAL',
          alertStyle: 'WARNING',
          textAlign: 'LEFT',
          effectType: 'MARQUEE',
          closeable: false,
          title: 'Maintenance',
          contents: ['Scheduled maintenance tonight at 22:00 UTC.'],
          link: null,
          external: false,
          customCss: null,
        },
      ],
    },
  },
  empty: {
    props: {
      hookName: 'theme:top',
      announcements: [],
    },
  },
};
