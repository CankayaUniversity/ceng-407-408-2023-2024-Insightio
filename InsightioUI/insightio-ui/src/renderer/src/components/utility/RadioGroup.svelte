<script>
  import { createEventDispatcher } from 'svelte'

  export let label = ''
  export let selectedValue
  export let options = []

  const dispatch = createEventDispatcher()

  function handleChange(event) {
    selectedValue = event.target.value
    dispatch('change', selectedValue)
  }
</script>

<div>
  {#if label}
    <!-- svelte-ignore a11y-label-has-associated-control -->
    <label class="block text-white text-sm font-bold mb-2">{label}</label>
  {/if}
  <div class="flex flex-col ml-4">
    {#each options as option}
      <label class="inline-flex items-center">
        <input
          type="radio"
          class="form-radio"
          name={label}
          value={option.value}
          checked={selectedValue === option.value}
          on:change={handleChange}
        />
        <span class="ml-2">{option.text}</span>
      </label>
    {/each}
  </div>
</div>

<style>
  .form-radio {
    color: theme('colors.blue.500');
  }
</style>
