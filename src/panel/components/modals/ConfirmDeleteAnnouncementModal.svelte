<!-- Confirm Delete Announcement Modal -->
<div aria-hidden="true" class="modal fade" bind:this={$modalElement} role="dialog" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered" role="dialog">
    <div class="modal-content">
      <div class="modal-body text-center">
        <div class="pb-3">
          <i class="fas fa-question-circle fa-3x d-block m-auto text-gray"></i>
        </div>
        {$_('modals.delete.description', { values: { title: $announcement.title } })}
      </div>
      <div class="modal-footer flex-nowrap">
        <button
          class="btn btn-link col-6 m-0"
          type="button"
          class:disabled={loading}
          on:click={hide}>
          {$_('modals.delete.cancel')}
        </button>
        <button
          class="btn btn-danger col-6 m-0"
          type="button"
          class:disabled={loading}
          on:click={onYesClick}>
          {$_('modals.delete.confirm')}
        </button>
      </div>
    </div>
  </div>
</div>

<script context="module">
    import {get, writable} from 'svelte/store';
    import ApiUtil from '@panomc/sdk/utils/api';

    const modalElement = writable();
  const announcement = writable({});

  let callback = (announcement) => {};
  let hideCallback = (announcement) => {};
  let modal;

  export function show(newAnnouncement) {
    announcement.set(newAnnouncement);

    modal = new window.bootstrap.Modal(get(modalElement), {
      backdrop: 'static',
      keyboard: false,
    });
    modal.show();
  }

  export function setCallback(newCallback) {
    callback = newCallback;
  }

  export function hide() {
    hideCallback(get(announcement));

    modal.hide();
  }

  export function onHide(newCallback) {
    hideCallback = newCallback;
  }
</script>

<script>
  import { _, showSuccessToast, showErrorToast } from '../../../main';
  let loading = false;

  async function onYesClick() {
    loading = true;

    try {
      const result = await ApiUtil.delete({
        path: `/api/panel/announcements/${get(announcement).id}`,
      });

      if (result.error) {
        // Handle error (e.g., show toast)
        console.error('Delete failed:', result.error);
        showErrorToast('plugins.pano-plugin-announcement.toasts.error-deleting', {
          values: { error: result.error.statusMessage || $_('modals.add-edit.error-unknown') },
        });
      } else {
        showSuccessToast('plugins.pano-plugin-announcement.toasts.announcement-deleted');
        callback(get(announcement));
        hide();
      }
    } catch (e) {
      console.error(e);
      showErrorToast('plugins.pano-plugin-announcement.toasts.error-deleting', {
        values: { error: $_('modals.add-edit.error-general') },
      });
    } finally {
      loading = false;
    }
  }
</script>
