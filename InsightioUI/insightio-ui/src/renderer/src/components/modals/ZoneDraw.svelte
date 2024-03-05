<script>
  import Overlay from '../utility/Overlay.svelte'
  import Dropdown from '../utility//Dropdown.svelte'
  import SwitchButton from '../utility/SwitchButton.svelte'
  import Button from '../utility/Button.svelte'
  import Icon from '../utility/Icon.svelte'
  import { createEventDispatcher } from 'svelte'

  export let isOpen
  export let targets

  let ctx
  let cameraFeedElement
  let drawingCanvas
  let drawing = false
  let drawType = 'line'
  let startPoint = null
  let drawings = {}
  let selectedTarget
  let videoLoaded = false
  let canvasLoaded = false
  let deviceIndex = 0
  let disableInputs = false
  let targetZones = {}
  let actualVideoWidth
  let actualVideoHeight
  let scaleX
  let scaleY
  let history = [{}] // Stack for undo
  let redoStack = [] // Stack for redo
  let dispatch = createEventDispatcher()

  function undo() {
    if (history.length > 0) {
      const currentState = JSON.parse(JSON.stringify(drawings))
      redoStack.push(currentState) // Save current state for redo
      const lastState = history.pop()
      drawings = lastState // Revert to previous state
      redrawCanvas()
    }
  }

  function redo() {
    if (redoStack.length > 0) {
      const nextState = redoStack.pop()
      history.push(JSON.parse(JSON.stringify(drawings))) // Save current state for undo
      drawings = nextState
      redrawCanvas()
    }
  }

  function deleteShape(targetId, index) {
    history.push(JSON.parse(JSON.stringify(drawings))) // Save current state for undo
    drawings[targetId].splice(index, 1)
    drawings = { ...drawings } // Trigger reactivity
    redrawCanvas()
    redoStack = [] // Clear redo stack on new action
  }

  function highlightShape(targetId, index, highlight) {
    const shape = drawings[targetId][index]
    if (shape) {
      shape.highlight = highlight
      redrawCanvas()
    }
  }

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
    const shapeType = drawType.charAt(0).toUpperCase() + drawType.slice(1) // "Line" or "Rectangle"
    const nextNumber = drawings[targetId].filter((d) => d.type === drawType).length + 1
    const shapeName = `${shapeType} ${nextNumber}` // "Line 1", "Rectangle 2", etc.

    drawings[targetId].push({ type: drawType, start, end, name: shapeName, editable: false })
    drawings = { ...drawings } // Reassign to trigger reactivity
    redrawCanvas()
    history.push(JSON.parse(JSON.stringify(drawings))) // Save new state for undo
    redoStack = [] // Clear redo stack on new action
  }

  function drawLine(start, end, highlight = false) {
    ctx.strokeStyle = highlight ? 'purple' : 'black'
    ctx.beginPath()
    ctx.moveTo(start.x, start.y)
    ctx.lineTo(end.x, end.y)
    ctx.stroke()
  }

  function drawRectangle(start, end, highlight = false) {
    ctx.strokeStyle = highlight ? 'purple' : 'black'
    ctx.beginPath()
    ctx.rect(start.x, start.y, end.x - start.x, end.y - start.y)
    ctx.stroke()
  }

  function handleKeydown(event) {
    if (event.ctrlKey && event.key === 'z') undo()
    if (event.ctrlKey && event.key === 'y') redo()
    if (event.type === 'contextmenu' || (event.type === 'keydown' && event.key === 'Escape')) {
      event.preventDefault()
      drawing = false
      startPoint = null
      ctx.clearRect(0, 0, drawingCanvas.width, drawingCanvas.height) // Clear canvas
    }
  }

  function mapDrawingsToTargetZones() {
    if (disableInputs) return

    targetZones = {}
    targets.forEach((target) => {
      const targetId = target.value
      if (drawings[targetId]) {
        targetZones[targetId] = drawings[targetId].map((drawing) => {
          const adjustedStart = {
            x: drawing.start.x * scaleX,
            y: drawing.start.y * scaleY
          }

          const adjustedEnd = {
            x: drawing.end.x * scaleX,
            y: drawing.end.y * scaleY
          }
          return {
            zoneName: drawing.name,
            zoneType: drawing.type === 'line' ? 0 : 1,
            startPoint: adjustedStart,
            endPoint: adjustedEnd
          }
        })
      }
    })
  }

  function clearCanvas() {
    if (!drawingCanvas) return
    ctx.clearRect(0, 0, drawingCanvas.width, drawingCanvas.height)
  }

  function redrawCanvas() {
    clearCanvas()
    const targetDrawings = drawings[selectedTarget.value] || []
    targetDrawings.forEach((drawing) => {
      const drawFunc = drawing.type === 'line' ? drawLine : drawRectangle
      drawFunc(drawing.start, drawing.end, drawing.highlight)
      ctx.fillStyle = drawing.highlight ? 'purple' : 'black'
      ctx.fillText(drawing.name, drawing.end.x + 5, drawing.end.y + 5)
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

  function onVideoMetadataLoaded() {
    actualVideoWidth = cameraFeedElement.videoWidth
    actualVideoHeight = cameraFeedElement.videoHeight

    // Calculate scale factors based on the video element's fixed size
    scaleX = actualVideoWidth / 730 // Since the video element width is fixed at 730px
    scaleY = actualVideoHeight / 620 // Since the video element height is fixed at 620px
  }

  function saveZones() {
    dispatch('save', targetZones)
    isOpen = false // Close the modal
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

  $: if (targets && drawings) {
    mapDrawingsToTargetZones()
  }
</script>

<svelte:window on:keydown={handleKeydown} />

<Overlay bind:showModal={isOpen} title="Draw Zones" on:close={() => (isOpen = false)}>
  <div class="flex w-full h-full">
    <!-- Video and Canvas Container -->
    <div class="relative flex-grow">
      <!-- svelte-ignore a11y-media-has-caption -->
      <video
        bind:this={cameraFeedElement}
        class="w-full h-full"
        autoplay
        on:resize={initCanvas}
        on:loadedmetadata={onVideoMetadataLoaded}
      ></video>
      <canvas
        bind:this={drawingCanvas}
        class="absolute top-0 left-0 w-full h-full cursor-crosshair"
        on:mousedown={startDrawing}
        on:mousemove={draw}
        on:mouseup={endDrawing}
        on:contextmenu={handleKeydown}
      ></canvas>
    </div>

    <!-- Shapes List -->
    <!-- svelte-ignore a11y-mouse-events-have-key-events -->
    <div
      class="sticky top-0 ml-6 w-56 bg-gray-700 text-white overflow-auto rounded-md"
      style="max-height: 555px; height: 555px;"
    >
      <h1 class="text-xl font-bold border-b-2 border-gray-600 p-4">Zone List</h1>
      {#each drawings[selectedTarget.value] || [] as drawing, index}
        <!-- svelte-ignore a11y-mouse-events-have-key-events -->
        <!-- svelte-ignore a11y-no-static-element-interactions -->
        <div
          class="group flex flex-col p-2 border-b border-gray-600"
          on:mouseover={() => highlightShape(selectedTarget.value, index, true)}
          on:mouseout={() => highlightShape(selectedTarget.value, index, false)}
        >
          <div class="relative flex flex-row">
            <input
              type="text"
              bind:value={drawing.name}
              class="text-white bg-gray-700 p-1 mb-1 w-full rounded-md group-hover:bg-gray-600 focus:bg-gray-600"
            />
            <Button
              class="absolute bg-red-500 bottom-2 right-2 rounded-md opacity-50 hover:opacity-100 transition-opacity duration-300"
              on:click={() => deleteShape(selectedTarget.value, index)}
            >
              <Icon icon="close" highlightOnHover class="w-5 h-5" tabindex={-1} />
            </Button>
          </div>
        </div>
      {/each}
    </div>
  </div>
  <div class="flex items-center justify-between space-x-4 p-4 bg-gray-800 text-white">
    <div class="flex items-center space-x-4">
      <div class="-translate-y-2">
        <SwitchButton
          labelOff="Line"
          labelOn="Zone"
          colorOn="bg-blue-500"
          colorOff="bg-green-500"
          on:change={(e) => (drawType = e.detail ? 'rectangle' : 'line')}
        />
      </div>
      <Dropdown
        bind:disabled={disableInputs}
        bind:selectedItem={selectedTarget}
        items={targets}
        placeholder="Select Target"
        on:change={() => redrawCanvas()}
      />
    </div>
    <Button class="py-2 px-4" on:click={saveZones} controlButton>Save</Button>
  </div>
</Overlay>

<style>
  video {
    width: 730;
    height: 620;
  }
</style>
