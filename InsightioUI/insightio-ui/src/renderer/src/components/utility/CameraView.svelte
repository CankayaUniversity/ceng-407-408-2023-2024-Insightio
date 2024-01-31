<script>
  import { onMount } from 'svelte'
  import Dropdown from './Dropdown.svelte'
  import { baseURL } from '../../api/tracker'
  import { getTargetCurrentCount, getTargetTotalCount } from '../../api/target'

  export let cameraId = 'e7a1174c-42b8-48d1-a359-99ed6f95848b'

  let videoElement
  let videoSource

  let targets = [
    {
      text: 'No Target',
      value: ''
    },
    {
      text: 'Banana',
      value: 21
    },
    {
      text: 'Bicycle',
      value: 42
    }
  ]
  let target = targets[0]
  let currentCount = 0
  let totalCount = 0

  async function fetchData() {
    if (target.value != '') {
      currentCount = await getTargetCurrentCount(cameraId, target.value)
      totalCount = await getTargetTotalCount(cameraId, target.value)
    }
  }

  onMount(() => {
    // Update video source dynamically
    videoElement.src = videoSource

    setInterval(fetchData, 850)
  })

  // Update video source when cameraId or targetClass changes
  $: if (cameraId && videoElement) {
    let frameKey = target.value !== '' ? 'frame' : 'raw_frame'
    let targetClassStr = target.value !== '' ? `_${target.value}` : ''
    videoElement.src = `${baseURL}/${frameKey}_${cameraId + targetClassStr}`
  }
</script>

<div class="camera-view w-full max-w-md mx-auto">
  <!-- Webcam view -->
  <img bind:this={videoElement} class="w-full h-auto bg-black" alt="Camera Stream" />
  <!-- Stylized Dropdown -->
  <Dropdown
    items={targets}
    bind:selectedItem={target}
    placeholder="Select target"
    class="mt-5 w-full"
    maxHeight="5"
    showSearch={true}
  />
  {#if target.value != ''}
    <div class="text-white mt-4 text-sm">
      Current count for Target {target.text}: {currentCount}
    </div>
    <div class="text-white mt-2 text-sm">
      Total count for Target {target.text}: {totalCount}
    </div>
  {/if}
</div>

<style>
  img {
    /* Fixed size */
    height: 330px;
  }
</style>
