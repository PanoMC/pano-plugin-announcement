<!-- Confirm Delete Announcement Modal -->
<div
  aria-hidden="true"
  class="modal fade"
  bind:this={$modalElement}
  role="dialog"
  tabindex="-1">
  <div class="modal-dialog modal-dialog-centered" role="dialog">
    <div class="modal-content">
      <div class="modal-body text-center">
        <div class="pb-3">
          <i class="fas fa-question-circle fa-3x d-block m-auto text-gray"></i>
        </div>
        Bu duyuruyu silmek istediğinizden emin misiniz?
      </div>
      <div class="modal-footer flex-nowrap">
        <button
          class="btn btn-link col-6 m-0"
          type="button"
          class:disabled={loading}
          on:click={hide}>
          İptal
        </button>
        <button
          class="btn btn-danger col-6 m-0"
          type="button"
          class:disabled={loading}
          on:click={onYesClick}>
          Evet, Sil
        </button>
      </div>
    </div>
  </div>
</div>

<script context="module">
  import { writable, get } from "svelte/store";

  const modalElement = writable();
  const announcement = writable({});

  let callback = (announcement) => {};
  let hideCallback = (announcement) => {};
  let modal;

  export function show(newAnnouncement) {
    announcement.set(newAnnouncement);

    modal = new window.bootstrap.Modal(get(modalElement), {
      backdrop: "static",
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
  let loading = false;

  function onYesClick() {
    loading = true;

    // TODO: Implement actual delete API call
    console.log("Deleting announcement:", get(announcement));

    // Simulate API call delay
    setTimeout(() => {
      loading = false;
      hide();

      // Show success message or handle error
      callback(get(announcement));
    }, 1000);
  }
</script>
