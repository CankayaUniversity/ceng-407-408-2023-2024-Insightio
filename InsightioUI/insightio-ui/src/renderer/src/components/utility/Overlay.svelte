<script>
  import { createEventDispatcher } from 'svelte'
  import { fade } from 'svelte/transition'
  import Button from './Button.svelte'
  import Icon from './Icon.svelte'

  export let showModal = false
  export let title = ''
  export let fitContent = false
  const dispatch = createEventDispatcher()

  let modalContent

  function closeModal() {
    showModal = false
    dispatch('close')
  }

  function handleClick(event) {
    if (event.target === event.currentTarget) {
      closeModal()
    }
  }

  $: {
    if (fitContent && modalContent) {
      modalContent.style.width = `calc(${modalContent.scrollWidth}px + 100px)`
    }
  }
</script>

{#if showModal}
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <!-- svelte-ignore a11y-no-static-element-interactions -->
  <div
    on:click={handleClick}
    class="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center"
    transition:fade={{ duration: 300 }}
  >
    <div
      bind:this={modalContent}
      class="bg-gray-800 p-4 rounded-lg shadow-lg relative"
      class:fit-content={fitContent}
      transition:fade={{ duration: 300 }}
    >
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl text-white">{title}</h2>
        <Button
          class="rounded-lg bg-transparent opacity-50 hover:opacity-100 hover:bg-red-500 text-sm text-white hover:text-white transition-colors duration-200"
          on:click={closeModal}
        >
          <Icon icon="close" class="w-6 h-6" />
        </Button>
      </div>

      <slot />
    </div>
  </div>
{/if}

<style>
  .fit-content {
    max-width: 90%;
    max-height: 90%;
    overflow: auto;
  }
</style>
