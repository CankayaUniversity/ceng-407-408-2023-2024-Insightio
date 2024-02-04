<script>
  import clsx from 'clsx'
  import { v4 } from 'uuid'

  export let id = v4().substring(0, 8)
  export let value
  export let type = 'text'
  export let showLabel = false
  export let label = ''
  export let placeholder = ''

  function handleInput(e) {
    value = e.target.value
  }
</script>

{#if showLabel}
  <label class="block text-white text-sm font-bold mb-2" for={id}>{label}</label>
{/if}
<input
  class={clsx(
    'shadow appearance-none border rounded text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline',
    $$props.class
  )}
  {id}
  {type}
  {placeholder}
  {value}
  on:input={handleInput}
/>
