<script>
  import {
    createCameraSetting,
    getAllCameraSettings,
    updateCameraSetting,
    deleteCameraSetting
  } from '../../api/cameras'
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
  import { success, warn } from '../../functions/toastifyWrapper'
  import Spinner from '../utility/Spinner.svelte'

  export let isEditMode = false

  let typeOptions = [
    { text: 'Built-in', value: 'built-in' },
    { text: 'IP Camera', value: 'ip-camera' }
  ]
  let showLoading = true
  let selectedConfig // Used only for clearing the dropdown
  let cameraConfigurations = []
  let configurationDropdownOptions = []
  let selectedConfiguration = {}
  let cameraType = typeOptions[0].value
  let tagOptions = Object.values(targets.labels).map((label) => ({ value: label }))
  let selectedTags = []
  let selectedTargets = []
  let cameraOptions = []
  let selectedCamera
  let cameraName = ''
  let deviceIndex = 0
  let deviceUrl = ''
  let openZoneDraw = false
  let isIpCam = false
  let zones = {}
  let resolution = ''
  let newTags = []
  let newCameraConfiguration = {
    name: '',
    type: '',
    ipAddress: '127.0.0.1',
    deviceIndex: 0,
    status: 'CONNECTED',
    targets: {},
    resolution: '',
    createdDate: '',
    createdBy: '',
    metadata: []
  }

  function initFields() {
    selectedConfig = {}
    selectedConfiguration = {}
    cameraType = typeOptions[0].value
    tagOptions = Object.values(targets.labels).map((label) => ({ value: label }))
    selectedTags = []
    selectedTargets = []
    cameraOptions = []
    selectedCamera
    cameraName = ''
    deviceIndex = 0
    deviceUrl = ''
    openZoneDraw = false
    isIpCam = false
    zones = {}
    resolution = ''
    newCameraConfiguration = {
      name: '',
      type: '',
      ipAddress: '127.0.0.1',
      deviceIndex: 0,
      status: 'CONNECTED',
      targets: {},
      resolution: '',
      createdDate: '',
      createdBy: '',
      metadata: []
    }
  }

  function handleFeedLoaded(e) {
    if (!isEditMode) {
      resolution = `${e.detail.width}x${e.detail.height}`
    }
  }

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

  function validateConfigurationsObject(conf) {
    if (!cameraConfigurations || !conf) {
      console.error('No configuration list or configuration provided')
      return false
    }

    // Check name
    const sameNameConfig = cameraConfigurations.find((o) => conf.name === o.name)
    if (sameNameConfig) {
      warn('A configuration with the same name already exists.')
      return false
    }

    if (cameraType === 'built-in') {
      // Check device index
      const sameIndexConfig = cameraConfigurations.find((o) => conf.deviceIndex === o.deviceIndex)
      if (sameIndexConfig) {
        warn('A configuration with the same device already exists.')
        return false
      }
    } else {
      // Check URL
      const sameIpConfig = cameraConfigurations.find((o) => conf.ipAddress === o.ipAddress)
      if (sameIpConfig) {
        warn('A configuration with the same address already exists.')
        return false
      }
    }

    return true
  }

  async function fetchCameraSettings() {
    showLoading = true
    const res = await getAllCameraSettings()
    if (res) {
      cameraConfigurations = res
      configurationDropdownOptions = cameraConfigurations.map((conf) => ({
        text: conf.name,
        value: conf.id
      }))
      showLoading = false
    }
  }

  function handleOpenZoneDraw() {
    if (selectedTags.length == 0) {
      warn('Please select targets first.')
      return
    }
    openZoneDraw = true
  }

  function handleCamType() {
    isIpCam = cameraType == 'ip-camera' ? true : false
  }

  async function saveSettings() {
    if (isEditMode) {
      const res = await updateCameraSetting(selectedConfiguration)
      console.log(res)
      if (res.status == 200) {
        success('Configuration changed successfully!')
      }
    } else {
      const isCameraBuiltIn = cameraType === 'built-in'
      const d = new Date()

      newCameraConfiguration = {
        name: cameraName,
        type: isCameraBuiltIn ? 'CONNECTEDCAMERA' : 'IPCAMERA',
        ipAddress: isCameraBuiltIn ? '127.0.0.1' : deviceUrl,
        deviceIndex: isCameraBuiltIn ? deviceIndex : 0,
        status: 'CONNECTED',
        targets: zones,
        resolution: resolution,
        createdDate: d.toISOString(),
        createdBy: '1e2f50c5-7dea-46ef-9a86-f4910d5d989f',
        metadata: []
      }

      console.log(newCameraConfiguration)

      if (validateConfigurationsObject(newCameraConfiguration)) {
        const res = await createCameraSetting(newCameraConfiguration)
        console.log(res)
        if (res.status == 200) {
          success('Configuration created successfully!')
          initFields()
        }
      }
    }
  }

  async function deleteSettings() {
    if (!selectedConfiguration) {
      warn('No configuration selected.')
      return
    }

    const res = await deleteCameraSetting(selectedConfiguration.id)

    if (res.status == 200) {
      success('Camera configuration deleted successfully')
      initFields()
    }
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

  $: isEditMode !== null && fetchCameraSettings()

  $: selectedTargets = selectedTags.map((l) => {
    return {
      text: l,
      value: Object.keys(targets.labels).find((key) => targets.labels[key] === l)
    }
  })

  $: isSettingsDisabled = Object.keys(selectedConfiguration).length == 0

  $: if (isEditMode) {
    // Perform any additional logic needed when edit mode is active
    cameraName = selectedConfiguration?.name || ''
    deviceUrl = selectedConfiguration?.ipAddress || ''
    deviceIndex = selectedConfiguration?.deviceIndex || 0

    if (selectedConfiguration && selectedConfiguration.targets) {
      let configTargets = []
      for (let i of Object.keys(selectedConfiguration.targets)) {
        configTargets.push(targets.labels[i])
      }
      newTags = [...configTargets]
    }
  }

  $: tagifyDisabled = isEditMode && isSettingsDisabled
</script>

<ZoneDraw
  bind:isOpen={openZoneDraw}
  bind:targets={selectedTargets}
  on:save={(e) => (zones = e.detail)}
/>

{#if !showLoading}
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
        <CameraView
          previewMode
          bind:isIpCam
          bind:deviceUrl
          bind:deviceIndex
          bind:targetOptions={selectedTargets}
          on:feedloaded={handleFeedLoaded}
          on:error={(e) => warn(e.detail)}
        />
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
        {#if isEditMode}
          <div class="mt-4 mb-5">
            <Dropdown
              class="w-full py-1 px-3"
              bind:selectedItem={selectedConfig}
              items={configurationDropdownOptions}
              placeholder="Select Camera Configuration"
              on:change={(e) => {
                selectedConfiguration = cameraConfigurations.find(
                  (conf) => conf.id === e.detail.value
                )
              }}
            />
          </div>
        {/if}
        <fieldset disabled={isEditMode && isSettingsDisabled}>
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
          {#if !isEditMode}
            <div class="mb-4">
              <RadioGroup
                label="Camera Type:"
                bind:selectedValue={cameraType}
                options={typeOptions}
                on:change={handleCamType}
              />
            </div>
            <div class="mb-2">
              {#if cameraType == 'built-in' || (selectedConfiguration && selectedConfiguration.type === 'CONNECTEDCAMERA')}
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label class="block text-white text-sm font-bold mb-2">Select A Camera</label>

                <Dropdown
                  class="w-56"
                  bind:selectedItem={selectedCamera}
                  items={cameraOptions}
                  placeholder="Select camera"
                />
              {:else if cameraType == 'ip-camera' || (selectedConfiguration && selectedConfiguration.type === 'IPCAMERA')}
                <Input
                  class="w-full py-1 px-2"
                  showLabel
                  label="Camera URL:"
                  type="text"
                  id="camip"
                  placeholder="https://path/to/cam.mjpg"
                  bind:value={deviceUrl}
                />
              {/if}
            </div>
          {/if}
          <div>
            <TagInput
              {tagOptions}
              title="Select Targets"
              helpText="Minimum 1, Maximum 5. Please draw target zones after targets are selected."
              bind:tags={selectedTags}
              bind:newTags
              minTags={1}
              maxTags={5}
              bind:disabled={tagifyDisabled}
            />
          </div>
          <div class="flex justify-end mt-10 gap-4">
            {#if isEditMode}
              <Button class="py-2 px-4" on:click={deleteSettings} controlButton color="red"
                >Delete</Button
              >
            {/if}
            <Button class="py-2 px-4" on:click={saveSettings} controlButton>Save</Button>
          </div>
        </fieldset>
      </div>
    </div>
  </div>
{:else}
  <div class="flex flex-col h-full w-full justify-center items-center" style="min-height: 545px;">
    <Spinner />
  </div>
{/if}
