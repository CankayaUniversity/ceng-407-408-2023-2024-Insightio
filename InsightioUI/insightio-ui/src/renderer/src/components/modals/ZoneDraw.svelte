<script>
  import Overlay from '../utility/Overlay.svelte'
  import Dropdown from '../utility//Dropdown.svelte'
  import SwitchButton from '../utility/SwitchButton.svelte'

  // Dummy targets for demonstration
  export let isOpen
  export let targets

  let cameraFeedElement, drawingCanvas, ctx
  let drawing = false
  let drawType = 'line'
  let startPoint = null
  let drawings = {}
  let selectedTarget
  let videoLoaded = false
  let canvasLoaded = false
  let deviceIndex = 0
  let disableInputs = false

  function startDrawing(event) {
    if (event.button !== 0) return // Only start on left click
    drawing = true
    startPoint = { x: event.offsetX, y: event.offsetY }
  }

  function draw(event) {
    if (!drawing) return
    redrawCanvas() // Redraw existing shapes before drawing the new one
    const currentPoint = { x: event.offsetX, y: event.offsetY }
    if (drawType === 'line') {
      drawLine(startPoint, currentPoint)
    } else if (drawType === 'rectangle') {
      drawRectangle(startPoint, currentPoint)
    }
  }

  function endDrawing(event) {
    if (!drawing) return
    drawing = false
    const endPoint = { x: event.offsetX, y: event.offsetY }
    saveDrawing(startPoint, endPoint)
    startPoint = null
  }

  function saveDrawing(start, end) {
    const targetId = selectedTarget.value
    if (!drawings[targetId]) {
      drawings[targetId] = []
    }
    drawings[targetId].push({ type: drawType, start, end })
    drawings = { ...drawings } // Reassign to trigger reactivity
    redrawCanvas() // Explicitly call to redraw after saving
  }

  function drawLine(start, end) {
    ctx.beginPath()
    ctx.moveTo(start.x, start.y)
    ctx.lineTo(end.x, end.y)
    ctx.stroke()
  }

  function drawRectangle(start, end) {
    ctx.beginPath()
    ctx.rect(start.x, start.y, end.x - start.x, end.y - start.y)
    ctx.stroke()
  }

  function cancelDrawing(event) {
    if (event.type === 'contextmenu' || (event.type === 'keydown' && event.key === 'Escape')) {
      event.preventDefault()
      drawing = false
      startPoint = null
      ctx.clearRect(0, 0, drawingCanvas.width, drawingCanvas.height) // Clear canvas
    }
  }

  function clearCanvas() {
    if (!drawingCanvas) return
    ctx.clearRect(0, 0, drawingCanvas.width, drawingCanvas.height)
  }

  function redrawCanvas() {
    clearCanvas() // Clears the canvas before redrawing
    const targetDrawings = drawings[selectedTarget.value] || []
    targetDrawings.forEach((drawing) => {
      if (drawing.type === 'line') {
        drawLine(drawing.start, drawing.end)
      } else if (drawing.type === 'rectangle') {
        drawRectangle(drawing.start, drawing.end)
      }
    })
  }

  function initCanvas() {
    drawingCanvas.width = cameraFeedElement.clientWidth
    drawingCanvas.height = cameraFeedElement.clientHeight
    drawingCanvas.style.width = `${cameraFeedElement.clientWidth}px`
    drawingCanvas.style.height = `${cameraFeedElement.clientHeight}px`
    ctx = drawingCanvas.getContext('2d')
    ctx.lineWidth = 2
    redrawCanvas()
    canvasLoaded = true
  }

  $: {
    if (!targets || targets.length == 0 || targets[0].value == '') {
      disableInputs = true
      targets = [
        {
          text: 'No Targets',
          value: ''
        }
      ]
      selectedTarget = targets[0]
    } else if (!selectedTarget || !targets.find((t) => t.value == selectedTarget.value)) {
      disableInputs = false
      selectedTarget = targets[0]
    } else {
      disableInputs = false
    }
  }

  $: if (isOpen && !videoLoaded && cameraFeedElement) {
    navigator.mediaDevices
      .enumerateDevices()
      .then((devices) => {
        const videoDevices = devices.filter((device) => device.kind === 'videoinput')
        if (videoDevices.length > deviceIndex) {
          return navigator.mediaDevices.getUserMedia({
            video: { deviceId: videoDevices[deviceIndex].deviceId }
          })
        } else {
          console.error('No video devices found or invalid device index.')
        }
      })
      .then((stream) => {
        cameraFeedElement.srcObject = stream
        videoLoaded = true
      })
      .catch((error) => console.error('Error accessing camera feed:', error))
  }

  // Updated to dynamically set canvas size based on video dimensions
  $: if (isOpen && videoLoaded && cameraFeedElement && drawingCanvas) {
    canvasLoaded = false
    initCanvas()
  }

  $: if (!isOpen) {
    videoLoaded = false
  }

  $: if (canvasLoaded && selectedTarget) {
    redrawCanvas()
  }

  $: console.log(selectedTarget)
</script>

<svelte:window on:keydown={cancelDrawing} />

<Overlay bind:showModal={isOpen} on:close={() => (isOpen = false)}>
  <div class="relative w-full h-full">
    <!-- svelte-ignore a11y-media-has-caption -->
    <video bind:this={cameraFeedElement} class="w-full h-full" autoplay on:resize={initCanvas}
    ></video>
    <canvas
      bind:this={drawingCanvas}
      class="absolute top-0 left-0 w-full h-full"
      on:mousedown={startDrawing}
      on:mousemove={draw}
      on:mouseup={endDrawing}
      on:contextmenu={cancelDrawing}
    ></canvas>
  </div>
  <div class="flex items-center justify-between space-x-4 p-4 bg-gray-800 text-white">
    <div class="flex items-center space-x-4">
      <Dropdown
        bind:disabled={disableInputs}
        bind:selectedItem={selectedTarget}
        items={targets}
        placeholder="Select Target"
        on:change={() => redrawCanvas()}
      />
    </div>
    <div class="flex items-center space-x-4 -translate-y-2">
      <SwitchButton
        labelOff="Line"
        labelOn="Zone"
        colorOn="bg-blue-500"
        colorOff="bg-green-500"
        on:change={(e) => (drawType = e.detail ? 'rectangle' : 'line')}
      />
    </div>
  </div>
</Overlay>

<style>
  canvas {
    cursor: crosshair;
  }
</style>
