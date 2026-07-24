<style>
  .thumbnail-wrapper {
    background-color: var(--bs-tertiary-bg);
  }
</style>

<tr>
  <th scope="row" class="align-middle text-center" style="width: 60px;">
    <div class="dropdown position-static">
      <button
        type="button"
        class="btn btn-link"
        aria-expanded="false"
        aria-haspopup="true"
        data-bs-toggle="dropdown"
        use:tooltip={[$_('pages.announcements.actions.label')]}
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
          class="dropdown-item"
          on:click={() => onDeleteClick(announcement.id)}
          class:disabled={buttonsLoading}>
          <i class="fas fa-trash me-2"></i>
          <span> {$_('pages.announcements.actions.delete')} </span>
        </button>
      </div>
    </div>
  </th>
  <td class="align-middle">
    <code>{announcement.id}</code>
  </td>
  <td class="align-middle" style="width: 60px;">
    {#if imageUrl}
      <button
        type="button"
        class="thumbnail-wrapper border rounded overflow-hidden flex-shrink-0 p-0"
        style="width: 48px; height: 48px;"
        on:click={() => onEditClick(announcement.id)}>
        <img
          src={imageUrl}
          alt={announcement.title}
          class="w-100 h-100 object-fit-cover"
          loading="lazy" />
      </button>
    {/if}
  </td>
  <td class="align-middle" style="max-width: 250px;">
    <div class="text-truncate">
      <button
        type="button"
        use:tooltip={[$_('buttons.edit')]}
        aria-label={$_('buttons.edit')}
        on:click={() => onEditClick(announcement.id)}
        class="btn btn-link p-0 text-start text-decoration-none w-100 text-truncate">
        {announcement.title}
      </button>
    </div>
  </td>
  <td class="align-middle text-nowrap" class:table-active={currentStatus}>
    {#if announcement.status}
      <span class="badge text-bg-success"> {$_('pages.announcements.filters.active')} </span>
    {:else}
      <span class="badge text-bg-secondary"> {$_('pages.announcements.filters.inactive')} </span>
    {/if}
  </td>
  <td class="align-middle text-nowrap" class:table-active={currentVisibility !== null}>
    {#if isShowing}
      <span class="badge text-bg-success"> {$_('pages.announcements.table.live')} </span>
    {:else}
      <span class="badge text-bg-danger"> {$_('pages.announcements.table.hidden')} </span>
    {/if}
  </td>
  <td class="align-middle text-nowrap">
    <span class="badge text-bg-primary">
      {announcement.type === 'BANNER'
        ? $_('pages.announcements.types.banner')
        : announcement.type === 'MODAL'
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

<script>
  import { _ } from '../../main';
  import { Date as DateComponent } from '@panomc/sdk/components/panel';
  import { base } from '@panomc/sdk/svelte';
  import { onMount } from 'svelte';
  import tooltip from '@panomc/sdk/utils/tooltip';

  export let announcement;
  export let currentStatus;
  export let currentVisibility;
  export let buttonsLoading = false;
  export let onEditClick;
  export let onDeleteClick;

  $: imageUrl = announcement.imageFileName
    ? `${base}/api/panel/announcements/image/${announcement.imageFileName}?thumbnail=true`
    : null;

  let now = Date.now();
  onMount(() => {
    const interval = setInterval(() => {
      now = Date.now();
    }, 1000);
    return () => clearInterval(interval);
  });

  $: isShowing =
    announcement.status &&
    (announcement.showFrom === null ||
      Number(announcement.showFrom) === 0 ||
      Number(announcement.showFrom) <= now) &&
    (announcement.until === null ||
      Number(announcement.until) === 0 ||
      Number(announcement.until) > now);
</script>
