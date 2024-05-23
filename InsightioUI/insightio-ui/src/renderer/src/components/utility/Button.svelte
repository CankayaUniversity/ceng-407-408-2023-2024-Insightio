<script>
  import clsx from 'clsx'
  import Icon from './Icon.svelte'

  // For icon button
  export let iconButton = false
  export let backgroundHover = false
  export let controlButton = false
  export let color = 'blue'
  export let icon = ''
  export let tabindex = 0
</script>

{#if !iconButton}
  <button
    type="button"
    on:click
    on:hover
    on:keyup
    class={clsx(
      controlButton && 'text-white font-bold rounded focus:outline-none focus:shadow-outline',
      controlButton && color == 'blue' && 'bg-blue-500 hover:bg-blue-700',
      controlButton && color == 'red' && 'bg-red-500 hover:bg-red-700',
      controlButton && color == 'yellow' && 'bg-yellow-500 hover:bg-yellow-700',
      $$props.class
    )}
    {tabindex}
  >
    <slot />
  </button>
{:else}
  <a
    href="javascript;"
    class={clsx($$props.class)}
    on:click|preventDefault
    style="-webkit-app-region: no-drag;"
  >
    <Icon {icon} {backgroundHover} />
  </a>
{/if}
