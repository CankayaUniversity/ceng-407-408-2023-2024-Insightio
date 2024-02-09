<script>
    import Input from '../utility/Input.svelte';
    import Button from '../utility/Button.svelte';
    import { onMount } from 'svelte';
  
    let accountPicture = ''; //'"C:/Users/zeynep/Pictures/profile1.jpg"'
    let name = '';
    let surname = '';
    let email = '';
    let password = '';
    let selectedFile;
    let previewUrl;
  
    function saveAccountSettings() {
      console.log('Account settings saved');
      // Burada, ayarları kaydetmek için gerekli işlemleri yapabilirsiniz.
    }
  
    function cancelAccountSettings() {
      console.log('Account settings canceled');
      // İptal işlemi için gerekli adımlar burada gerçekleştirilebilir.
    }

    function handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        selectedFile = file;
        const reader = new FileReader();
        reader.onload = (e) => {
          previewUrl = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    }

    function uploadFile() {
      // Burada dosya yükleme işlemini gerçekleştirebilirsiniz.
      console.log('Dosya yükleniyor:', selectedFile);
    }

</script>
  
<div class="flex h-full w-full bg-gray-800 text-white">
    <div class="w-1/3 flex flex-col items-center p-4">
      <div class="file-upload">
        {#if previewUrl}
          <img src={previewUrl} alt="Profil resmi önizlemesi" class="preview" />
        {/if}
        <input type="file" accept="image/*" on:change={handleFileChange} />
        <button on:click={uploadFile}>Yükle</button>
      </div>
      
    </div>
    <div class="w-2/3 flex flex-col justify-center p-4">
      <h2 class="text-3xl font-bold mb-4">Info</h2>
      <Input class="w-full py-2 px-3 mb-4" showLabel label="Name:" type="text" placeholder="Name" bind:value={name} />
      <Input class="w-full py-2 px-3 mb-4" showLabel label="Surname:" type="text" placeholder="Surname" bind:value={surname} />
      <Input class="w-full py-2 px-3 mb-4" showLabel label="E-mail:" type="email" placeholder="Email" bind:value={email} />
      <Input class="w-full py-2 px-3 mb-6" showLabel label="Password:" type="password" placeholder="Password" bind:value={password} />
      <div class="flex justify-end gap-4">
        <Button class="py-2 px-4 bg-blue-500 hover:bg-blue-700" on:click={saveAccountSettings}>Save</Button>
        <Button class="py-2 px-4 bg-red-500 hover:bg-red-700" on:click={cancelAccountSettings}>Cancel</Button>
      </div>
    </div>
</div>

<style>
  .preview {
    width: 100px;
    height: 100px;
    object-fit: cover;
  }
</style>
  