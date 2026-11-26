<style>
  /* The preview's motion, as on the site (theme/Announcements.svelte). */
  .announcement-preview__marquee {
    overflow: hidden;
    white-space: nowrap;
  }

  .announcement-preview__marquee-content {
    display: inline-block;
    padding-left: 100%;
    animation: announcement-preview-marquee var(--ann-duration, 20s) linear infinite;
  }

  @keyframes announcement-preview-marquee {
    from {
      transform: translateX(0);
    }
    to {
      transform: translateX(-100%);
    }
  }

  .announcement-preview__marquee-pause:hover {
    animation-play-state: paused;
  }

  .announcement-preview__marquee-full {
    padding-left: 0;
  }

  .announcement-preview__marquee-loop {
    padding-left: 0;
    animation-name: announcement-preview-loop;
  }

  @keyframes announcement-preview-loop {
    from {
      transform: translateX(0);
    }
    to {
      transform: translateX(-50%);
    }
  }

  .announcement-preview__flash {
    animation: announcement-preview-flash 1s infinite alternate;
  }

  @keyframes announcement-preview-flash {
    from {
      opacity: 1;
    }
    to {
      opacity: 0.6;
    }
  }

  .announcement-preview__sep {
    display: inline-block;
    margin: 0 0.85em;
    opacity: 0.75;
  }
</style>

<!-- Add / Edit Announcement Modal -->
<div
  class="modal fade"
  bind:this={$modalElement}
  tabindex="-1"
  role="dialog"
  aria-hidden="true"
  on:shown.bs.modal={() => (announcementModalShown = true)}
  on:hide.bs.modal={() => (announcementModalShown = false)}>
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          {$mode === 'edit' ? $_('modals.add-edit.edit-title') : $_('modals.add-edit.new-title')}
        </h5>
        <div class="ms-auto me-1 d-flex align-items-center gap-1">
          <button
            type="button"
            class="btn btn-link"
            title={$_('modals.add-edit.custom-css')}
            aria-label={$_('modals.add-edit.custom-css')}
            disabled={!announcementModalShown}
            on:click={openCustomCss}>
            <i class="fas fa-code"></i>
          </button>
        </div>
        <button
          type="button"
          class="btn-close ms-0"
          aria-label={$_('modals.add-edit.close')}
          on:click={hide}></button>
      </div>
      <div class="modal-body">
        <div class="row">
          <div class="col-xl-5 border-end">
            <p class="form-label">{$_('modals.add-edit.status')}</p>
            <div class="form-check form-switch mb-3">
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
              {#if ($announcement.link || '').trim() !== ''}
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
            {/if}

            {#if $announcement.displayType === 'BANNER'}
              <div class="mb-3">
                <label class="form-label d-block">{$_('modals.add-edit.location')}</label>
                <div class="vstack gap-2 pb-2">
                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="location"
                      id="locationGlobal"
                      bind:group={$announcement.location}
                      value="GLOBAL" />
                    <label class="form-check-label" for="locationGlobal">
                      {$_('modals.add-edit.location-global')}
                    </label>
                  </div>
                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="location"
                      id="locationHome"
                      bind:group={$announcement.location}
                      value="HOME" />
                    <label class="form-check-label" for="locationHome">
                      {$_('modals.add-edit.location-home')}
                    </label>
                  </div>
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label d-block">{$_('modals.add-edit.effects')}</label>
                <div class="vstack gap-2 pb-2">
                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="bannerEffect"
                      id="effectNone"
                      bind:group={$announcement.bannerEffect}
                      value="NONE" />
                    <label class="form-check-label" for="effectNone">
                      {$_('modals.add-edit.effect-none')}
                    </label>
                  </div>
                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="bannerEffect"
                      id="effectMarquee"
                      bind:group={$announcement.bannerEffect}
                      value="MARQUEE" />
                    <label class="form-check-label" for="effectMarquee">
                      {$_('modals.add-edit.effect-marquee')}
                    </label>
                  </div>
                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="bannerEffect"
                      id="effectFlash"
                      bind:group={$announcement.bannerEffect}
                      value="FLASH" />
                    <label class="form-check-label" for="effectFlash">
                      {$_('modals.add-edit.effect-flash')}
                    </label>
                  </div>
                </div>
              </div>

              {#if $announcement.bannerEffect === 'MARQUEE'}
                <div class="mb-3">
                  <label class="form-label d-flex justify-content-between" for="marqueeSpeed">
                    <span>{$_('modals.add-edit.marquee-speed')}</span>
                    <span class="text-body-secondary">
                      {$_('modals.add-edit.marquee-speed-value', {
                        values: { speed: $announcement.displayOptions.marqueeSpeed },
                      })}
                    </span>
                  </label>
                  <input
                    type="range"
                    class="form-range"
                    id="marqueeSpeed"
                    min="10"
                    max="300"
                    step="5"
                    bind:value={$announcement.displayOptions.marqueeSpeed} />
                  <div class="d-flex justify-content-between small text-body-secondary">
                    <span>{$_('modals.add-edit.slow')}</span>
                    <span>{$_('modals.add-edit.fast')}</span>
                  </div>
                </div>

                <div class="form-check form-switch mb-3">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="pauseOnHover"
                    role="switch"
                    bind:checked={$announcement.displayOptions.pauseOnHover} />
                  <label class="form-check-label ms-2" for="pauseOnHover">
                    {$_('modals.add-edit.pause-on-hover')}
                  </label>
                </div>
              {/if}

              {#if ($announcement.bannerContents?.length || 0) > 1}
                <div class="mb-3">
                  <p class="form-label d-block">{$_('modals.add-edit.multi-mode')}</p>
                  <div class="btn-group w-100" role="group">
                    <input
                      type="radio"
                      class="btn-check"
                      id="multiRotate"
                      autocomplete="off"
                      bind:group={$announcement.displayOptions.multiMode}
                      value="ROTATE" />
                    <label class="btn btn-outline-primary" for="multiRotate">
                      {$_('modals.add-edit.multi-mode-rotate')}
                    </label>

                    <input
                      type="radio"
                      class="btn-check"
                      id="multiInline"
                      autocomplete="off"
                      bind:group={$announcement.displayOptions.multiMode}
                      value="INLINE" />
                    <label class="btn btn-outline-primary" for="multiInline">
                      {$_('modals.add-edit.multi-mode-inline')}
                    </label>
                  </div>
                </div>

                {#if $announcement.bannerEffect === 'MARQUEE'}
                  <div class="form-check form-switch mb-3">
                    <input
                      class="form-check-input"
                      type="checkbox"
                      id="fullStart"
                      role="switch"
                      bind:checked={$announcement.displayOptions.fullStart} />
                    <label class="form-check-label ms-2" for="fullStart">
                      {$_('modals.add-edit.full-start')}
                    </label>
                    <small class="d-block text-body-secondary">
                      {$announcement.displayOptions.multiMode === 'INLINE'
                        ? $_('modals.add-edit.full-start-info-inline')
                        : $_('modals.add-edit.full-start-info-rotate')}
                    </small>
                  </div>
                {/if}

                {#if $announcement.displayOptions.multiMode === 'INLINE'}
                  <div class="mb-3">
                    <p class="form-label d-block">{$_('modals.add-edit.separator')}</p>
                    <div class="d-flex flex-wrap gap-2">
                      {#each Object.entries(SEPARATORS) as [key, symbol]}
                        <button
                          type="button"
                          class="btn btn-sm {$announcement.displayOptions.separator === key
                            ? 'btn-primary'
                            : 'btn-outline-secondary'}"
                          style="min-width: 40px;"
                          title={$_(`modals.add-edit.separator-${key.toLowerCase()}`)}
                          aria-label={$_(`modals.add-edit.separator-${key.toLowerCase()}`)}
                          aria-pressed={$announcement.displayOptions.separator === key}
                          on:click={() => ($announcement.displayOptions.separator = key)}>
                          {symbol}
                        </button>
                      {/each}
                      <button
                        type="button"
                        class="btn btn-sm {$announcement.displayOptions.separator === 'CUSTOM'
                          ? 'btn-primary'
                          : 'btn-outline-secondary'}"
                        aria-pressed={$announcement.displayOptions.separator === 'CUSTOM'}
                        on:click={() => ($announcement.displayOptions.separator = 'CUSTOM')}>
                        {$_('modals.add-edit.separator-custom')}
                      </button>
                    </div>
                    {#if $announcement.displayOptions.separator === 'CUSTOM'}
                      <div class="form-floating mt-2">
                        <input
                          type="text"
                          class="form-control"
                          id="customSeparator"
                          maxlength="8"
                          bind:value={$announcement.displayOptions.customSeparator}
                          placeholder={$_('modals.add-edit.separator-custom-placeholder')} />
                        <label for="customSeparator"
                          >{$_('modals.add-edit.separator-custom-placeholder')}</label>
                      </div>
                    {/if}
                  </div>
                {:else if $announcement.bannerEffect !== 'MARQUEE'}
                  <div class="mb-3">
                    <label class="form-label d-flex justify-content-between" for="rotateSeconds">
                      <span>{$_('modals.add-edit.rotate-seconds')}</span>
                      <span class="text-body-secondary">
                        {$_('modals.add-edit.rotate-seconds-value', {
                          values: { seconds: $announcement.displayOptions.rotateSeconds },
                        })}
                      </span>
                    </label>
                    <input
                      type="range"
                      class="form-range"
                      id="rotateSeconds"
                      min="2"
                      max="30"
                      step="1"
                      bind:value={$announcement.displayOptions.rotateSeconds} />
                  </div>
                {/if}
              {/if}

              {#if $announcement.bannerEffect !== 'MARQUEE'}
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
              {/if}
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

          <div class="col-xl-7 d-flex flex-column">
            <!-- Title -->
            <div class="input-group mb-3">
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
            <div class="mb-3">
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
                        style="min-height: 256px;"></textarea>
                      <label for="content-{activeContentIndex}"
                        >{$_('modals.add-edit.content-label')}</label>
                    </div>
                  {/if}
                {/key}
              {:else if $announcement.displayType === 'MODAL'}
                <div class="vstack gap-2">
                  <p class="form-label mb-0" id="modalImageLabel">{$_('modals.add-edit.image')}</p>
                  <div class="position-relative w-100" role="group" aria-labelledby="modalImageLabel">
                    {#key displayImage}
                      {#if displayImage}
                        <div class="ratio ratio-16x9 border rounded">
                          <button
                            type="button"
                            class="btn border-0 shadow-none w-100 h-100 p-0 bg-transparent"
                            title={$_('modals.add-edit.image-change')}
                            aria-label={$_('modals.add-edit.image-change')}
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
                          style="aspect-ratio: 16/9;"
                          icon="fas fa-image fa-3x"
                          title={$_('modals.add-edit.image-drop-placeholder')}
                          subtitle={$_('modals.add-edit.image-format-info')}
                          accept={['image/png', 'image/jpeg', 'image/gif', 'image/webp']}
                          maxFileSize={2 * 1024 * 1024}
                          on:drop={(e) => processFile(e.detail)}
                          on:error={handleFileError} />
                      {/if}
                    {/key}
                  </div>
                  <div role="group" aria-labelledby="modalContentLabel">
                    <p class="form-label" id="modalContentLabel">
                      {$_('modals.add-edit.content-label')}
                    </p>
                    <Editor
                      bind:content={$announcement.modalContent}
                      bind:isEmpty={isEditorEmpty}
                      showHtml={true}
                      contentStyles={'min-height: 300px;'} />
                  </div>
                </div>
              {/if}
            </div>

            {#if $announcement.displayType === 'BANNER'}
              <!-- Look: a live preview, the background and the text colour -->
              <div class="mb-3">
                <p class="form-label d-block">{$_('modals.add-edit.preview')}</p>
                <div
                  class="alert mb-0 overflow-hidden {previewLook.alert
                    ? `alert-${($announcement.alertStyle || 'INFO').toLowerCase()}`
                    : ''} {$announcement.bannerEffect === 'MARQUEE'
                    ? ''
                    : previewAlignClass}"
                  style={previewLook.style || null}>
                  <div class:announcement-preview__flash={$announcement.bannerEffect === 'FLASH'}>
                    {#if $announcement.bannerEffect === 'MARQUEE'}
                      <div class="announcement-preview__marquee">
                        {#key previewKey}
                          {#if previewLoop}
                            <div
                              class="announcement-preview__marquee-content announcement-preview__marquee-loop"
                              class:announcement-preview__marquee-pause={previewOptions.pauseOnHover !== false}
                              use:previewMarquee={{ speed: $announcement.displayOptions.marqueeSpeed, loop: true }}>
                              {#each [0, 1] as copy}
                                <span
                                  >{#each Array.from({ length: 4 }) as _}{#each previewContents as content}<span
                                        >{@html content}</span
                                      ><span class="announcement-preview__sep">{previewSeparator}</span
                                      >{/each}{/each}</span>
                              {/each}
                            </div>
                          {:else}
                            <div
                              class="announcement-preview__marquee-content"
                              class:announcement-preview__marquee-pause={previewOptions.pauseOnHover !== false}
                              class:announcement-preview__marquee-full={previewOptions.fullStart &&
                                previewFilled.length > 1}
                              use:previewMarquee={{ speed: $announcement.displayOptions.marqueeSpeed, loop: false }}>
                              {#each previewContents as content, i}
                                {#if i > 0}<span class="announcement-preview__sep">{previewSeparator}</span
                                  >{/if}<span>{@html content}</span>
                              {/each}
                            </div>
                          {/if}
                        {/key}
                      </div>
                    {:else}
                      <div class="d-flex flex-wrap align-items-center {previewJustifyClass}">
                        {#each previewContents as content, i}
                          {#if i > 0}<span class="announcement-preview__sep">{previewSeparator}</span
                            >{/if}<span>{@html content}</span>
                        {/each}
                      </div>
                    {/if}
                  </div>
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label" for="background">{$_('modals.add-edit.background')}</label>
                <select
                  class="form-select"
                  id="background"
                  bind:value={$announcement.displayOptions.background}>
                  {#each BACKGROUNDS as key}
                    <option value={key}>{$_(`modals.add-edit.background-${key.toLowerCase().replaceAll('_', '-')}`)}</option>
                  {/each}
                </select>
                {#if $announcement.displayOptions.background.startsWith('THEME')}
                  <small class="d-block mt-1 text-body-secondary">{$_('modals.add-edit.background-theme-info')}</small>
                {/if}
              </div>

              {#if $announcement.displayOptions.background === 'ALERT'}
                <div class="mb-3">
                  <p class="form-label d-block">{$_('modals.add-edit.style')}</p>
                  <div class="d-flex flex-wrap gap-2">
                    {#each ['PRIMARY', 'SECONDARY', 'SUCCESS', 'DANGER', 'WARNING', 'INFO', 'LIGHT', 'DARK'] as color}
                      <button
                        type="button"
                        class="btn btn-{color.toLowerCase()} p-0 rounded-circle border {$announcement.alertStyle ===
                        color
                          ? 'border-3 border-primary shadow-sm'
                          : 'border-1 border-secondary border-opacity-25'}"
                        style="width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; transition: all 0.2s;"
                        aria-pressed={$announcement.alertStyle === color}
                        on:click={() => ($announcement.alertStyle = color)}>
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
              {/if}

              {#if $announcement.displayOptions.background === 'GRADIENT'}
                <div class="mb-3">
                  <p class="form-label d-block">{$_('modals.add-edit.gradient')}</p>
                  <div class="d-flex flex-wrap gap-2">
                    {#each Object.entries(GRADIENTS) as [key, [from, to]]}
                      <button
                        type="button"
                        class="btn p-0 rounded-3 border {$announcement.displayOptions.gradient === key
                          ? 'border-3 border-primary shadow-sm'
                          : 'border-1 border-secondary border-opacity-25'}"
                        style="width: 56px; height: 32px; background: linear-gradient(90deg, {from}, {to});"
                        title={$_(`modals.add-edit.gradient-${key.toLowerCase()}`)}
                        aria-label={$_(`modals.add-edit.gradient-${key.toLowerCase()}`)}
                        aria-pressed={$announcement.displayOptions.gradient === key}
                        on:click={() => ($announcement.displayOptions.gradient = key)}>
                        {#if $announcement.displayOptions.gradient === key}
                          <i class="fas fa-check text-white"></i>
                        {/if}
                      </button>
                    {/each}
                    <button
                      type="button"
                      class="btn btn-sm {$announcement.displayOptions.gradient === 'CUSTOM'
                        ? 'btn-primary'
                        : 'btn-outline-secondary'}"
                      aria-pressed={$announcement.displayOptions.gradient === 'CUSTOM'}
                      on:click={() => ($announcement.displayOptions.gradient = 'CUSTOM')}>
                      {$_('modals.add-edit.gradient-custom')}
                    </button>
                  </div>
                  {#if $announcement.displayOptions.gradient === 'CUSTOM'}
                    <div class="d-flex gap-3 mt-2">
                      <div>
                        <label class="form-label small mb-1" for="gradientFrom">{$_('modals.add-edit.gradient-from')}</label>
                        <input
                          type="color"
                          class="form-control form-control-color"
                          id="gradientFrom"
                          bind:value={$announcement.displayOptions.gradientFrom} />
                      </div>
                      <div>
                        <label class="form-label small mb-1" for="gradientTo">{$_('modals.add-edit.gradient-to')}</label>
                        <input
                          type="color"
                          class="form-control form-control-color"
                          id="gradientTo"
                          bind:value={$announcement.displayOptions.gradientTo} />
                      </div>
                    </div>
                  {/if}
                </div>
              {/if}

              {#if $announcement.displayOptions.background === 'GRADIENT' || $announcement.displayOptions.background === 'THEME_GRADIENT'}
                <div class="mb-3">
                  <label class="form-label d-flex justify-content-between" for="gradientAngle">
                    <span>{$_('modals.add-edit.gradient-angle')}</span>
                    <span class="text-body-secondary">{$announcement.displayOptions.gradientAngle}°</span>
                  </label>
                  <input
                    type="range"
                    class="form-range"
                    id="gradientAngle"
                    min="0"
                    max="360"
                    step="15"
                    bind:value={$announcement.displayOptions.gradientAngle} />
                </div>
              {/if}

              {#if $announcement.displayOptions.background === 'CUSTOM'}
                <div class="mb-3">
                  <label class="form-label" for="backgroundColor">{$_('modals.add-edit.background-color')}</label>
                  <input
                    type="color"
                    class="form-control form-control-color"
                    id="backgroundColor"
                    bind:value={$announcement.displayOptions.backgroundColor} />
                </div>
              {/if}

              <div class="mb-3">
                <p class="form-label d-block">{$_('modals.add-edit.text-color')}</p>
                <div class="d-flex align-items-center gap-3">
                  <div class="btn-group" role="group">
                    <input
                      type="radio"
                      class="btn-check"
                      id="textColorAuto"
                      autocomplete="off"
                      bind:group={$announcement.displayOptions.textColor}
                      value="AUTO" />
                    <label class="btn btn-outline-primary" for="textColorAuto">
                      {$_('modals.add-edit.text-color-auto')}
                    </label>
                    <input
                      type="radio"
                      class="btn-check"
                      id="textColorCustom"
                      autocomplete="off"
                      bind:group={$announcement.displayOptions.textColor}
                      value="CUSTOM" />
                    <label class="btn btn-outline-primary" for="textColorCustom">
                      {$_('modals.add-edit.text-color-custom')}
                    </label>
                  </div>
                  {#if $announcement.displayOptions.textColor === 'CUSTOM'}
                    <input
                      type="color"
                      class="form-control form-control-color"
                      aria-label={$_('modals.add-edit.text-color')}
                      bind:value={$announcement.displayOptions.textColorValue} />
                  {/if}
                </div>
              </div>
            {/if}
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button
          type="button"
          class="btn w-100"
          class:btn-secondary={$mode === 'create'}
          class:btn-primary={$mode !== 'create'}
          class:disabled={loading || !isFormValid || ($mode === 'edit' && !isChanged)}
          disabled={loading || !isFormValid || ($mode === 'edit' && !isChanged)}
          on:click={onSaveAnnouncement}>
          {$mode === 'create' ? $_('modals.add-edit.btn-create') : $_('modals.add-edit.btn-save')}
        </button>
      </div>
    </div>
  </div>
</div>

<div
  class="modal fade"
  bind:this={customCssModalElement}
  tabindex="-1"
  role="dialog"
  aria-hidden="true"
  on:hidden.bs.modal={returnToAnnouncement}>
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">{$_('modals.add-edit.custom-css')}</h5>
        <button
          type="button"
          class="btn-close"
          data-bs-dismiss="modal"
          aria-label={$_('modals.add-edit.close')}></button>
      </div>
      <form on:submit|preventDefault={applyCustomCss}>
        <div class="modal-body">
          <textarea
            class="form-control font-monospace"
            bind:value={customCssDraft}
            aria-label={$_('modals.add-edit.custom-css')}
            rows="8"></textarea>
          <small class="d-block mt-2 text-body-secondary">
            {@html $_('modals.add-edit.custom-css-info', {
              values: {
                selector: `<code>#${$announcement.displayType === 'MODAL' ? 'announcementModal' : 'pano-announcement'}-${$announcement.id || 'ID'}</code>`,
              },
            })}
          </small>
        </div>
        <div class="modal-footer">
          <button type="submit" class="btn btn-primary w-100">
            {$_('modals.add-edit.btn-apply')}
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<script context="module">
  import { get, writable } from 'svelte/store';
  import {
    parseOptions,
    bannerLook,
    separatorText,
    SEPARATORS,
    GRADIENTS,
  } from '../../../theme/bannerLook.js';

  const BACKGROUNDS = ['ALERT', 'THEME_SOFT', 'THEME_SOLID', 'THEME_GRADIENT', 'GRADIENT', 'CUSTOM'];

  const modalElement = writable();
  const mode = writable('create');
  // Options start filled, so the form can read them before the modal was opened once.
  const announcement = writable({ displayOptions: parseOptions(null) });
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
        displayOptions: parseOptions(cloned.displayOptions),
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
        displayOptions: parseOptions(null),
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
  import { api } from '@panomc/sdk/plugin-api';
  import { Editor, DragAndDropZone } from '@panomc/sdk/components/panel';
  import { _, showSuccessToast, showErrorToast } from '../../../main';

  function errorText(error) {
    const code = typeof error === 'string' ? error : error?.code;
    if (code === 'NOT_FOUND') return $_('modals.add-edit.error-not-found');
    if (code === 'BAD_REQUEST') return $_('modals.add-edit.error-invalid');
    return $_('modals.add-edit.error-general');
  }

  let isEditorEmpty = true;
  let activeContentIndex = 0;
  let loading = false;
  let customCssModalElement;
  let customCssDraft = '';
  let announcementModalShown = false;
  let openingCustomCss = false;

  let dropZoneActive = false;

  function openCustomCss() {
    if (openingCustomCss) return;
    openingCustomCss = true;
    customCssDraft = $announcement.customCss || '';
    get(modalElement).addEventListener(
      'hidden.bs.modal',
      () => {
        openingCustomCss = false;
        window.bootstrap.Modal.getOrCreateInstance(customCssModalElement).show();
      },
      { once: true },
    );
    modal.hide();
  }

  function applyCustomCss() {
    $announcement.customCss = customCssDraft;
    window.bootstrap.Modal.getInstance(customCssModalElement).hide();
  }

  function returnToAnnouncement() {
    modal.show();
  }

  $: displayImage =
    $modalImage ||
    ($isImageRemoved
      ? null
      : $announcement.imageFileName
        ? `/api/plugins/pano-plugin-announcement/panel/announcements/image/${$announcement.imageFileName}`
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
      showErrorToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-size');
    } else if (error === 'INVALID_TYPE') {
      showErrorToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-type');
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
      showErrorToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-size');
      if (modalImageInput) modalImageInput.value = '';
      return;
    }

    // File type check
    if (!allowedTypes.includes(file.type)) {
      showErrorToast('plugins.pano-plugin-announcement.modals.add-edit.image-error-type');
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
        displayOptions: JSON.stringify(
          $announcement.displayType === 'BANNER' ? optionsToSave($announcement.displayOptions) : {},
        ),
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
        result = await api.panel.put({
          path: `/announcements/${$announcement.id}`,
          body: formData,
          headers: {}, // ApiUtil might need null/empty headers to not set Content-Type to application/json
        });
      } else {
        result = await api.panel.post({
          path: `/announcements`,
          body: formData,
          headers: {},
        });
      }

      if (result.error) {
        console.error('Save failed:', result.error);
        showErrorToast('plugins.pano-plugin-announcement.toasts.error-saving', {
          error: errorText(result.error),
        });
      } else {
        showSuccessToast(
          $mode === 'create'
            ? 'plugins.pano-plugin-announcement.toasts.announcement-added'
            : 'plugins.pano-plugin-announcement.toasts.announcement-updated',
        );
        hide();
        callback($announcement);
      }
    } catch (e) {
      console.error(e);
      showErrorToast('plugins.pano-plugin-announcement.toasts.error-saving', {
        error: $_('modals.add-edit.error-general'),
      });
    } finally {
      loading = false;
    }
  }

  // Numbers from range inputs, the custom separator trimmed; the backend checks every value again.
  function optionsToSave(options) {
    return {
      ...options,
      marqueeSpeed: Number(options.marqueeSpeed),
      rotateSeconds: Number(options.rotateSeconds),
      gradientAngle: Number(options.gradientAngle),
      customSeparator: (options.customSeparator || '').trim(),
    };
  }

  // Live preview of the banner, drawn with the same helper as the site (theme/bannerLook.js).
  $: previewOptions = parseOptions(optionsToSave($announcement.displayOptions || {}));
  $: previewLook = bannerLook(previewOptions);
  $: previewSeparator = separatorText(previewOptions);
  $: previewFilled = ($announcement.bannerContents || []).filter((c) => c && c.trim() !== '');
  $: previewContents =
    previewFilled.length === 0
      ? [$_('modals.add-edit.preview-empty')]
      : previewFilled.length > 1 && previewOptions.multiMode !== 'INLINE'
        ? [previewFilled[0]]
        : previewFilled;
  $: previewKey = `${previewContents.join('\u0000')}|${previewSeparator}|${previewOptions.fullStart}`;
  $: previewAlignClass =
    $announcement.textAlign === 'LEFT'
      ? 'text-start'
      : $announcement.textAlign === 'RIGHT'
        ? 'text-end'
        : 'text-center';
  $: previewJustifyClass =
    $announcement.textAlign === 'LEFT'
      ? 'justify-content-start'
      : $announcement.textAlign === 'RIGHT'
        ? 'justify-content-end'
        : 'justify-content-center';

  $: previewLoop =
    previewOptions.fullStart && previewFilled.length > 1 && previewOptions.multiMode === 'INLINE';

  /** The marquee duration from its width, so the preview moves at the chosen px per second. */
  function previewMarquee(node, params) {
    let current = params;
    const apply = () => {
      // The endless line moves one of its two groups per pass.
      const distance = (node.offsetWidth || 0) / (current.loop ? 2 : 1);
      if (distance > 0) {
        node.style.setProperty(
          '--ann-duration',
          `${Math.max(distance / Math.max(Number(current.speed) || 60, 5), 2)}s`,
        );
      }
    };
    apply();
    const observer = new ResizeObserver(apply);
    observer.observe(node);
    return {
      update(next) {
        current = next;
        apply();
      },
      destroy() {
        observer.disconnect();
      },
    };
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
