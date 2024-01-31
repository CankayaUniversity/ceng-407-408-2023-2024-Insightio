<script>
  import clsx from 'clsx'
  import Icon from './Icon.svelte'

  // For icon button
  export let iconButton = false
  export let backgroundHover = false
  export let controlButton = false
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
      controlButton &&
        'bg-blue-500 hover:bg-blue-700 text-white font-bold rounded focus:outline-none focus:shadow-outline',
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
