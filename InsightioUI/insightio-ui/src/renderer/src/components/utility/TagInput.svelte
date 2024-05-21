<script>
  import { createEventDispatcher, onMount } from 'svelte'
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
  export let disabled = false
  export let newTags = []

  let id = v4()
  let tagify
  let tagInput

  const dispatch = createEventDispatcher()

  function addTag() {
    let temp = []
    tagify.value.forEach((t) => {
      temp = [...temp, t.value]
    })
    tags = [...temp]
  }

  function onTagAddedOrRemoved(e) {
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

    if (e.type === 'remove') {
      const removedTag = e.detail.data.value
      dispatch('tagRemoved', removedTag)
    }
  }

  onMount(() => {
    tagify = new Tagify(tagInput, {
      id: id,
      whitelist: tagOptions,
      minTags: minTags,
      maxTags: maxTags,
      userInput: !disabled,
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

  $: tagify && tagify.setDisabled(disabled)

  $: {
    if (newTags.length != 0 && tagify) {
      tags = [...newTags]
      tagify.removeAllTags()
      tagify.addTags(newTags)
      newTags = []
    }
  }
  /* eslint-disable */
</script>

<div class={clsx('flex justify-start', $$props.class)}>
  <div class="w-full">
    <!-- svelte-ignore a11y-label-has-associated-control -->
    <label class="block mt-2 mb-2 text-sm font-bold text-white">{title}</label>
    <input bind:this={tagInput} type="text" class="tagify w-full" on:change={addTag} />
    <div class="float-left mt-2 text-xs text-white">
      <p>{helpText}</p>
    </div>
  </div>
</div>

<style>
  .tagify {
    --tags-border-color: #2d3748;
    --tags-background: #2d3748;
    --tag-bg: #4a5568;
    --tag-hover: #3182ce;
    --tag-text-color: #ffffff;
    --tag-pad: 6px 8px;
    --tag-text-color--edit: #ffffff;
    --tag-text-size: 0.875rem;
    --tag-remove-bg: #991b1b;
    --tag-remove-btn-color: #ffffff;
    --tag-remove-btn-bg: #374151;
    --input-color: #ffffff;
    --placeholder-color: #a0aec0;
    --tag-inset-shadow-size: 1.35em;

    min-width: 0;
  }
</style>
