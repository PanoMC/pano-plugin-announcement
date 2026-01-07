<!-- Add / Edit Announcement Modal -->
<div
  class="modal fade"
  bind:this={$modalElement}
  tabindex="-1"
  role="dialog"
  aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          {$mode === "edit" ? "Duyuru Düzenle" : "Yeni Duyuru"}
        </h5>
        <button
          type="button"
          class="btn-close"
          aria-label="Kapat"
          title="Kapat"
          on:click={hide}></button>
      </div>
      <div class="modal-body">
        <form on:submit|preventDefault={onSaveAnnouncement}>
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
                placeholder="Duyuru başlığını girin"
                required />
              <label for="title">Başlık</label>
            </div>
          </div>
          <div class="form-floating mb-3">
            <select
              class="form-select"
              id="status"
              bind:value={$announcement.status}>
              <option value="ACTIVE">Aktif</option>
              <option value="DRAFT">Taslak</option>
            </select>
            <label for="status">Durum</label>
          </div>
          <div class="mb-3">
            <label class="form-label" for="timePermanent">Zaman Tipi</label>
            <div class="btn-group w-100" role="group">
              <input
                type="radio"
                class="btn-check"
                id="timePermanent"
                autocomplete="off"
                bind:group={$announcement.timeType}
                value="PERMANENT" />
              <label class="btn btn-outline-primary" for="timePermanent">
                Kalıcı
              </label>

              <input
                type="radio"
                class="btn-check"
                id="timeTimed"
                autocomplete="off"
                bind:group={$announcement.timeType}
                value="TIMED" />
              <label class="btn btn-outline-primary" for="timeTimed">
                Zamanlı
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
                placeholder="Zaman Seçin"
                required />
              <label for="time">Zaman Seçin</label>
            </div>
          {/if}
          <div class="form-floating mb-3">
            <select
              class="form-select"
              id="displayType"
              bind:value={$announcement.displayType}>
              <option value="BANNER">Banner</option>
              <option value="MODAL">Modal</option>
            </select>
            <label for="displayType">Tür</label>
          </div>

          {#if $announcement.displayType === "MODAL"}
            <div class="mb-3">
              <label class="form-label" for="modalFrequencyPermanent"
                >Gösterim</label>
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
                  Sürekli
                </label>

                <input
                  type="radio"
                  class="btn-check"
                  id="modalFrequencyOnce"
                  autocomplete="off"
                  bind:group={$announcement.modalDisplayFrequency}
                  value="ONCE" />
                <label class="btn btn-outline-primary" for="modalFrequencyOnce">
                  Bir Defa
                </label>
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label" for="modalSizeNormal">Modal Boyutu</label>
              <div class="btn-group w-100" role="group">
                <input
                  type="radio"
                  class="btn-check"
                  id="modalSizeSmall"
                  autocomplete="off"
                  bind:group={$announcement.modalSize}
                  value="SMALL" />
                <label class="btn btn-outline-primary" for="modalSizeSmall">
                  Küçük
                </label>

                <input
                  type="radio"
                  class="btn-check"
                  id="modalSizeNormal"
                  autocomplete="off"
                  bind:group={$announcement.modalSize}
                  value="NORMAL" />
                <label class="btn btn-outline-primary" for="modalSizeNormal">
                  Normal
                </label>

                <input
                  type="radio"
                  class="btn-check"
                  id="modalSizeLarge"
                  autocomplete="off"
                  bind:group={$announcement.modalSize}
                  value="LARGE" />
                <label class="btn btn-outline-primary" for="modalSizeLarge">
                  Büyük
                </label>

                <input
                  type="radio"
                  class="btn-check"
                  id="modalSizeFull"
                  autocomplete="off"
                  bind:group={$announcement.modalSize}
                  value="FULL" />
                <label class="btn btn-outline-primary" for="modalSizeFull">
                  Tam
                </label>
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label" for="modalImage"> Görsel </label>
              <div class="position-relative w-100">
                <div class="ratio ratio-16x9 border rounded overflow-hidden">
                  <img
                    src={modalImage}
                    class="object-fit-contain w-100 h-100"
                    alt="Modal görseli" />
                </div>
                <button
                  type="button"
                  class="btn btn-sm btn-secondary position-absolute top-0 start-100 translate-middle"
                  on:click={() => modalImageInput.click()}
                  title="Değiştir"
                  aria-label="Değiştir">
                  <i class="fas fa-pencil"></i>
                </button>
              </div>
              <input
                class="d-none"
                id="modalImage"
                type="file"
                bind:files={modalImageFiles}
                on:change={onModalImageChange}
                bind:this={modalImageInput}
                accept="image/png,image/jpeg,image/gif" />
              <small class="d-block mt-2 text-muted">
                PNG, JPEG, GIF format, maximum 2 mb.
              </small>
            </div>
          {/if}
          <div class="form-floating mb-3">
            <textarea
              class="form-control"
              id="content"
              bind:value={$announcement.content}
              placeholder="İçeriği buraya yazın"
              style="height: 300px;"></textarea>
            <label for="content">İçerik</label>
          </div>
          <div class="form-floating">
            <textarea
              class="form-control"
              id="customCss"
              bind:value={$announcement.customCss}
              placeholder="Özel CSS kodlarınızı buraya yazın"
              style="height: 120px;"></textarea>
            <label for="customCss">Özel CSS</label>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button
          type="button"
          class="btn btn-primary w-100"
          class:disabled={loading}
          on:click={onSaveAnnouncement}>
          Kaydet
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

  let callback = (announcement) => {};
  let hideCallback = (announcement) => {};
  let modal;

  export function show(newMode, newAnnouncement = null) {
    mode.set(newMode);

    if (newAnnouncement) {
      announcement.set({
        modalSize: "NORMAL",
        ...newAnnouncement,
      });
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
        modalDisplayFrequency: "ALWAYS",
        modalSize: "NORMAL",
      });
    }

    modal = new window.bootstrap.Modal(get(modalElement), {
      backdrop: "static",
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
  let loading = false;
  let modalImageFiles = null;
  let modalImageInput;
  let modalImage = "/placeholder-image.png";

  async function onModalImageChange(event) {
    const file = event.target.files[0];

    if (!file) {
      return;
    }

    // Modal image validation: max 2MB, PNG, JPEG, GIF
    const maxSize = 2 * 1024 * 1024; // 2MB
    const allowedTypes = ["image/png", "image/jpeg", "image/gif"];

    // File size check
    if (file.size > maxSize) {
      alert("Dosya boyutu 2MB'dan büyük olamaz!");
      modalImageInput.value = "";
      modalImageFiles = null;
      return;
    }

    // File type check
    if (!allowedTypes.includes(file.type)) {
      alert("Sadece PNG, JPEG ve GIF formatları desteklenir!");
      modalImageInput.value = "";
      modalImageFiles = null;
      return;
    }

    const reader = new FileReader();
    reader.readAsDataURL(file);

    reader.onload = (e) => {
      modalImage = e.target.result;
    };
  }

  function onSaveAnnouncement() {
    loading = true;
    // TODO: Implement actual save API call
    console.log("Saving announcement:", get(announcement));
    console.log("Modal image:", modalImageFiles);

    // Simulate API call delay
    setTimeout(() => {
      loading = false;
      hide();
      callback(get(announcement));
    }, 1000);
  }
</script>
