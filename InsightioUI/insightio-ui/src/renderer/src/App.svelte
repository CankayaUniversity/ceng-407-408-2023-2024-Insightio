<script>
  import Layout from './components/Layout.svelte'
  import pageStore from './stores/pageStore'

  // Method to change the active page
  function changePage(pageKey) {
    pageStore.setActivePage(pageKey)
  }

  // To get the active page component, you can use a derived store or a reactive statement
  $: activePage = $pageStore.pages.find((page) => page.key === $pageStore.activePage)
</script>

<main>
  <Layout
    activePage={activePage.key}
    on:pageChange={(e) => {
      changePage(e.detail)
    }}
  >
    <svelte:component this={activePage.component} />
  </Layout>
</main>
