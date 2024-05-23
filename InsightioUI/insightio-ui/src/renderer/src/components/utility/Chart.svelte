<script>
  import { onMount } from 'svelte'
  import Chart from 'chart.js/auto'

  export let currentChartData
  export let chartType = 'bar' // Default chart type
  export let filters
  export let targets

  const colors = [
    `rgba(255, 99, 132, opacity)`, // Red
    `rgba(54, 162, 235, opacity)`, // Blue
    `rgba(255, 206, 86, opacity)`, // Yellow
    `rgba(75, 192, 192, opacity)`, // Green
    `rgba(153, 102, 255, opacity)` // Purple
    // Add more colors if needed
  ]

  let chartInstance
  let canvas
  let targetColors
  let prevChartType = chartType

  function getColor(target, opacity = 1.0) {
    if (Object.keys(targetColors).includes(target)) {
      return targetColors[target].replace('opacity', opacity)
    }
    return 'rgba(0, 0, 0, 0.1)'
  }

  function createChartData() {
    return {
      labels: currentChartData[0].labels,
      datasets: currentChartData.map((countData) => ({
        type: chartType,
        label: countData.target,
        data: countData.data,
        // Set hidden property based on filters object
        // eslint-disable-next-line no-prototype-builtins
        hidden: filters.hasOwnProperty(countData.target)
          ? !filters[countData.target]
          : countData.hidden,
        backgroundColor: getColor(countData.target, 0.5),
        borderColor: getColor(countData.target, 1.0),
        borderWidth: 1
      }))
    }
  }

  function getChartConfig() {
    let data = createChartData()

    return {
      type: chartType,
      data: data,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            ticks: { color: 'white' }
          },
          x: {
            ticks: { color: 'white' }
          }
        },
        plugins: {
          legend: {
            labels: {
              color: 'white',
              generateLabels: (chart) => {
                const original = Chart.defaults.plugins.legend.labels.generateLabels
                const labelsOriginal = original.call(this, chart)
                return labelsOriginal.map((label) => ({
                  ...label,
                  text: `${label.text}`
                }))
              }
            },
            onClick: (event, legendItem) => {
              const index = legendItem.datasetIndex
              const ci = chartInstance

              // Use the filters dictionary if it's provided
              if (filters && Object.keys(filters).includes(legendItem.text)) {
                // Directly toggle the visibility state in filters dictionary
                filters[legendItem.text] = !filters[legendItem.text]

                // Update dataset's hidden property based on the new visibility state
                ci.data.datasets[index].hidden = !filters[legendItem.text]
              } else {
                // Default toggle visibility behavior
                const meta = ci.getDatasetMeta(index)
                meta.hidden = meta.hidden === null ? !ci.data.datasets[index].hidden : null
              }

              ci.update()
            }
          },
          tooltip: {
            titleFontColor: 'white',
            bodyFontColor: 'white'
          }
        }
      }
    }
  }

  function updateChart() {
    if (!chartInstance || !currentChartData) return

    if (prevChartType != chartType) {
      chartInstance.destroy()
      chartInstance = new Chart(canvas.getContext('2d'), getChartConfig())
      prevChartType = chartType
    } else {
      const updatedChartData = createChartData()
      chartInstance.data = updatedChartData
      chartInstance.update()
    }
  }

  onMount(() => {
    targetColors = (() => {
      let a = {}
      colors.forEach((c, i) => {
        a[targets[i]] = colors[i]
      })
      return a
    })()
    chartInstance = new Chart(canvas.getContext('2d'), getChartConfig())
  })

  $: if (chartInstance && currentChartData && chartType) {
    targetColors = (() => {
      let a = {}
      colors.forEach((c, i) => {
        a[targets[i]] = colors[i]
      })
      return a
    })()
    updateChart()
  }
</script>

<div class="w-full" style="height: 480px; width: 535px;">
  <canvas bind:this={canvas}></canvas>
</div>
