<script>
  import clsx from 'clsx'
  import icons from '../../functions/icons'
  import { onMount } from 'svelte'

  export let icon
  export let backgroundHover = false
  export let highlightOnHover = false

  let size
  let xy
  let ready = false

  // Icons that require different viewport settings
  const specialIcons = [
    {
      icon: 'plus',
      size: 28.8,
      xy: -2.4
    },
    {
      icon: 'plusSolid',
      size: 28.8,
      xy: -2.4
    },
    {
      icon: 'arrowRight',
      size: 1024,
      xy: 0
    },
    {
      icon: 'arrowLeft',
      size: 1024,
      xy: 0
    }
  ]

  function getIconViewBox(icon) {
    let iconSetting = specialIcons.find((i) => i.icon == icon)

    if (iconSetting) {
      size = iconSetting.size
      xy = iconSetting.xy
    } else {
      size = 33.6
      xy = -4.8
    }
  }

  onMount(() => {
    getIconViewBox(icon)
    ready = true
  })
</script>

{#if ready}
  <svg
    width="30px"
    height="30px"
    viewBox={`${xy} ${xy} ${size} ${size}`}
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    transform="rotate(0)"
    class={clsx(highlightOnHover && 'highlight', $$props.class)}
    style={$$props.style}
  >
    {#if backgroundHover}
      <g class="bg-carrier-hover" stroke-width="0" transform="translate(0,0), scale(1)"
        ><rect
          x={`${xy}`}
          y={`${xy}`}
          width={`${size}`}
          height={`${size}`}
          rx="16.8"
          fill="#373d52"
          strokewidth="0"
        ></rect></g
      >
    {/if}
    <g stroke-linecap="round" stroke-linejoin="round" stroke="#CCCCCC" stroke-width="0.096"></g><g>
      {@html icons[icon]}
    </g></svg
  >
{/if}

<style>
  .highlight {
    opacity: 0.5;
    cursor: pointer;
    transition: opacity 0.3s ease;
    -webkit-app-region: no-drag;
  }

  .highlight:hover {
    opacity: 1;
  }

  .bg-carrier-hover {
    opacity: 0; /* Start fully transparent */
    transform: scale(0.95); /* Start slightly scaled down */
    transition:
      opacity 0.3s ease-in-out,
      transform 0.3s ease-in-out;
  }

  /* When the parent SVG is hovered, show the bg-carrier if bgCarrierClass is applied */
  svg:hover .bg-carrier-hover {
    display: block;
    opacity: 1;
    transform: scale(1);
  }
</style>
