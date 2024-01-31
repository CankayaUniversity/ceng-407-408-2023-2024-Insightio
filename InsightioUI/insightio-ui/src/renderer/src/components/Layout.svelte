<script>
  import Icon from './utility/Icon.svelte'
  import pageStore from '../stores/pageStore'
  import { createEventDispatcher } from 'svelte'
  import appIcon from '../assets/logo.png?asset'
  import Button from './utility/Button.svelte'
  import { close, minimize } from '../api/ipc'

  export let activePage

  let dispatch = createEventDispatcher()
  let logoutModalActive = false
</script>

<div class="flex h-screen text-white bg-gray-800">
  <!-- Sidebar -->
  <div class="flex flex-col w-16 bg-gray-900 rounded-r-xl items-center non-draggable">
    <div class="flex flex-col items-center justify-center h-16 w-full p-2">
      <img src={appIcon} alt="App Icon" class="mt-2 h-9 w-9 app-icon" draggable="false" />
      <span class="mt-1 text-center app-name">Insightio</span>
    </div>
    <div class="flex flex-col justify-between flex-1">
      <nav>
        {#each $pageStore.pages as page}
          <Button
            class="flex items-center justify-center h-16 w-16"
            on:click={dispatch('pageChange', `${page.key}`)}
            iconButton={true}
            icon={page.icon + (activePage == page.key ? 'Solid' : '')}
            backgroundHover={true}
          />
        {/each}
      </nav>
      <Button
        class="flex items-center justify-center h-16 w-16"
        on:click={() => {
          logoutModalActive = true
          dispatch('showLogoutModal')
        }}
        iconButton={true}
        icon={'logout' + (logoutModalActive ? 'Solid' : '')}
        backgroundHover={true}
      />
    </div>
  </div>

  <!-- Main content + Footer -->
  <div class="flex-1 flex flex-col">
    <!-- Title Bar -->
    <div class="title-bar gap-1 mr-2">
      <Button on:click={minimize}>
        <Icon icon="minimize" highlightOnHover class="w-5 h-5" tabindex={-1} />
      </Button>
      <Button on:click={close}>
        <Icon icon="close" highlightOnHover class="w-5 h-5" tabindex={-1} />
      </Button>
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <slot />
    </div>

    <footer class="p-4 text-right text-xs">© 2023 Insightio. All rights reserved.</footer>
  </div>
</div>

<style>
  .non-draggable {
    -webkit-app-region: no-drag;
    user-select: none;
  }

  .app-icon {
    user-select: none; /* Make the app icon unselectable */
  }

  .app-name {
    user-select: none; /* Make the app name unselectable */
    text-align: center;
    font-size: 0.6rem;
    line-height: 1rem;
  }

  .title-bar {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 8px;
    gap: 0.75rem;
    -webkit-app-region: drag;
  }

  .main-content {
    flex-grow: 1;
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>
