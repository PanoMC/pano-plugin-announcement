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

  /* Starts right of the box (padding = the box width) and moves its own width left; the duration comes from the
     admin's speed (px per second) and the measured width, so every text moves at the same speed. */
  .announcement-announcements__marquee-content {
    display: inline-block;
    padding-left: 100%;
    animation: announcement-marquee-animation var(--ann-duration, 20s) linear infinite;
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

  .announcement-announcements__item {
    display: inline;
  }

  .announcement-announcements__sep {
    display: inline-block;
    margin: 0 0.85em;
    opacity: 0.75;
  }

  .announcement-announcements__inline {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    row-gap: 0.25rem;
  }

  .announcement-announcements__fade {
    animation: announcement-fade-animation 0.45s ease both;
  }

  @keyframes announcement-fade-animation {
    from {
      opacity: 0;
      transform: translateY(4px);
    }
    to {
      opacity: 1;
      transform: none;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    .announcement-announcements__marquee-content {
      animation: none;
      white-space: normal;
      padding-left: 0;
    }

    .announcement-announcements__fade {
      animation: none;
    }
  }
</style>

{#if visibleBanners.length > 0}
  <div class="announcement-announcements announcement-announcements__banners vstack gap-2">
    {#each visibleBanners as banner (banner.id)}
      {@const o = optionsOf(banner)}
      {@const look = bannerLook(o)}
      {@const contents = banner.contents || []}
      {@const inline = contents.length > 1 && o.multiMode === 'INLINE'}
      {@const index = (activeContentIndex[banner.id] || 0) % Math.max(contents.length, 1)}
      <div
        id="pano-announcement-{banner.id}"
        class="announcement-banner announcement-announcements__alert alert {colourClass(banner, look)} overflow-hidden mb-0 {alignClass(banner)}"
        class:alert-dismissible={banner.closeable}
        style={look.style || null}
        role="alert">
        <div class="announcement-content" class:announcement-announcements__flash={isFlash(banner)}>
          {#if banner.effectType === 'MARQUEE'}
            <div class="announcement-announcements__marquee">
              {#key inline ? 'all' : index}
                <div
                  class="announcement-announcements__marquee-content"
                  use:marqueeSpeed={o.marqueeSpeed}
                  onanimationiteration={() => !inline && nextContent(banner)}>
                  {#if inline}
                    {#each contents as content, i}
                      {#if i > 0}<span class="announcement-announcements__sep" aria-hidden="true"
                          >{separatorText(o)}</span
                        >{/if}<span class="announcement-announcements__item">{@html content}</span>
                    {/each}
                  {:else}
                    <span class="announcement-announcements__item">{@html contents[index]}</span>
                  {/if}
                </div>
              {/key}
            </div>
          {:else if inline}
            <div class="announcement-announcements__inline {getJustifyClass(banner.textAlign)}">
              {#each contents as content, i}
                {#if i > 0}<span class="announcement-announcements__sep" aria-hidden="true"
                    >{separatorText(o)}</span
                  >{/if}<span class="announcement-announcements__item">{@html content}</span>
              {/each}
            </div>
          {:else}
            {#key index}
              <div class:announcement-announcements__fade={contents.length > 1}>
                {@html contents[index]}
              </div>
            {/key}
          {/if}
        </div>

        {#if banner.closeable}
          <button
            type="button"
            class="btn-close"
            class:btn-close-white={look.lightText}
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
          {@html `<style>${banner.customCss.replaceAll('<', '').replace(/\{selector\}/g, `#pano-announcement-${banner.id}`)}</style>`}
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
      <div class="modal-content overflow-hidden">
        <div class="announcement-announcements__header modal-header">
          <h5 class="modal-title">{modal.title}</h5>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            title={$_('buttons.close')}
            aria-label={$_('buttons.close')}></button>
        </div>

        <div class="announcement-announcements__body modal-body p-0 position-relative">
          {#if modal.imageFileName}
            <img
              src="/api/plugins/pano-plugin-announcement/announcements/image/{modal.imageFileName}"
              class="announcement-announcements__image img-fluid w-100 object-fit-cover"
              style="max-height: 400px;"
              alt={modal.title} />
          {/if}

          <div class="announcement-announcements__body-2 modal-body">
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
            {@html `<style>${modal.customCss.replaceAll('<', '').replace(/\{selector\}/g, `#announcementModal-${modal.id}`)}</style>`}
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
  import { parseOptions, bannerLook, separatorText } from './bannerLook.js';

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

  // Several contents shown one after the other: each stays the admin's display time. A marquee moves to the next one
  // when its pass ends instead (onanimationiteration).
  $effect(() => {
    const intervals = [];
    finalAnnouncements.forEach((a) => {
      if (a.type !== 'BANNER' || (a.contents?.length || 0) < 2 || a.effectType === 'MARQUEE') return;
      const o = optionsOf(a);
      if (o.multiMode === 'INLINE') return;
      activeContentIndex[a.id] = 0;
      intervals.push(setInterval(() => nextContent(a), Math.max(2, o.rotateSeconds) * 1000));
    });

    return () => {
      intervals.forEach(clearInterval);
    };
  });

  const optionsCache = new Map();

  function optionsOf(announcement) {
    const key = `${announcement.id}:${announcement.displayOptions ?? ''}`;
    if (!optionsCache.has(key)) optionsCache.set(key, parseOptions(announcement.displayOptions));
    return optionsCache.get(key);
  }

  function nextContent(announcement) {
    const count = announcement.contents?.length || 0;
    if (count < 2) return;
    activeContentIndex[announcement.id] = ((activeContentIndex[announcement.id] || 0) + 1) % count;
  }

  /** Sets the marquee's duration from its width so it moves at `speed` px per second, also after a resize. */
  function marqueeSpeed(node, speed) {
    let current = speed;
    const apply = () => {
      const distance = node.offsetWidth || 0;
      if (distance > 0) {
        node.style.setProperty('--ann-duration', `${Math.max(distance / Math.max(current || 60, 5), 2)}s`);
      }
    };
    apply();
    const observer = typeof ResizeObserver === 'function' ? new ResizeObserver(apply) : null;
    observer?.observe(node);
    return {
      update(next) {
        current = next;
        apply();
      },
      destroy() {
        observer?.disconnect();
      },
    };
  }

  /** The classic alert colour, or none when the admin picked another background (set inline by bannerLook). */
  function colourClass(banner, look) {
    return look.alert ? ALERT_CLASSES[banner.alertStyle || 'INFO'] : '';
  }

  /** A marquee moves across the whole width, so it has no alignment. */
  function alignClass(banner) {
    return banner.effectType === 'MARQUEE' ? '' : getTextAlignClass(banner.textAlign);
  }

  function isFlash(banner) {
    return banner.effectType === 'FLASH';
  }

  function getJustifyClass(align) {
    switch (align) {
      case 'LEFT':
        return 'justify-content-start';
      case 'RIGHT':
        return 'justify-content-end';
      default:
        return 'justify-content-center';
    }
  }

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
