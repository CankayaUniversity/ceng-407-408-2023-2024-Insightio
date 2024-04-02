<script>
  import Input from '../utility/Input.svelte'
  import Button from '../utility/Button.svelte'
  import profileImg from '../../assets/profile.png'
  import Icon from '../utility/Icon.svelte'
  import patterns from '../../functions/regex'
  import hash from '../../functions/hash'
  import userStore from '../../stores/userStore'
  import { failure, success, warn } from '../../functions/toastifyWrapper'
  import { createUser, updateUser } from '../../api/users'
  import SwitchButton from '../utility/SwitchButton.svelte'
  import readWriteDataUri from '../../functions/dataUri'

  let username = ''
  let email = ''
  let password = ''
  let passwordConfirm = ''
  let usernameValidation = false
  let emailValidation = false
  let passwordValidation = false
  let selectedFile
  let previewUrl
  let fileInput
  let dataUri
  let currentMode = 'edit'

  function validate() {
    if (usernameValidation) {
      warn('Please enter a valid username containing at least 4 characters')
      return false
    }
    if (emailValidation) {
      warn('Please enter a valid username containing at least 4 characters')
      return false
    }
    if (passwordValidation && password != passwordConfirm) {
      warn('Please make sure password fields hold the same password')
      return false
    }
    return true
  }

  async function saveAccountSettings() {
    if (validate()) {
      if (selectedFile) {
        readWriteDataUri(selectedFile, (e) => {
          dataUri = e.target.result
        })
      }
      const d = new Date()
      const user = {
        username: username,
        email: email,
        password: password != '' ? await hash(password) : $userStore.password,
        organizationId: $userStore.organizationId,
        isAdmin: isEditMode ? $userStore.isAdmin : false,
        isCreate: isEditMode ? $userStore.isCreate : true,
        createdDate: isEditMode ? $userStore.createdDate : d.toISOString(),
        createdBy: isEditMode ? $userStore.createdBy : $userStore._id,
        metadata: [
          {
            categoryId: '660977b2e0f48cd2a2ecd01a',
            value: dataUri ? dataUri : ''
          }
        ]
      }

      if (isEditMode) {
        const res = await updateUser(user)

        if (!('error' in res)) {
          success('User successfully updated!')
        } else {
          failure('Failed to update account')
        }
      } else {
        const res = await createUser(user)

        if (!('error' in res)) {
          success('User successfully created!')
          resetAccountSettings()
        } else {
          failure('Failed to create the new account')
        }
      }
    }
  }

  function handleFileChange(event) {
    const file = event.target.files[0]
    if (file) {
      selectedFile = file
      readWriteDataUri(selectedFile, (e) => {
        previewUrl = e.target.result
      })
    }
  }

  function handleModeChange() {
    if (isEditMode) {
      username = $userStore.username
      email = $userStore.email
      password = ''
      passwordConfirm = ''
      previewUrl = $userStore.metadata[0] ? $userStore.metadata[0].value : profileImg
      selectedFile = null
      dataUri = $userStore.metadata[0] ? $userStore.metadata[0].value : profileImg
    } else {
      resetAccountSettings()
    }
  }

  function resetAccountSettings() {
    username = ''
    email = ''
    password = ''
    passwordConfirm = ''
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

  $: isEditMode = currentMode == 'edit'

  $: isEditMode != undefined && handleModeChange()
</script>

<div class="flex flex-col h-full w-full bg-gray-800 text-white">
  <div class="px-4 pt-4 flex justify-between items-center">
    <h1 class="text-5xl font-bold">Account</h1>
    <div></div>
  </div>

  {#if $userStore.isAdmin}
    <div class="flex justify-center" style="margin-left: 41rem;">
      <SwitchButton
        checked={true}
        labelOff="Add"
        labelOn="Edit"
        colorOff="bg-green-500"
        colorOn="bg-blue-500"
        on:change={(e) => (currentMode = e.detail ? 'edit' : 'add')}
      />
    </div>
  {/if}

  <div class="flex flex-wrap justify-center px-4 pt-4 mt-6">
    <div class="border-2 border-gray-700 p-4 rounded-lg" style="width: 65%;">
      <!-- Account Picture Section -->
      <div class="flex flex-wrap justify-center p-4">
        <div class="w-full md:w-1/2 lg:w-1/3 px-2 md:mb-0">
          <div class="flex flex-col items-center">
            <div class="flex items-center mb-4 w-full">
              <h2 class="text-3xl font-bold mb-6">Account Picture</h2>
            </div>

            <div class="w-64 h-64 mt-10 mb-4">
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
            <div class="mb-6">
              <Input
                class="p-1.5"
                showLabel
                label="Username:"
                type="text"
                placeholder="Username"
                pattern={patterns['username']}
                errorMessage="Username is too short"
                on:validation={(e) => (usernameValidation = !e.detail)}
                bind:value={username}
              />
            </div>
            <div class="mb-6">
              <Input
                class="p-1.5"
                showLabel
                label="E-mail:"
                type="email"
                placeholder="Email"
                pattern={patterns['email']}
                errorMessage="Invalid email format"
                on:validation={(e) => (emailValidation = !e.detail)}
                bind:value={email}
              />
            </div>
            <div class="mb-6">
              <Input
                class="p-1.5"
                showLabel
                label="Password:"
                type="password"
                placeholder="Password"
                pattern={patterns['password']}
                errorMessage="Password must contain at least 8 characters"
                on:validation={(e) => (passwordValidation = !e.detail)}
                bind:value={password}
              />
            </div>
            <div class="mb-6">
              <Input
                class="p-1.5"
                showLabel
                label="Confirm Password:"
                type="password"
                placeholder="Retype password"
                pattern={`${password}`}
                errorMessage="Passwords do not match"
                bind:value={passwordConfirm}
              />
            </div>
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
