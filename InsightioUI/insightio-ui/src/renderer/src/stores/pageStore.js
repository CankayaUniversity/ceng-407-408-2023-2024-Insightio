import { writable } from 'svelte/store';
import Dashboard from '../components/pages/Dashboard.svelte';
import Account from '../components/pages/Account.svelte';
import CameraConfiguration from '../components/pages/CameraConfiguration.svelte';
import Settings from '../components/pages/Settings.svelte';

function createPagesStore() {
    const { subscribe, update } = writable({
      activePage: 'dashboard', // default active page
      pages: [
        {
          key: 'dashboard',
          title: 'Dashboard',
          component: Dashboard,
          icon: 'home'
        },
        {
            key: 'account',
            title: 'Account',
            component: Account,
            icon: 'user'
          },
          {
            key: 'cameraConfiguration',
            title: 'Camera Configuration',
            component: CameraConfiguration,
            icon: 'plus'
          },
          {
            key: 'settings',
            title: 'Settings',
            component: Settings,
            icon: 'gear'
          }
        // Add other page objects here
      ]
    });
  
    return {
      subscribe,
      setActivePage: (pageKey) => update(store => {
        store.activePage = pageKey;
        return store;
      }),
      // You can add other methods if needed
    };
  }
  
  const pageStore = createPagesStore();
  export default pageStore;