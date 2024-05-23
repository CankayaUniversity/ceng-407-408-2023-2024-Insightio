<script>
  import { createEventDispatcher } from 'svelte'
  import clsx from 'clsx'

  export let checked = false
  export let labelOn = 'On'
  export let labelOff = 'Off'
  export let colorOn = 'bg-green-500'
  export let colorOff = 'bg-gray-500'

  const dispatch = createEventDispatcher()

  function toggle() {
    checked = !checked
    dispatch('change', checked)
  }
</script>

<div class="flex items-center justify-center mt-4 space-x-2">
  <span
    class={clsx('text-white transition-opacity duration-300', {
      'opacity-50': checked,
      'opacity-100': !checked
    })}
  >
    {labelOff}
  </span>
  <button
    class={clsx(
      'relative inline-flex items-center p-1 rounded-full cursor-pointer focus:outline-none switch-bg',
      checked ? colorOn : colorOff
    )}
    role="switch"
    aria-checked={checked}
    on:click={toggle}
  >
    <span
      class={clsx(
        'transform transition-transform duration-200 inline-block rounded-full bg-white shadow ring-0 switch-button',
        checked ? 'translate-x-full' : 'translate-x-0'
      )}
    />
  </button>
  <span
    class={clsx('text-white transition-opacity duration-300', {
      'opacity-50': !checked,
      'opacity-100': checked
    })}
  >
    {labelOn}
  </span>
</div>

<style>
  .switch-bg {
    width: 3rem;
    height: 1.5rem;
  }

  .switch-button {
    width: 1.25rem;
    height: 1.25rem;
  }
</style>
