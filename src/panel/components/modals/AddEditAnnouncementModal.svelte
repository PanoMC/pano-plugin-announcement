<!-- Add / Edit Announcement Modal -->
<div class="modal fade" bind:this={$modalElement} tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          {$mode === 'edit' ? $_('modals.add-edit.edit-title') : $_('modals.add-edit.new-title')}
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
          <div class="col-md-7 border-end d-flex flex-column">
            <!-- Title -->
            <div class="input-group mb-4">
              {#if $mode === 'edit'}
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

            <!-- Content -->
            <div class="mb-4">
              {#if $announcement.displayType === 'BANNER'}
                <div class="d-flex justify-content-between align-items-center mb-2 gap-2">
                  <div class="overflow-x-auto flex-grow-1" style="scrollbar-width: thin;">
                    <div class="btn-group btn-group-sm flex-nowrap mb-1" role="group">
                      {#each $announcement?.bannerContents || [] as item, i}
                        <button
                          type="button"
                          class="btn {activeContentIndex === i
                            ? 'btn-primary'
                            : 'btn-outline-primary'} text-nowrap"
                          on:click={() => (activeContentIndex = i)}>
                          {$_('modals.add-edit.content-tab', {
                            values: { index: i + 1 },
                          })}
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
                  {#if ($announcement?.bannerContents?.length || 0) > 1}
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
                  {#if $announcement?.bannerContents}
                    <div class="form-floating">
                      <textarea
                        class="form-control"
                        id="content-{activeContentIndex}"
                        bind:value={$announcement.bannerContents[activeContentIndex]}
                        placeholder={$_('modals.add-edit.content-placeholder')}
                        required
                        style="height: 120px;"></textarea>
                      <label for="content-{activeContentIndex}"
                        >{$_('modals.add-edit.content-label')}</label>
                    </div>
                  {/if}
                {/key}
              {:else if $announcement.displayType === 'MODAL'}
                <div class="mb-4">
                  <div class="position-relative w-100 mb-3">
                    {#key displayImage}
                      {#if displayImage}
                        <div class="ratio ratio-16x9 border rounded">
                          <button
                            type="button"
                            class="btn border-0 shadow-none w-100 h-100 p-0 bg-transparent"
                            use:tooltip={[
                              $_('modals.add-edit.image-change'),
                              { placement: 'bottom' },
                            ]}
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
                          style="z-index: 10;"
                          on:click={onRemoveImage}
                          on:mouseenter|stopPropagation
                          on:mouseleave|stopPropagation
                          aria-label={$_('modals.add-edit.image-remove')}>
                          <i class="fas fa-minus"></i>
                        </button>
                        <input
                          class="d-none"
                          id="modalImage"
                          type="file"
                          on:change={onModalImageChange}
                          bind:this={modalImageInput}
                          accept="image/png,image/jpeg,image/gif,image/webp" />
                      {:else}
                        <DragAndDropZone
                          class="mb-3"
                          style="aspect-ratio: 16/9;"
                          accept={['image/png', 'image/jpeg', 'image/gif', 'image/webp']}
                          maxFileSize={2 * 1024 * 1024}
                          on:drop={(e) => processFile(e.detail)}
                          on:error={handleFileError}>
                          <i class="fas fa-image fa-3x mb-3 opacity-50"></i>
                          <p class="mb-0 opacity-75 fw-medium">
                            {$_('modals.add-edit.image-drop-placeholder')}
                          </p>
                          <small
                            class="opacity-50 text-uppercase fw-semibold"
                            style="font-size: 0.7rem; letter-spacing: 0.5px;"
                            >{$_('modals.add-edit.image-format-info')}</small>
                        </DragAndDropZone>
                      {/if}
                    {/key}
                  </div>
                  <Editor
                    bind:content={$announcement.modalContent}
                    bind:isEmpty={isEditorEmpty}
                    showHtml={true}
                    contentStyles={'height: 300px;'} />
                </div>
              {/if}
            </div>

            <!-- Custom CSS -->
            <div class="form-floating flex-grow-1">
              <textarea
                class="form-control"
                id="customCss"
                bind:value={$announcement.customCss}
                placeholder={$_('modals.add-edit.custom-css-placeholder')}
                style="height: 100%; min-height: 120px;"></textarea>
              <label for="customCss">{$_('modals.add-edit.custom-css')}</label>
            </div>
            <small class="d-block mt-1">
              {@html $_('modals.add-edit.custom-css-info', {
                values: {
                  selector: `<code>#pano-announcement-${$announcement.id || 'ID'}</code>`,
                },
              })}
            </small>
          </div>

          <div class="col-md-5">
            <div class="form-check form-switch mb-4">
              <input
                class="form-check-input"
                type="checkbox"
                id="status"
                role="switch"
                checked={$announcement.status === 'ACTIVE'}
                on:change={(e) => ($announcement.status = e.target.checked ? 'ACTIVE' : 'DRAFT')} />
              <label class="form-check-label ms-2 fw-bold" for="status">
                {$announcement.status === 'ACTIVE'
                  ? $_('modals.add-edit.status-active')
                  : $_('modals.add-edit.status-inactive')}
              </label>
            </div>

            <div class="mb-3">
              <label class="form-label" for="showFromNow">{$_('modals.add-edit.show-from')}</label>
              <div class="btn-group w-100" role="group">
                <input
                  type="radio"
                  class="btn-check"
                  id="showFromNow"
                  autocomplete="off"
                  bind:group={$announcement.showFromType}
                  value="NOW" />
                <label class="btn btn-outline-primary" for="showFromNow">
                  {$_('modals.add-edit.show-from-now')}
                </label>

                <input
                  type="radio"
                  class="btn-check"
                  id="showFromCustom"
                  autocomplete="off"
                  bind:group={$announcement.showFromType}
                  value="CUSTOM" />
                <label class="btn btn-outline-primary" for="showFromCustom">
                  {$_('modals.add-edit.show-from-custom')}
                </label>
              </div>
            </div>
            {#if $announcement.showFromType === 'CUSTOM'}
              <div class="form-floating mb-3">
                <input
                  type="datetime-local"
                  class="form-control"
                  id="showFrom"
                  bind:value={$announcement.showFrom}
                  placeholder={$_('modals.add-edit.show-from-select')}
                  required />
                <label for="showFrom">{$_('modals.add-edit.show-from-select')}</label>
              </div>
            {/if}

            <div class="mb-3">
              <label class="form-label" for="timePermanent"
                >{$_('modals.add-edit.time-type')}</label>
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
            {#if $announcement.timeType === 'TIMED'}
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

            <hr />

            <div class="form-floating mb-3">
              <select class="form-select" id="displayType" bind:value={$announcement.displayType}>
                <option value="BANNER">{$_('modals.add-edit.type-banner')}</option>
                <option value="MODAL">{$_('modals.add-edit.type-modal')}</option>
              </select>
              <label for="displayType">{$_('modals.add-edit.type')}</label>
            </div>

            {#if $announcement.displayType === 'BANNER' || $announcement.displayType === 'MODAL'}
              <div class="form-floating mb-3">
                <input
                  type="text"
                  class="form-control"
                  id="link"
                  bind:value={$announcement.link}
                  placeholder={$_('modals.add-edit.link-placeholder')} />
                <label for="link">{$_('modals.add-edit.link')}</label>
              </div>
              <div class="form-check form-switch mb-3">
                <input
                  class="form-check-input"
                  type="checkbox"
                  id="external"
                  role="switch"
                  bind:checked={$announcement.external} />
                <label class="form-check-label ms-2" for="external">
                  {$_('modals.add-edit.external')}
                </label>
              </div>
            {/if}

            {#if $announcement.displayType === 'BANNER'}
              <div class="form-floating mb-3">
                <select class="form-select" id="location" bind:value={$announcement.location}>
                  <option value="GLOBAL">{$_('modals.add-edit.location-global')}</option>
                  <option value="HOME">{$_('modals.add-edit.location-home')}</option>
                </select>
                <label for="location">{$_('modals.add-edit.location')}</label>
              </div>

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

              <div class="mb-3">
                <p class="form-label d-block">{$_('modals.add-edit.style')}</p>
                <div class="d-flex flex-wrap gap-2">
                  {#each ['PRIMARY', 'SECONDARY', 'SUCCESS', 'DANGER', 'WARNING', 'INFO', 'LIGHT', 'DARK'] as color}
                    <button
                      type="button"
                      class="btn btn-{color.toLowerCase()} p-0 rounded-circle border {$announcement.alertStyle === color
                        ? 'border-3 border-primary shadow-sm'
                        : 'border-1 border-secondary border-opacity-25'}"
                      style="width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; transition: all 0.2s;"
                      on:click={() => ($announcement.alertStyle = color)}
                      title={color}>
                      {#if $announcement.alertStyle === color}
                        <i
                          class="fas fa-check {['LIGHT', 'WARNING', 'INFO'].includes(color)
                            ? 'text-dark'
                            : 'text-white'}"></i>
                      {/if}
                    </button>
                  {/each}
                </div>
              </div>

              <div class="mb-3">
                <p class="form-label d-block">{$_('modals.add-edit.text-align')}</p>
                <div class="btn-group btn-group-sm w-100" role="group">
                  <input
                    type="radio"
                    class="btn-check"
                    id="textAlignLeft"
                    autocomplete="off"
                    bind:group={$announcement.textAlign}
                    value="LEFT" />
                  <label class="btn btn-outline-primary" for="textAlignLeft">
                    <i class="fas fa-align-left"></i>
                  </label>

                  <input
                    type="radio"
                    class="btn-check"
                    id="textAlignCenter"
                    autocomplete="off"
                    bind:group={$announcement.textAlign}
                    value="CENTER" />
                  <label class="btn btn-outline-primary" for="textAlignCenter">
                    <i class="fas fa-align-center"></i>
                  </label>

                  <input
                    type="radio"
                    class="btn-check"
                    id="textAlignRight"
                    autocomplete="off"
                    bind:group={$announcement.textAlign}
                    value="RIGHT" />
                  <label class="btn btn-outline-primary" for="textAlignRight">
                    <i class="fas fa-align-right"></i>
                  </label>
                </div>
              </div>
              <div class="form-check form-switch mb-3">
                <input
                  class="form-check-input"
                  type="checkbox"
                  id="closeable"
                  role="switch"
                  bind:checked={$announcement.closeable} />
                <label class="form-check-label ms-2" for="closeable">
                  {$_('modals.add-edit.closeable')}
                </label>
              </div>
            {/if}

            {#if $announcement.displayType === 'MODAL'}
              <div class="mb-3">
                <label class="form-label" for="modalFrequencyPermanent"
                  >{$_('modals.add-edit.display')}</label>
                <div class="btn-group w-100" role="group">
                  <input
                    type="radio"
                    class="btn-check"
                    id="modalFrequencyPermanent"
                    autocomplete="off"
                    bind:group={$announcement.displayFrequency}
                    value="ALWAYS" />
                  <label class="btn btn-outline-primary" for="modalFrequencyPermanent">
                    {$_('modals.add-edit.display-always')}
                  </label>

                  <input
                    type="radio"
                    class="btn-check"
                    id="modalFrequencyOnce"
                    autocomplete="off"
                    bind:group={$announcement.displayFrequency}
                    value="ONCE" />
                  <label class="btn btn-outline-primary" for="modalFrequencyOnce">
                    {$_('modals.add-edit.display-once')}
                  </label>
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label" for="modalSizeNormal"
                  >{$_('modals.add-edit.modal-size')}</label>
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
            {/if}
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button
          type="button"
          class="btn btn-primary w-100"
          class:disabled={loading || !isFormValid || ($mode === 'edit' && !isChanged)}
          disabled={loading || !isFormValid || ($mode === 'edit' && !isChanged)}
          on:click={onSaveAnnouncement}>
          {$mode === 'create' ? $_('modals.add-edit.btn-create') : $_('modals.add-edit.btn-save')}
        </button>
      </div>
    </div>
  </div>
</div>

<script context="module">
  import {get, writable} from 'svelte/store';

  const modalElement = writable();
  const mode = writable('create');
  const announcement = writable({});
  const initialAnnouncement = writable({});
  const modalImage = writable(null);
  const isImageRemoved = writable(false);
  const selectedFile = writable(null);

  let callback = (announcement) => {};
  let hideCallback = (announcement) => {};
  let modal;

  export function show(newMode, newAnnouncement = null) {
    mode.set(newMode);
    modalImage.set(null);
    isImageRemoved.set(false);
    selectedFile.set(null);

    if (newAnnouncement) {
      const cloned = JSON.parse(JSON.stringify(newAnnouncement));
      const activeType = cloned.displayType || cloned.type || 'BANNER';
      const contents = cloned.contents || [cloned.content || ''];
      const state = {
        ...cloned,
        modalSize:
          cloned.size === 0
            ? 'SMALL'
            : cloned.size === 2
              ? 'LARGE'
              : cloned.size === 3
                ? 'FULL'
                : 'NORMAL',
        displayFrequency: cloned.displayFrequency || 'ALWAYS',
        displayType: activeType,
        closeable: cloned.closeable || false,
        showFromType: cloned.showFrom ? 'CUSTOM' : 'NOW',
        showFrom: cloned.showFrom ? new Date(cloned.showFrom).toISOString().slice(0, 16) : '',
        location: cloned.location || 'GLOBAL',
        external: cloned.external || false,
        bannerContents: activeType === 'BANNER' ? contents : cloned.bannerContents || [''],
        modalContent: activeType === 'MODAL' ? contents[0] : cloned.modalContent || '',
        alertStyle: cloned.alertStyle || 'INFO',
        textAlign: cloned.textAlign || 'CENTER',
      };
      announcement.set(state);
      initialAnnouncement.set(JSON.parse(JSON.stringify(state)));
    } else {
      announcement.set({
        id: null,
        title: '',
        status: 'ACTIVE',
        timeType: 'PERMANENT',
        time: '',
        showFromType: 'NOW',
        showFrom: '',
        displayType: 'BANNER',
        customCss: '',
        link: '',
        bannerEffect: 'NONE',
        closeable: true,
        location: 'GLOBAL',
        bannerContents: [''],
        modalContent: '',
        displayFrequency: 'ALWAYS',
        modalSize: 'NORMAL',
        external: false,
        alertStyle: 'INFO',
        textAlign: 'CENTER',
      });
    }

    modal = new window.bootstrap.Modal(get(modalElement), {
      backdrop: 'static',
      keyboard: false,
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
  import ApiUtil from '@panomc/sdk/utils/api';
  import { Editor, DragAndDropZone } from '@panomc/sdk/components';
  import { base } from '@panomc/sdk/svelte';
  import tooltip from '@panomc/sdk/utils/tooltip';
  import { showToast } from '@panomc/sdk/toasts';
  import { _ } from '../../../main';

  let isEditorEmpty = true;
  let activeContentIndex = 0;
  let loading = false;

  let dropZoneActive = false;

  $: displayImage =
    $modalImage ||
    ($isImageRemoved
      ? null
      : $announcement.imageFileName
        ? `${base}/api/panel/announcements/image/${$announcement.imageFileName}`
        : null);

  $: isFormValid = (() => {
    if (!$announcement.title || $announcement.title.trim() === '') return false;

    if ($announcement.displayType === 'BANNER') {
      if (
        !$announcement.bannerContents ||
        $announcement.bannerContents.length === 0 ||
        $announcement.bannerContents.every((c) => !c || c.trim() === '')
      )
        return false;
    } else if ($announcement.displayType === 'MODAL') {
      if (!$announcement.modalContent || $announcement.modalContent.trim() === '') return false;
      if ($mode === 'create' && !$selectedFile) return false;
    }

    return true;
  })();

  $: isChanged =
    $mode === 'create' ||
    JSON.stringify($announcement) !== JSON.stringify($initialAnnouncement) ||
    $selectedFile !== null ||
    $isImageRemoved;

  $: if ($announcement.displayType !== 'BANNER') {
    activeContentIndex = 0;
  }
  let modalImageFiles = null;
  let modalImageInput;

  function handleFileError(event) {
    const { error } = event.detail;
    if (error === 'INVALID_SIZE') {
      showToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-size');
    } else if (error === 'INVALID_TYPE') {
      showToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-type');
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
    if (modalImageInput) modalImageInput.value = '';
  }

  function processFile(file) {
    isImageRemoved.set(false);
    // Modal image validation: max 2MB, PNG, JPEG, GIF, WEBP
    const maxSize = 2 * 1024 * 1024; // 2MB
    const allowedTypes = ['image/png', 'image/jpeg', 'image/gif', 'image/webp'];

    // File size check
    if (file.size > maxSize) {
      showToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-size');
      if (modalImageInput) modalImageInput.value = '';
      return;
    }

    // File type check
    if (!allowedTypes.includes(file.type)) {
      showToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-type');
      if (modalImageInput) modalImageInput.value = '';
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
        status: $announcement.status === 'ACTIVE',
        link: $announcement.link || '',
        type: $announcement.displayType,
        effectType: $announcement.displayType === 'BANNER' ? $announcement.bannerEffect : 'NONE',
        until: $announcement.timeType === 'TIMED' ? new Date($announcement.time).getTime() : null,
        showFrom:
          $announcement.showFromType === 'CUSTOM'
            ? new Date($announcement.showFrom).getTime()
            : null,
        contents: JSON.stringify(
          $announcement.displayType === 'BANNER'
            ? $announcement.bannerContents
            : [$announcement.modalContent],
        ),
        customCss: $announcement.customCss || '',
        size:
          $announcement.displayType === 'MODAL'
            ? $announcement.modalSize === 'SMALL'
              ? 0
              : $announcement.modalSize === 'NORMAL'
                ? 1
                : $announcement.modalSize === 'LARGE'
                  ? 2
                  : 3
            : null,
        displayFrequency: $announcement.displayFrequency,
        location: $announcement.location,
        external: $announcement.external || false,
        closeable: $announcement.displayType === 'BANNER' ? $announcement.closeable : false,
        alertStyle: $announcement.displayType === 'BANNER' ? $announcement.alertStyle : 'INFO',
        textAlign: $announcement.displayType === 'BANNER' ? $announcement.textAlign : 'CENTER',
        removeImage: $isImageRemoved,
      };

      const formData = new FormData();
      Object.keys(data).forEach((key) => {
        if (data[key] !== null && data[key] !== undefined) {
          formData.append(key, data[key]);
        }
      });

      if ($selectedFile) {
        formData.append('image', $selectedFile);
      }

      let result;
      if ($mode === 'edit') {
        result = await ApiUtil.put({
          path: `/api/panel/announcements/${$announcement.id}`,
          body: formData,
          headers: {}, // ApiUtil might need null/empty headers to not set Content-Type to application/json
        });
      } else {
        result = await ApiUtil.post({
          path: `/api/panel/announcements`,
          body: formData,
          headers: {},
        });
      }

      if (result.error) {
        console.error('Save failed:', result.error);
        showToast('plugins.pano-plugin-announcement.toasts.error-saving', {
          values: {
            error: result.error || $_('modals.add-edit.error-unknown'),
          },
        });
      } else {
        showToast(
          $mode === 'create'
            ? 'plugins.pano-plugin-announcement.toasts.announcement-added'
            : 'plugins.pano-plugin-announcement.toasts.announcement-updated',
        );
        hide();
        callback($announcement);
      }
    } catch (e) {
      console.error(e);
      showToast('plugins.pano-plugin-announcement.toasts.error-saving', {
        values: { error: $_('modals.add-edit.error-general') },
      });
    } finally {
      loading = false;
    }
  }

  function addContent() {
    announcement.update((a) => {
      a.bannerContents = [...a.bannerContents, ''];
      return a;
    });
    activeContentIndex = get(announcement).bannerContents.length - 1;
  }

  function removeActiveContent() {
    const currentContents = get(announcement).bannerContents;
    if (currentContents.length <= 1) return;

    announcement.update((a) => {
      a.bannerContents = a.bannerContents.filter((_, i) => i !== activeContentIndex);
      return a;
    });

    if (activeContentIndex >= get(announcement).bannerContents.length) {
      activeContentIndex = get(announcement).bannerContents.length - 1;
    }
  }
</script>
