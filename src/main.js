import { PanoPlugin, viewComponent } from "@panomc/sdk";

export default class PanoAnnouncementPlugin extends PanoPlugin {
  onLoad() {
    const pano = this.pano

    console.log("announcement enabled", "isPanel:" + pano.isPanel);

    if (pano.isPanel) {
      pano.ui.page.register({
        path: "/announcements",
        component: viewComponent(() => import("./panel/AnnouncementsPage.svelte")),
        layout: viewComponent(() => import("./panel/AnnouncementsLayout.svelte")),
        resetLayout: false,
      });

      pano.ui.nav.site.editNavLinks((navigationItems) => {
        const postsIndex = navigationItems.findIndex(
          (item) => item.href === "/posts",
        );
        const announcementsLink = {
          href: "/announcements",
          icon: "fas fa-bullhorn",
          text: "plugins.pano-plugin-announcement.components.site-navigation-menu.announcements",
          startsWith: false,
        };

        if (postsIndex !== -1) {
          navigationItems.splice(postsIndex + 1, 0, announcementsLink);
        } else {
          navigationItems.push(announcementsLink);
        }

        return navigationItems;
      });
    }
  }

  onContextUpdate(ctx) {
  }

  onUnload() {
  }
}