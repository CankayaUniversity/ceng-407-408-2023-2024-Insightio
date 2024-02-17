<script>
  import SwitchButton from '../utility/SwitchButton.svelte'
  import CameraAdd from './CameraAdd.svelte'

  let currentMode = 'add'
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

  <div class="p-4 mt-4">
    <div class="flex flex-grow border-2 border-gray-700 rounded-lg">
      {#if currentMode == 'add'}
        <CameraAdd />
      {:else}
        <p>Not implemented</p>
      {/if}
    </div>
  </div>
</div>
