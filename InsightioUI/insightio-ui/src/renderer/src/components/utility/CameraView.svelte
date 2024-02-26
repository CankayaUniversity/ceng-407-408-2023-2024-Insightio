<script>
  import { onMount, tick } from 'svelte'
  import Dropdown from './Dropdown.svelte'
  import { baseURL } from '../../api/tracker'
  import { getTargetCurrentCount, getTargetTotalCount } from '../../api/target'
  import { ffServerURL, startRtspStream } from '../../api/ipc'

  export let cameraId = ''
  export let previewMode = false
  export let isIpCam = true
  export let deviceUrl = ''
  export let deviceIndex = 0

  let videoElement
  let ready = false
  let previousIpCamUrl = ''

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
    },
    {
      text: 'Fully Annotated',
      value: 'all_annotated_frame'
    }
  ]
  let target = targets[0]
  let currentCount = 0
  let totalCount = 0

  async function fetchData() {
    if (target.value != '' && target.value != 'all_annotated_frame') {
      currentCount = await getTargetCurrentCount(cameraId, target.value)
      console.log(currentCount)
      totalCount = await getTargetTotalCount(cameraId, target.value)
    }
  }

  window.api.receive('rtsp-feed-started', () => {
    ready = true
  })

  // Update video source when cameraId or targetClass changes
  $: if (!previewMode && cameraId && videoElement) {
    if (target.value == 'all_annotated_frame') {
      videoElement.src = `${baseURL}/all_annotated_frame_${cameraId}`
    } else if (target.value != '') {
      let targetClassStr = `_${target.value}`
      videoElement.src = `${baseURL}/frame_${cameraId + targetClassStr}`
    } else {
      videoElement.src = `${baseURL}/raw_frame_${cameraId}`
    }
  }

  $: if (previewMode) {
    ;(async () => {
      await tick()
    })()
    if (isIpCam && deviceUrl) {
      if (previousIpCamUrl != deviceUrl) {
        ready = false
        startRtspStream(deviceUrl)
        previousIpCamUrl = deviceUrl
      }
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

  onMount(() => {
    if (!previewMode) {
      setInterval(fetchData, 850)
    }
  })
</script>

<div class="camera-view w-full max-w-md mx-auto">
  {#if !previewMode}
    <img bind:this={videoElement} class="w-full bg-black" alt="Camera Stream" />

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
    <img src={ffServerURL} class="w-full bg-black" alt="RTSP" />
  {:else}
    <video bind:this={videoElement} class="w-full bg-black" autoplay muted></video>
  {/if}
</div>

<style>
  img,
  video {
    /* Fixed size */
    height: 405px;
    width: 620px;
  }
</style>
