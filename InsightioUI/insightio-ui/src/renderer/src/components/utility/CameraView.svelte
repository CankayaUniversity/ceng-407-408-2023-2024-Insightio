<script>
  import { createEventDispatcher, onMount, tick } from 'svelte'
  import Dropdown from './Dropdown.svelte'
  import { baseURL } from '../../api/tracker'
  import { getTargetCurrentCount, getTargetTotalCount } from '../../api/target'
  import Icon from './Icon.svelte'
  import clsx from 'clsx'

  export let width = '600px'
  export let height = '405px'
  export let cameraId = ''
  export let previewMode = false
  export let isIpCam = true
  export let deviceUrl = ''
  export let deviceIndex = 0
  export let targetOptions = []
  export let showFullscreenButton = false
  export let showBorders = false

  let ipCamElement
  let imgElement
  let videoElement
  let ready = false
  let dispatch = createEventDispatcher()

  let targets
  let target = {
    text: 'No Target',
    value: ''
  }
  let currentCount = 0
  let totalCount = 0

  async function waitRender() {
    await tick()
  }

  function handleLoad(e) {
    if (e.target == imgElement) {
      dispatch('feedloaded', {
        width: imgElement.naturalWidth,
        height: imgElement.naturalHeight
      })
    } else {
      let vElement = videoElement ? videoElement : ipCamElement
      dispatch('feedloaded', { width: vElement.videoWidth, height: vElement.videoHeight })
    }
  }

  async function fetchData() {
    if (target.value != '' && target.value != 'all_annotated_frame') {
      currentCount = await getTargetCurrentCount(cameraId, target.value)
      totalCount = await getTargetTotalCount(cameraId, target.value)
    }
  }

  function toggleFullScreen() {
    let element
    if (!previewMode) element = imgElement
    else if (isIpCam) element = ipCamElement
    else element = videoElement

    if (!document.fullscreenElement) {
      element.requestFullscreen().catch((err) => {
        console.error(`Error attempting to enable full-screen mode: ${err.message} (${err.name})`)
      })
    } else {
      document.exitFullscreen()
    }
  }

  // Update video source when cameraId or targetClass changes
  $: if (!previewMode && cameraId && imgElement) {
    if (target.value == 'all_annotated_frame') {
      imgElement.src = `${baseURL}/all_annotated_frame_${cameraId}`
    } else if (target.value != '') {
      let targetClassStr = `_${target.value}`
      imgElement.src = `${baseURL}/frame_${cameraId + targetClassStr}`
    } else {
      imgElement.src = `${baseURL}/raw_frame_${cameraId}`
    }
  }

  $: if (previewMode) {
    waitRender()
    if (isIpCam && deviceUrl) {
      ready = true
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
            dispatch('error', 'Media device not found')
          }
        })()
      } catch (error) {
        console.error('Error accessing media devices:', error)
        dispatch('error', 'Error accessing media device')
      }
    }
  }

  $: {
    if (targetOptions.length > 0) {
      targets = [
        {
          text: 'No Target',
          value: ''
        },
        ...targetOptions,
        {
          text: 'Fully Annotated',
          value: 'all_annotated_frame'
        }
      ]
    } else if (targetOptions.length == 0) {
      targets = [
        {
          text: 'No Target',
          value: ''
        },
        {
          text: 'Fully Annotated',
          value: 'all_annotated_frame'
        }
      ]
    }
  }

  onMount(() => {
    if (!previewMode) {
      setInterval(fetchData, 850)
    }
  })
</script>

<div
  class={clsx('camera-view', showBorders && 'border-2 rounded border-gray-700')}
  style={`--camera-view-width: ${width}; --camera-view-height: ${height};`}
>
  {#if !previewMode}
    <img bind:this={imgElement} class="w-full bg-black" alt="Camera Stream" on:load={handleLoad} />

    <Dropdown
      items={targets}
      bind:selectedItem={target}
      placeholder="Select target"
      class="mt-5 w-full"
      maxHeight="5"
      showSearch={true}
    />
    {#if target.value != 'all_annotated_frame'}
      <div class="text-white mt-4 text-sm">
        Current count for Target {target.text}: {currentCount}
      </div>
      <div class="text-white mt-2 text-sm">
        Total count for Target {target.text}: {totalCount}
      </div>
    {/if}
  {:else if isIpCam && ready}
    <video
      bind:this={ipCamElement}
      src={deviceUrl}
      class="w-full bg-black"
      autoplay
      muted
      on:loadedmetadata={handleLoad}
    />
  {:else}
    <video
      bind:this={videoElement}
      class="w-full bg-black"
      autoplay
      muted
      on:loadedmetadata={handleLoad}
    />
  {/if}
  {#if showFullscreenButton}
    <button
      class="absolute bottom-2 right-2 bg-gray-800 text-white rounded-full focus:outline-none focus:ring"
      on:click={toggleFullScreen}
    >
      <Icon icon="fullscreen" class="w-6 h-6" />
    </button>
  {/if}
</div>

<style>
  .camera-view {
    position: relative;
    width: var(--camera-view-width, 560px); /* default width */
    height: var(--camera-view-height, 405px); /* default height */
    overflow: visible; /* allow contents to overflow */
  }

  .camera-view img,
  .camera-view video {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }

  .camera-view button {
    position: absolute;
    bottom: 2px;
    right: 2px;
    background-color: rgba(0, 0, 0, 0.6);
    color: white;
    padding: 10px;
    border-radius: 50%;
    border: none;
  }
</style>
