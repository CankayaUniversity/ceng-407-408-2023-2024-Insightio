<script>
  import { onMount } from 'svelte'
  import Chart from 'chart.js/auto'

  export let currentChartData
  export let chartType = 'bar' // Default chart type
  export let onToggleVisibility

  let chartInstance
  let canvas

  onMount(() => {
    chartInstance = new Chart(canvas.getContext('2d'), getChartConfig())
  })

  function getColor(index, opacity = 1) {
    const colors = [
      `rgba(255, 99, 132, ${opacity})`, // Red
      `rgba(54, 162, 235, ${opacity})`, // Blue
      `rgba(255, 206, 86, ${opacity})`, // Yellow
      `rgba(75, 192, 192, ${opacity})`, // Green
      `rgba(153, 102, 255, ${opacity})` // Purple
      // Add more colors if needed
    ]
    return colors[index % colors.length]
  }

  function getChartConfig() {
    return {
      type: chartType,
      data: {
        labels: currentChartData.labels,
        datasets: currentChartData.datasets.map((dataset, index) => ({
          label: dataset.target,
          data: dataset.data,
          backgroundColor: getColor(index, 0.5), // Adjust opacity as needed
          borderColor: getColor(index, 1),
          borderWidth: 1
        }))
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        legend: {
          onClick: (e, legendItem) => {
            const index = legendItem.datasetIndex
            const ci = chartInstance
            const meta = ci.getDatasetMeta(index)

            // Toggle visibility
            meta.hidden = meta.hidden === null ? !ci.data.datasets[index].hidden : null

            // Update chart
            ci.update()
          }
        },
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
              const target = legendItem.text
              onToggleVisibility(target) // Call the passed-in function to toggle visibility
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

    // Update labels
    chartInstance.data.labels = currentChartData.labels

    // Update datasets
    chartInstance.data.datasets = currentChartData.datasets.map((dataset, index) => ({
      ...dataset, // Spread existing dataset properties
      label: dataset.target, // Assign label from the dataset's target
      data: dataset.data, // Assign data
      backgroundColor: getColor(index, 0.5), // Set background color
      borderColor: getColor(index, 1), // Set border color
      type: chartType // Set the chart type
    }))

    chartInstance.update()
  }

  $: if (chartInstance && currentChartData && chartType) {
    updateChart()
  }
</script>

<div class="w-full h-96">
  <canvas bind:this={canvas}></canvas>
</div>
