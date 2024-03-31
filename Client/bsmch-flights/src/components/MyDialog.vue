<template>
  <v-card style="padding: 1%;">
    <v-tabs slider-color="white" v-model="tab">
      <v-tab v-for="item in getInfo" :key="item.title">
        {{ item.title }}
      </v-tab>
    </v-tabs>
    <v-tabs-items v-model="tab">
      <v-tab-item v-for="component in getInfo" :key="component.componentName">
        <component :is="component.componentName" />
      </v-tab-item>
    </v-tabs-items>
  </v-card>
</template>

<script>
import Register from './Resister.vue';
import Login from "./Login.vue";

export default {
  components: {
    Register,
    Login,
  },
   props: {
    changeInfo: {
      type: Boolean,
      required: false,
      default: false
    },
  },
  data() {
    return {
      tab: null,
    };
  },
 computed: {
  getInfo(){
    if(this.changeInfo){
      return [{ componentName: 'Register', title: 'שינוי פרטים' }]
    }

    return [
        { componentName: 'Login', title: 'התחבר' },
        { componentName: 'Register', title: 'הרשם' },
      ];
  },
 }
};
</script>
