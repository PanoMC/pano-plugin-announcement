<style>
  .announcements-container {
    z-index: 1100;
  }

  .announcement-banner {
    transition: all 0.3s ease;
  }

  .flash-container {
    animation: flash-animation 1s infinite alternate;
  }

  @keyframes flash-animation {
    from {
      opacity: 1;
    }
    to {
      opacity: 0.6;
    }
  }

  .announcement-modal-content :global(img) {
    max-width: 100%;
    height: auto;
  }
</style>

{#if visibleBanners.length > 0}
  <div class="announcements-container">
    {#each visibleBanners as banner (banner.id)}
      <div
        id="pano-announcement-{banner.id}"
        class="announcement-banner alert alert-info border-0 rounded-0 m-0 p-2 position-relative overflow-hidden"
        class:alert-dismissible={banner.closeable}
        role="alert">
        <div class="d-flex align-items-center justify-content-center w-100">
          <div class="announcement-content text-center flex-grow-1">
            {#if banner.effectType === "MARQUEE"}
              <!-- svelte-ignore a11y-distracting-elements -->
              <marquee behavior="scroll" direction="left" scrollamount="5">
                {@html banner.contents[activeContentIndex[banner.id] || 0]}
              </marquee>
            {:else if banner.effectType === "FLASH"}
              <div class="flash-container">
                {@html banner.contents[activeContentIndex[banner.id] || 0]}
              </div>
            {:else}
              {@html banner.contents[activeContentIndex[banner.id] || 0]}
            {/if}
          </div>

          {#if banner.closeable}
            <button
              type="button"
              class="btn-close position-relative"
              style="z-index: 2;"
              onclick={(e) => {
                e.stopPropagation();
                closeBanner(banner.id);
              }}
              aria-label="Close"></button>
          {/if}
        </div>

        {#if banner.link}
          <a href={banner.link} class="stretched-link" target={banner.external ? "_blank" : null} rel={banner.external ? "noopener noreferrer" : null}></a>
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
    class="modal fade"
    id="announcementModal-{modal.id}"
    tabindex="-1"
    aria-hidden="true"
    use:setupModal={modal}>
    <div
      class="modal-dialog {getModalSizeClass(
        modal.size,
      )} modal-dialog-centered">
      <div class="modal-content overflow-hidden border-0 shadow">
        <div class="modal-body p-0 position-relative">
          <button
            type="button"
            class="btn-close position-absolute top-0 end-0 m-3"
            style="z-index: 1050; filter: drop-shadow(0px 0px 2px rgba(255,255,255,0.8));"
            data-bs-dismiss="modal"
            aria-label="Close"></button>

          {#if modal.imageFileName}
            <img
              src="/api/announcements/image/{modal.imageFileName}"
              class="img-fluid w-100 object-fit-cover"
              style="max-height: 400px;"
              alt={modal.title} />
          {/if}

          <div class="p-4">
            <h4 class="mb-3">{modal.title}</h4>
            <div class="announcement-modal-content mb-4">
              {@html modal.contents[0]}
            </div>

            <div class="d-flex justify-content-end gap-2">
              {#if modal.link}
                <a href={modal.link} class="btn btn-primary px-4" target={modal.external ? "_blank" : null} rel={modal.external ? "noopener noreferrer" : null}
                  >{$_("buttons.visit")}</a>
              {/if}
            </div>
          </div>

          {#if modal.customCss}
            {@html `<style>${modal.customCss.replace(/\{selector\}/g, `#announcementModal-${modal.id}`)}</style>`}
          {/if}
        </div>
      </div>
    </div>
  </div>
{/each}

<script context="module">
  import ApiUtil from "@panomc/sdk/utils/api";

  export async function load(event) {
    if (!event) return { announcements: [] };

    try {
      const res = await ApiUtil.get({
        path: "/api/announcements",
        request: event,
      });
      return {
        announcements: res.data || [],
      };
    } catch (err) {
      console.error("[Announcements] Failed to fetch:", err);
      return { announcements: [] };
    }
  }
</script>

<script>
  import { _ } from "../main";
  import { page, browser, base } from "@panomc/sdk/svelte";

  let { announcements = [], hookName } = $props();

  let closedBanners = $state([]);
  let activeContentIndex = $state({});

  let visibleAnnouncements = $derived(
    announcements.filter((a) => {
      if (closedBanners.includes(a.id)) return false;
      if (a.type === "BANNER") {
        if (a.location === "HOME") {
          return hookName === "page:top";
        }
        if (a.location === "GLOBAL") {
          return hookName === "theme:top";
        }
      }
      return true;
    }),
  );

  let visibleBanners = $derived(
    visibleAnnouncements.filter((a) => a.type === "BANNER"),
  );
  let visibleModals = $derived(
    visibleAnnouncements.filter((a) => {
      if (a.type !== "MODAL") return false;
      if (hookName !== "theme:top") return false; // Modals only in theme:top
      if (a.displayFrequency === "ONCE") {
        if (browser) {
          return !localStorage.getItem(`announcement_modal_${a.id}`);
        }
      }
      return true;
    }),
  );

  $effect(() => {
    const intervals = [];
    announcements.forEach((a) => {
      if (a.type === "BANNER" && a.contents.length > 1) {
        activeContentIndex[a.id] = 0;
        const interval = setInterval(() => {
          activeContentIndex[a.id] =
            (activeContentIndex[a.id] + 1) % a.contents.length;
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
        return "modal-sm";
      case 2:
        return "modal-lg";
      case 3:
        return "modal-fullscreen";
      default:
        return "";
    }
  }

  function setupModal(node, modalData) {
    if (!browser) return;

    let bootstrapModal;
    let destroyed = false;

    async function init() {
      while (!window.bootstrap && !destroyed) {
        await new Promise((resolve) => setTimeout(resolve, 100));
      }

      if (destroyed || !window.bootstrap) return;

      bootstrapModal = new window.bootstrap.Modal(node);
      bootstrapModal.show();
    }

    init();

    if (modalData.displayFrequency === "ONCE") {
      localStorage.setItem(`announcement_modal_${modalData.id}`, "true");
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
