<script>
  import Chart from '../utility/Chart.svelte'
  import CameraView from '../utility/CameraView.svelte'
  import Dropdown from '../utility/Dropdown.svelte'

  // TODO: Replace with actual data
  const mockData = {
    day: {
      labels: Array.from({ length: 24 }, (_, i) => `${i}:00`),
      datasets: [
        {
          target: 'Human',
          data: Array.from({ length: 24 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Cat',
          data: Array.from({ length: 24 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Car',
          data: Array.from({ length: 24 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Dog',
          data: Array.from({ length: 24 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Bicycle',
          data: Array.from({ length: 24 }, () => Math.floor(Math.random() * 100))
        }
      ]
    },
    week: {
      labels: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
      datasets: [
        {
          target: 'Human',
          data: Array.from({ length: 7 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Cat',
          data: Array.from({ length: 7 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Car',
          data: Array.from({ length: 7 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Dog',
          data: Array.from({ length: 7 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Bicycle',
          data: Array.from({ length: 7 }, () => Math.floor(Math.random() * 100))
        }
      ]
    },
    month: {
      labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
      datasets: [
        {
          target: 'Human',
          data: Array.from({ length: 4 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Cat',
          data: Array.from({ length: 4 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Car',
          data: Array.from({ length: 4 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Dog',
          data: Array.from({ length: 4 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Bicycle',
          data: Array.from({ length: 4 }, () => Math.floor(Math.random() * 100))
        }
      ]
    },
    year: {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
      datasets: [
        {
          target: 'Human',
          data: Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Cat',
          data: Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Car',
          data: Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Dog',
          data: Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
        },
        {
          target: 'Bicycle',
          data: Array.from({ length: 12 }, () => Math.floor(Math.random() * 100))
        }
      ]
    }
  }

  const validTimeFrames = [
    { text: 'Day', value: 'day' },
    { text: 'Week', value: 'week' },
    { text: 'Month', value: 'month' },
    { text: 'Year', value: 'year' }
  ]

  let currentTimeFrame = 'day'
  const targets = ['Human', 'Bicycle', 'Dog', 'Cat', 'Car']
  let targetVisibility = Object.fromEntries(targets.map((target) => [target, true]))
  let currentChartData = mockData[currentTimeFrame]
  const chartTypes = ['bar', 'line', 'pie', 'doughnut', 'radar', 'polarArea', 'bubble']
  let currentChartTypeIndex = 0
  let currentChartType = chartTypes[currentChartTypeIndex]
  let currentCameraIndex = 0
  const maxCameraIndex = 4

  function updateTimeFrame(timeframe) {
    currentTimeFrame = timeframe
    currentChartData = mockData[timeframe]
  }

  function navigateCamera(direction) {
    // not implemented
    console.log(direction)
  }

  function navigateChart(direction) {
    if (direction === 'next') {
      currentChartTypeIndex = (currentChartTypeIndex + 1) % chartTypes.length
    } else {
      currentChartTypeIndex = (currentChartTypeIndex - 1 + chartTypes.length) % chartTypes.length
    }
    currentChartType = chartTypes[currentChartTypeIndex]
  }

  function toggleTargetVisibility(target) {
    targetVisibility[target] = !targetVisibility[target]
  }

  $: currentChartData = {
    labels: mockData.day.labels, // Use appropriate labels
    datasets: mockData.day.datasets.map((dataset) => ({
      ...dataset,
      hidden: !targetVisibility[dataset.target] // Control visibility
    }))
  }
</script>

<div class="flex flex-col h-full w-full bg-gray-800 text-white">
  <!-- Dashboard Header -->
  <div class="px-4 pt-4 flex justify-between items-center">
    <h1 class="text-5xl font-bold">Dashboard</h1>
    <div></div>
  </div>

  <!-- Camera Navigation centered below Dashboard title -->
  <div class="flex justify-center py-2">
    <button class="mx-2" on:click={() => navigateCamera('prev')}>{'<'}</button>
    <div class="flex items-center justify-center">
      <!-- eslint-disable-next-line no-unused-vars -->
      {#each Array(maxCameraIndex + 1) as _, i (i)}
        <span
          class={`inline-block mx-1 ${currentCameraIndex === i ? 'text-white' : 'text-gray-400'}`}
          >●</span
        >
      {/each}
    </div>
    <button class="mx-2" on:click={() => navigateCamera('next')}>{'>'}</button>
  </div>

  <!-- Dashboard Content -->
  <div class="flex flex-grow">
    <!-- Chart Section -->
    <div class="flex flex-col w-1/2 p-4">
      <h2 class="text-3xl pl-3 font-bold">Charts</h2>
      <div class="mt-6 flex items-center justify-between">
        <button class="p-2" on:click={() => navigateChart('prev')}>{'<'}</button>
        <div class="flex-grow">
          <div class="flex flex-row">
            <p class="text-base mr-3">Time Frame:</p>
            <Dropdown
              items={validTimeFrames}
              selectedItem={validTimeFrames[0]}
              on:change={(e) => updateTimeFrame(e.detail.value)}
            />
          </div>
          <!-- Chart Component -->
          <Chart
            bind:currentChartData
            bind:chartType={currentChartType}
            onToggleVisibility={toggleTargetVisibility}
          />
        </div>
        <button class="p-2" on:click={() => navigateChart('next')}>{'>'}</button>
      </div>
    </div>

    <!-- Camera View Section with fixed size -->
    <div class="w-1/2 flex justify-center p-4">
      <div class="w-full max-w-lg">
        <h2 class="text-3xl mb-6 font-bold">Camera View</h2>

        <CameraView {currentCameraIndex} />
        <!-- Include other camera related content here -->
      </div>
    </div>
  </div>
</div>
