<template>
  <v-app>
    <frame />
  </v-app>
</template>

<script>
import frame from "@/components/Frame";

export default {
  components: {
    frame,
  },
};
</script>

<style>
.v-app {
  color: light-dark(black, white);
}
a {
  cursor: pointer;
}
.v-application {
  font-family: "Assistant";
}
.swal2-container {
  font-family: "Assistant";
}
</style>
