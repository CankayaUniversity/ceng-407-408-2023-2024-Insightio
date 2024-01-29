<script>
  import { createEventDispatcher } from 'svelte'
  import Button from '../utility/Button.svelte'
  import Input from '../utility/Input.svelte'
  import Icon from '../utility/Icon.svelte'

  const dispatch = createEventDispatcher()

  let username = ''
  let password = ''

  function submit() {
    if (username.trim().length > 0 && password.trim().length > 0) {
      dispatch('login')
    }
  }

  function handleKeydown(e) {
    if (e.key === 'Enter') {
      submit()
    }
  }

  function minimize() {
    window.api.send('minimize-window')
  }

  function close() {
    window.api.send('close-window')
  }
</script>

<svelte:window on:keydown={handleKeydown} />

<div class="flex items-center justify-center h-screen bg-gray-800">
  <div class="title-bar w-full fixed top-0 left-0 gap-1 mr-2">
    <Button on:click={minimize}>
      <Icon icon="minimize" highlightOnHover class="w-5 h-5" />
    </Button>
    <Button on:click={close}>
      <Icon icon="close" highlightOnHover class="w-5 h-5" />
    </Button>
  </div>
  <div class="w-full max-w-xs mt-20">
    <h1 class="mb-6 text-center text-4xl font-bold text-white">Login</h1>
    <div class="mb-2">
      <Input
        class="w-full py-2 px-3"
        showLabel
        label="Username:"
        type="username"
        id="username"
        placeholder="Username"
        bind:value={username}
      />
    </div>
    <div class="mb-6">
      <Input
        class="w-full py-2 px-3"
        showLabel
        label="Password:"
        type="password"
        id="password"
        placeholder="********"
        bind:value={password}
      />
    </div>
    <div class="flex items-center justify-between">
      <Button class="py-2 px-4" on:click={submit} controlButton>Login</Button>
      <a
        class="inline-block align-baseline font-bold text-sm text-blue-500 hover:text-blue-800"
        href="javascript;"
      >
        Forgot Password?
      </a>
    </div>
    <p class="mt-8 text-right text-white text-xs">© 2023 Insightio. All rights reserved.</p>
  </div>
</div>

<style>
  .title-bar {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 8px;
    gap: 0.75rem;
    -webkit-app-region: drag;
  }
</style>
