// Plugin-level options of the Pano plugin kit (@panomc/plugin-kit). The namespace is `announcement`
// (the plugin id minus `pano-plugin-`); the only view file sits in src/theme, so none moved.
export default {
  viewDirs: ['src/theme'],
  styles: {
    // The inline styles of Announcements.svelte (close button z-index and filter, modal image height) stay as written: an
    // inline style keeps winning over Bootstrap's dark palette rule for .btn-close, which a layered class could not do.
    styleAttrAllow: ['Announcements'],
  },
};
