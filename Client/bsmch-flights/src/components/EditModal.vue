<template>
    <v-dialog v-model="modalOpen" max-width="500" :persistent="true">
      <v-card>
        <v-card-title class="title">{{ title }}</v-card-title>
        <v-card-text>
            <v-btn @click="toggleInput">שנה מחיר</v-btn>
          <v-text-field
                  v-if="showInput"
                  v-model="newPrice"
                  label="enter the new price"
                
                ></v-text-field>
                <v-card-text class="mt-5">   
          <slot></slot>
          <v-btn color="success" @click="changePrice()"
                    >בצע שינויים</v-btn
                  >
                  </v-card-text>
        </v-card-text>
      </v-card>
    </v-dialog>
  </template>
  
  <script>
  import api from "@/api/api";
import Swal from "sweetalert2";

  export default {
    name: "Modal",
    data(){
        return{
            showInput: false,
            newPrice: null,
        }
    },
    props: {
        flight: {},
        modalOpen: {},
      title: {
        type: String,
        default: "Modal Title"
      }
    },
methods: {
    toggleInput() {
      this.showInput = !this.showInput; // Toggle the visibility of the input box
    },
    async changePrice() {
      await api.Flights().changeFlightsPrice(this.flight[0].id, this.newPrice);
      Swal.fire({
          type: "success",
          title: "המחיר עודכן",
        });
        this.$router.push("/");
    },
}
    
  };
  </script>
  
<style scoped>
.title {
  display: flex;
  justify-content: center;
  font-family: "Assistant" !important;
}
</style>
  
