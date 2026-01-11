<script>
  import { _ } from "../../main";
  import { Date as DateComponent } from "@panomc/sdk/components";
  import { base } from "@panomc/sdk/svelte";

  export let announcement;
  export let currentStatus;
  export let currentVisibility;
  export let buttonsLoading = false;
  export let onEditClick;
  export let onDeleteClick;

  $: imageUrl = announcement.imageFileName 
    ? `${base}/api/panel/announcements/image/${announcement.imageFileName}?thumbnail=true` 
    : null;

  import { onMount } from "svelte";
  let now = Date.now();
  onMount(() => {
    const interval = setInterval(() => {
      now = Date.now();
    }, 1000);
    return () => clearInterval(interval);
  });

  $: isShowing = announcement.status && 
                  (announcement.showFrom === null || Number(announcement.showFrom) === 0 || Number(announcement.showFrom) <= now) && 
                  (announcement.until === null || Number(announcement.until) === 0 || Number(announcement.until) > now);
</script>

<tr>
  <td class="align-middle text-center" style="width: 40px;">
    <div class="dropdown position-static">
      <button
        type="button"
        class="btn btn-sm btn-link p-0"
        data-bs-toggle="dropdown"
        title={$_('pages.announcements.actions.label')}
        aria-label={$_('pages.announcements.actions.label')}>
        <span class="fas fa-ellipsis-v"></span>
      </button>
      <div class="dropdown-menu dropdown-menu-start animate__animated animate__fadeIn">
        <button
          type="button"
          class="dropdown-item"
          on:click={() => onEditClick(announcement.id)}
          class:disabled={buttonsLoading}>
          <span>
            <i class="fas fa-edit me-2"></i>
            {$_('pages.announcements.actions.edit')}
          </span>
        </button>
        <button
          type="button"
          class="dropdown-item text-danger"
          on:click={() => onDeleteClick(announcement.id)}
          class:disabled={buttonsLoading}>
          <i class="fas fa-trash me-2"></i>
          <span> {$_('pages.announcements.actions.delete')} </span>
        </button>
      </div>
    </div>
  </td>
  <td class="align-middle" style="width: 60px;">
    <code>{announcement.id}</code>
  </td>
  <td class="align-middle" style="width: 60px;">
    {#if imageUrl}
      <div class="thumbnail-wrapper border rounded overflow-hidden flex-shrink-0" style="width: 48px; height: 48px;">
        <img 
          src={imageUrl} 
          alt={announcement.title} 
          class="w-100 h-100 object-fit-cover"
          loading="lazy" />
      </div>
    {/if}
  </td>
  <td class="align-middle">
    <div class="text-truncate">
      <button
        type="button"
        on:click={() => onEditClick(announcement.id)}
        class="btn btn-link p-0 fw-semibold text-primary text-decoration-none text-start shadow-none d-block text-truncate w-100">
        {announcement.title}
      </button>
    </div>
  </td>
  <td class="align-middle" class:table-active={currentStatus}>
    {#if announcement.status}
      <span class="badge text-bg-success"> {$_('pages.announcements.filters.active')} </span>
    {:else}
      <span class="badge text-bg-secondary"> {$_('pages.announcements.filters.inactive')} </span>
    {/if}
  </td>
  <td class="align-middle" class:table-active={currentVisibility !== null}>
    {#if isShowing}
      <span class="badge rounded-pill text-bg-success-subtle text-success border border-success-subtle">
        <i class="fas fa-eye me-1"></i> {$_('pages.announcements.table.live')}
      </span>
    {:else}
      <span class="badge rounded-pill text-bg-danger-subtle text-danger border border-danger-subtle">
        <i class="fas fa-eye-slash me-1"></i> {$_('pages.announcements.table.hidden')}
      </span>
    {/if}
  </td>
  <td class="align-middle">
    <span class="badge text-bg-primary">
      {announcement.type === "BANNER"
        ? $_('pages.announcements.types.banner')
        : announcement.type === "MODAL"
          ? $_('pages.announcements.types.modal')
          : announcement.type}
    </span>
  </td>
  <td class="align-middle text-nowrap">
    <DateComponent time={announcement.createdAt} fullFormat={true} />
  </td>
  <td class="align-middle text-nowrap">
    <DateComponent time={announcement.updatedAt} fullFormat={true} />
  </td>
</tr>

<style>
  .thumbnail-wrapper {
    background-color: var(--bs-tertiary-bg);
  }
</style>
