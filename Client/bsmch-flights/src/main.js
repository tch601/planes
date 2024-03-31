import Vue from 'vue';
import { Icon } from 'leaflet';
import App from './App.vue';
import router from './router';
import store from './store';
import 'leaflet/dist/leaflet.css';
import Vuetify from 'vuetify/lib';
import 'vuetify/src/styles/main.sass';
import AwesomeLoaders from 'bsm-loaders';
import VueConfetti from 'vue-confetti';
import VueI18n from 'vue-i18n';
import EN from './locale/en.json';
import HE from './locale/he.json';

Vue.config.productionTip = false;

// this part resolve an issue where the markers would not appear
delete Icon.Default.prototype._getIconUrl;

Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

Vue.use(AwesomeLoaders);

Vue.use(Vuetify);

Vue.use(VueConfetti);

Vue.use(VueI18n);

const i18n = new VueI18n({
  locale: 'HE',
  messages: {
    EN: EN,
    HE: HE,
  },
});

new Vue({
  router,
  store,
  vuetify: new Vuetify({
    rtl: true,
    theme: { dark: true },
    icons: {
      iconfont: 'mdiSvg', // 'mdi' || 'mdiSvg' || 'md' || 'fa' || 'fa4' || 'faSvg'
    },
  }),
  i18n,
  render: (h) => h(App),
}).$mount('#app');
