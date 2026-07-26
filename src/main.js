import {PanoPlugin, viewComponent} from '@panomc/sdk';
import {derived} from 'svelte/store';
import {_ as i18n} from '@panomc/sdk/utils/language';
import { showToast } from '@panomc/sdk/toasts';

const pluginId = 'pano-plugin-announcement';

export const _ = derived(i18n, ($_fn) => {
  return (key, options) => $_fn(`plugins.${pluginId}.${key}`, options);
});

// Success/failure colouring for this plugin's toasts, matching the panel. showToast from
// @panomc/sdk/toasts is the host panel's ToastContainer `show`, whose signature is
// (text, params, toastComponent, options): passing undefined for toastComponent keeps the
// host's DefaultToast, and options.variant maps to Bootstrap's text-success / text-danger.
// These live here rather than in @panomc/sdk/toasts because this plugin is pinned to
// @panomc/sdk 1.0.0-dev.39, which predates the variants; they can be dropped for a direct
// SDK import once that pin moves. On an older panel build the extra argument is ignored and
// the toast renders neutral, so this degrades instead of breaking.
export function showSuccessToast(text, params = {}) {
  return showToast(text, params, undefined, { variant: 'success' });
}

export function showErrorToast(text, params = {}) {
  return showToast(text, params, undefined, { variant: 'danger' });
}

export default class PanoAnnouncementPlugin extends PanoPlugin {
  onLoad() {
    const pano = this.pano;

    if (pano.isPanel) {
      pano.ui.page.register({
        path: '/announcements',
        component: viewComponent(() => import('./panel/AnnouncementsPage.svelte')),
        permission: `pano.plugin.${pluginId}.manage.announcements`,
      });

      pano.ui.nav.site.editNavLinks((navigationItems) => {
        const postsIndex = navigationItems.findIndex((item) => item.href === '/posts');
        const announcementsLink = {
          href: '/announcements',
          icon: 'fas fa-bullhorn',
          text: `plugins.${pluginId}.components.site-navigation-menu.announcements`,
          startsWith: false,
          permission: `pano.plugin.${pluginId}.manage.announcements`,
        };

        if (postsIndex !== -1) {
          navigationItems.splice(postsIndex + 1, 0, announcementsLink);
        } else {
          navigationItems.push(announcementsLink);
        }

        return navigationItems;
      });
    } else {
      const announcementsComponent = viewComponent(() => import('./theme/Announcements.svelte'));

      pano.ui.app.onLoad(async (data, event) => {
        const { load } = await import('./theme/Announcements.svelte');
        const res = await load({ ...event, hookName: 'page:home:top' });
        data.announcements = res.announcements || [];

        if (res.hookOptions?.invisible) {
          pano.ui.hook.setVisible('page:home:top', announcementsComponent, false);
        }
      });

      pano.ui.hook.register({
        name: 'theme:top',
        component: announcementsComponent,
        skipLoad: true,
      });
      pano.ui.hook.register({
        name: 'page:home:top',
        component: announcementsComponent,
        skipLoad: true,
      });
    }
  }

  onContextUpdate(ctx) {}

  onUnload() {}
}
