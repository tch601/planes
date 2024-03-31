<template>
  <v-card>
    <v-card-text>
      <template>
        <v-stepper v-model="e6" vertical>
          <v-stepper-step :complete="e6 > 1" step="1">
            פרטים אישיים
          </v-stepper-step>

          <v-stepper-content step="1" class="mr-n8">
            <v-row>
              
              <v-col cols="11" v-if="userInfo">
                <personal-details   ref="personal" @continue="e6 = 2" :firstNamePut="userInfo.firstname"
                 :idPut="userInfo.id" :lastNamePut="userInfo.lastname" :userNamePut="userInfo.username"
                 :emailPut="userInfo.email" :passwordPut="userInfo.password"/>
              </v-col>
              <v-col cols="11" v-else>            
                 <personal-details ref="personal" @continue="e6 = 2"/>
              </v-col>
            </v-row>
          </v-stepper-content>

          <v-stepper-step :complete="e6 > 2" step="2">
            פרטי סיסמא
          </v-stepper-step>

          <v-stepper-content step="2" class="mr-n8">
            <v-col  v-if="userInfo">
              <Password ref="password" @continue="e6 = 3" @back="e6 = 1" :passwordPut="userInfo.password"/>
            </v-col>
            <v-col  v-else>
              <Password ref="password" @continue="e6 = 3" @back="e6 = 1" />
            </v-col>
          </v-stepper-content>

          <v-stepper-step :complete="e6 > 3" step="3">
            פרטי כרטיס אשראי
          </v-stepper-step>

          <v-stepper-content step="3" class="mr-n8" v-if="userInfo">
            <CreditCard ref="creditcard" @continue="e6 = 4" @back="e6 = 2" :numberCardPut="userInfo.creditcard"/>
          </v-stepper-content>

          <v-stepper-content step="3" class="mr-n8" v-else>
            <CreditCard ref="creditcard" @continue="e6 = 4" @back="e6 = 2" />
          </v-stepper-content>

          <v-stepper-step :complete="e6 > 4" step="4">
            תמונת פרופיל
            <small>שדה זה הוא לא חובה</small>
          </v-stepper-step>

          <v-stepper-content step="4" class="mr-n8 ml-6">
            <v-card color="'grey' lighten-1" class="mb-5" height="50px">
              <v-text-field
                solo
                label="הכנס/י קישור לתמונה רצויה"
                class="input-group--focused"
                v-model="image"
                :rules="[imageRule]"
              />
            </v-card>
            <v-btn color="primary" @click="register()"  v-show="!userInfo">הרשם</v-btn>
            <v-btn color="primary" @click="register()"  v-show="userInfo">שנה פרטים</v-btn>
            <v-btn text @click="e6 = 3">חזור</v-btn>
          </v-stepper-content>
        </v-stepper>
      </template>
    </v-card-text>
    <Loading />
  </v-card>
</template>

<script>
import eventbus from "@/plugins/eventbus";
import Loading from "@/components/Loading";
import PersonalDetails from "@/components/RegisterComponents/PersonalDetails";
import Password from "@/components/RegisterComponents/Password";
import CreditCard from "@/components/RegisterComponents/CreditCard";
import api from "@/api/api";
import Swal from "sweetalert2";
import Axios from '../api/api';
import { mapState } from "vuex";

export default {
  async created(){
    await this.getUserInfo();
  },
   watch: {
    async jwt() {
     await this.getUserInfo();
     
    },
    userInfo(){
      if(this.userInfo && this.userInfo.image){
      this.image=this.userInfo.image;
     }
    }

  },

  components: {
    Loading,
    PersonalDetails,
    Password,
    CreditCard,
  },

  data() {
    return {
      userInfo: {},
      e6: 1,
      image: "",
      imageRule: (value) => {
        const pattern = /(http(s?):)([/|.|\w|\s|-])*\.(?:jpg|jpeg|gif|png)/;
        return pattern.test(value) || "Invalid URL";
      },

    };
  },
   computed: {
    ...mapState(["jwt"]),
 
  },
  methods: {
    async register() {
      const user = {
        id: this.$refs.personal.id,
        username: this.$refs.personal.username,
        firstname: this.$refs.personal.firstname,
        lastname: this.$refs.personal.lastname,
        email: this.$refs.personal.email,
        password: this.$refs.password.password,
        image:
          this.image != ""
            ? this.image
            : "https://as1.ftcdn.net/v2/jpg/02/59/39/46/1000_F_259394679_GGA8JJAEkukYJL9XXFH2JoC3nMguBPNH.jpg",
        joindate: this.getToday(),
        creditcard: this.$refs.creditcard.fullCredit,
        permission: { id: 2, name: "משתמש" },
      };

      eventbus.$emit("startloading");
      try {
        if(this.jwt){
          const jwt = await api.Users().updateUser(user);
        }else{
          const jwt = await api.Users().register(user);
        }
        setTimeout(() => {
          Swal.fire({
            type: "success",
            title: "איזה כיף!",
            text: "הרשמה והתחברות ושינוי פרטים בוצעו בהצלחה",
          });
          eventbus.$emit("closedialog", true);
          this.$store.dispatch("setJWT", jwt);
        }, 2000);
      } catch (error) {
        setTimeout(() => {
          Swal.fire({
            type: "error",
            title: "אופס",
            text: "נראה שהייתה שגיאה ברישום",
          });
        }, 2000);
      } finally {
        setTimeout(() => {
          eventbus.$emit("stoploading");
        }, 2000);
      }
    },
       async getUserInfo(){
      if(this.jwt!=="---"){
         this.userInfo = await Axios.Users().getUser(this.jwt);

         return this.userInfo;
      }else{
        this.userInfo = null;

        return null;
      }
    },
    getToday() {
      const date = new Date();

      return date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear();
    }
  },
};
</script>
