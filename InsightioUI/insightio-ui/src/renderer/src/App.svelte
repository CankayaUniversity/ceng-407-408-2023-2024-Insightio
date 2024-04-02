<script>
  import { fade } from 'svelte/transition'
  import Layout from './components/Layout.svelte'
  import Login from './components/pages/Login.svelte'
  import userStore from './stores/userStore'
  import pageStore from './stores/pageStore'
  import { notify } from './functions/toastifyWrapper'
  import { onMount } from 'svelte'
  import Overlay from './components/utility/Overlay.svelte'
  import Button from './components/utility/Button.svelte'

  let loggedIn = false
  let showLogoutModal = false

  // Method to change the active page
  function changePage(pageKey) {
    pageStore.setActivePage(pageKey)
  }

  function handleLogin(e) {
    notify(`Welcome ${e.detail.username}`)
    loggedIn = true
  }

  function handleLogoutConfirmation(answer) {
    if (answer) {
      localStorage.removeItem('user')
      loggedIn = false
      showLogoutModal = false
    } else {
      showLogoutModal = false
    }
  }

  onMount(() => {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      const userData = JSON.parse(storedUser)
      // one week in milliseconds
      const oneWeek = 7 * 24 * 60 * 60 * 1000
      const currentTime = new Date().getTime()

      if (currentTime - userData.timestamp < oneWeek) {
        userStore.set(userData)
        loggedIn = true
      } else {
        // Expire the session after one week
        localStorage.removeItem('user')
        loggedIn = false
      }
    }
  })

  // To get the active page component, you can use a derived store or a reactive statement
  $: activePage = $pageStore.pages.find((page) => page.key === $pageStore.activePage)
</script>

<main>
  <Overlay
    fitContent
    showModal={showLogoutModal}
    title="Logout Confirmation"
    on:close={() => (showLogoutModal = false)}
  >
    <p class="text-white">Are you sure you want to logout?</p>
    <div class="flex space-x-2 mt-4 float-right">
      <Button
        class="px-2 py-2"
        controlButton
        color="red"
        on:click={() => handleLogoutConfirmation(true)}>Yes</Button
      >
      <Button
        class="px-2 py-2"
        controlButton
        color="blue"
        on:click={() => handleLogoutConfirmation(false)}>No</Button
      >
    </div>
  </Overlay>

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
      bind:logoutModalActive={showLogoutModal}
      on:showLogoutModal={() => (showLogoutModal = true)}
    >
      <svelte:component this={activePage.component} />
    </Layout>
  {/if}
</main>
