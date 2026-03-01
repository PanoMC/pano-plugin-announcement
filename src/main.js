import {PanoPlugin, viewComponent} from '@panomc/sdk';
import {derived} from 'svelte/store';
import {_ as i18n} from '@panomc/sdk/utils/language';

const pluginId = 'pano-plugin-announcement';

export const _ = derived(i18n, ($_fn) => {
  return (key, options) => $_fn(`plugins.${pluginId}.${key}`, options);
});

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
