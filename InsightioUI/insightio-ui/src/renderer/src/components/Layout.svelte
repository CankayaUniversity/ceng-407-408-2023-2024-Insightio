<script>
  import Icon from './utility/Icon.svelte'
  import pageStore from '../stores/pageStore'
  import { createEventDispatcher } from 'svelte'
  import appIcon from '../assets/icon.png?asset'

  export let activePage

  let dispatch = createEventDispatcher()
</script>

<div class="flex h-screen text-white bg-gray-800">
  <!-- Sidebar -->
  <div class="flex flex-col w-16 bg-gray-900 rounded-r-xl">
    <div class="flex items-center justify-center h-16">
      <!-- App icon (not a button) -->
      <img src={appIcon} alt="App Icon" class="h-8 w-8" />
    </div>
    <div class="flex flex-col justify-between flex-1">
      <nav>
        {#each $pageStore.pages as page}
          <a
            href="javascript;"
            class="flex items-center justify-center h-16 w-16"
            on:click|preventDefault={dispatch('pageChange', `${page.key}`)}
          >
            <Icon
              icon={page.icon + (activePage == page.key ? 'Solid' : '')}
              backgroundHover={true}
            />
            <!-- Gear Icon -->
          </a>
        {/each}
      </nav>
      <a
        href="javascript;"
        class="flex items-center justify-center h-16 w-16 mt-auto"
        on:click|preventDefault={dispatch('logout')}
      >
        <Icon icon="logout" backgroundHover={true} />
      </a>
    </div>
  </div>

  <!-- Main content + Footer -->
  <div class="flex-1 flex flex-col">
    <main class="flex-1 flex items-center justify-center p-4">
      <div class="flex flex-col items-center justify-center">
        <slot />
      </div>
    </main>
    <footer class="p-4 text-right text-xs">© 2023 Insightio. All rights reserved.</footer>
  </div>
</div>
