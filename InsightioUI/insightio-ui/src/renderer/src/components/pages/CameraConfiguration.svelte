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
  import SwitchButton from '../utility/SwitchButton.svelte'

  let typeOptions = [
    { text: 'Built-in', value: 'built-in' },
    { text: 'IP Camera', value: 'ip-camera' }
  ]
  let cameraType = typeOptions[0].value
  let tagOptions = Object.values(targets.labels).map((label) => ({ value: label }))
  let selectedTags = []
  let cameraOptions = []
  let selectedCamera
  let cameraName = ''
  let deviceIndex = 0
  let deviceIp = ''
  let currentMode = 'add'

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
</script>

<div class="flex flex-col h-full w-full bg-gray-800 text-white">
  <!-- Camera Configuration Header -->
  <div class="px-4 pt-4 flex justify-between items-center">
    <h1 class="text-5xl font-bold">Camera Configuration</h1>
    <div></div>
  </div>

  <div class="flex justify-end px-6">
    <SwitchButton
      labelOff="Add"
      labelOn="Edit"
      colorOff="bg-green-500"
      colorOn="bg-blue-500"
      on:change={(e) => (currentMode = e.detail ? 'edit' : 'add')}
    />
  </div>

  <div class="p-4 mt-2">
    <div class="flex flex-grow border-2 border-gray-700 p-3">
      <!-- Camera preview section -->
      <div class="flex flex-col w-1/2">
        <div class="flex flex-row pl-3">
          <Icon icon="camera" class="w-10 h-10" />
          <h2 class="text-3xl ml-2 mb-6 font-bold">Camera View</h2>
        </div>
        <div class="mt-6 flex items-center justify-between">
          <CameraView previewMode bind:deviceIp bind:deviceIndex />
        </div>
      </div>

      <!-- Settings section -->
      <div class="w-1/2 flex justify-center">
        <div class="w-full max-w-lg">
          <div class="flex flex-row gap-3">
            <Icon icon="gearSolid" class="w-9 h-9" />
            <h2 class="text-3xl mr-1 mb-6 font-bold">Settings</h2>
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
              <RadioGroup
                label="Camera Type:"
                bind:selectedValue={cameraType}
                options={typeOptions}
              />
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
                helpText="Minimum 1, Maximum 5"
                bind:tags={selectedTags}
                minTags={1}
                maxTags={5}
              />
            </div>
            <div class="flex justify-end mt-2">
              <Button class="py-2 px-4" on:click={saveSettings} controlButton>Save</Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
