<script>
  import clsx from 'clsx'
  import Icon from './Icon.svelte'

  // For icon button
  export let iconButton = false
  export let backgroundHover = false
  export let icon = ''
</script>

{#if !iconButton}
  <button on:click on:hover class={$$props.class} on:click>
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
