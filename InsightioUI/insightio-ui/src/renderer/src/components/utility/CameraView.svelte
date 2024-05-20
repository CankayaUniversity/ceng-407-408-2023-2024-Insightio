<script>
  import { createEventDispatcher, onMount, tick } from 'svelte'
  import { getTargetCurrentCount, getTargetTotalCount } from '../../api/target'
  import { startStream, stopStream, videoServerUrl } from '../../api/ipc'
  import { warn } from '../../functions/toastifyWrapper'
  import Dropdown from './Dropdown.svelte'
  import { baseURL } from '../../api/tracker'
  import { test } from '../../functions/regex'
  import videojs from 'video.js'
  import 'video.js/dist/video-js.css'
  import Icon from './Icon.svelte'
  import clsx from 'clsx'
  import { v4 } from 'uuid'

  export let id = v4()
  export let width = '600px'
  export let height = '405px'
  export let cameraId = ''
  export let previewMode = false
  export let isIpCam = true
  export let deviceUrl = ''
  export let deviceIndex = -1
  export let targetOptions = []
  export let showFullscreenButton = false
  export let showBorders = false

  let ipCamElement
  let imgElement
  let videoElement
  let videoPlayer
  let ready = false
  let renderVideo = true
  let urlChanged = false
  let oldUrl = ''
  let dispatch = createEventDispatcher()
  let vidStreamId = ''

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

  async function initVideoPlayer() {
    videoPlayer = videojs(ipCamElement, {
      autoplay: true,
      muted: true,
      techOrder: ['html5']
    })
    videoPlayer.src({ src: `${videoServerUrl}${vidStreamId}`, type: 'video/mp4' })
  }

  function disposeVideoPlayer() {
    if (videoPlayer) {
      videoPlayer.dispose()
      stopStream(vidStreamId)
      videoPlayer = null
      vidStreamId = '' // Reset the stream ID
      ready = false
      renderVideo = false
    }
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

    dispatch('loadedmetadata')
  }

  async function fetchData() {
    if (target.value != '' && target.value != 'all_annotated_frame') {
      const currentCountData = await getTargetCurrentCount(cameraId, target.value)
      currentCount = currentCountData.data
      const totalCountData = await getTargetTotalCount(cameraId, target.value)
      totalCount = totalCountData.data
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

  async function setupVideoStream() {
    if (!ipCamElement || !isIpCam || !deviceUrl) return

    if (videoPlayer) {
      videoPlayer.dispose()
      videoPlayer = null
      renderVideo = false // Disable video rendering
      tick().then(() => {
        renderVideo = true // Re-enable rendering asynchronously
      })
      stopStream(vidStreamId, () => {
        startStream(deviceUrl, (msg, success) => {
          if (success) {
            vidStreamId = msg
            initVideoPlayer()
            ready = true
          } else {
            warn(msg)
          }
        })
      })
    } else {
      startStream(deviceUrl, (msg, success) => {
        if (success) {
          vidStreamId = msg
          initVideoPlayer()
          ready = true
        } else {
          warn(msg)
        }
      })
    }
  }

  function resetTarget() {
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
      target = targets[0]
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
      target = targets[0]
    }
    currentCount = 0
    totalCount = 0
    if (imgElement) {
      imgElement.src = `${baseURL}/raw_frame_${cameraId}`
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

  $: if (oldUrl != deviceUrl && test(deviceUrl, 'url')) {
    oldUrl = deviceUrl
    urlChanged = true
  } else {
    urlChanged = false
  }

  $: if (previewMode) {
    waitRender()
    if (!ready && ipCamElement && isIpCam && test(deviceUrl, 'url')) {
      setupVideoStream()
    } else if (urlChanged && ipCamElement && isIpCam && test(deviceUrl, 'url')) {
      setupVideoStream()
      urlChanged = false
    } else if (ready && isIpCam && !test(deviceUrl, 'url')) {
      disposeVideoPlayer()
    } else if (!isIpCam) {
      // Otherwise, access the camera by device index
      try {
        if (videoPlayer) {
          videoPlayer.dispose()
          videoPlayer = null
        }
        if (deviceIndex > -1) {
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
        }
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

  $: if (cameraId) {
    resetTarget()
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
    <img
      {id}
      bind:this={imgElement}
      class="w-full bg-black"
      alt="Camera Stream"
      on:load={handleLoad}
    />

    <Dropdown
      items={targets}
      bind:selectedItem={target}
      placeholder="Select target"
      class="mt-5 w-full"
      maxHeight="5"
      showSearch={true}
    />
    {#if target.value != 'all_annotated_frame' && target.value != ''}
      <div class="text-white mt-4 text-sm">
        Current count for Target {target.text}: {currentCount}
      </div>
      <div class="text-white mt-2 text-sm">
        Total count for Target {target.text}: {totalCount}
      </div>
    {/if}
  {:else if renderVideo && isIpCam}
    <video
      {id}
      bind:this={ipCamElement}
      class="video-js vjs-fluid w-full bg-black"
      autoplay
      muted
      on:resize
      on:loadedmetadata={handleLoad}
    />
  {:else}
    <video
      {id}
      bind:this={videoElement}
      class="w-full bg-black"
      autoplay
      muted
      on:resize
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

  .camera-view .video-js {
    width: 100% !important;
    height: 100% !important;
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
