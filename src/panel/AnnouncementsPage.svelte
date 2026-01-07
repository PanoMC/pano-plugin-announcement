<style global>
  .announcement {
    color: red;
  }
</style>

<i class="announcement">
  Hello {name}!
</i>

<PageNav slot="middle">
  <PageNavItem href="/announcements">test1</PageNavItem>
  <PageNavItem href="/settings">test2</PageNavItem>
  <PageNavItem href="/omg">test3</PageNavItem>
</PageNav>

<script context="module">
  export async function load(event) {
    console.log("Load came");
  }
</script>

<script>
  import { PageNavItem, PageNav } from "@panomc/sdk/components";
  import {_} from "@panomc/sdk/utils/language"

  export let name;
</script>
