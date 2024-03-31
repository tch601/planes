<template>
  <div>
    <v-card color="'grey' lighten-1" class="mb-5">
      <v-container fluid>
        <v-form v-model="valid">
          <v-row>
            <v-col>
              <v-text-field
                solo
                label="שם פרטי"
                class="input-group--focused"
                type="text"
                v-model="firstname"
                :rules="[rules.required, rules.onlyAlphabet]"
                required
                prepend-inner-icon="person"
              />
            </v-col>
            <v-col>
              <v-text-field
                solo
                label="שם משפחה"
                class="input-group--focused"
                type="text"
                v-model="lastname"
                :rules="[rules.required, rules.onlyAlphabet]"
                required
                prepend-inner-icon="person"
              />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-text-field
                solo
                label="שם משתמש"
                class="input-group--focused"
                type="text"
                v-model="username"
                :rules="[rules.required]"
                required
                prepend-inner-icon="account_circle"
              />
            </v-col>
            <v-col v-if="idPut">
              <v-text-field
              disabled
                solo
                label="תעודת זהות"
                class="input-group--focused"
                type="text"
                v-model="id"
                
                prepend-inner-icon="face"
              />
            </v-col>

            <v-col v-else>
              <v-text-field
              
                solo
                label="תעודת זהות"
                class="input-group--focused"
                type="text"
                v-model="id"
                :rules="[
                  rules.onlyDigits,
                  rules.required,
                  () => id.length == 9 || 'תעודת זהות לא תקינה',
                  () => !isNaN(id) || 'תעודת זהות לא תקינה',
                ]"
                required
                prepend-inner-icon="face"
              />
            </v-col>
          </v-row>
          <v-row>
            <v-col xs12>
              <v-text-field
                solo
                label="אימייל"
                class="input-group--focused"
                type="email"
                v-model="email"
                :rules="[rules.required, rules.email]"
                required
                prepend-inner-icon="email"
              />
            </v-col>
          </v-row>
        </v-form>
      </v-container>
    </v-card>
    <v-btn :disabled="!valid" color="primary" @click="$emit('continue')"
      >המשך</v-btn
    >
  </div>
</template>

<script>
import eventbus from '@/plugins/eventbus';
export default {
  watch: {
    firstNamePut() {
      this.changeInfo();
    }},
  created(){
    this.changeInfo();
  },
  props: {
    userNamePut:{
      type: String,
      required: false,
    },
    firstNamePut: {
      type: String,
      required: false,
    },
    emailPut: {
      type: String,
      required: false,
    },
    passwordPut: {
      type: String,
      required: false,
    },
    lastNamePut: {
      type: String,
      required: false,
    },
    idPut: {
      type: Number,
      required: false,
    },
  },
   methods: {
     changeInfo() {
    if(this.firstNamePut){
        this.firstname=this.firstNamePut;
        this.email=this.emailPut;
        this.lastname=this.lastNamePut;
        this.id=this.idPut;
        this.password=this.passwordPut;
        this.username=this.userNamePut;
      }
    
    }},
  data() {
    return {
      firstname: '',
      lastname: '',
      username: '',
      id: '',
      email: '',
      valid: true,
      rules: {
        required: (value) => !!value || 'חובה למלא שדה זה',
        onlyAlphabet: (value) => {
          const pattern = /^[א-ת ]+$/i;
          return pattern.test(value) || 'שם לא חוקי';
        },
        email: (value) => {
          const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
          return pattern.test(value) || 'מייל לא חוקי';
        },
        onlyDigits: (value) =>
          /^[0-9]*$/.test(value) || 'חייב להכיל מספרים בלבד',
      },
    };
  },
};
</script>
