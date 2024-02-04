<script>
  import { onMount } from 'svelte'
  import Tagify from '@yaireo/tagify'
  import '@yaireo/tagify/dist/tagify.css'
  import { v4 } from 'uuid'
  import clsx from 'clsx'

  export let tags
  export let tagOptions
  export let title = ''
  export let minTags = 1
  export let maxTags = 5
  export let maxSuggestions = 10
  export let helpText = ''

  let id = v4()
  let tagify
  let tagInput

  function addTag() {
    let temp = []
    tagify.value.forEach((t) => {
      temp = [...temp, t.value]
    })
    tags = [...temp]
  }

  function onTagAddedOrRemoved() {
    // Check the number of tags after each add/remove operation
    const tagsCount = tagify.value.length
    const removeButtons = tagInput.previousSibling.querySelectorAll('.tagify__tag__removeBtn')

    if (tagsCount <= 1) {
      // If there is only one tag, hide the remove button
      removeButtons.forEach((btn) => (btn.style.display = 'none'))
    } else {
      // If there are multiple tags, show the remove buttons
      removeButtons.forEach((btn) => (btn.style.display = ''))
    }
  }

  onMount(() => {
    tagify = new Tagify(tagInput, {
      id: id,
      whitelist: tagOptions,
      minTags: minTags,
      maxTags: maxTags,
      enforceWhitelist: true,
      editTags: {
        keepInvalid: false
      },
      dropdown: {
        classname: 'custom-dropdown custom-scroll',
        maxItems: maxSuggestions,
        enabled: 1,
        closeOnSelect: false,
        highlightFirst: true
      },
      texts: {
        duplicate: 'Duplicates are not allowed.'
      }
    })

    tagify.addTags(tags)

    tagify.on('add', onTagAddedOrRemoved)
    tagify.on('remove', onTagAddedOrRemoved)
  })
</script>

<div class={clsx('flex justify-start', $$props.class)}>
  <div class="w-full">
    <!-- svelte-ignore a11y-label-has-associated-control -->
    <label class="block mt-2 mb-2 text-sm font-bold">{title}</label>
    <input
      bind:this={tagInput}
      type="text"
      class="tagify w-full text-sm border border-gray-800 rounded outline-none"
      on:change={addTag}
    />
    <div class="float-left mt-2 text-sm text-white">
      <p>{helpText}</p>
    </div>
  </div>
</div>
