<template>
  <span>
    <v-card
      width="540px"
      @click="dialog = true"
      color="rgba(255, 255, 255, 0.8)"
      light
      class="ma-1"
    >
      <v-card-title primary-title class="pb-3 pt-2">
        <v-list-item-avatar color="grey darken-3">
          <v-img
            class="elevation-6"
            :src="pictureUrl"
            :lazy-src="defaultPicture"
            @error="imgError"
          ></v-img>
        </v-list-item-avatar>
        <h2>{{ username }}</h2>
        <v-spacer></v-spacer>
        <v-rating
          style="transform: rotateY(180deg);"
          readonly
          half-increments
          v-model="rating"
          color="warning"
          background-color="grey"
        ></v-rating>
      </v-card-title>
      <v-card-text class="text-truncate"> {{ text }} </v-card-text>
    </v-card>

    <v-dialog v-model="dialog" max-width="400px">
      <v-card dark>
        <v-card-title primary-title>
          <v-list-item-avatar color="grey darken-3">
            <v-img
              class="elevation-6"
              :src="pictureUrl"
              :lazy-src="defaultPicture"
              @error="imgError"
            ></v-img>
          </v-list-item-avatar>
          <h2>{{ username }}</h2>
          <v-spacer></v-spacer>
          <v-btn icon dark @click="dialog = false">
            <v-icon>close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text> {{ text }} </v-card-text>
      </v-card>
    </v-dialog>
  </span>
</template>

<script>
export default {
  props: ['username', 'img', 'text', 'rating'],
  data() {
    return {
      dialog: false,
      didLoadFail: false,
      defaultPicture: 'https://image.flaticon.com/icons/png/512/64/64572.png',
    };
  },
  computed: {
    pictureUrl() {
      return this.didLoadFail ? this.defaultPicture : this.img;
    },
  },
  methods: {
    imgError() {
      this.didLoadFail = true;
    },
  },
};
</script>
