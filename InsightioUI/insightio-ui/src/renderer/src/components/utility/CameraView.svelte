<script>
  import { onMount } from 'svelte'
  import Dropdown from './Dropdown.svelte'
  import { baseURL } from '../../api/tracker'
  import { getTargetCurrentCount, getTargetTotalCount } from '../../api/target'

  export let cameraId = ''
  export let previewMode = false
  export let deviceIp = ''
  export let deviceIndex = 0

  let videoElement
  let noTargetSelected = false

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
    if (!previewMode) {
      setInterval(fetchData, 850)
    }
  })

  // Update video source when cameraId or targetClass changes
  $: if (!previewMode && cameraId && videoElement) {
    if (target.value != '') {
      noTargetSelected = false
      let targetClassStr = `_${target.value}`
      videoElement.src = `${baseURL}/frame_${cameraId + targetClassStr}`
    } else {
      noTargetSelected = true
    }
  }

  $: if (previewMode || noTargetSelected) {
    if (deviceIp) {
      // If an IP is provided, use it as the video source URL
      videoElement.src = deviceIp
    } else {
      // Otherwise, access the camera by device index
      try {
        ;(async () => {
          const devices = await navigator.mediaDevices.enumerateDevices()
          const videoDevices = devices.filter((device) => device.kind === 'videoinput')

          if (videoDevices.length > deviceIndex) {
            const stream = await navigator.mediaDevices.getUserMedia({
              video: { deviceId: { exact: videoDevices[deviceIndex].deviceId } }
            })
            videoElement.srcObject = stream
          } else {
            console.error('Device index out of range')
          }
        })()
      } catch (error) {
        console.error('Error accessing media devices:', error)
      }
    }
  }
</script>

<div class="camera-view w-full max-w-md mx-auto">
  {#if !previewMode && !noTargetSelected}
    <img bind:this={videoElement} class="w-full h-auto bg-black" alt="Camera Stream" />

    <Dropdown
      items={targets}
      bind:selectedItem={target}
      placeholder="Select target"
      class="mt-5 w-full"
      maxHeight="5"
      showSearch={true}
    />
    <div class="text-white mt-4 text-sm">
      Current count for Target {target.text}: {currentCount}
    </div>
    <div class="text-white mt-2 text-sm">
      Total count for Target {target.text}: {totalCount}
    </div>
  {:else}
    <video bind:this={videoElement} class="w-full h-auto bg-black" autoplay muted></video>

    {#if !previewMode && noTargetSelected}
      <Dropdown
        items={targets}
        bind:selectedItem={target}
        placeholder="Select target"
        class="mt-5 w-full"
        maxHeight="5"
        showSearch={true}
      />
    {/if}
  {/if}
</div>

<style>
  img {
    /* Fixed size */
    height: 330px;
  }
</style>
