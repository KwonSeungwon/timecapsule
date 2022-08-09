import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import VueClipboard from 'vue-clipboard2';

VueClipboard.config.autoSetContainer = true;


createApp(App).use(router,VueClipboard).mount('#app')
