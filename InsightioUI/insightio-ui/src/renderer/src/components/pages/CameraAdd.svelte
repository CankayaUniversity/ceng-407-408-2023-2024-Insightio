<script>
  import CameraView from '../utility/CameraView.svelte'
  import Dropdown from '../utility/Dropdown.svelte'
  import Input from '../utility/Input.svelte'
  import RadioGroup from '../utility/RadioGroup.svelte'
  import Icon from '../utility/Icon.svelte'
  import TagInput from '../utility/TagInput.svelte'
  import targets from '../../data/trackerTargets'
  import { onMount } from 'svelte'
  import Button from '../utility/Button.svelte'
  import ZoneDraw from '../modals/ZoneDraw.svelte'
  import { warn } from '../../functions/toastifyWrapper'

  let typeOptions = [
    { text: 'Built-in', value: 'built-in' },
    { text: 'IP Camera', value: 'ip-camera' }
  ]
  let cameraType = typeOptions[0].value
  let tagOptions = Object.values(targets.labels).map((label) => ({ value: label }))
  let selectedTags = []
  let selectedTargets = []
  let cameraOptions = []
  let selectedCamera
  let cameraName = ''
  let deviceIndex = 0
  let deviceIp = ''
  let openZoneDraw = false

  function updateCameraOptions() {
    navigator.mediaDevices.enumerateDevices().then((devices) => {
      cameraOptions = devices
        .filter((device) => device.kind === 'videoinput')
        .map((device, index) => ({
          text: device.label || `Camera ${index + 1}`,
          value: index
        }))
    })
  }

  function handleOpenZoneDraw() {
    if (selectedTags.length == 0) {
      warn('Please select targets first.')
      return
    }
    openZoneDraw = true
  }

  function saveSettings() {
    console.log('pressed')
  }

  onMount(() => {
    updateCameraOptions()

    const intervalId = setInterval(updateCameraOptions, 10000)

    navigator.mediaDevices.addEventListener('devicechange', updateCameraOptions)

    return () => {
      clearInterval(intervalId)
      navigator.mediaDevices.removeEventListener('devicechange', updateCameraOptions)
    }
  })

  $: selectedTargets = selectedTags.map((l) => {
    return {
      text: l,
      value: Object.keys(targets.labels).find((key) => targets.labels[key] === l)
    }
  })
</script>

<ZoneDraw
  bind:isOpen={openZoneDraw}
  bind:targets={selectedTargets}
  on:save={(e) => console.log(e.detail)}
/>

<!-- Camera preview section -->
<div class="flex flex-col w-1/2 ml-10">
  <div class="flex flex-row pl-3 pt-3">
    <Icon icon="camera" class="w-10 h-10" />
    <h2 class="text-3xl ml-2 mb-4 font-bold">Camera View</h2>
  </div>
  <div class="mt-6 flex flex-col items-center justify-between">
    <div class="translate-x-36 -translate-y-6">
      <!-- Open zone draw modal button -->
      <Button on:click={handleOpenZoneDraw}>
        <span
          class="text-white text-base transition-opacity duration-300 opacity-50 hover:opacity-100 flex"
        >
          <p class="translate-y-1">Draw Zones</p>
          <Icon icon="edit" class="h-8 w-8" />
        </span>
      </Button>
    </div>
    <div class="-translate-y-6 -translate-x-6 border-2 rounded border-gray-700">
      <CameraView previewMode bind:deviceIp bind:deviceIndex />
    </div>
  </div>
</div>

<!-- Settings section -->
<div class="w-1/2 flex justify-center mr-10">
  <div class="w-full max-w-lg">
    <div class="flex flex-row pt-3 gap-3">
      <Icon icon="gearSolid" class="w-9 h-9" />
      <h2 class="text-3xl mr-1 mb-4 font-bold">Settings</h2>
    </div>
    <div class="flex flex-col ml-3 mt-3">
      <div class="mb-2">
        <Input
          class="w-full py-1 px-3"
          showLabel
          label="Name:"
          type="text"
          id="cameraName"
          placeholder="Camera Name"
          bind:value={cameraName}
        />
      </div>
      <div class="mb-4">
        <RadioGroup label="Camera Type:" bind:selectedValue={cameraType} options={typeOptions} />
      </div>
      <div class="mb-2">
        {#if cameraType == 'built-in'}
          <!-- svelte-ignore a11y-label-has-associated-control -->
          <label class="block text-white text-sm font-bold mb-2">Select A Camera</label>

          <Dropdown
            class="w-56"
            bind:selectedItem={selectedCamera}
            items={cameraOptions}
            placeholder="Select camera"
          />
        {:else}
          <Input
            class="w-full py-1 px-2"
            showLabel
            label="Camera IP:"
            type="text"
            id="camip"
            placeholder="127.0.0.1"
            bind:value={deviceIp}
          />
        {/if}
      </div>
      <div>
        <TagInput
          {tagOptions}
          title="Select Targets"
          helpText="Minimum 1, Maximum 5. Please draw target zones after targets are selected."
          bind:tags={selectedTags}
          minTags={1}
          maxTags={5}
        />
      </div>
      <div class="flex justify-end mt-10">
        <Button class="py-2 px-4" on:click={saveSettings} controlButton>Save</Button>
      </div>
    </div>
  </div>
</div>
