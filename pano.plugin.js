// Plugin-level options of the Pano plugin kit (@panomc/plugin-kit). The namespace is `announcement`
// (the plugin id minus `pano-plugin-`); the only view file sits in src/theme, so none moved.
export default {
  viewDirs: ['src/theme'],
  styles: {
    // The banner close button and modal image use inline styles.
    styleAttrAllow: ['Announcements'],
  },
};
