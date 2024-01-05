<script>
  import { onMount } from 'svelte'
  import Dropdown from './Dropdown.svelte'

  export let useFrames = false

  let target = {
    text: 'Human',
    value: 'human'
  }
  let targets = [
    target,
    {
      text: 'Car',
      value: 'car'
    }
  ]
  let currentCount = 1
  let totalCount = 32

  let videoElement

  onMount(() => {
    if (!videoElement) return

    if (useFrames) {
      // logic to render video frames
    } else {
      // Access webcam
      navigator.mediaDevices
        .getUserMedia({ video: true })
        .then((stream) => {
          videoElement.srcObject = stream
        })
        .catch(console.error)
    }
  })
</script>

<div class="camera-view w-full max-w-md mx-auto">
  <!-- Webcam view -->
  <video ref={videoElement} class="w-full h-auto bg-black" autoplay muted></video>
  <!-- Stylized Dropdown -->
  <Dropdown
    items={targets}
    bind:selectedItem={target}
    placeholder="Select target"
    class="mt-5 w-full"
  />
  <div class="text-white mt-4 text-sm">
    Current count for Target {target.text}: {currentCount}
  </div>
  <div class="text-white mt-2 text-sm">
    Lifetime count for Target {target.text}: {totalCount}
  </div>
</div>

<style>
  video {
    /* Fixed size */
    height: 330px;
  }
</style>
