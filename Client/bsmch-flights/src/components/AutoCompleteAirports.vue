<template>
  <v-autocomplete
    rounded
    solo
    v-model="input"
    :items="sortedAirportNames"
    :clearable="!from"
    :success="success"
    :label="`${!from ? $t('בחר לאן לטוס') : $t('בחר מאיפה לטוס')}`"
    ref="typing"
  >
    <template v-slot:no-data>
      <v-list-item>
        <v-list-item-title> אין עיר בשם </v-list-item-title>
      </v-list-item>
    </template>
  </v-autocomplete>
</template>

<script>
import api from "@/api/api";

export default {
  props: ["from", "name"],
  data() {
    return {
      success: this.from ? true : false,
      input: this.from ? "דבלין, אירלנד" : null,
      airports: [],
      userAirport: null,
    };
  },
  async created() {
    this.airports = await api.Airports().getAllAirports();
    this.userAirport = this.airports.find(
      (curr) => curr.name == "דבלין, אירלנד"
    );
  },

  computed: {
    sortedAirportNames() {
      return this.airports.map((curr) => curr.name).sort();
    },
  },

  watch: {
    input(val) {
      this.success = val ? true : false;
      const wantedAirport = this.airports.find((curr) => curr.name == val);
      this.$emit(
        "airport",
        wantedAirport != undefined ? wantedAirport : { name: "" }
      );
    },
    name() {
      this.input = this.name;
    },
  },
};
</script>
