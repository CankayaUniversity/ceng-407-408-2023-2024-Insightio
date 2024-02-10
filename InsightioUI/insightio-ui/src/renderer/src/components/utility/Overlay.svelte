<script>
  import { createEventDispatcher } from 'svelte'
  import { fade } from 'svelte/transition'
  import Button from './Button.svelte'
  import Icon from './Icon.svelte'

  export let showModal = false
  export let title
  const dispatch = createEventDispatcher()

  // Function to close the modal and dispatch an event
  function closeModal() {
    showModal = false
    dispatch('close')
  }

  // Close modal when clicking outside of it
  function handleClick(event) {
    if (event.target === event.currentTarget) {
      closeModal()
    }
  }
</script>

<!-- svelte-ignore a11y-no-static-element-interactions -->
{#if showModal}
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div
    on:click={handleClick}
    class="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center"
    transition:fade={{ duration: 300 }}
  >
    <div
      class="bg-gray-800 p-4 rounded-lg shadow-lg max-w-5xl max-h-max w-full m-4 relative"
      transition:fade={{ duration: 300 }}
    >
      <!-- Header with Title and Close Button -->
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl text-white">{title}</h2>
        <Button
          class="rounded-lg bg-transparent opacity-50 hover:opacity-100 hover:bg-red-500 text-sm text-white hover:text-white transition-colors duration-200 z-10"
          on:click={closeModal}
        >
          <Icon icon="close" highlightOnHover class="w-6 h-6" tabindex={-1} />
        </Button>
      </div>

      <slot />
    </div>
  </div>
{/if}
