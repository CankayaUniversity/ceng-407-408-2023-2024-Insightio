<script>
  import clsx from 'clsx'
  import { createEventDispatcher } from 'svelte'
  import { v4 } from 'uuid'

  export let id = v4().substring(0, 8)
  export let value
  export let type = 'text'
  export let showLabel = false
  export let label = ''
  export let placeholder = ''
  export let pattern = ''
  export let errorMessage = 'Invalid format'

  const dispatch = createEventDispatcher()
  let showError = false

  function validate(value) {
    if (pattern && value) {
      const regex = new RegExp(pattern)
      showError = !regex.test(value)
      dispatch('validation', !showError)
    } else {
      showError = false
    }
  }

  function handleInput(e) {
    value = e.target.value
    validate(value)
  }
</script>

{#if showLabel}
  <label class="block text-white text-sm font-bold mb-1" for={id}>{label}</label>
{/if}
<input
  class={clsx(
    'shadow appearance-none border rounded text-gray-700 leading-tight focus:outline-none focus:shadow-outline',
    { 'border-red-500': showError },
    { 'mb-1': !showError },
    { 'mb-0': showError },
    $$props.class
  )}
  {id}
  {type}
  {placeholder}
  {value}
  on:input={handleInput}
/>
{#if showError}
  <p class="text-red-500 text-xs italic mt-1">{errorMessage}</p>
{/if}
