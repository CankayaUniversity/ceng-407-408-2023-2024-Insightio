<script>
  import clsx from 'clsx'
  import { slide } from 'svelte/transition'
  import { createEventDispatcher } from 'svelte'

  export let items
  export let selectedItem = undefined
  export let value = undefined
  export let placeholder = 'Select...'

  let dispatch = createEventDispatcher()

  let searchTerm = ''
  let isOpen = false

  function selectItem(index) {
    if (selectedItem != items[index]) {
      dispatch('change', items[index])
    }
    selectedItem = items[index]
    value = selectedItem.value
    isOpen = false
  }

  function toggleDropdown() {
    isOpen = !isOpen
  }

  $: filteredItems = items.filter((item) =>
    item.text.toLowerCase().includes(searchTerm.toLowerCase())
  )
</script>

<div class="relative max-w-xs">
  <button
    on:click={toggleDropdown}
    class={clsx(
      'bg-gray-700 text-white py-2 px-4 rounded leading-tight focus:outline-none focus:ring focus:border-blue-300',
      $$props.class
    )}
  >
    <div class="flex justify-between items-center">
      <span>
        {#if selectedItem}
          {selectedItem.text}
        {:else}
          {placeholder}
        {/if}
      </span>
      <svg
        class={`fill-current h-4 w-4 transition-transform duration-200 transform ${
          isOpen ? 'rotate-180' : 'rotate-0'
        }`}
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 20 20"
      >
        <path d="M5.5 7L10 11.5L14.5 7H5.5Z" />
      </svg>
    </div>
  </button>
  {#if isOpen}
    <div
      transition:slide={{ duration: 200 }}
      class="absolute z-10 bg-slate-500 shadow-lg mt-1 rounded w-full"
    >
      <input
        type="text"
        class="w-full px-4 py-2 bg-gray-600 text-white focus:outline-none focus:ring focus:border-blue-300 rounded-t"
        placeholder="Type to search..."
        bind:value={searchTerm}
      />
      <ul class="max-h-60 overflow-auto text-white bg-gray-700 rounded-b">
        {#each filteredItems as item, index}
          <button
            on:click={() => selectItem(index)}
            class={clsx(
              'w-full text-left px-4 py-2 hover:bg-gray-600 cursor-pointer focus:outline-none',
              selectedItem && selectedItem == item && 'bg-blue-400'
            )}
          >
            {item.text}
          </button>
        {/each}
      </ul>
    </div>
  {/if}
</div>
