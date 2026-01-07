<article class="container vstack gap-3">
  <!-- Action Menu -->
  <PageActions leftClasses="d-lg-flex d-none" middleClasses="d-lg-flex d-none">
    <div slot="right">
      <a
        href="{base}/announcements/create"
        class="btn btn-secondary"
        role="button">
        <i class="fas fa-plus"></i>
        <span class="d-lg-inline d-none ms-2"> Yeni Duyuru</span>
      </a>
    </div>
  </PageActions>

  <!-- Announcements Table -->
  <div class="card">
    <CardHeader>
      <div slot="left">
        {filteredAnnouncements?.length || 0} Duyuru
      </div>
      <CardFilters slot="right">
        <CardFiltersItem
          href="{base}/announcements"
          active={currentStatus === "ALL"}>
          Tümü
        </CardFiltersItem>
        <CardFiltersItem
          href="{base}/announcements?status=ACTIVE"
          active={currentStatus === "ACTIVE"}>
          Aktif
        </CardFiltersItem>
        <CardFiltersItem
          href="{base}/announcements?status=INACTIVE"
          active={currentStatus === "INACTIVE"}>
          Pasif
        </CardFiltersItem>
      </CardFilters>
    </CardHeader>

    <!-- No Announcements -->
    {#if !filteredAnnouncements || filteredAnnouncements.length === 0}
      <NoContent />
    {:else}
      <!-- Announcements Table -->
      <div class="table-responsive">
        <table class="table table-hover">
          <thead>
            <tr>
              <th scope="col" class="align-middle text-nowrap"></th>
              <th scope="col" class="align-middle text-nowrap"> ID </th>
              <th scope="col" class="align-middle text-nowrap"> Başlık </th>
              <th scope="col" class="align-middle text-nowrap"> Durum </th>
              <th scope="col" class="align-middle text-nowrap"> Tür </th>
              <th scope="col" class="align-middle text-nowrap"> Zaman </th>
            </tr>
          </thead>
          <tbody>
            {#each filteredAnnouncements as announcement, index (announcement.id)}
              <tr>
                <td class="align-middle">
                  <div class="dropdown position-static">
                    <button
                      type="button"
                      class="btn btn-sm btn-link"
                      data-bs-toggle="dropdown"
                      title="İşlemler"
                      aria-label="İşlemler">
                      <span class="fas fa-ellipsis-v"></span>
                    </button>
                    <div
                      class="dropdown-menu dropdown-menu-start animate__animated animate__fadeIn">
                      <button
                        type="button"
                        class="dropdown-item"
                        on:click={() => onEditClick(announcement.id)}
                        class:disabled={buttonsLoading}>
                        <span>
                          <i class="fas fa-edit me-2"></i>
                          Düzenle
                        </span>
                      </button>
                      <button
                        type="button"
                        class="dropdown-item"
                        on:click={() => onDeleteClick(announcement.id)}
                        class:disabled={buttonsLoading}>
                        <i class="fas fa-trash me-2"></i>
                        <span> Sil </span>
                      </button>
                    </div>
                  </div>
                </td>
                <td class="align-middle">
                  <code>{announcement.id}</code>
                </td>
                <td class="align-middle">
                  <div class="fw-semibold">
                    <a
                      href="{base}/announcements/{announcement.id}"
                      class="text-decoration-none">
                      {announcement.title}
                    </a>
                  </div>
                </td>
                <td class="align-middle">
                  {#if announcement.status === "ACTIVE"}
                    <span class="badge text-bg-success"> Aktif </span>
                  {:else if announcement.status === "INACTIVE"}
                    <span class="badge text-bg-secondary"> Pasif </span>
                  {:else}
                    <span class="badge text-bg-warning"> Zamanlandı </span>
                  {/if}
                </td>
                <td class="align-middle">
                  <span class="badge text-bg-primary">
                    {announcement.displayType === "BANNER"
                      ? "Banner"
                      : announcement.displayType === "MODAL"
                        ? "Modal"
                        : announcement.displayType}
                  </span>
                </td>
                <td class="align-middle text-nowrap">
                  <small>
                    {new Date(announcement.time).toLocaleDateString("tr", {
                      year: "numeric",
                      month: "short",
                      day: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </small>
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    {/if}
  </div>

  <!-- Edit Modal -->
  <svelte:component this={AddEditAnnouncementModal} />

  <!-- Delete Confirmation Modal -->
  <svelte:component this={ConfirmDeleteAnnouncementModal} />
</article>

<script context="module">
  /**
   * @type {import('@sveltejs/kit').PageLoad}
   */

  export async function load(event) {
    return {};
  }

  // Mock data for now - replace with actual API call
  const announcements = [
    {
      id: 1,
      title: "Sample Announcement 1",
      displayType: "BANNER",
      time: "2024-01-15 10:30:00",
      timeType: "PERMANENT",
      status: "ACTIVE",
      content: "",
      customCss: "",
    },
    {
      id: 2,
      title: "Sample Announcement 2",
      displayType: "MODAL",
      time: "2024-01-14 14:20:00",
      timeType: "TIMED",
      status: "INACTIVE",
      content: "Bu bir örnek modal duyurusu içeriğidir.",
      customCss:
        ".custom-announcement { background: linear-gradient(45deg, #ff6b6b, #4ecdc4); }",
    },
  ];
</script>

<script>
  import { base, page } from "@panomc/sdk/svelte";

  import {
    PageActions,
    CardHeader,
    CardFilters,
    CardFiltersItem,
    NoContent,
  } from "@panomc/sdk/components";

  import ConfirmDeleteAnnouncementModal, {
    show as showDeleteAnnouncementModal,
    setCallback as setDeleteAnnouncementModalCallback,
    onHide as onDeleteAnnouncementModalHide,
  } from "./components/modals/ConfirmDeleteAnnouncementModal.svelte";

  import AddEditAnnouncementModal, {
    show as showAddEditAnnouncementModal,
    setCallback as setAddEditAnnouncementModalCallback,
  } from "./components/modals/AddEditAnnouncementModal.svelte";

  export let data = { announcements };

  $: currentStatus = $page.url.searchParams.get("status") || "ALL";
  $: filteredAnnouncements =
    currentStatus === "ALL"
      ? data.announcements
      : data.announcements.filter((a) => a.status === currentStatus);

  let buttonsLoading = false;

  function onEditClick(id) {
    const announcement = data.announcements.find((a) => a.id === id);
    if (announcement) {
      showAddEditAnnouncementModal("edit", {
        id: announcement.id,
        title: announcement.title,
        status: announcement.status,
        timeType: announcement.timeType || "PERMANENT",
        time:
          announcement.timeType === "TIMED"
            ? new Date(announcement.time).toISOString().slice(0, 16)
            : "", // datetime-local format
        displayType: announcement.displayType,
        content: announcement.content || "",
        customCss: announcement.customCss || "",
        modalDisplayFrequency: announcement.modalDisplayFrequency || "ALWAYS",
      });
    }
  }

  setAddEditAnnouncementModalCallback(() => {
    // This will be called when announcement is successfully saved
    location.reload();
  });

  function onDeleteClick(id) {
    const announcement = data.announcements.find((a) => a.id === id);
    if (announcement) {
      showDeleteAnnouncementModal(announcement);
    }
  }

  // Set up delete modal callbacks
  setDeleteAnnouncementModalCallback(() => {
    // This will be called when announcement is successfully deleted
    // Here you would typically refresh the data or remove from local array
    console.log("Announcement deleted successfully");
    // For now, just reload the page or update local data
    location.reload();
  });

  onDeleteAnnouncementModalHide(() => {
    // This will be called when modal is hidden (cancelled or after delete)
    console.log("Delete modal hidden");
  });
</script>
