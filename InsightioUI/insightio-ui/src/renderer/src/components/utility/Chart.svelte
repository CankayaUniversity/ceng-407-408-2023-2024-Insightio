<script>
  import { onMount } from 'svelte'
  import Chart from 'chart.js/auto'

  export let currentChartData
  export let chartType = 'bar' // Default chart type
  export let visibilityCallback
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
        hidden: countData.hidden,
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
            ticks: { color: 'white' } // X-axis labels color
          }
        },
        plugins: {
          legend: {
            labels: {
              color: 'white' // Legend labels color
            },
            onClick: (event, legendItem) => {
              const index = legendItem.datasetIndex
              const ci = chartInstance
              const meta = ci.getDatasetMeta(index)
              // Toggle visibility
              if (ci.data.datasets[index]) {
                meta.hidden = meta.hidden === null ? !ci.data.datasets[index].hidden : null
              } else {
                let dataIndex = currentChartData[0].labels.indexOf(legendItem.text)
                let isVisible = visibilityCallback(legendItem.text)
                if (isVisible) {
                  ci.data.datasets.forEach((d, i) => {
                    ci.hide(i, dataIndex)
                  })
                  legendItem.hidden = true
                } else {
                  ci.data.datasets.forEach((d, i) => {
                    ci.show(i, dataIndex)
                  })
                  legendItem.hidden = false
                }
              }

              ci.update()
            }
          },
          tooltip: {
            titleFontColor: 'white', // Tooltip title color
            bodyFontColor: 'white' // Tooltip body color
          }
        }
      }
    }
  }

  function updateChart() {
    // Ensure chartInstance and currentChartData are correctly referenced
    if (!chartInstance || !currentChartData) return

    if (prevChartType != chartType) {
      chartInstance.destroy()
      chartInstance = new Chart(canvas.getContext('2d'), getChartConfig())
      prevChartType = chartType
    } else {
      chartInstance.data = createChartData()
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
    updateChart()
  }
</script>

<div class="w-full" style="height: 410px; width: 450px;">
  <canvas bind:this={canvas}></canvas>
</div>
