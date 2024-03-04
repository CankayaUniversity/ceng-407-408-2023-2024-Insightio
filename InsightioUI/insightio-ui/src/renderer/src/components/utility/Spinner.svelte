<script>
  export let size = 50 // default size of the spinner
</script>

<div class="loading-container">
  <div class="spinner" style="width: {size}px; height: {size}px;"></div>
</div>

<style>
  .loading-container {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%; /* or any other size you want */
  }

  .spinner {
    border: 4px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: #fff;
    animation: spin 1s ease-in-out infinite;
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
</style>
