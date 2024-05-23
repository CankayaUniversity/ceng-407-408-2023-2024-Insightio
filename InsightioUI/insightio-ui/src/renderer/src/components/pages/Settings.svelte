<script>
    import Button from '../utility/Button.svelte';
    //import { saveSettings, cancelSettings } from '../../functions/settings';
  
    let theme = 'system';
  
    /*function handleSaveSettings() {
      saveSettings(theme);
    }
  
    function handleCancelSettings() {
      cancelSettings();
      theme = localStorage.getItem('theme') || 'system'; // Reset theme to the stored value
    }*/
  
    $: {
      theme = localStorage.getItem('theme') || 'system';
    }
  </script>
  
  <div class="flex flex-col h-full w-full bg-gray-800 text-white">
    <div class="px-4 pt-4 flex justify-between items-center">
      <h1 class="text-5xl font-bold">Settings</h1>
      <div></div>
    </div>
  
    <div class="flex flex-wrap justify-center px-4 pt-4 mt-6">
      <div class="border-2 border-gray-700 p-4 rounded-lg" style="width: 50%;">
        <div class="flex flex-wrap justify-start p-4">
          <div class="w-full md:w-1/2 lg:w-1/3 px-2 md:mb-0">
            <div class="flex flex-col items-center">
              <div class="flex items-center mb-4 w-full">
                <h2 class="text-3xl font-bold mb-6">Theme</h2>
              </div>
  
              <div class="mb-6">
                <label class="flex items-center mb-4">
                  <input type="radio" name="theme" value="system" bind:group={theme} />
                  <span class="ml-2">System default</span>
                </label>
                <label class="flex items-center mb-4">
                  <input type="radio" name="theme" value="light" bind:group={theme} />
                  <span class="ml-2">Light</span>
                </label>
                <label class="flex items-center mb-4">
                  <input type="radio" name="theme" value="dark" bind:group={theme} />
                  <span class="ml-2">Dark</span>
                </label>
              </div>
            </div>
          </div>
  
          <div class="w-28 block"></div>
        </div>
  
        <div class="flex justify-end space-x-3 mt-4">
          <Button
            class="bg-red-500 px-4 p-2 hover:bg-red-700 text-white font-bold rounded focus:outline-none focus:shadow-outline"
            
            controlButton={true}
            color="red"
          >Cancel</Button>
          <Button
            class="bg-blue-500 px-4 p-2 hover:bg-blue-700 text-white font-bold rounded focus:outline-none focus:shadow-outline"
            
            controlButton={true}
            color="blue"
          >Save</Button>
        </div>
      </div>
    </div>
  </div>
  