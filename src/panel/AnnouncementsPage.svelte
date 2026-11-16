<article class="container vstack gap-3">
  <!-- Action Menu -->
  <PageActions leftClasses="d-lg-flex d-none" middleClasses="d-lg-flex d-none">
    <div slot="right">
      <button type="button" class="btn btn-secondary" on:click={onCreateClick}>
        <i class="fas fa-plus"></i>
        <span class="ms-2">{$_('pages.announcements.new-announcement')}</span>
      </button>
    </div>
  </PageActions>

  <!-- Announcements Table -->
  <div class="card">
    <CardHeader>
      <div slot="left">
        {$_('pages.announcements.count', { values: { count: data.announcementCount || 0 } })}
      </div>
      <div slot="middle" style="width: 250px;">
        <SearchInput
          autofocus
          initialValue={search}
          searching={isSearching}
          debounceMs={500}
          on:change={onSearchInput} />
      </div>
      <CardFilters slot="right">
        <CardFiltersItem href={filterHref(search)} active={!filterActive}>
          {$_('pages.announcements.filters.all')}
        </CardFiltersItem>
        <CardFiltersItem href={filterHref(search, 'ACTIVE')} active={currentStatus === 'ACTIVE'}>
          {$_('pages.announcements.filters.active')}
        </CardFiltersItem>
        <CardFiltersItem
          href={filterHref(search, 'INACTIVE')}
          active={currentStatus === 'INACTIVE'}>
          {$_('pages.announcements.filters.inactive')}
        </CardFiltersItem>
        <CardFiltersItem href={filterHref(search, null, 'true')} active={currentVisibility === 'true'}>
          {$_('pages.announcements.filters.live')}
        </CardFiltersItem>
        <CardFiltersItem href={filterHref(search, null, 'false')} active={currentVisibility === 'false'}>
          {$_('pages.announcements.filters.hidden')}
        </CardFiltersItem>
      </CardFilters>
    </CardHeader>

    <!-- No Announcements -->
    {#if !data.announcements || data.announcements.length === 0}
      <NoContent icon="" />
    {:else}
      <!-- Announcements Table -->
      <div class="table-responsive">
        <table class="table table-hover">
          <thead>
            <tr>
              <th scope="col" class="align-middle text-nowrap" style="width: 40px;"></th>
              <th scope="col" class="align-middle text-nowrap" style="width: 60px;">
                {$_('pages.announcements.table.id')}</th>
              <th scope="col" class="align-middle text-nowrap">
                {$_('pages.announcements.table.title')}</th>
              <th
                scope="col"
                class="align-middle text-nowrap"
                style="width: 100px;">
                {$_('pages.announcements.table.status')}</th>
              <th
                scope="col"
                class="align-middle text-nowrap"
                style="width: 100px;">
                {$_('pages.announcements.table.showing')}</th>
              <th scope="col" class="align-middle text-nowrap">
                {$_('pages.announcements.table.type')}</th>
              <th scope="col" class="align-middle text-nowrap">
                {$_('pages.announcements.table.date')}</th>
              <th scope="col" class="align-middle text-nowrap">
                {$_('pages.announcements.table.updated-at')}</th>
            </tr>
          </thead>
          <tbody>
            {#each data.announcements as announcement (announcement.id)}
              <AnnouncementRow
                {announcement}
                {buttonsLoading}
                {onEditClick}
                {onDeleteClick} />
            {/each}
          </tbody>
        </table>
      </div>
      <div class="card-footer">
        <Pagination
          {...paginationProps(data.page, data.pageInfo)}
          on:firstPageClick={() => onPageClick(1)}
          on:lastPageClick={() => onPageClick(data.pageInfo.totalPages)}
          on:pageLinkClick={(event) => onPageClick(event.detail.page)} />
      </div>
    {/if}
  </div>

  <!-- Edit Modal -->
  <svelte:component this={AddEditAnnouncementModal} />

  <!-- Delete Confirmation Modal -->
  <svelte:component this={ConfirmDeleteAnnouncementModal} />
</article>

<script context="module">
  import { buildQueryParams } from '@panomc/sdk/utils/api';
  import { api } from '@panomc/sdk/plugin-api';
  import { paginationProps } from './pagination.js';

  /**
   * @type {import("@sveltejs/kit").PageLoad}
   */

  export async function load(event) {
    const {
      parent,
      url: { searchParams },
    } = event;
    const { pageTitle } = await parent();

    pageTitle.set('plugins.pano-plugin-announcement.pages.announcements.title');

    const page = searchParams.get('page') || 1;
    const statusParam = searchParams.get('status');
    let status = null;
    if (statusParam === 'ACTIVE') status = true;
    else if (statusParam === 'INACTIVE') status = 'false';

    const visibleParam = searchParams.get('visible');
    let visible = null;
    if (visibleParam === 'true') visible = true;
    else if (visibleParam === 'false') visible = 'false';
    const search = searchParams.get('search')?.trim() || null;

    const queryParams = buildQueryParams({
      page,
      status,
      visible,
      search,
    });

    const body = await api.panel.get({
      path: `/announcements` + queryParams,
      request: event,
    });

    if (body.error) {
      throw new Error(body.error.message || body.error.code);
    }

    return {
      data: {
        announcements: body.items,
        announcementCount: body.page.totalItems,
        pageInfo: body.page,
        page: parseInt(page),
      },
    };
  }
</script>

<script>
  import { base, page, goto } from '@panomc/sdk/svelte';

  import {
    PageActions,
    CardHeader,
    CardFilters,
    CardFiltersItem,
    NoContent,
    Pagination,
    SearchInput,
  } from '@panomc/sdk/components/panel';

  import { _ } from '../main';
  import AnnouncementRow from './components/AnnouncementRow.svelte';

  import ConfirmDeleteAnnouncementModal, {
    show as showDeleteAnnouncementModal,
    setCallback as setDeleteAnnouncementModalCallback,
  } from './components/modals/ConfirmDeleteAnnouncementModal.svelte';

  import AddEditAnnouncementModal, {
    show as showAddEditAnnouncementModal,
    setCallback as setAddEditAnnouncementModalCallback,
  } from './components/modals/AddEditAnnouncementModal.svelte';

  export let data;

  $: currentStatus = $page.url.searchParams.get('status');
  $: currentVisibility = $page.url.searchParams.get('visible');
  $: filterActive = currentStatus || currentVisibility;
  $: search = $page.url.searchParams.get('search') || '';

  let buttonsLoading = false;
  let isSearching = false;

  function filterHref(searchValue, status = null, visible = null) {
    return '/announcements' + buildQueryParams({ status, visible, search: searchValue || null });
  }

  async function refreshData(pageNum = data.page, searchValue = search) {
    const statusVal = $page.url.searchParams.get('status');
    const visibleVal = $page.url.searchParams.get('visible');

    const queryParams = buildQueryParams({
      page: pageNum === 1 ? null : pageNum,
      status: statusVal,
      visible: visibleVal,
      search: searchValue.trim() || null,
    });

    isSearching = true;
    try {
      await goto(`${base}/announcements${queryParams}`, { invalidateAll: true });
    } finally {
      isSearching = false;
    }
  }

  function onSearchInput(event) {
    refreshData(1, event.detail.value);
  }

  async function onPageClick(pageNum) {
    await refreshData(pageNum);
  }

  function onCreateClick() {
    showAddEditAnnouncementModal('create');
  }

  function onEditClick(id) {
    const announcement = data.announcements.find((a) => a.id === id);
    if (announcement) {
      showAddEditAnnouncementModal('edit', {
        ...announcement,
        displayType: announcement.type,
        bannerEffect: announcement.effectType,
        timeType: announcement.until ? 'TIMED' : 'PERMANENT',
        time: announcement.until ? new Date(announcement.until).toISOString().slice(0, 16) : '',
        modalSize:
          announcement.size === 0
            ? 'SMALL'
            : announcement.size === 1
              ? 'NORMAL'
              : announcement.size === 2
                ? 'LARGE'
                : 'FULL',
        status: announcement.status ? 'ACTIVE' : 'DRAFT',
      });
    }
  }

  setAddEditAnnouncementModalCallback(() => {
    refreshData();
  });

  function onDeleteClick(id) {
    const announcement = data.announcements.find((a) => a.id === id);
    if (announcement) {
      showDeleteAnnouncementModal(announcement);
    }
  }

  setDeleteAnnouncementModalCallback(() => {
    refreshData();
  });
</script>
