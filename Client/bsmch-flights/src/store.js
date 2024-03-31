import Vue from 'vue';
import Vuex from 'vuex';
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    jwt: "---",
  },
  plugins: [createPersistedState()],
  mutations: {
    setJWT(state, jwt) {
      state.jwt = jwt;
    },
  },
  actions: {
    setJWT({ commit }, jwt) {
      commit('setJWT', jwt);
    },
    defaultJWT({ commit }) {
      commit('setJWT', '---')
    }
  },
});
