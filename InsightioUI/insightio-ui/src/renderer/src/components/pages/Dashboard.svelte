<script>
  import Chart from '../utility/Chart.svelte'
  import CameraView from '../utility/CameraView.svelte'
  import Dropdown from '../utility/Dropdown.svelte'
  import Icon from '../utility/Icon.svelte'
  import clsx from 'clsx'
  import Button from '../utility/Button.svelte'
  import CountHelper from '../../functions/countData'
  import { getAllCameraSettings } from '../../api/cameras'
  import Spinner from '../utility/Spinner.svelte'
  import pageStore from '../../stores/pageStore'
  import targets from '../../data/trackerTargets'

  // TODO: Replace with actual data
  const mockData = [
    {
      target: 'Human',
      counts: [
        // Hours
        Array.from({ length: 24 }, () => Math.floor(Math.random() * 100)),
        // Days
        Array.from({ length: 7 }, () => Math.floor(Math.random() * 100)),
        // Weeks
        Array.from({ length: 4 }, () => Math.floor(Math.random() * 100)),
        // Months
        Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
      ]
    },
    {
      target: 'Car',
      counts: [
        // Hours
        Array.from({ length: 24 }, () => Math.floor(Math.random() * 100)),
        // Days
        Array.from({ length: 7 }, () => Math.floor(Math.random() * 100)),
        // Weeks
        Array.from({ length: 4 }, () => Math.floor(Math.random() * 100)),
        // Months
        Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
      ]
    },
    {
      target: 'Cat',
      counts: [
        // Hours
        Array.from({ length: 24 }, () => Math.floor(Math.random() * 100)),
        // Days
        Array.from({ length: 7 }, () => Math.floor(Math.random() * 100)),
        // Weeks
        Array.from({ length: 4 }, () => Math.floor(Math.random() * 100)),
        // Months
        Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
      ]
    },
    {
      target: 'Dog',
      counts: [
        // Hours
        Array.from({ length: 24 }, () => Math.floor(Math.random() * 100)),
        // Days
        Array.from({ length: 7 }, () => Math.floor(Math.random() * 100)),
        // Weeks
        Array.from({ length: 4 }, () => Math.floor(Math.random() * 100)),
        // Months
        Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
      ]
    },
    {
      target: 'Bicycle',
      counts: [
        // Hours
        Array.from({ length: 24 }, () => Math.floor(Math.random() * 100)),
        // Days
        Array.from({ length: 7 }, () => Math.floor(Math.random() * 100)),
        // Weeks
        Array.from({ length: 4 }, () => Math.floor(Math.random() * 100)),
        // Months
        Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
      ]
    }
  ]

  const mockTargets = mockData.map((o) => o.target)
  const chartTypes = ['bar', 'line', 'radar', 'pie', 'doughnut', 'polarArea']

  let selectedCameraSetting
  let cameraSettings = []
  let currentTimeFrame = 'day'
  let currentCameraIndex = 0
  let currentChartTypeIndex = 0
  let targetVisibilities = CountHelper.initTargetVisibilityArray(mockData)
  let currentChartData = CountHelper.getFramedCountData(mockData, currentTimeFrame)
  let currentChartType = chartTypes[currentChartTypeIndex]
  let maxCameraIndex
  let showLoading = false
  let targetOptions

  function updateTimeFrame(timeframe) {
    currentTimeFrame = timeframe
    targetVisibilities = CountHelper.initTimeVisibilityArray(currentTimeFrame)
    currentChartData = CountHelper.getFramedCountData(mockData, currentTimeFrame)
  }

  function navigateCamera(direction) {
    if (direction === 'next' && currentCameraIndex != maxCameraIndex) {
      currentCameraIndex += 1
    } else if (direction === 'prev' && currentCameraIndex != 0) {
      currentCameraIndex -= 1
    }
    selectedCameraSetting = cameraSettings[currentCameraIndex]
  }

  function navigateChart(direction) {
    if (direction === 'next') {
      currentChartTypeIndex = (currentChartTypeIndex + 1) % chartTypes.length
    } else {
      currentChartTypeIndex = (currentChartTypeIndex - 1 + chartTypes.length) % chartTypes.length
    }
    currentChartType = chartTypes[currentChartTypeIndex]
  }

  async function fetchSettings() {
    showLoading = true
    const res = await getAllCameraSettings()
    if (res) {
      cameraSettings = res
      let camerasExist = cameraSettings.length > 0 ? true : false
      maxCameraIndex = camerasExist ? cameraSettings.length - 1 : 0
      selectedCameraSetting = camerasExist ? cameraSettings[0] : null
      showLoading = false
    }
  }

  $: {
    if (selectedCameraSetting) {
      targetOptions = []
      Object.keys(selectedCameraSetting.targets).forEach((targetId) => {
        const option = {
          text: targets.labels[parseInt(targetId)],
          value: targetId
        }
        targetOptions = [...targetOptions, option]
      })
    }
  }

  $: $pageStore.activePage === 'dashboard' && fetchSettings()
</script>

{#if !showLoading}
  <div class="flex flex-col h-full w-full bg-gray-800 text-white">
    <!-- Dashboard Header -->
    <div class="px-4 pt-4 flex justify-between items-center">
      <h1 class="text-5xl font-bold">Dashboard</h1>
      <div></div>
    </div>

    {#if cameraSettings.length > 0}
      <!-- Camera Navigation -->
      <div class="flex justify-center py-2">
        <Button on:click={() => navigateCamera('prev')}>
          <Icon icon="arrowLeft" highlightOnHover class="h-5 w-5" />
        </Button>
        <div class="flex items-center justify-center">
          <!-- eslint-disable-next-line no-unused-vars -->
          {#each Array(maxCameraIndex + 1) as _, i (i)}
            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <span
              class="inline-block mx-1"
              on:click={() => {
                currentCameraIndex = i
                selectedCameraSetting = cameraSettings[i]
              }}
            >
              <Icon
                icon="circleFull"
                highlightOnHover
                class="h-4 w-4"
                style={clsx(currentCameraIndex === i && 'opacity: 1.0 !important;')}
              />
            </span>
          {/each}
        </div>
        <Button on:click={() => navigateCamera('next')}>
          <Icon icon="arrowRight" highlightOnHover class="h-5 w-5" />
        </Button>
      </div>
    {/if}

    <!-- Dashboard Content -->
    <div class="flex flex-grow">
      {#if cameraSettings.length > 0}
        <!-- Chart Section -->
        <div class="flex flex-col w-1/2 p-4 ml-8">
          <div class="flex flex-row pl-3">
            <Icon icon="chart" class="w-10 h-10" />
            <h2 class="text-3xl pl-3 font-bold">Charts</h2>
          </div>
          <div class="mt-6 flex items-center justify-between">
            <Button on:click={() => navigateChart('prev')}>
              <Icon icon="arrowLeft" highlightOnHover class="h-7 w-7" />
            </Button>
            <div class="flex-grow">
              <div class="flex flex-row">
                <p class="text-base mt-1 mr-3">Time Frame:</p>
                <Dropdown
                  items={CountHelper.validTimeFrames}
                  selectedItem={CountHelper.validTimeFrames[0]}
                  on:change={(e) => updateTimeFrame(e.detail.value)}
                />
              </div>
              <!-- Chart Component -->
              <Chart
                bind:currentChartData
                bind:chartType={currentChartType}
                bind:filters={targetVisibilities}
                targets={mockTargets}
              />
            </div>
            <Button on:click={() => navigateChart('next')}>
              <Icon icon="arrowRight" highlightOnHover class="h-7 w-7" />
            </Button>
          </div>
        </div>

        <!-- Camera View Section with fixed size -->
        <div class="w-1/2 flex justify-center p-4 mr-8">
          <div class="w-full max-w-lg">
            <div class="flex flex-row gap-3">
              <Icon icon="camera" class="w-10 h-10" />
              <h2 class="text-3xl mb-6 font-bold">Camera View</h2>
            </div>

            {#if selectedCameraSetting}
              <CameraView
                height="405px"
                width="515px"
                bind:cameraId={selectedCameraSetting.id}
                {targetOptions}
                showFullscreenButton
              />
            {/if}
          </div>
        </div>
      {:else}
        <div class="flex flex-grow items-center justify-center">
          <h1 class="text-3xl font-bold">No cameras to show</h1>
        </div>
      {/if}
    </div>
  </div>
{:else}
  <Spinner />
{/if}
