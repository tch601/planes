<template>
  <div>
    <v-card color="'grey' lighten-1" class="mb-5 ml-10">
      <v-container fluid>
        <v-form v-model="valid">
          <v-row>
            <v-text-field
              solo
              v-model="password"
              color="primary lighten-3"
              label="סיסמא"
              :append-icon="showpass ? 'visibility' : 'visibility_off'"
              @click:append="showpass = !showpass"
              :type="showpass ? 'text' : 'password'"
              :rules="[rules.required, rules.password]"
            >
            </v-text-field>
            <template>
              <v-progress-linear
                :value="progress"
                :color="color"
                background-color="null"
              />
            </template>
          </v-row>

          <v-row>
            <v-text-field
              solo
              label="וודא סיסמא"
              class="input-group--focused"
              :rules="[
                rules.required,
                (value) =>
                  !(value != this.password) || 'הסיסמאות חייבות להיות זהות',
              ]"
              type="password"
              required
            />
          </v-row>
        </v-form>
      </v-container>
    </v-card>
    <v-btn :disabled="!valid" color="primary" @click="$emit('continue')"
      >המשך</v-btn
    >
    <v-btn text @click="$emit('back')">חזור</v-btn>
  </div>
</template>

<script>
import eventbus from '@/plugins/eventbus';

export default {
  watch: {
    passwordPut() {
      this.changeInfo();
    }},
  created(){
    this.changeInfo();
  },
    methods: {
     changeInfo() {
    if(this.passwordPut){
        this.password=this.passwordPut;
      }
    
    }},
  data: () => ({
    password: '',
    showpass: false,
    rules: {
      required: (value) => !!value || 'חובה למלא שדה זה',
      password: (value) => {
        const pattern = /^(?=.*\d)(?=.*[a-zA-Z]).{8,}$/;
        return (
          pattern.test(value) ||
          'על הסיסמא להיות מינימום 8 תווים ולהכיל לפחות אות אחת ומספר אחד'
        );
      },
    },
    valid: true,
  }),
    props: {
    passwordPut: {
      type: String,
      required: false,
    },
  },
  computed: {
    progress() {
      return Math.min(100, this.password.length * 10);
    },
    color() {
      return ['error', 'warning', 'success'][Math.floor(this.progress / 40)];
    },
  },
};
</script>
