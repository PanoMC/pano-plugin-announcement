<style>
  /* Kept rules: a z-index, the flash and marquee animations with the size and overflow the marquee needs. */
  .announcement-announcements__banners {
    z-index: 10;
  }

  .announcement-announcements__flash {
    animation: announcement-flash-animation 1s infinite alternate;
  }

  @keyframes announcement-flash-animation {
    from {
      opacity: 1;
    }
    to {
      opacity: 0.6;
    }
  }

  .announcement-announcements__marquee {
    overflow: hidden;
    white-space: nowrap;
    width: 100%;
  }

  .announcement-announcements__marquee-content {
    display: inline-block;
    padding-left: 100%;
    animation: announcement-marquee-animation 20s linear infinite;
  }

  .announcement-announcements__marquee-content:hover {
    animation-play-state: paused;
  }

  @keyframes announcement-marquee-animation {
    0% {
      transform: translateX(0);
    }
    100% {
      transform: translateX(-100%);
    }
  }

  @media (prefers-reduced-motion: reduce) {
    .announcement-announcements__marquee-content {
      animation: none;
      white-space: normal;
      padding-left: 0;
    }
  }
</style>

{#if visibleBanners.length > 0}
  <div class="announcement-announcements announcement-announcements__banners vstack gap-2">
    {#each visibleBanners as banner (banner.id)}
      <div
        id="pano-announcement-{banner.id}"
        class="announcement-banner announcement-announcements__alert alert {ALERT_CLASSES[banner.alertStyle || 'INFO']} overflow-hidden mb-0 {getTextAlignClass(banner.textAlign)}"
        class:alert-dismissible={banner.closeable}
        role="alert">
        <div class="announcement-content">
          {#if banner.effectType === 'MARQUEE'}
            <div class="announcement-announcements__marquee">
              <div class="announcement-announcements__marquee-content">
                {@html banner.contents[activeContentIndex[banner.id] || 0]}
              </div>
            </div>
          {:else if banner.effectType === 'FLASH'}
            <div class="announcement-announcements__flash">
              {@html banner.contents[activeContentIndex[banner.id] || 0]}
            </div>
          {:else}
            {@html banner.contents[activeContentIndex[banner.id] || 0]}
          {/if}
        </div>

        {#if banner.closeable}
          <button
            type="button"
            class="btn-close"
            style="z-index: 2;"
            onclick={(e) => {
              e.stopPropagation();
              closeBanner(banner.id);
            }}
            title={$_('buttons.close')}
            aria-label={$_('buttons.close')}></button>
        {/if}

        {#if banner.link}
          <a
            href={banner.link}
            class="stretched-link"
            title={banner.title}
            aria-label={banner.title}
            target={banner.external ? '_blank' : null}
            rel={banner.external ? 'noopener noreferrer' : null}></a>
        {/if}

        {#if banner.customCss}
          {@html `<style>${banner.customCss.replace(/\{selector\}/g, `#pano-announcement-${banner.id}`)}</style>`}
        {/if}
      </div>
    {/each}
  </div>
{/if}

{#each visibleModals as modal (modal.id)}
  <div
    class="announcement-announcements modal fade"
    id="announcementModal-{modal.id}"
    tabindex="-1"
    aria-hidden="true"
    use:setupModal={modal}>
    <div class="modal-dialog {getModalSizeClass(modal.size)} modal-dialog-centered">
      <div class="modal-content overflow-hidden border-0 shadow">
        <div class="announcement-announcements__body modal-body p-0 position-relative">
          <button
            type="button"
            class="btn-close position-absolute top-0 end-0 m-3"
            style="z-index: 1050; filter: drop-shadow(0px 0px 2px rgba(255,255,255,0.8));"
            data-bs-dismiss="modal"
            title={$_('buttons.close')}
            aria-label={$_('buttons.close')}></button>

          {#if modal.imageFileName}
            <img
              src="/api/plugins/pano-plugin-announcement/announcements/image/{modal.imageFileName}"
              class="announcement-announcements__image img-fluid w-100 object-fit-cover"
              style="max-height: 400px;"
              alt={modal.title} />
          {/if}

          <div class="announcement-announcements__body-2 modal-body">
            <h4>{modal.title}</h4>
            <div class="announcement-announcements__modal-content">
              {@html modal.contents[0]}
            </div>
          </div>

          {#if modal.link}
            <div class="announcement-announcements__footer modal-footer">
              <a
                href={modal.link}
                class="announcement-announcements__action btn btn-secondary w-100"
                target={modal.external ? '_blank' : null}
                rel={modal.external ? 'noopener noreferrer' : null}>{$_('buttons.visit')}</a>
            </div>
          {/if}

          {#if modal.customCss}
            {@html `<style>${modal.customCss.replace(/\{selector\}/g, `#announcementModal-${modal.id}`)}</style>`}
          {/if}
        </div>
      </div>
    </div>
  </div>
{/each}

<script module>
  // Mounts itself on both hooks; src/main.js only hides the empty home hook.
  export const view = { hook: ['theme:top', 'page:home:top'], skipLoad: true };
  import { api } from '@panomc/sdk/plugin-api';

  export async function load(event) {
    if (!event) return { announcements: [] };

    let output = {};

    try {
      const res = await api.get({
        path: '/announcements',
        request: event,
      });

      const anyAnnouncement = (res.items ?? []).find(
        (announcement) => announcement.type === 'BANNER' && announcement.location === 'HOME',
      );

      // this prevents extra gap due to hook container when there is no announcement
      if (event.hookName === 'page:home:top' && !anyAnnouncement) {
        output = { hookOptions: { invisible: true } };
      }

      output = {
        ...output,
        announcements: res.items || [],
      };
    } catch (err) {
      console.error('[Announcements] Failed to fetch:', err);
      output = { ...output, announcements: [] };
    }

    return output;
  }
</script>

<script>
  import { derived } from 'svelte/store';
  import { _ as i18n } from '@panomc/sdk/utils/language';
  import { page, browser, base } from '@panomc/sdk/svelte';

  // plugin translations: `$_('key')` reads `plugins.pano-plugin-announcement.key`
  const _ = derived(i18n, ($_fn) => (key, options) => $_fn(`plugins.pano-plugin-announcement.${key}`, options));

  // The alert style names the admin can pick, written as full class names so the build sees them.
  const ALERT_CLASSES = {
    PRIMARY: 'alert-primary',
    SECONDARY: 'alert-secondary',
    SUCCESS: 'alert-success',
    DANGER: 'alert-danger',
    WARNING: 'alert-warning',
    INFO: 'alert-info',
    LIGHT: 'alert-light',
    DARK: 'alert-dark',
  };

  let { announcements = [], hookName } = $props();

  let pageAnnouncements = $derived($page.data.announcements || []);
  let finalAnnouncements = $derived(announcements.length > 0 ? announcements : pageAnnouncements);

  let closedBanners = $state([]);
  let activeContentIndex = $state({});

  let visibleAnnouncements = $derived(
    finalAnnouncements.filter((a) => {
      if (closedBanners.includes(a.id)) return false;
      if (a.type === 'BANNER') {
        if (a.location === 'HOME') {
          return hookName === 'page:home:top';
        }
        if (a.location === 'GLOBAL') {
          return hookName === 'theme:top';
        }
      }
      return true;
    }),
  );

  let visibleBanners = $derived(visibleAnnouncements.filter((a) => a.type === 'BANNER'));
  let visibleModals = $derived(
    visibleAnnouncements.filter((a) => {
      if (a.type !== 'MODAL') return false;
      if (hookName !== 'theme:top') return false; // Modals only in theme:top
      if (a.displayFrequency === 'ONCE') {
        if (browser) {
          return !localStorage.getItem(`announcement_modal_${a.id}`);
        }
      }
      return true;
    }),
  );

  $effect(() => {
    const intervals = [];
    finalAnnouncements.forEach((a) => {
      if (a.type === 'BANNER' && a.contents.length > 1) {
        activeContentIndex[a.id] = 0;
        const interval = setInterval(() => {
          activeContentIndex[a.id] = (activeContentIndex[a.id] + 1) % a.contents.length;
        }, 5000);
        intervals.push(interval);
      }
    });

    return () => {
      intervals.forEach(clearInterval);
    };
  });

  function closeBanner(id) {
    if (!closedBanners.includes(id)) {
      closedBanners.push(id);
    }
  }

  function getModalSizeClass(size) {
    switch (size) {
      case 0:
        return 'modal-sm';
      case 2:
        return 'modal-lg';
      case 3:
        return 'modal-fullscreen';
      default:
        return '';
    }
  }

  function getTextAlignClass(align) {
    switch (align) {
      case 'LEFT':
        return 'text-start';
      case 'RIGHT':
        return 'text-end';
      case 'CENTER':
      default:
        return 'text-center';
    }
  }

  function setupModal(node, modalData) {
    if (!browser) return;

    let bootstrapModal;
    let destroyed = false;

    function init() {
      if (destroyed || !window.bootstrap) return;

      bootstrapModal = new window.bootstrap.Modal(node);
      bootstrapModal.show();
    }

    init();

    if (modalData.displayFrequency === 'ONCE') {
      localStorage.setItem(`announcement_modal_${modalData.id}`, 'true');
    }

    return {
      destroy() {
        destroyed = true;
        if (bootstrapModal) {
          bootstrapModal.hide();
          bootstrapModal.dispose();
        }
      },
    };
  }
</script>
