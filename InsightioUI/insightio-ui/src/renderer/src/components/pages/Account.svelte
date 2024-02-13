<script>
    import Input from '../utility/Input.svelte';
    import Button from '../utility/Button.svelte';
    import anonimimg from '../../assets/anonim.png';
  
    let name = '';
    let surname = '';
    let email = '';
    let password = '';
    let selectedFile;
    let previewUrl;
    let fileInput; // reference for input element
  
    function saveAccountSettings() {
      console.log('Account settings saved');
      console.log('File Uploading:', selectedFile);
    }
  
    function cancelAccountSettings() {
      console.log('Account settings canceled');
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

    function triggerFileSelect() {
      fileInput.click(); // trigger click method of input element
    }

</script>


<div class="container mx-auto px-4 py-8">
  <div class="flex flex-wrap justify-center -mx-2">
    <div class="w-full md:w-1/2 lg:w-1/3 px-2 mb-4 md:mb-0 mr-10">
      <div class="flex flex-col items-center">
        <div class="flex items-center mb-4">
          <h2 class="text-3xl font-bold mb-6">Account Picture</h2>
        </div>
        
        <div class="w-48 h-48 mb-6">
          <img src={previewUrl || anonimimg} alt="Profil resmi önizlemesi" class="rounded-full object-cover h-full w-full" />
        </div>
        <!-- Hidden File Input -->
        <input type="file" accept="image/*" bind:this={fileInput} on:change={handleFileChange} style="display:none;" />

        <!-- Edit Icon triggers file input dialog -->
        <button class="ml-28  -mt-16 bg-slate-500 p-2 rounded-full border-slate-800 border-8" on:click={triggerFileSelect}>
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-7 h-7">
            <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
          </svg>
        </button>
      </div>
    </div>

    <!-- Information Section -->
    <div class="w-full md:w-1/2 lg:w-1/3 px-2 ml-10">
      <div class="flex flex-col">
     <div class="flex items-center mb-4">
      <div class="mt-1">
        <!-- Information Icon -->
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-10 h-10 mr-2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 9h3.75M15 12h3.75M15 15h3.75M4.5 19.5h15a2.25 2.25 0 0 0 2.25-2.25V6.75A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25v10.5A2.25 2.25 0 0 0 4.5 19.5Zm6-10.125a1.875 1.875 0 1 1-3.75 0 1.875 1.875 0 0 1 3.75 0Zm1.294 6.336a6.721 6.721 0 0 1-3.17.789 6.721 6.721 0 0 1-3.168-.789 3.376 3.376 0 0 1 6.338 0Z" />
        </svg>
      </div>

      <h2 class="text-3xl font-bold flex items-center">Info  </h2>

     </div>

        <Input class="mb-4 p-1.5" showLabel label="Name:" type="text" placeholder="Name" bind:value={name} />
        <Input class="mb-4 p-1.5" showLabel label="Surname:" type="text" placeholder="Surname" bind:value={surname} />
        <Input class="mb-4 p-1.5" showLabel label="E-mail:" type="email" placeholder="Email" bind:value={email} />
        <Input class="mb-4 p-1.5" showLabel label="Password:" type="password" placeholder="Password" bind:value={password}/>
      

        <div class="flex justify-end mt-4">
          <Button class="mr-5 bg-red-700 px-2 p-2 hover:bg-red-800 text-white font-bold rounded focus:outline-none focus:shadow-outline" on:click={cancelAccountSettings}>Cancel</Button>
          <Button class= "bg-blue-500 px-4 p-2 hover:bg-blue-700 text-white font-bold rounded focus:outline-none focus:shadow-outline" on:click={saveAccountSettings}>Save</Button>
          
        </div>
      </div>
    </div>
  </div>
</div>
  