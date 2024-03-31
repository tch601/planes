<template>
  <v-form v-model="valid" ref="form">
    <v-card text>
      <v-card-text>
        <v-container grid-list-md>
          <v-row>
            <v-text-field
              :rules="[rules.required, rules.email]"
              solo
              label="כתובת מייל"
              class="input-group--focused"
              type="email"
              required
              prepend-inner-icon="email"
              v-model="email"
            />
          </v-row>
          <v-row>
            <v-text-field
              solo
              :append-icon="showpass ? 'visibility' : 'visibility_off'"
              :rules="[rules.required, rules.password]"
              :type="showpass ? 'text' : 'password'"
              label="סיסמא"
              hint="At least 8 characters, one letter and one number"
              class="input-group--focused"
              required
              prepend-inner-icon="fa-key"
              @click:append="showpass = !showpass"
              v-model="password"
            />
          </v-row>
        </v-container>
      </v-card-text>
      <v-row align="center" justify="start">
        <v-col cols="2">
          <v-btn color="blue darken-3" @click="closeModal()">סגור חלון</v-btn>
        </v-col>
        <v-col cols="2">
          <v-btn id="loginBtn" color="blue darken-3" @click="logIn()"
            >התחבר</v-btn
          >
        </v-col>
      </v-row>
    </v-card>
  </v-form>
</template>

<script>
import eventbus from "@/plugins/eventbus";
import api from "@/api/api";
import Swal from "sweetalert2";

export default {
  methods: {
    closeModal() {
      this.email = "";
      this.password = "";
      eventbus.$emit("closedialog");
    },
    async logIn() {
      if (this.$refs.form.validate()) {
        try {
          const jwt = await api.Users().loginUser(this.email, this.password);
          this.$store.dispatch("setJWT", jwt);
          this.closeModal();
        } catch (error) {
          Swal.fire({
            type: "error",
            title: "אופס",
            text: "נראה שחלק מהפרטים שהזנת שגויים",
          });
        }
      }
    },
  },

  data() {
    return {
      valid: true,
      showpass: false,
      rules: {
        required: (value) => !!value || "Required",
        email: (value) => {
          const pattern =
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
          return pattern.test(value) || "Invalid e-mail.";
        },
        password: (value) => {
          const pattern = /^(?=.*\d)(?=.*[a-zA-Z]).{8,}$/;
          return pattern.test(value) || "Invalid password";
        },
      },
      email: "",
      password: "",
    };
  },
};
</script>

<style scoped>
div.v-input__icon.v-input__icon--prepend-inner i {
  font-size: larger;
}
</style>