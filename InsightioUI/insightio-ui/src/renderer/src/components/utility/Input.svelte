<script>
  import clsx from 'clsx'
  import { createEventDispatcher } from 'svelte'
  import { v4 } from 'uuid'
  import Button from './Button.svelte'
  import Icon from './Icon.svelte'

  export let id = v4().substring(0, 8)
  export let value
  export let type = 'text'
  export let showLabel = false
  export let label = ''
  export let placeholder = ''
  export let pattern = ''
  export let errorMessage = 'Invalid format'
  export let errorMessagePos = '2'
  export let isPasteInput = false

  const dispatch = createEventDispatcher()
  let input
  let pasted = false
  let showError = false

  function validate(value) {
    if (pattern && (value != null || value != undefined)) {
      const regex = new RegExp(pattern)
      showError = !regex.test(value)
      dispatch('validation', !showError)

      if (showError && isPasteInput) {
        value = ''
        input.value = ''
      } else if (isPasteInput) {
        dispatch('paste', value)
      }
    } else {
      showError = false
    }
  }

  function handleInput(e) {
    if (!isPasteInput) {
      value = e.target.value
      validate(value)
    }
  }

  // Clears the input field
  function clearInput() {
    value = ''
    pasted = false // Reset the state
    dispatch('clear') // Optionally dispatch an event
  }

  // Handles the paste action
  function handlePaste() {
    navigator.clipboard.readText().then((clipText) => {
      value = clipText
      pasted = true // Set state to show the clear button
      validate(value)
    })
  }
</script>

<div class="relative">
  {#if showLabel}
    <label class="block text-white text-sm font-bold mb-1" for={id}>{label}</label>
  {/if}
  <input
    class={clsx(
      'shadow appearance-none border rounded text-gray-700 leading-tight focus:outline-none focus:shadow-outline',
      isPasteInput && 'cursor-not-allowed',

      { 'border-red-500': showError },
      'w-full',
      $$props.class
    )}
    {id}
    {type}
    {placeholder}
    {value}
    readonly={isPasteInput}
    on:input={handleInput}
  />
  {#if isPasteInput}
    <Button
      on:click={pasted ? clearInput : handlePaste}
      class={clsx(
        'absolute right-1 top-0 mt-6 bg-gray-700 rounded-xl p-0.5',
        pasted ? 'bg-red-500' : 'bg-gray-700'
      )}
    >
      <Icon class="w-6 h-6 z-10" icon={pasted ? 'close' : 'paste'} />
    </Button>
  {/if}
  {#if showError}
    <p class={`text-red-500 text-xs italic absolute -bottom-${errorMessagePos} w-96`}>
      {errorMessage}
    </p>
  {/if}
</div>
