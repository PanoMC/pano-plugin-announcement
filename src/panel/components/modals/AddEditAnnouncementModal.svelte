<!-- Add / Edit Announcement Modal -->
<div
  class="modal fade"
  bind:this={$modalElement}
  tabindex="-1"
  role="dialog"
  aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          {$mode === "edit" ? $_('modals.add-edit.edit-title') : $_('modals.add-edit.new-title')}
        </h5>
        <button
          type="button"
          class="btn-close"
          aria-label={$_('modals.add-edit.close')}
          title={$_('modals.add-edit.close')}
          on:click={hide}></button>
      </div>
      <div class="modal-body">
        <div class="row">
          <div class="col-md-5 border-end">
            <div class="input-group mb-3">
              {#if $mode === "edit"}
                <span class="input-group-text">#{$announcement.id}</span>
              {/if}
              <div class="form-floating flex-grow-1">
                <input
                  type="text"
                  class="form-control form-control-lg"
                  id="title"
                  bind:value={$announcement.title}
                  placeholder={$_('modals.add-edit.title-placeholder')}
                  required />
                <label for="title">{$_('modals.add-edit.title-label')}</label>
              </div>
            </div>
            <div class="form-floating mb-3">
              <select
                class="form-select"
                id="status"
                bind:value={$announcement.status}>
                <option value="ACTIVE">{$_('modals.add-edit.status-active')}</option>
                <option value="DRAFT">{$_('modals.add-edit.status-inactive')}</option>
              </select>
              <label for="status">{$_('modals.add-edit.status')}</label>
            </div>
            <div class="mb-3">
              <label class="form-label" for="timePermanent">{$_('modals.add-edit.time-type')}</label>
              <div class="btn-group w-100" role="group">
                <input
                  type="radio"
                  class="btn-check"
                  id="timePermanent"
                  autocomplete="off"
                  bind:group={$announcement.timeType}
                  value="PERMANENT" />
                <label class="btn btn-outline-primary" for="timePermanent">
                  {$_('modals.add-edit.time-permanent')}
                </label>

                <input
                  type="radio"
                  class="btn-check"
                  id="timeTimed"
                  autocomplete="off"
                  bind:group={$announcement.timeType}
                  value="TIMED" />
                <label class="btn btn-outline-primary" for="timeTimed">
                  {$_('modals.add-edit.time-timed')}
                </label>
              </div>
            </div>
            {#if $announcement.timeType === "TIMED"}
              <div class="form-floating mb-3">
                <input
                  type="datetime-local"
                  class="form-control"
                  id="time"
                  bind:value={$announcement.time}
                  placeholder={$_('modals.add-edit.time-select')}
                  required />
                <label for="time">{$_('modals.add-edit.time-select')}</label>
              </div>
            {/if}

            {#if $announcement.displayType === "BANNER" || $announcement.displayType === "MODAL"}
              <div class="form-floating mb-3">
                <input
                  type="text"
                  class="form-control"
                  id="link"
                  bind:value={$announcement.link}
                  placeholder={$_('modals.add-edit.link-placeholder')} />
                <label for="link">{$_('modals.add-edit.link')}</label>
              </div>
            {/if}

            <div class="form-floating mb-3">
              <select
                class="form-select"
                id="displayType"
                bind:value={$announcement.displayType}>
                <option value="BANNER">{$_('modals.add-edit.type-banner')}</option>
                <option value="MODAL">{$_('modals.add-edit.type-modal')}</option>
              </select>
              <label for="displayType">{$_('modals.add-edit.type')}</label>
            </div>

            {#if $announcement.displayType === "BANNER"}
              <div class="form-floating mb-3">
                <select
                  class="form-select"
                  id="bannerEffect"
                  bind:value={$announcement.bannerEffect}>
                  <option value="NONE">{$_('modals.add-edit.effect-none')}</option>
                  <option value="MARQUEE">{$_('modals.add-edit.effect-marquee')}</option>
                  <option value="FLASH">{$_('modals.add-edit.effect-flash')}</option>
                </select>
                <label for="bannerEffect">{$_('modals.add-edit.effects')}</label>
              </div>
            {/if}

            {#if $announcement.displayType === "MODAL"}
              <div class="mb-3">
                <label class="form-label" for="modalFrequencyPermanent"
                >{$_('modals.add-edit.display')}</label>
                <div class="btn-group w-100" role="group">
                  <input
                    type="radio"
                    class="btn-check"
                    id="modalFrequencyPermanent"
                    autocomplete="off"
                    bind:group={$announcement.modalDisplayFrequency}
                    value="ALWAYS" />
                  <label
                    class="btn btn-outline-primary"
                    for="modalFrequencyPermanent">
                    {$_('modals.add-edit.display-always')}
                  </label>

                  <input
                    type="radio"
                    class="btn-check"
                    id="modalFrequencyOnce"
                    autocomplete="off"
                    bind:group={$announcement.modalDisplayFrequency}
                    value="ONCE" />
                  <label class="btn btn-outline-primary" for="modalFrequencyOnce">
                    {$_('modals.add-edit.display-once')}
                  </label>
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label" for="modalSizeNormal">{$_('modals.add-edit.modal-size')}</label>
                <div class="btn-group w-100" role="group">
                  <input
                    type="radio"
                    class="btn-check"
                    id="modalSizeSmall"
                    autocomplete="off"
                    bind:group={$announcement.modalSize}
                    value="SMALL" />
                  <label class="btn btn-outline-primary" for="modalSizeSmall">
                    {$_('modals.add-edit.modal-size-small')}
                  </label>

                  <input
                    type="radio"
                    class="btn-check"
                    id="modalSizeNormal"
                    autocomplete="off"
                    bind:group={$announcement.modalSize}
                    value="NORMAL" />
                  <label class="btn btn-outline-primary" for="modalSizeNormal">
                    {$_('modals.add-edit.modal-size-normal')}
                  </label>

                  <input
                    type="radio"
                    class="btn-check"
                    id="modalSizeLarge"
                    autocomplete="off"
                    bind:group={$announcement.modalSize}
                    value="LARGE" />
                  <label class="btn btn-outline-primary" for="modalSizeLarge">
                    {$_('modals.add-edit.modal-size-large')}
                  </label>

                  <input
                    type="radio"
                    class="btn-check"
                    id="modalSizeFull"
                    autocomplete="off"
                    bind:group={$announcement.modalSize}
                    value="FULL" />
                  <label class="btn btn-outline-primary" for="modalSizeFull">
                    {$_('modals.add-edit.modal-size-full')}
                  </label>
                </div>
              </div>

              <div class="mb-4">
                <label class="form-label" for="modalImage"> {$_('modals.add-edit.image')} </label>
                <div class="position-relative w-100">
                  {#if displayImage}
                    <div class="ratio ratio-16x9 border rounded overflow-hidden">
                      <button
                        type="button"
                        class="btn border-0 shadow-none w-100 h-100 p-0 bg-transparent"
                        use:tooltip={[$_('modals.add-edit.image-change'), { placement: "bottom" }]}
                        on:click={() => modalImageInput.click()}>
                        <img
                          src={displayImage}
                          class="img-fluid w-100 h-100 object-fit-contain"
                          alt={$_('modals.add-edit.image')} />
                      </button>
                    </div>
                    <button
                      type="button"
                      class="btn btn-sm btn-danger position-absolute top-0 start-100 translate-middle"
                      on:click={onRemoveImage}
                      title={$_('modals.add-edit.image-remove')}
                      aria-label={$_('modals.add-edit.image-remove')}>
                      <i class="fas fa-minus"></i>
                    </button>
                  {:else}
                    <div class="list-group w-100">
                      <button
                        type="button"
                        class="btn w-100 list-group-item list-group-item-action drop-zone d-flex flex-column align-items-center justify-content-center border rounded shadow-none m-0"
                        class:drag-over={dropZoneActive}
                        style="height: 180px; cursor: pointer;"
                        on:click={() => modalImageInput.click()}
                        on:drop={handleDrop}
                        on:dragover={handleDragOver}
                        on:dragleave={handleDragLeave}>
                        <i class="fas fa-image fa-3x mb-3 opacity-50"></i>
                        <p class="mb-0 opacity-75 fw-medium">{$_('modals.add-edit.image-drop-placeholder')}</p>
                        <small class="opacity-50 text-uppercase fw-semibold" style="font-size: 0.7rem; letter-spacing: 0.5px;">{$_('modals.add-edit.image-format-info')}</small>
                      </button>
                    </div>
                  {/if}
                </div>
                <input
                  class="d-none"
                  id="modalImage"
                  type="file"
                  bind:files={modalImageFiles}
                  on:change={onModalImageChange}
                  bind:this={modalImageInput}
                  accept="image/png,image/jpeg,image/gif,image/webp" />
              </div>
            {/if}
          </div>
          <div class="col-md-7">
            <div class="mb-3">
              {#if $announcement.displayType === "BANNER"}
                <div class="d-flex justify-content-between align-items-center mb-2 gap-2">
                  <div class="overflow-x-auto flex-grow-1" style="scrollbar-width: thin;">
                    <div class="btn-group btn-group-sm flex-nowrap mb-1" role="group">
                      {#each $announcement?.contents || [] as item, i}
                        <button
                          type="button"
                          class="btn {activeContentIndex === i ? 'btn-primary' : 'btn-outline-primary'} text-nowrap"
                          on:click={() => (activeContentIndex = i)}>
                          {$_('modals.add-edit.content-tab', { values: { index: i + 1 } })}
                        </button>
                      {/each}
                      <button
                        type="button"
                        class="btn btn-outline-primary"
                        on:click={addContent}
                        title={$_('modals.add-edit.add-content')}>
                        <i class="fas fa-plus"></i>
                      </button>
                    </div>
                  </div>
                  {#if ($announcement?.contents?.length || 0) > 1}
                    <button
                      type="button"
                      class="btn btn-sm btn-link"
                      on:click={removeActiveContent}
                      title={$_('modals.add-edit.remove-content')}>
                      <i class="fas fa-trash"></i>
                    </button>
                  {/if}
                </div>
                {#key activeContentIndex}
                  {#if $announcement?.contents}
                    <Editor
                      bind:content={$announcement.contents[activeContentIndex]}
                      bind:isEmpty={isEditorEmpty}
                      showHtml={true}
                      contentStyles={"height: 300px;"} />
                  {/if}
                {/key}
              {:else}
                {#if $announcement?.contents}
                  <Editor
                    bind:content={$announcement.contents[0]}
                    bind:isEmpty={isEditorEmpty}
                    showHtml={true}
                    contentStyles={"height: 300px;"} />
                {/if}
              {/if}
            </div>
            <div class="form-floating">
              <textarea
                class="form-control"
                id="customCss"
                bind:value={$announcement.customCss}
                placeholder={$_('modals.add-edit.custom-css-placeholder')}
                style="height: 120px;"></textarea>
              <label for="customCss">{$_('modals.add-edit.custom-css')}</label>
            </div>
            <small class="text-muted d-block mt-1">
              {@html $_('modals.add-edit.custom-css-info', { values: { selector: `<code>#pano-announcement-${$announcement.id || 'ID'}</code>` } })}
            </small>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button
          type="button"
          class="btn btn-primary w-100"
          class:disabled={loading || !isFormValid || ($mode === "edit" && !isChanged)}
          disabled={loading || !isFormValid || ($mode === "edit" && !isChanged)}
          on:click={onSaveAnnouncement}>
          {$mode === "create" ? $_('modals.add-edit.btn-create') : $_('modals.add-edit.btn-save')}
        </button>
      </div>
    </div>
  </div>
</div>

<script context="module">
  import { writable, get } from "svelte/store";

  const modalElement = writable();
  const mode = writable("create");
  const announcement = writable({});
  const initialAnnouncement = writable({});
  const modalImage = writable(null);
  const isImageRemoved = writable(false);
  const selectedFile = writable(null);

  let callback = (announcement) => {
  };
  let hideCallback = (announcement) => {
  };
  let modal;

  export function show(newMode, newAnnouncement = null) {
    mode.set(newMode);
    modalImage.set(null);
    isImageRemoved.set(false);
    selectedFile.set(null);

    if (newAnnouncement) {
      const cloned = JSON.parse(JSON.stringify(newAnnouncement));
      const state = {
        modalSize: "NORMAL",
        modalDisplayFrequency: "ALWAYS",
        ...cloned,
        contents: cloned.contents || [cloned.content || ""]
      };
      announcement.set(state);
      initialAnnouncement.set(JSON.parse(JSON.stringify(state)));
    } else {
      announcement.set({
        id: null,
        title: "",
        status: "ACTIVE",
        timeType: "PERMANENT",
        time: "",
        displayType: "BANNER",
        content: "",
        customCss: "",
        link: "",
        bannerEffect: "NONE",
        contents: [""],
        modalDisplayFrequency: "ALWAYS",
        modalSize: "NORMAL"
      });
    }

    modal = new window.bootstrap.Modal(get(modalElement), {
      backdrop: "static",
      keyboard: false
    });
    modal.show();
  }

  export function hide() {
    hideCallback(get(announcement));
    modal.hide();
  }

  export function setCallback(newCallback) {
    callback = newCallback;
  }

  export function onHide(newCallback) {
    hideCallback = newCallback;
  }
</script>

<script>
  import ApiUtil from "@panomc/sdk/utils/api"
  import { Editor } from "@panomc/sdk/components";
  import { base } from "@panomc/sdk/svelte";
  import tooltip from "@panomc/sdk/utils/tooltip";
  import { showToast } from "@panomc/sdk/toasts";
  import { _ } from "../../../main";

  let isEditorEmpty = true;
  let activeContentIndex = 0;
  let loading = false;

  let dropZoneActive = false;

  $: displayImage = $modalImage || ($isImageRemoved ? null : ($announcement.imageFileName ? `${base}/api/panel/announcements/image/${$announcement.imageFileName}` : null));

  $: isFormValid = (() => {
    if (!$announcement.title || $announcement.title.trim() === "") return false;

    if (!$announcement.contents || $announcement.contents.length === 0 || $announcement.contents.every(c => !c || c.trim() === "")) return false;

    if ($announcement.displayType === "MODAL") {
      if ($announcement.contents.length > 1) return false;
      if ($mode === "create" && !$selectedFile) return false;
    }

    return true;
  })();

  $: isChanged = $mode === "create" ||
    JSON.stringify($announcement) !== JSON.stringify($initialAnnouncement) ||
    $selectedFile !== null ||
    $isImageRemoved;

  $: if ($announcement.displayType !== "BANNER") {
    activeContentIndex = 0;
    if ($announcement.contents && $announcement.contents.length > 1) {
      $announcement.contents = [$announcement.contents[0]];
    }
  }
  let modalImageFiles = null;
  let modalImageInput;

  function handleDragOver(event) {
    event.preventDefault();
    dropZoneActive = true;
  }

  function handleDragLeave() {
    dropZoneActive = false;
  }

  function handleDrop(event) {
    event.preventDefault();
    dropZoneActive = false;

    const files = event.dataTransfer.files;
    if (files.length > 0) {
      processFile(files[0]);
    }
  }

  async function onModalImageChange(event) {
    const file = event.target.files[0];
    if (file) {
      processFile(file);
    }
  }

  function onRemoveImage() {
    modalImage.set(null);
    selectedFile.set(null);
    isImageRemoved.set(true);
    if (modalImageInput) modalImageInput.value = "";
  }

  function processFile(file) {
    isImageRemoved.set(false);
    // Modal image validation: max 2MB, PNG, JPEG, GIF, WEBP
    const maxSize = 2 * 1024 * 1024; // 2MB
    const allowedTypes = ["image/png", "image/jpeg", "image/gif", "image/webp"];

    // File size check
    if (file.size > maxSize) {
      showToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-size');
      if (modalImageInput) modalImageInput.value = "";
      return;
    }

    // File type check
    if (!allowedTypes.includes(file.type)) {
      showToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-type');
      if (modalImageInput) modalImageInput.value = "";
      return;
    }

    const reader = new FileReader();
    reader.readAsDataURL(file);

    reader.onload = (e) => {
      modalImage.set(e.target.result);
    };

    // Also update modalImageFiles if it wasn't from input
    // Note: Creating a fake FileList or manually handling the file in onSave
    // We'll just store the file in a separate variable for saving
    selectedFile.set(file);
  }

  async function onSaveAnnouncement() {
    if (!isFormValid) return;

    loading = true;

    try {
      const data = {
        title: $announcement.title,
        status: $announcement.status === "ACTIVE",
        link: $announcement.link || "",
        type: $announcement.displayType,
        effectType: $announcement.displayType === "BANNER" ? $announcement.bannerEffect : "NONE",
        until: $announcement.timeType === "TIMED" ? new Date($announcement.time).getTime() : null,
        contents: JSON.stringify($announcement.contents),
        customCss: $announcement.customCss || "",
        size: $announcement.displayType === "MODAL" ? ($announcement.modalSize === "SMALL" ? 0 : $announcement.modalSize === "NORMAL" ? 1 : $announcement.modalSize === "LARGE" ? 2 : 3) : null,
        modalDisplayFrequency: $announcement.modalDisplayFrequency,
        removeImage: $isImageRemoved
      };

      const formData = new FormData();
      Object.keys(data).forEach(key => {
        if (data[key] !== null && data[key] !== undefined) {
          formData.append(key, data[key]);
        }
      });

      if ($selectedFile) {
        formData.append("image", $selectedFile);
      }

      let result;
      if ($mode === "edit") {
        result = await ApiUtil.put({
          path: `/api/panel/announcements/${$announcement.id}`,
          body: formData,
          headers: {} // ApiUtil might need null/empty headers to not set Content-Type to application/json
        });
      } else {
        result = await ApiUtil.post({
          path: `/api/panel/announcements`,
          body: formData,
          headers: {}
        });
      }

      if (result.error) {
        console.error("Save failed:", result.error);
        showToast('plugins.pano-plugin-announcement.toasts.error-saving', {
          values: { error: result.error.statusMessage || $_('modals.add-edit.error-unknown') }
        });
      } else {
        showToast($mode === 'create' ? 'plugins.pano-plugin-announcement.toasts.announcement-added' : 'plugins.pano-plugin-announcement.toasts.announcement-updated');
        hide();
        callback($announcement);
      }
    } catch (e) {
      console.error(e);
      showToast('plugins.pano-plugin-announcement.toasts.error-saving', {
        values: { error: $_('modals.add-edit.error-general') }
      });
    } finally {
      loading = false;
    }
  }

  function addContent() {
    announcement.update(a => {
      a.contents = [...a.contents, ""];
      return a;
    });
    activeContentIndex = get(announcement).contents.length - 1;
  }

  function removeActiveContent() {
    const currentContents = get(announcement).contents;
    if (currentContents.length <= 1) return;

    announcement.update(a => {
      a.contents = a.contents.filter((_, i) => i !== activeContentIndex);
      return a;
    });

    if (activeContentIndex >= get(announcement).contents.length) {
      activeContentIndex = get(announcement).contents.length - 1;
    }
  }
</script>
