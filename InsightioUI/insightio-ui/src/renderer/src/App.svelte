<script>
  import { fade } from 'svelte/transition'
  import Layout from './components/Layout.svelte'
  import Login from './components/pages/Login.svelte'
  import pageStore from './stores/pageStore'

  let loggedIn = false
  // let showLogin = true

  // Method to change the active page
  function changePage(pageKey) {
    pageStore.setActivePage(pageKey)
  }

  function handleLogin() {
    loggedIn = true
    // Wait for the animation to finish before removing the Login component
    // setTimeout(() => {
    //   showLogin = false
    // }, 300) // Should match the duration of the fade transition
  }

  // To get the active page component, you can use a derived store or a reactive statement
  $: activePage = $pageStore.pages.find((page) => page.key === $pageStore.activePage)
</script>

<main>
  {#if !loggedIn}
    <div in:fade={{ duration: 300 }}>
      <Login on:login={handleLogin} />
    </div>
  {:else}
    <Layout
      activePage={activePage.key}
      on:pageChange={(e) => {
        changePage(e.detail)
      }}
    >
      <svelte:component this={activePage.component} />
    </Layout>
  {/if}
</main>
