// export {default as test} from "./Component.svelte";

let pano;

export async function onLoad(panoApi) {
    pano = panoApi;
}

export async function onEnable() {
  console.log("announcement enabled", "isPanel:" + pano.isPanel)

  if (pano.isPanel) {
    pano.ui.page.register({
      path: "/announcements",
      component: () => import("./panel/AnnouncementsPage.svelte"),
      resetLayout: false,
    });

    pano.ui.nav.site.editNavLinks((navigationItems) => {
      const postsIndex = navigationItems.findIndex(
        (item) => item.href === "/posts",
      );
      const annoucementsLink = {
        href: "/announcements",
        icon: "fas fa-bullhorn",
        text: "plugins.pano-plugin-announcement.components.site-navigation-menu.announcements",
        startsWith: false,
      };

      if (postsIndex !== -1) {
        navigationItems.splice(postsIndex + 1, 0, annoucementsLink);
      } else {
        navigationItems.push(annoucementsLink);
      }

      return navigationItems;
    });
  }
}