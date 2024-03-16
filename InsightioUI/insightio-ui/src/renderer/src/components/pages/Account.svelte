<script>
  import Input from '../utility/Input.svelte'
  import Button from '../utility/Button.svelte'
  import profileImg from '../../assets/profile.png'
  import Icon from '../utility/Icon.svelte'
  import patterns from '../../functions/regex'

  let name = ''
  let surname = ''
  let email = ''
  let password = ''
  let selectedFile
  let previewUrl
  let fileInput
  let dataUri

  async function saveAccountSettings() {
    if (selectedFile) {
      const reader = new FileReader()
      reader.onload = (e) => {
        dataUri = e.target.result
        console.log('Data URI:', dataUri)
        // Save or process dataUri as needed here
      }
      reader.readAsDataURL(selectedFile)
    }
    console.log('Account settings saved:', { name, surname, email, password, dataUri })
  }

  function handleFileChange(event) {
    const file = event.target.files[0]
    if (file) {
      selectedFile = file
      const reader = new FileReader()
      reader.onload = (e) => {
        previewUrl = e.target.result
      }
      reader.readAsDataURL(file)
    }
  }

  function resetAccountSettings() {
    name = ''
    surname = ''
    email = ''
    password = ''
    previewUrl = profileImg
    selectedFile = null
    dataUri = ''
  }

  function triggerFileSelect() {
    fileInput.click()
  }

  $: {
    if (dataUri) previewUrl = dataUri
  }
</script>

<div class="flex flex-col h-full w-full bg-gray-800 text-white">
  <div class="px-4 pt-4 flex justify-between items-center">
    <h1 class="text-5xl font-bold">Account</h1>
    <div></div>
  </div>

  <div class="flex flex-wrap justify-center px-4 pt-4 mt-14">
    <div class="border-2 border-gray-700 p-4 rounded-lg" style="width: 65%;">
      <!-- Account Picture Section -->
      <div class="flex flex-wrap justify-center p-4">
        <div class="w-full md:w-1/2 lg:w-1/3 px-2 md:mb-0">
          <div class="flex flex-col items-center">
            <div class="flex items-center mb-4 w-full">
              <h2 class="text-3xl font-bold mb-6">Account Picture</h2>
            </div>

            <div class="w-60 h-60 mt-6 mb-4">
              <img
                src={previewUrl || profileImg}
                alt="Profile preview"
                class="rounded-full object-cover h-full w-full"
              />
            </div>
            <!-- Hidden File Input -->
            <input
              type="file"
              accept="image/*"
              bind:this={fileInput}
              on:change={handleFileChange}
              style="display:none;"
            />

            <!-- Edit Icon triggers file input dialog -->
            <button
              class="ml-28 -mt-16 bg-slate-500 p-2 rounded-full border-slate-800 border-8"
              on:click={triggerFileSelect}
            >
              <Icon icon="edit" size="24" xy="0" class="w-7 h-7" />
            </button>
          </div>
        </div>

        <div class="w-28 block"></div>

        <!-- Information Section -->
        <div class="w-full md:w-1/2 lg:w-1/3 px-2">
          <div class="flex flex-col">
            <div class="flex items-center mb-4">
              <div class="mt-1">
                <!-- Information Icon -->
                <Icon
                  icon="profileDetails"
                  size="24"
                  xy="0"
                  class="w-10 h-10 mr-4"
                  fill="currentColor"
                />
              </div>

              <h2 class="text-3xl font-bold flex items-center">Info</h2>
            </div>

            <Input
              class="mb-4 p-1.5"
              showLabel
              label="Name:"
              type="text"
              placeholder="Name"
              bind:value={name}
            />
            <Input
              class="mb-4 p-1.5"
              showLabel
              label="Surname:"
              type="text"
              placeholder="Surname"
              bind:value={surname}
            />
            <Input
              class="mb-4 p-1.5"
              showLabel
              label="E-mail:"
              type="email"
              placeholder="Email"
              pattern={patterns['email']}
              bind:value={email}
            />
            <Input
              class="mb-4 p-1.5"
              showLabel
              label="Password:"
              type="password"
              placeholder="Password"
              bind:value={password}
            />

            <div class="flex justify-end space-x-3 mt-4">
              <Button
                class="bg-red-500 px-4 p-2 hover:bg-red-700 text-white font-bold rounded focus:outline-none focus:shadow-outline"
                on:click={resetAccountSettings}>Reset</Button
              >
              <Button
                class="bg-blue-500 px-4 p-2 hover:bg-blue-700 text-white font-bold rounded focus:outline-none focus:shadow-outline"
                on:click={saveAccountSettings}>Save</Button
              >
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
