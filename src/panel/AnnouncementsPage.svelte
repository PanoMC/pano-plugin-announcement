<script context="module">
  export async function load(event) {
    console.log("Load came")
  }
</script>

<script>
    export let name;
</script>

<style global>
  .announcement {
      color: red;
  }
</style>

<i class="announcement">
  Hello {name}!
</i>

