<script>
  import { createEventDispatcher } from 'svelte'
  import { fade } from 'svelte/transition'

  export let showModal = false
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
      class="bg-gray-800 p-4 rounded-lg shadow-lg max-w-md w-full m-4"
      transition:fade={{ duration: 300 }}
    >
      <slot />
    </div>
  </div>
{/if}
