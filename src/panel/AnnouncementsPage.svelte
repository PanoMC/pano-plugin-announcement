<article class="container vstack gap-3">
  <!-- Action Menu -->
  <PageActions leftClasses="d-lg-flex d-none" middleClasses="d-lg-flex d-none">
    <div slot="right">
      <button
        type="button"
        class="btn btn-secondary"
        on:click={onCreateClick}>
        <i class="fas fa-plus"></i>
        <span class="d-lg-inline d-none ms-2"> {$_('pages.announcements.new-announcement')}</span>
      </button>
    </div>
  </PageActions>

  <!-- Announcements Table -->
  <div class="card">
    <CardHeader>
      <div slot="left">
        {$_('pages.announcements.count', { values: { count: data.announcementCount || 0 } })}
      </div>
      <CardFilters slot="right">
        <CardFiltersItem
          href="/announcements"
          active={!filterActive}>
          {$_('pages.announcements.filters.all')}
        </CardFiltersItem>
        <CardFiltersItem
          href="/announcements?status=ACTIVE"
          active={currentStatus === "ACTIVE"}>
          {$_('pages.announcements.filters.active')}
        </CardFiltersItem>
        <CardFiltersItem
          href="/announcements?status=INACTIVE"
          active={currentStatus === "INACTIVE"}>
          {$_('pages.announcements.filters.inactive')}
        </CardFiltersItem>
        <CardFiltersItem
          href="/announcements?visible=true"
          active={currentVisibility === "true"}>
          {$_('pages.announcements.filters.live')}
        </CardFiltersItem>
        <CardFiltersItem
          href="/announcements?visible=false"
          active={currentVisibility === "false"}>
          {$_('pages.announcements.filters.hidden')}
        </CardFiltersItem>
      </CardFilters>
    </CardHeader>

    <!-- No Announcements -->
    {#if !data.announcements || data.announcements.length === 0}
      <NoContent />
    {:else}
      <!-- Announcements Table -->
      <div class="table-responsive">
        <table class="table table-hover">
          <thead>
          <tr>
            <th scope="col" class="align-middle text-nowrap" style="width: 40px;"></th>
            <th scope="col" class="align-middle text-nowrap" style="width: 60px;"> {$_('pages.announcements.table.id')}</th>
            <th scope="col" class="align-middle text-nowrap" style="width: 60px;"></th>
            <th scope="col" class="align-middle text-nowrap"> {$_('pages.announcements.table.title')}</th>
            <th scope="col" class="align-middle text-nowrap" style="width: 100px;" class:table-active={currentStatus}> {$_('pages.announcements.table.status')}</th>
            <th scope="col" class="align-middle text-nowrap" style="width: 100px;" class:table-active={currentVisibility}> {$_('pages.announcements.table.showing')}</th>
            <th scope="col" class="align-middle text-nowrap"> {$_('pages.announcements.table.type')}</th>
            <th scope="col" class="align-middle text-nowrap"> {$_('pages.announcements.table.date')}</th>
            <th scope="col" class="align-middle text-nowrap"> {$_('pages.announcements.table.updated-at')}</th>
          </tr>
          </thead>
          <tbody>
          {#each data.announcements as announcement (announcement.id)}
            <AnnouncementRow 
              {announcement} 
              {currentStatus}
              {currentVisibility}
              {buttonsLoading}
              onEditClick={onEditClick}
              onDeleteClick={onDeleteClick} />
          {/each}
          </tbody>
        </table>
      </div>
    {/if}
    <div class="card-footer">
      <Pagination
        page={data.page}
        totalPage={data.totalPage}
        on:firstPageClick={() => onPageClick(1)}
        on:lastPageClick={() => onPageClick(data.totalPage)}
        on:pageLinkClick={(event) => onPageClick(event.detail.page)} />
    </div>
  </div>

  <!-- Edit Modal -->
  <svelte:component this={AddEditAnnouncementModal} />

  <!-- Delete Confirmation Modal -->
  <svelte:component this={ConfirmDeleteAnnouncementModal} />
</article>

<script context="module">
  import ApiUtil, { buildQueryParams } from "@panomc/sdk/utils/api";

  /**
   * @type {import("@sveltejs/kit").PageLoad}
   */

  export async function load(event) {
    const { parent, url: { searchParams } } = event;
    const { pageTitle } = await parent();

    pageTitle.set('plugins.pano-plugin-announcement.pages.announcements.title');

    const page = searchParams.get("page") || 1;
    const statusParam = searchParams.get("status");
    let status = null;
    if (statusParam === "ACTIVE") status = true;
    else if (statusParam === "INACTIVE") status = "false";

    const visibleParam = searchParams.get("visible");
    let visible = null;
    if (visibleParam === "true") visible = true;
    else if (visibleParam === "false") visible = "false";

    const queryParams = buildQueryParams({
      page,
      status,
      visible
    });

    const body = await ApiUtil.get({
      path: `/api/panel/announcements` + queryParams,
      request: event,
    });

    if (body.error) {
      return { data: { announcements: [], announcementCount: 0, totalPage: 1, page: 1 } };
    }

    body.page = parseInt(page);
    return { data: body };
  }
</script>

<script>
  import { base, page, goto } from "@panomc/sdk/svelte";

  import {
    PageActions,
    CardHeader,
    CardFilters,
    CardFiltersItem,
    NoContent,
    Pagination,
    Date
  } from "@panomc/sdk/components";

  import { _ } from "../main";
  import AnnouncementRow from "./components/AnnouncementRow.svelte";

  import ConfirmDeleteAnnouncementModal, {
    show as showDeleteAnnouncementModal,
    setCallback as setDeleteAnnouncementModalCallback,
    onHide as onDeleteAnnouncementModalHide
  } from "./components/modals/ConfirmDeleteAnnouncementModal.svelte";

  import AddEditAnnouncementModal, {
    show as showAddEditAnnouncementModal,
    setCallback as setAddEditAnnouncementModalCallback
  } from "./components/modals/AddEditAnnouncementModal.svelte";

  export let data;

  $: currentStatus = $page.url.searchParams.get("status");
  $: currentVisibility = $page.url.searchParams.get("visible");
  $: filterActive = currentStatus || currentVisibility;

  let buttonsLoading = false;

  async function refreshData() {
    const pageNum = data.page === 1 ? null : data.page;
    const statusVal = $page.url.searchParams.get("status");
    const visibleVal = $page.url.searchParams.get("visible");
 
    const queryParams = buildQueryParams({
      page: pageNum,
      status: statusVal,
      visible: visibleVal
    });
 
    await goto(`${base}/announcements${queryParams}`, { invalidateAll: true });
  }

  async function onPageClick(pageNum) {
    data.page = pageNum;
    await refreshData();
  }

  function onCreateClick() {
    showAddEditAnnouncementModal("create");
  }

  function onEditClick(id) {
    const announcement = data.announcements.find((a) => a.id === id);
    if (announcement) {
      showAddEditAnnouncementModal("edit", {
        ...announcement,
        displayType: announcement.type,
        bannerEffect: announcement.effectType,
        timeType: announcement.until ? "TIMED" : "PERMANENT",
        time: announcement.until ? new Date(announcement.until).toISOString().slice(0, 16) : "",
        modalSize: announcement.size === 0 ? "SMALL" : announcement.size === 1 ? "NORMAL" : announcement.size === 2 ? "LARGE" : "FULL",
        status: announcement.status ? "ACTIVE" : "DRAFT"
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
